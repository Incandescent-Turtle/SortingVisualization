package main.visualizers;

import main.VisualSortingTool;
import main.sorters.Sorter.Sorters;
import main.vcs.ColorVisualComponent;
import main.vcs.VisualComponent;
import main.visualizers.bases.BarVisualizer;

import java.awt.*;

public class ColorGradientVisualizer extends BarVisualizer
{
	public ColorGradientVisualizer(VisualSortingTool sortingTool)
	{
		super(sortingTool, Sorters.COLOR_GRADIENT);
	}
	
	@Override
	public void setDefaultValues()
	{
		super.setDefaultValues();
		componentGap = 0;
		minMargin = 0;		
	}

	//called for each bar
	@Override
	protected void drawComponent(Graphics2D g, double x, VisualComponent[] array, int i)
	{
		g.setColor(getHighlightAt(i));
		//draws this bar
		drawBar(g, x, sortingTool.getVisualizerHeight());
		g.setColor(((ColorVisualComponent)sortingTool.getSorter().getArray()[i]).getColor());
	}

	/**
	 * this is overriden to populate the highlights array with gradient values before real <br>
	 * highlights are set
	 */
	@Override
	public void resetHighlights()
	{
		for(int i : highlightsToRest)
		{
			highlight(i, ((ColorVisualComponent) sortingTool.getSorter(identifier).getArray()[i]).getColor());
		}
		highlightsToRest.clear();
	}
}