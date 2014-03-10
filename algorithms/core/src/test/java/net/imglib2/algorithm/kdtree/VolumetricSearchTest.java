/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.algorithm.kdtree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.RealPoint;

import org.junit.Test;

/**
 * TODO
 * 
 */
public class VolumetricSearchTest
{

	@Test
	public void testVolumetricSearch()
	{
		final ArrayList< FinalInterval > list = new ArrayList< FinalInterval >();
		list.add( new FinalInterval( new long[] { 0, 0 }, new long[] { 1, 1 } ) );
		list.add( new FinalInterval( new long[] { 2, 2 }, new long[] { 3, 3 } ) );
		new VolumetricSearch< FinalInterval >( list );
	}

	@Test
	public void testFind()
	{
		final Random random = new Random( 12345 );
		//
		// Make up 100 random examples which hopefully will encompass
		// more corner cases than I can think of.
		//
		final long[] min = new long[ 3 ];
		final long[] max = new long[ 3 ];
		final double[] position = new double[ 3 ];
		for ( int i = 0; i < 100; i++ )
		{
			final int nIntervals = random.nextInt( 50 ) + 1;
			final ArrayList< FinalInterval > list = new ArrayList< FinalInterval >();
			for ( int j = 0; j < nIntervals; j++ )
			{
				for ( int k = 0; k < 3; k++ )
				{
					min[ k ] = Math.abs( random.nextLong() ) % 100;
					max[ k ] = min[ k ] + Math.abs( random.nextLong() ) % 100;
				}
				list.add( new FinalInterval( min, max ) );
			}
			for ( int j = 0; j < 3; j++ )
			{
				position[ j ] = random.nextDouble() * 100.0;
			}
			final VolumetricSearch< FinalInterval > vs = new VolumetricSearch< FinalInterval >( list );
			final List< FinalInterval > result = vs.find( new RealPoint( position ) );
			for ( final FinalInterval interval : result )
			{
				for ( int j = 0; j < 3; j++ )
				{
					assertTrue( position[ j ] >= interval.realMin( j ) );
					assertTrue( position[ j ] <= interval.realMax( j ) );
				}
			}
			for ( final FinalInterval interval : list )
			{
				if ( result.contains( interval ) )
					continue;
				boolean good = true;
				for ( int j = 0; j < 3; j++ )
				{
					good &= position[ j ] >= interval.realMin( j );
					good &= position[ j ] <= interval.realMax( j );
				}
				assertFalse( good );
			}
		}

	}

}
