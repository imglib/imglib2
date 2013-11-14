package net.imglib2.descriptors.geometric.diameter;

import java.awt.Polygon;

import net.imglib2.descriptors.ModuleInput;

public class FeretsDiameterPolygon extends FeretsDiameter
{
	@ModuleInput
	Polygon polygon;

	@Override
	protected double calculateFeature() 
	{
		double diameter = 0.0;
		
		for (int i = 0; i < polygon.npoints; i++)
		{
			for (int j = 0; j < polygon.npoints; j++)
			{
				if ( i != j)
				{
					double dist = 0.0;
					
					dist += (polygon.xpoints[i] - polygon.xpoints[j]) * (polygon.xpoints[i] - polygon.xpoints[j]);
					dist += (polygon.ypoints[i] - polygon.ypoints[j]) * (polygon.ypoints[i] - polygon.ypoints[j]);
					
					diameter = Math.max(diameter, dist);
				}
			}
		}
		
		return Math.sqrt(diameter);
	}
	
	@Override
	public double priority()
	{
		return 1.0;
	}

}
