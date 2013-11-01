package net.imglib2.ops.features.providers;

import java.awt.Polygon;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.type.logic.BitType;

public class GetPolygon extends AbstractFeature< Polygon >
{

	@RequiredFeature
	GetBinaryMask binaryMask = new GetBinaryMask();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Polygon Tracer";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GetPolygon copy()
	{
		return new GetPolygon();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Polygon recompute()
	{
		final RandomAccessibleInterval< BitType > ii = binaryMask.get();
		return PolygonTools.extractPolygon( ii, new int[ ii.numDimensions() ] );
	}
}
