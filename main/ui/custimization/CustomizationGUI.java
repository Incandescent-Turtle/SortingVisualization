package main.ui.custimization;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.VisualSortingTool;
import main.algorithms.Algorithm;
import main.interfaces.OnChangeAction;
import main.interfaces.RetrieveAction;
import main.sorters.Sorter;
import main.ui.GUIHandler;
import main.ui.custimization.storage.StorageValue;
import main.visualizers.bases.Visualizer;

@SuppressWarnings("serial")
public class CustomizationGUI extends JPanel
{
	public static final Preferences PREFS = Preferences.userRoot().node(VisualSortingTool.class.getSimpleName());
	
	//these panels stack on top of eachother, each showing the respective sorter/algorithm
	private JPanel sorterPanels, algorithmPanels;
	
	private CardLayout sorterLayout = new CardLayout();
	private CardLayout algorithmLayout = new CardLayout();
	
	/**
	 * the right side bar that contains all the customization settings 
	 * for all sorters/visualizers/algorithms
	 */
	public CustomizationGUI() {}
	
	/**
	 * to be called right after instantiation 
	 */
	public void init(VisualSortingTool sortingTool)
	{
		//will stack panels on top of eachother, starting from top
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//panel to hold sorter/visualizer settings
		sorterPanels = new JPanel(sorterLayout);
		//panel to hold individual algorithm settings
		algorithmPanels = new JPanel(algorithmLayout);
		
		for(Sorter sorter : sortingTool.getSorters())
		{
			sorterPanels.add(new CustomizationPanel(sortingTool, sorter), sorter.toString());
		}
		
		for(Algorithm algorithm : sortingTool.getAlgorithms())
		{
			algorithmPanels.add(new CustomizationPanel(sortingTool, algorithm), algorithm.toString());
		}
		
		addSectionTitle("Customization");
		add(sorterPanels);
		
		JPanel generalAlgorithmPanel = new JPanel();
		addSectionTitle("All Algorithms");
		generalAlgorithmPanel.setLayout(new BoxLayout(generalAlgorithmPanel, BoxLayout.Y_AXIS));
		Algorithm.addGeneralAlgorithmCustimizationComponents(sortingTool, generalAlgorithmPanel);
		add(generalAlgorithmPanel);
		
		add(algorithmPanels);
		
		//creates an invisible JLabel to push all the elemnents to the top....a little hacky
		JLabel fill = new JLabel();
		fill.setPreferredSize(new Dimension(0, 1000));
		fill.setMinimumSize(new Dimension(0, 0));
		add(fill);
		
		JButton resetToSave = new JButton("Reset to Saved Values");
		resetToSave.setAlignmentX(CENTER_ALIGNMENT);
		resetToSave.addActionListener(e -> 
		{
			StorageValue.loadAll(PREFS);
			sortingTool.getSorter().getVisualizer().resetHighlights();
			ColorButton.recolorButtons();
			sortingTool.getSorter().recalculateAndRepaint();
			GUIHandler.update();

		});
		add(resetToSave);
		GUIHandler.addToggleable(resetToSave);

		JButton resetToDefaultValues = new JButton("Reset to Default Values");
		resetToDefaultValues.setAlignmentX(CENTER_ALIGNMENT);
		resetToDefaultValues.addActionListener(e -> 
		{
			//delete
			StorageValue.removeAll(PREFS);
			StorageValue.loadAll(PREFS);
			sortingTool.getSorter().getVisualizer().resetHighlights();
			ColorButton.recolorButtons();
			sortingTool.getSorter().recalculateAndRepaint();
			GUIHandler.update();
		});
		GUIHandler.addToggleable(resetToDefaultValues);
		add(resetToDefaultValues);
		
		sortingTool.add(this, BorderLayout.LINE_END);
	}
	
	public void addSectionTitle(String title)
	{
		JLabel label = new JLabel("All Algorithns");
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		add(label);
		//adds space/underline below
		JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
		line.setPreferredSize(new Dimension(20,30));
		line.setVisible(true);
		add(line);
	}
	
	/**
	 * this makes the specified {@link Sorter}s {@link CustomizationPanel} display
	 */
	public void changeSorterPanel(Sorter sorter)
	{
		sorterLayout.show(sorterPanels, sorter.toString());
	}
	
	/**
	 * this makes the specified {@link Algorithm}s {@link CustomizationPanel} display
	 */
	public void changeAlgorithmPanel(Algorithm algorithm)
	{
		algorithmLayout.show(algorithmPanels, algorithm.toString());
	}
	
	/**
	 * Helper method to easily create spinners to modify int values
	 * @param changeAction this will be called with the new value passed in on a change
	 * @param onUpdate returns the value that when {@link #update()} is called will replace the spinners value
	 * @return
	 */
	public static JSpinner createJSpinner(VisualSortingTool sortingTool, SpinnerNumberModel nm, OnChangeAction<Integer> changeAction, RetrieveAction<Integer> onUpdate)
	{
		JSpinner spinner = new JSpinner(nm);
		GUIHandler.addUpdatables(() -> spinner.setValue(onUpdate.retrieve()));
		spinner.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				changeAction.doStuff((int) spinner.getValue());
				sortingTool.getSorter().recalculateAndRepaint();
			}
		});
		return spinner;
	}
	
	/**
	 * @return a button that switches the default color to pink
	 */
	public static JButton createMakePinkButton(VisualSortingTool sortingTool)
	{
		JButton makePink = new JButton("Make Pink");
		makePink.setBackground(new Color(222, 165,164));
		makePink.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Visualizer vis = sortingTool.getSorter().getVisualizer();
				
				//pastel pink
				vis.setDefaultColor(new Color(222, 165,164));
				ColorButton.recolorButtons();
				vis.resetHighlights();
				sortingTool.repaint();
			}
		});
		return makePink;
	}
}