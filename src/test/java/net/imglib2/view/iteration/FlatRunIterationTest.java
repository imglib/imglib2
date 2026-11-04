/*
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

package net.imglib2.view.iteration;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.AbstractArrayCursor;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.Views;

import org.junit.Test;

/**
 * Tests the fast path in {@link IterableTransformBuilder} for views whose
 * elements are a box of source elements in the same flat order, which then
 * iterate with a {@link FlatIndexCursor} over a flat cursor on the source.
 */
public class FlatRunIterationTest
{
	private static final long[] DIMS = { 5, 4, 6 };

	private static < I extends RandomAccessibleInterval< IntType > > I fill( final I img )
	{
		int i = 0;
		for ( final IntType t : Views.flatIterable( img ) )
			t.set( i++ );
		return img;
	}

	private static Img< IntType > arrayImg()
	{
		return fill( ArrayImgs.ints( DIMS ) );
	}

	private static Img< IntType > cellImg()
	{
		return fill( new CellImgFactory<>( new IntType(), 2, 3, 4 ).create( DIMS ) );
	}

	/**
	 * Check that both cursors of {@code Views.flatIterable( view )} visit the
	 * positions of {@code view} in flat order, with the values that random
	 * access sees there.
	 */
	private static void assertFlatIteration( final RandomAccessibleInterval< IntType > view )
	{
		final int n = view.numDimensions();
		final long[] dims = view.dimensionsAsLongArray();
		final long[] min = view.minAsLongArray();
		final long size = Intervals.numElements( view );
		final RandomAccess< IntType > ra = view.randomAccess();
		final RandomAccessibleInterval< IntType > flat = Views.flatIterable( view );
		for ( final Cursor< IntType > c : Arrays.asList( flat.cursor(), flat.localizingCursor() ) )
		{
			final long[] expected = new long[ n ];
			for ( long i = 0; i < size; ++i )
			{
				assertTrue( c.hasNext() );
				c.fwd();
				IntervalIndexer.indexToPositionWithOffset( i, dims, min, expected );
				assertArrayEquals( "position at " + i, expected, c.positionAsLongArray() );
				ra.setPosition( expected );
				assertEquals( "value at " + i, ra.get().get(), c.get().get() );
			}
			assertFalse( c.hasNext() );
		}
	}

	private static boolean isFlatRun( final RandomAccessibleInterval< IntType > view )
	{
		return Views.flatIterable( view ).cursor() instanceof FlatIndexCursor;
	}

	private static boolean isFastFlatRun( final RandomAccessibleInterval< IntType > view )
	{
		final Cursor< IntType > c = Views.flatIterable( view ).cursor();
		return c instanceof FlatIndexCursor && ( ( FlatIndexCursor< IntType > ) c ).getSourceCursor() instanceof AbstractArrayCursor;
	}

	@Test
	public void testTranslate()
	{
		final RandomAccessibleInterval< IntType > view = Views.translate( arrayImg(), 3, -2, 7 );
		assertTrue( isFastFlatRun( view ) );
		assertFlatIteration( view );
	}

	@Test
	public void testSingletonReshape()
	{
		for ( final long[] shape : new long[][] { { 5, 4, 1, 6 }, { 1, 5, 4, 6, 1 }, { 5, 4, 6, 1 } } )
		{
			final RandomAccessibleInterval< IntType > view = Views.reshape( arrayImg(), shape );
			assertTrue( Arrays.toString( shape ), isFastFlatRun( view ) );
			assertFlatIteration( view );
		}
		// removing singleton dimensions
		final RandomAccessibleInterval< IntType > withSingletons = Views.reshape( arrayImg(), 5, 1, 4, 6 );
		final RandomAccessibleInterval< IntType > view = Views.reshape( withSingletons, 1, 5, 4, 6, 1 );
		assertTrue( isFastFlatRun( view ) );
		assertFlatIteration( view );
	}

