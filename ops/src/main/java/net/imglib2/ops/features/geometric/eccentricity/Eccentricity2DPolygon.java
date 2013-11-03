package net.imglib2.ops.features.geometric.eccentricity;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.type.numeric.real.DoubleType;

public class Eccentricity2DPolygon extends AbstractFeature implements Eccentricity
{
	@RequiredInput
	Polygon polygon;

	@Override
	public boolean isCompatible( Class< ? > clazz )
	{
		return Eccentricity.class.isAssignableFrom(  clazz );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Eccentricity on Polygon Feature";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Eccentricity2DPolygon copy()
	{
		return new Eccentricity2DPolygon();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		Rectangle2D rec = polygon.getBounds2D();

		double result = ( rec.getWidth() > rec.getHeight() ) ? rec.getWidth() / rec.getHeight() : rec.getHeight() / rec.getWidth();

		return new DoubleType( result );
	}
}
