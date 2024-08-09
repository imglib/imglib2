/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