	@Test
	public void testAddDimension()
	{
		final RandomAccessibleInterval< IntType > view = Views.addDimension( arrayImg(), 0, 0 );
		assertTrue( isFastFlatRun( view ) );
		assertFlatIteration( view );

		// a new dimension of size > 1 repeats elements: not a flat run
		final RandomAccessibleInterval< IntType > repeated = Views.addDimension( arrayImg(), 0, 2 );
		assertFalse( isFlatRun( repeated ) );
		assertFlatIteration( repeated );

		// ... unless it is a size-1 sub-interval
		final RandomAccessibleInterval< IntType > slab = Views.interval( repeated, new long[] { 0, 0, 0, 1 }, new long[] { 4, 3, 5, 1 } );
		assertTrue( isFastFlatRun( slab ) );
		assertFlatIteration( slab );
	}

	@Test
	public void testTranslatedSlice()
	{
		// a MixedTransform with a zero component and translation of the others
		final RandomAccessibleInterval< IntType > view = Views.hyperSlice( Views.translate( arrayImg(), 1, 2, 3 ), 2, 5 );
		assertTrue( isFastFlatRun( view ) );
		assertFlatIteration( view );
	}

	@Test
	public void testNotOrderPreserving()
	{
		final RandomAccessibleInterval< IntType > permuted = Views.permute( arrayImg(), 0, 1 );
		assertFalse( isFlatRun( permuted ) );
		assertFlatIteration( permuted );

		final RandomAccessibleInterval< IntType > inverted = Views.invertAxis( arrayImg(), 1 );
		assertFalse( isFlatRun( inverted ) );
		assertFlatIteration( inverted );
	}

	@Test
	public void testCellImg()
	{
		// a CellImg is iterated in flat order by random access
		for ( final RandomAccessibleInterval< IntType > view : Arrays.asList(
				Views.translate( cellImg(), 3, -2, 7 ),
				Views.reshape( cellImg(), 5, 1, 4, 6 ) ) )
		{
			final Cursor< IntType > c = Views.flatIterable( view ).cursor();
			assertTrue( c instanceof FlatIndexCursor );
			assertTrue( ( ( FlatIndexCursor< IntType > ) c ).getSourceCursor() instanceof RandomAccessibleIntervalCursor );
			assertFlatIteration( view );
		}
	}

	@Test
	public void testSubInterval()
	{
		// not a contiguous run of the ArrayImg: iterated by random access
		final RandomAccessibleInterval< IntType > view = Views.interval( Views.translate( arrayImg(), 3, -2, 7 ), new long[] { 4, -1, 8 }, new long[] { 6, 1, 10 } );
		assertTrue( isFlatRun( view ) );
		assertFalse( isFastFlatRun( view ) );
		assertFlatIteration( view );
	}

	@Test
	public void testMixedAboveReshape()
	{
		// [R, M] with M inserting a dimension of size 1 above a genuine reshape
		final RandomAccessibleInterval< IntType > view = Views.addDimension( Views.reshape( arrayImg(), 8, 15 ), 0, 0 );
		assertTrue( isFastFlatRun( view ) );
		assertFlatIteration( view );

		// [M, R] with M a translated slice below a genuine reshape
		final RandomAccessibleInterval< IntType > below = Views.reshape( Views.hyperSlice( Views.translate( arrayImg(), 1, 2, 3 ), 2, 5 ), 2, 10 );
		assertTrue( isFastFlatRun( below ) );
		assertFlatIteration( below );
	}

	@Test
	public void testLoopBuilder()
	{
		for ( final RandomAccessibleInterval< IntType > view : Arrays.asList(
				Views.translate( arrayImg(), 3, -2, 7 ),
				Views.reshape( arrayImg(), 5, 1, 4, 6 ),
				Views.translate( cellImg(), 3, -2, 7 ) ) )
		{
			final Img< IntType > out = ArrayImgs.ints( view.dimensionsAsLongArray() );
			LoopBuilder.setImages( view, out ).forEachPixel( ( a, b ) -> b.set( a ) );
			final Cursor< IntType > expected = Views.flatIterable( view ).cursor();
			final Cursor< IntType > actual = out.cursor();
			while ( expected.hasNext() )
				assertEquals( expected.next().get(), actual.next().get() );
		}
	}
}
