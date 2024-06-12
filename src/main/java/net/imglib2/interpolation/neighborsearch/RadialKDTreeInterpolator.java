package net.imglib2.interpolation.neighborsearch;

import java.util.function.DoubleUnaryOperator;

import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.neighborsearch.RadiusNeighborSearch;
import net.imglib2.neighborsearch.RadiusNeighborSearchOnKDTree;
import net.imglib2.type.numeric.NumericType;

/**
 * A {@link RealRandomAccess} for {@link KDTree}s using a radial function used
 * by {@link RadialKDTreeInterpolatorFactory}.
 */
public class RadialKDTreeInterpolator< T extends NumericType< T > > extends RealPoint implements RealRandomAccess< T >
{
	protected static final double minThreshold = Double.MIN_VALUE * 1000;

	protected final RadiusNeighborSearch< T > search;

	protected final double maxRadius;

	protected final double maxSquaredRadius;

	protected final KDTree< T > tree;

	protected final T value;

	protected final T tmp;

	protected final DoubleUnaryOperator squaredRadiusFunction;

	public RadialKDTreeInterpolator(
			final KDTree< T > tree,
			final DoubleUnaryOperator squaredRadiusFunction,
			final double maxRadius,
			final T t )
	{
		super( tree.numDimensions() );

		this.squaredRadiusFunction = squaredRadiusFunction;
		this.tree = tree;
		this.search = new RadiusNeighborSearchOnKDTree< T >( tree );
		this.maxRadius = maxRadius;
		this.maxSquaredRadius = maxRadius * maxRadius;
		this.value = t.copy();
		this.tmp = t.copy();
	}

	public double getMaxRadius()
	{
		return maxRadius;
	}

	@Override
	public T get()
	{
		value.setZero();
		search.search( this, maxRadius, false );
		if ( search.numNeighbors() == 0 )
			return value;

		for ( int i = 0; i < search.numNeighbors(); ++i )
		{
			// can't multiply the value returned
			tmp.set( search.getSampler( i ).get() );
			tmp.mul( squaredRadiusFunction.applyAsDouble( search.getSquareDistance( i ) ) );
			value.add( tmp );
		}
		return value;
	}

	@Override
	public RadialKDTreeInterpolator< T > copy()
	{
		return new RadialKDTreeInterpolator< T >( tree, squaredRadiusFunction, maxRadius, value );
	}

	@Override
	public RadialKDTreeInterpolator< T > copyRealRandomAccess()
	{
		return copy();
	}
}
