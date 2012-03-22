/**
 * Copyright (c) 2009--2012, Pietzsch, Preibisch & Saalfeld
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.imglib2.interpolation.neighborsearch;

import net.imglib2.RandomAccess;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.neighborsearch.KNearestNeighborSearch;
import net.imglib2.type.numeric.RealType;

/**
 * {@link RealRandomAccess} to a {@link RandomAccess} that performs linear interpolation
 * based on all k-nearest neighbors
 *
 * @param <T>
 *
 * @author Stephan Preibisch
 */
public class InverseDistanceWeightingInterpolator< T extends RealType< T > > extends RealPoint implements RealRandomAccess< T >
{
	final protected KNearestNeighborSearch< T > search;
	final T value;
	final int numNeighbors;
	final double p;
	
	/**
	 * Creates a new {@link InverseDistanceWeightingInterpolator} based on a {@link KNearestNeighborSearch}
	 * 
	 * @param search - the {@link KNearestNeighborSearch}
	 * @param p
	 */
	public InverseDistanceWeightingInterpolator( final KNearestNeighborSearch< T > search, final double p )
	{
		super( search.numDimensions() );

		this.search = search;
		this.p = p;
		
		search.search( this );
		this.value = search.getSampler( 0 ).get().copy();
		this.numNeighbors = search.numNeighbors();
	}

	@Override
	public T get()
	{
		search.search( this );

		double sumIntensity = 0;
		double sumWeights = 0;
		
		for ( int i = 0; i < numNeighbors; ++i )
		{
			final T t = search.getSampler( i ).get();
			
			if ( t == null )
				break;
			
			final double weight = computeWeight( search.getDistance( i ) );
			
			sumWeights += weight;
			sumIntensity += t.getRealDouble() * weight;
		}
		
		value.setReal( sumIntensity / sumWeights  );
		
		return value;
	}
	
	protected double computeWeight( final double distance )
	{
		return 1.0 / Math.pow( distance, p ); 
	}

	@Override
	public InverseDistanceWeightingInterpolator< T > copy()
	{
		//TODO: Ugly cast, needs a change in the KNearestNeighborSearch interface
		return new InverseDistanceWeightingInterpolator< T >( (KNearestNeighborSearch<T>)search.copy(), p );
	}

	@Override
	public InverseDistanceWeightingInterpolator< T > copyRealRandomAccess()
	{
		return copy();
	}
}
