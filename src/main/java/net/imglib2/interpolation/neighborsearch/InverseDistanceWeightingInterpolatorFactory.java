/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.RealInterval;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.neighborsearch.KNearestNeighborSearch;
import net.imglib2.type.numeric.RealType;

/**
 * Factory for {@link InverseDistanceWeightingInterpolator} instances that work
 * on a {@link KNearestNeighborSearch}.
 *
 * @param <T>
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 */
public class InverseDistanceWeightingInterpolatorFactory< T extends RealType< T > >
		implements InterpolatorFactory< T, KNearestNeighborSearch< T > >
{
	final double p;

	public InverseDistanceWeightingInterpolatorFactory()
	{
		this.p = 3;
	}

	public InverseDistanceWeightingInterpolatorFactory( final double p )
	{
		this.p = p;
	}

	/**
	 * Creates a new {@link InverseDistanceWeightingInterpolator} using a copy
	 * of the passed {@link KNearestNeighborSearch}.
	 */
	@Override
	public InverseDistanceWeightingInterpolator< T > create( final KNearestNeighborSearch< T > search )
	{
		return new InverseDistanceWeightingInterpolator< T >( search.copy(), p );
	}

	/**
	 * <p>
	 * Creates a new {@link InverseDistanceWeightingInterpolator} using a copy
	 * of the passed {@link KNearestNeighborSearch}.
	 * </p>
	 *
	 * <p>
	 * For now, ignore the {@link RealInterval} and return
	 * {@link #create(KNearestNeighborSearch)}.
	 * </p>
	 */
	@Override
	public InverseDistanceWeightingInterpolator< T > create( final KNearestNeighborSearch< T > search, final RealInterval interval )
	{
		return create( search );
	}
}
