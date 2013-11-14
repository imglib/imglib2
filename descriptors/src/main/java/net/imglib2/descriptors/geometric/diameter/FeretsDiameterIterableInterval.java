package net.imglib2.descriptors.geometric.diameter;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.descriptors.ModuleInput;

public class FeretsDiameterIterableInterval extends FeretsDiameter
{
	@ModuleInput
	IterableInterval< ? > ii;

	@Override
	protected double calculateFeature() 
	{
        double diameter = 0.0f;

        Cursor< ? > cursor = ii.localizingCursor();

        List<Point> points = new ArrayList<Point>((int) ii.size());

        int[] position = new int[cursor.numDimensions()];
        while (cursor.hasNext()) 
        {
        	cursor.fwd();
            cursor.localize(position);
            points.add(new Point(position));
                
        }

        for (Point p : points) 
        {
                for (Point p2 : points) 
                {
                        double dist = 0.0f;
                        for (int i = 0; i < p.numDimensions(); i++) 
                        {
                                dist += (p.getIntPosition(i) - p2.getIntPosition(i))
                                                * (p.getIntPosition(i) - p2.getIntPosition(i));
                        }
                        diameter = Math.max(diameter, dist);
                }
        }

        // sqrt for euclidean
        diameter = Math.sqrt(diameter);
		
		return diameter;
	}
	
	@Override
	public double priority()
	{
		return -1.0;
	}

}
