package main.visualizers;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.SpinnerNumberModel;

import main.VisualSortingTool;
import main.sorters.Sorter.Sorters;
import main.ui.custimization.ColorButton;
import main.ui.custimization.CustomizationGUI;
import main.ui.custimization.CustomizationPanel;
import main.ui.custimization.values.StorageValue;
import main.vcs.VisualComponent;
import main.visualizers.bases.Visualizer;

public class ColorGradientVisualizer extends Visualizer
{
	public ColorGradientVisualizer(VisualSortingTool sortingTool)
	{
		super(sortingTool, Sorters.COLOR_GRADIENT);
	}
	
	@Override
	public void setDefaultValues()
	{
		componentWidth = 15;
		componentGap = 2;
		minMargin = 2;		
	}

	@Override
	public void addCustomizationComponents(CustomizationPanel cp)
	{
		SpinnerNumberModel nm = new SpinnerNumberModel(componentWidth, 1, 100, 1);
		
		//spinner to change bar width
		cp.addRow("Bar Width:", CustomizationGUI.createJSpinner(sortingTool, nm, n -> componentWidth = n, () -> componentWidth));
		
		//spinner to change gap between bars
		nm = new SpinnerNumberModel(componentGap, 0, 20, 1);
		cp.addRow("Gap:", CustomizationGUI.createJSpinner(sortingTool, nm, n -> componentGap = n, () -> componentGap));
		
		//spinner to change left/right margin
		nm = new SpinnerNumberModel(minMargin, 0, 100, 1);
		cp.addRow("Margin:", CustomizationGUI.createJSpinner(sortingTool, nm, n -> minMargin = n, () -> minMargin));	
		
		//change background button
		cp.addRow(ColorButton.createBackgroundColorPickingButton(sortingTool), true);
	}
	
	@Override
	public void addStorageValues()
	{
		//setting up specified values with preferences
		StorageValue.addStorageValues(
				createWidthStorageValue(),
				createGapStorageValue(),
				createMarginStorageValue()
		);
	}
	
	@Override
	public void drawArray(Graphics g, VisualComponent[] array, int size)
	{
		for(int i = 0; i < size; i++)
		{
			//honestly idk what this is, i just dont know how to make a gradient, but somehow i made this
			int red, green, blue;
			red = green = blue = 255;
			if(array[i].getValue() <= 255)
			{
				red = 255 - array[i].getValue();
			} else if(array[i].getValue() <= 255*2) {
				red = 0;
				green = 255 - (array[i].getValue() - 255);
			} else {
				red = 0;
				green = 0;
				blue = 255 - (array[i].getValue() - 255 - 255);
				if(blue < 0 || blue > 255) blue = 255;
			}
			g.setColor(highlights[i] != defaultColor ? highlights[i] : new Color(red, green, blue));
			int maxHeight = sortingTool.getVisualizerHeight() - 20;
			g.fillRect(getRealHMargins(size) + i*(componentWidth + componentGap), sortingTool.getVisualizerHeight()-maxHeight, componentWidth, maxHeight);
			highlights[i] = defaultColor; 
		}
	}
}