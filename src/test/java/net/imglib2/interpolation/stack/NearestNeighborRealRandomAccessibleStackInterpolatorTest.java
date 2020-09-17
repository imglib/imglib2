/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.interpolation.stack;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.imglib2.FinalInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.Views;

/**
 * @author Stephan Saalfeld
 *
 */
public class NearestNeighborRealRandomAccessibleStackInterpolatorTest
{

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{}

	@Test
	public void test()
	{
		final FinalInterval interval = new FinalInterval( 6, 33 );
		final FinalInterval cropInterval = new FinalInterval( interval.dimension( 0 ), interval.dimension( 1 ), 10 );

		final ArrayList< RealRandomAccessible< LongType > > stack = new ArrayList<>();
		for ( int i = 0; i < cropInterval.dimension( 2 ); ++i )
		{
			final int slice = i;
			final FunctionRandomAccessible< LongType > indices = new FunctionRandomAccessible<>(
					2,
					( x, y ) -> {
						y.set( slice * interval.dimension( 0 ) * interval.dimension( 1 ) + IntervalIndexer.positionToIndex( x, interval ) );
					},
					LongType::new );

			stack.add( Views.interpolate( indices, new NearestNeighborInterpolatorFactory<>() ) );
		}

		final RealRandomAccessible< LongType > interpolatedStack = new Interpolant<>( stack, new NearestNeighborRealRandomAccessibleStackInterpolatorFactory<>(), 3 );

		int i = 0;
		for ( final LongType t : Views.flatIterable( Views.interval( Views.raster( interpolatedStack ), cropInterval ) ) )
			Assert.assertTrue( t.get() == i++ );
	}
}
