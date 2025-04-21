/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
/**
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.imglib2.view.composite;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

/**
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 */
public class CompositeTest
{
	private static final Random rnd = new Random( 0 );

	private static final int w = 10, h = 17, d = 23;

	private static final double[] refDouble = new double[ w * h * d ];

	private static final float[] refFloat = new float[ refDouble.length ];

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Arrays.setAll( refDouble, i -> rnd.nextDouble() * 100 );
		for ( int i = 0; i < refFloat.length; ++i )
			refFloat[ i ] = rnd.nextFloat() * 100;
	}

	@Test
	public final void testDoubleVector()
	{
		final RealComposite< DoubleType > vector = DoubleType.wrapVector( refDouble );
		for ( int i = 0; i < vector.getLength(); ++i )
			assertEquals( refDouble[ i ], vector.get( i ).get(), 0.0000001 );
		final RealComposite< DoubleType > vector2 = DoubleType.createVector( refDouble.length );
		for ( int i = 0; i < vector2.getLength(); ++i )
			assertEquals( 0, vector2.get( i ).get(), 0.0000001 );

		for ( int i = 0; i < vector2.getLength(); ++i )
			vector2.get( i ).set( refDouble[ i ] );
		for ( int i = 0; i < vector2.getLength(); ++i )
			assertEquals( refDouble[ i ], vector2.get( i ).get(), 0.0000001 );
	}

	@Test
	public final void testFloatVector()
	{
		final RealComposite< FloatType > vector = FloatType.wrapVector( refFloat );
		for ( int i = 0; i < vector.getLength(); ++i )
			assertEquals( refFloat[ i ], vector.get( i ).get(), 0.0000001 );
		final RealComposite< FloatType > vector2 = FloatType.createVector( refFloat.length );
		for ( int i = 0; i < vector2.getLength(); ++i )
			assertEquals( 0, vector2.get( i ).get(), 0.0000001 );

		for ( int i = 0; i < vector2.getLength(); ++i )
			vector2.get( i ).set( refFloat[ i ] );
		for ( int i = 0; i < vector2.getLength(); ++i )
			assertEquals( refFloat[ i ], vector2.get( i ).get(), 0.0000001 );
	}

	@Test
	public final void testCollapse()
	{
		final ArrayImg< DoubleType, DoubleArray > img = ArrayImgs.doubles( refDouble, w, h, d );
		final CompositeIntervalView< DoubleType, RealComposite< DoubleType > > collapsed = Views.collapseReal( img );
		final ArrayRandomAccess< DoubleType > srcAccess = img.randomAccess();
		final Cursor< RealComposite< DoubleType > > compositeCursor = Views.iterable( collapsed ).cursor();
		int i = 0;
		RealComposite< DoubleType > composite = null;
		while ( compositeCursor.hasNext() )
		{
			if ( i == 0 )
			{
				composite = compositeCursor.next();
				compositeCursor.localize( srcAccess );
			}

			srcAccess.setPosition( i, collapsed.numDimensions() );
			assertEquals( composite.get( i ).get(), srcAccess.get().get(), 0.0000001 );

			++i;
			if ( i == img.dimension( collapsed.numDimensions() ) )
				i = 0;
		}
	}

	@Test
	public final void testInflate()
	{
		final ArrayImg< DoubleType, DoubleArray > img = ArrayImgs.doubles( refDouble, w, h, d );
		final CompositeIntervalView< DoubleType, RealComposite< DoubleType > > collapsed = Views.collapseReal( img );
		final IntervalView< DoubleType > inflated = Views.interval( Views.inflate( collapsed ), img );
		final Cursor< DoubleType > srcCursor = Views.iterable( img ).cursor();
		final Cursor< DoubleType > inflatedCursor = Views.iterable( inflated ).cursor();
		while ( srcCursor.hasNext() )
		{
			assertEquals( srcCursor.next().get(), inflatedCursor.next().get(), 0.0000001 );
		}
	}

	@Test
	public final void testInterleave()
	{
		final ArrayImg< DoubleType, DoubleArray > img = ArrayImgs.doubles( refDouble, w, h, d );
		final CompositeIntervalView< DoubleType, RealComposite< DoubleType > > collapsedFirst =
				Views.collapseReal(
						Views.moveAxis( img, 0, img.numDimensions() - 1 ) );
		final IntervalView< DoubleType > interleaved = Views.interval( Views.interleave( collapsedFirst ), img );
		final Cursor< DoubleType > srcCursor = Views.iterable( img ).cursor();
		final Cursor< DoubleType > interleavedCursor = Views.iterable( interleaved ).cursor();
		while ( srcCursor.hasNext() )
		{
			assertEquals( srcCursor.next().get(), interleavedCursor.next().get(), 0.0000001 );
		}
	}
}
