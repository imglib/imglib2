package net.imglib2.ops.descriptors.geometric.eccentricity;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import net.imglib2.ops.descriptors.ModuleInput;

public class EccentricityPolygon extends Eccentricity
{
	@ModuleInput
	Polygon polygon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		System.out.println( "Eccentricity Polygon" );
		Rectangle2D rec = polygon.getBounds2D();
		return ( rec.getWidth() > rec.getHeight() ) ? rec.getWidth() / rec.getHeight() : rec.getHeight() / rec.getWidth();
	}

	@Override
	public double priority()
	{
		return 10;
	}
}
