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

package net.imglib2.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.AbstractArrayCursor;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.array.ArraySubIntervalCursor;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarCursor;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.img.planar.PlanarPlaneSubsetCursor;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.transform.Transform;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.transform.integer.ReshapeTransform;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.view.iteration.FlatIndexCursor;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link Views#reshape}.
 * <p>
 * The image is filled so that the element at flat index {@code i} holds the
 * value {@code i}. A reshape must preserve flat order, so most assertions
 * reduce to "the value equals the flat index in the new shape".
 */
public class ReshapeViewTest
{
	/** 5 * 4 * 6 = 120 elements. */
	private static final long[] DIMS = { 5, 4, 6 };

	private static final long NUM_ELEMENTS = 120;

	/** View shapes: 1-D, 2-D, one coprime with DIMS, 4-D, and a general group. */
	private static final long[][] SHAPES = { { NUM_ELEMENTS }, { 4, 30 }, { 8, 15 }, { 2, 2, 5, 6 }, { 20, 6 }, { 3, 5, 8 }, { 10, 2, 6 } };

	private Img< IntType > img;

	@Before
	public void setUp()
	{
		img = fill( new ArrayImgFactory<>( new IntType() ).create( DIMS ) );
	}

	/**
	 * Set every element to its flat index.
	 */
	private static < I extends RandomAccessibleInterval< IntType > > I fill( final I img )
	{
		int i = 0;
		for ( final IntType t : Views.flatIterable( img ) )
			t.set( i++ );
		return img;
	}

	/**
	 * Check that {@code view} has the given {@code shape}, is zero-min, and
	 * holds the elements of {@code source} in flat order: through its cursors,
	 * which must report the right position, and through random access, visited
	 * in scattered order.
	 */
	private static void assertReshapeOf( final RandomAccessibleInterval< IntType > source, final RandomAccessibleInterval< IntType > view, final long[] shape )
	{
		final String what = "reshape to " + Arrays.toString( shape );
		assertArrayEquals( what, shape, view.dimensionsAsLongArray() );
		assertArrayEquals( what, new long[ shape.length ], view.minAsLongArray() );

		final int[] expected = new int[ ( int ) Intervals.numElements( source ) ];
		int k = 0;
		for ( final IntType t : Views.flatIterable( source ) )
			expected[ k++ ] = t.get();
		assertEquals( what, expected.length, Intervals.numElements( view ) );

		final RandomAccessibleInterval< IntType > flat = Views.flatIterable( view );
		for ( final Cursor< IntType > c : Arrays.asList( flat.cursor(), flat.localizingCursor() ) )
		{
			final long[] position = new long[ shape.length ];
			for ( int i = 0; i < expected.length; ++i )
			{
				assertTrue( what, c.hasNext() );
				c.fwd();
				IntervalIndexer.indexToPosition( i, shape, position );
				assertArrayEquals( what + " " + c.getClass().getSimpleName() + " position at " + i, position, c.positionAsLongArray() );
				for ( int d = 0; d < shape.length; ++d )
					assertEquals( position[ d ], c.getLongPosition( d ) );
				assertEquals( what + " " + c.getClass().getSimpleName() + " value at " + i, expected[ i ], c.get().get() );
			}
			assertFalse( what, c.hasNext() );
		}

		final RandomAccess< IntType > ra = view.randomAccess();
		final long[] position = new long[ shape.length ];
		for ( int i = 0; i < expected.length; ++i )
		{
			final int scattered = ( int ) ( ( i * 37L ) % expected.length );
			IntervalIndexer.indexToPosition( scattered, shape, position );
			ra.setPosition( position );
			assertEquals( what + " random access at " + Arrays.toString( position ), expected[ scattered ], ra.get().get() );
		}
	}

	@Test
	public void testDimensionsAndMin()
	{
		final RandomAccessibleInterval< IntType > view = Views.reshape( img, 4, 30 );
		assertArrayEquals( new long[] { 4, 30 }, view.dimensionsAsLongArray() );
		assertArrayEquals( new long[] { 0, 0 }, view.minAsLongArray() );
		assertEquals( 2, view.numDimensions() );
		assertEquals( view.dimensionsAsLongArray().length, Views.reshape( img, new FinalInterval( 4, 30 ) ).numDimensions() );
	}

	@Test
	public void testReshapes()
	{
		for ( final long[] shape : SHAPES )
		{
			final RandomAccessibleInterval< IntType > view = Views.reshape( img, shape );
			assertReshapeOf( img, view, shape );
			assertTrue( view.randomAccess() instanceof ReshapeRandomAccess );
			assertTrue( Views.flatIterable( view ).cursor() instanceof FlatIndexCursor );
		}
	}

	@Test
	public void testSourceMinIsIgnored()
	{
		final RandomAccessibleInterval< IntType > translated = Views.translate( img, 100, -7, 3 );
		for ( final long[] shape : new long[][] { { 8, 15 }, DIMS, { 5, 1, 4, 6 } } )
		{
			final RandomAccessibleInterval< IntType > view = Views.reshape( translated, shape );
			assertReshapeOf( img, view, shape );
		}
		// the zeroMin translation below the reshape still allows the fast cursor
		assertTrue( Views.reshape( translated, 8, 15 ).cursor() instanceof FlatIndexCursor );
	}

	@Test
	public void testReshapeOfPermutedSource()
	{
		final RandomAccessibleInterval< IntType > permuted = Views.permute( img, 0, 2 );
		for ( final long[] shape : SHAPES )
			assertReshapeOf( permuted, Views.reshape( permuted, shape ), shape );
	}

	@Test
	public void testOtherImgTypes()
	{
		final List< RandomAccessibleInterval< IntType > > sources = Arrays.asList(
				fill( new PlanarImgFactory<>( new IntType() ).create( DIMS ) ),
				fill( new CellImgFactory<>( new IntType(), 3, 3, 4 ).create( DIMS ) ) );
		for ( final RandomAccessibleInterval< IntType > source : sources )
			for ( final long[] shape : SHAPES )
				assertReshapeOf( source, Views.reshape( source, shape ), shape );
	}

	@Test
	public void testCursorTypes()
	{
		// PlanarImg: the whole image, or a single plane
		final Img< IntType > planar = fill( new PlanarImgFactory<>( new IntType() ).create( DIMS ) );
		final Cursor< IntType > full = Views.reshape( planar, 8, 15 ).cursor();
		assertTrue( full instanceof FlatIndexCursor );
		assertTrue( ( ( FlatIndexCursor< IntType > ) full ).getSourceCursor() instanceof PlanarCursor );
		final Cursor< IntType > plane = Views.reshape( Views.hyperSlice( planar, 2, 3 ), 2, 10 ).cursor();
		assertTrue( plane instanceof FlatIndexCursor );
		assertTrue( ( ( FlatIndexCursor< IntType > ) plane ).getSourceCursor() instanceof PlanarPlaneSubsetCursor );

		// ArrayImg
		final Cursor< IntType > array = Views.reshape( img, 8, 15 ).cursor();
		assertTrue( ( ( FlatIndexCursor< IntType > ) array ).getSourceCursor() instanceof AbstractArrayCursor );

		// CellImg iterates cell by cell, which is not flat, so it is iterated
		// in flat order by random access
		final Img< IntType > cells = fill( new CellImgFactory<>( new IntType(), 3, 3, 4 ).create( DIMS ) );
		final Cursor< IntType > cellCursor = Views.reshape( cells, 8, 15 ).cursor();
		assertTrue( cellCursor instanceof FlatIndexCursor );
		assertTrue( ( ( FlatIndexCursor< IntType > ) cellCursor ).getSourceCursor() instanceof RandomAccessibleIntervalCursor );

		// permuted source is not flat
		assertTrue( Views.reshape( Views.permute( img, 0, 2 ), 8, 15 ).cursor() instanceof RandomAccessibleIntervalCursor );
	}

	@Test
	public void testIntervalOfReshape()
	{
		final RandomAccessibleInterval< IntType > view = Views.reshape( img, 8, 15 );

		// not a single flat run
		final RandomAccessibleInterval< IntType > sub = Views.interval( view, new long[] { 2, 3 }, new long[] { 5, 9 } );
		assertTrue( Views.flatIterable( sub ).cursor() instanceof RandomAccessibleIntervalCursor );
		checkValues( sub, 8 );

		// full rows that map to planes 2..3 of the 5x4x6 source (flat 40..79)
		final RandomAccessibleInterval< IntType > rows = Views.interval( view, new long[] { 0, 5 }, new long[] { 7, 9 } );
		assertTrue( Views.flatIterable( rows ).cursor() instanceof FlatIndexCursor );
		checkValues( rows, 8 );

		// full rows that do not map to a box in the source (flat 24..79)
		final RandomAccessibleInterval< IntType > unaligned = Views.interval( view, new long[] { 0, 3 }, new long[] { 7, 9 } );
		assertTrue( Views.flatIterable( unaligned ).cursor() instanceof RandomAccessibleIntervalCursor );
		checkValues( unaligned, 8 );

		// a partial row that maps to one row of the 5x4x6 source (flat 25..29)
		final RandomAccessibleInterval< IntType > partial = Views.interval( view, new long[] { 1, 3 }, new long[] { 5, 3 } );
		assertTrue( Views.flatIterable( partial ).cursor() instanceof FlatIndexCursor );
		checkValues( partial, 8 );

		// a partial row that straddles two source rows (flat 26..30) is a
		// single flat run, but not a box in the source
		final RandomAccessibleInterval< IntType > straddling = Views.interval( view, new long[] { 2, 3 }, new long[] { 6, 3 } );
		assertTrue( Views.flatIterable( straddling ).cursor() instanceof RandomAccessibleIntervalCursor );
		checkValues( straddling, 8 );
	}

	@Test
	public void testTranslateOfReshape()
	{
		final RandomAccessibleInterval< IntType > view = Views.translate( Views.reshape( img, 8, 15 ), 3, -4 );
		final Cursor< IntType > c = view.localizingCursor();
		assertTrue( c instanceof FlatIndexCursor );
		while ( c.hasNext() )
		{
			c.fwd();
			assertEquals( ( c.getLongPosition( 0 ) - 3 ) + 8 * ( c.getLongPosition( 1 ) + 4 ), c.get().get() );
		}
		checkValues( Views.zeroMin( view ), 8 );
	}

	/**
	 * Check that every element of {@code view} (a sub-interval of a reshape to
	 * width {@code width}) has value {@code x + width * y}, through cursors and
	 * random access.
	 */
	private static void checkValues( final RandomAccessibleInterval< IntType > view, final long width )
	{
		final RandomAccess< IntType > ra = view.randomAccess();
		final RandomAccessibleInterval< IntType > flat = Views.flatIterable( view );
		for ( final Cursor< IntType > c : Arrays.asList( flat.cursor(), flat.localizingCursor() ) )
		{
			long count = 0;
			while ( c.hasNext() )
			{
				c.fwd();
				assertEquals( c.getLongPosition( 0 ) + width * c.getLongPosition( 1 ), c.get().get() );
				ra.setPosition( c );
				assertEquals( c.get().get(), ra.get().get() );
				++count;
			}
			assertEquals( Intervals.numElements( view ), count );
		}
	}

	@Test
	public void testNestedReshapeCollapses()
	{
		final RandomAccessibleInterval< IntType > inner = Views.reshape( img, 8, 15 );
		final long[] shape = { 2, 2, 5, 6 };
		final RandomAccessibleInterval< IntType > view = Views.reshape( inner, shape );
		assertReshapeOf( img, view, shape );

		final RandomAccessible< IntType > source = ( ( IntervalView< IntType > ) view ).getSource();
		assertTrue( source instanceof ReshapeTransformView );
		assertSame( img, ( ( ReshapeTransformView< IntType > ) source ).getSource() );
		assertEquals( new ReshapeTransform( shape, DIMS ), ( ( ReshapeTransformView< IntType > ) source ).getTransformToSource() );
	}

	@Test
	public void testRoundTrip()
	{
		final RandomAccessibleInterval< IntType > back = Views.reshape( Views.reshape( img, 8, 15 ), DIMS );
		assertReshapeOf( img, back, DIMS );
		// the collapsed reshape is the identity, which TransformBuilder removes
		assertTrue( back.randomAccess() instanceof ArrayRandomAccess );
	}

	@Test
	public void testReshapeOfCroppedReshape()
	{
		// The inner reshape's view is cropped, so the outer reshape's source
		// dimensions don't match, and the two must not be collapsed.
		final RandomAccessibleInterval< IntType > cropped = Views.interval( Views.reshape( img, 120 ), new long[] { 0 }, new long[] { 59 } );
		final RandomAccessibleInterval< IntType > view = Views.reshape( cropped, 6, 10 );
		assertReshapeOf( cropped, view, new long[] { 6, 10 } );

		final RandomAccessible< IntType > source = ( ( IntervalView< IntType > ) view ).getSource();
		assertTrue( ( ( ReshapeTransformView< IntType > ) source ).getSource() instanceof ReshapeTransformView );
	}

	@Test
	public void testInvalidDimensionsThrow()
	{
		assertThrows( IllegalArgumentException.class, () -> Views.reshape( img, 7, 17 ) );
		assertThrows( IllegalArgumentException.class, () -> Views.reshape( img, 0, 120 ) );
		assertThrows( IllegalArgumentException.class, () -> Views.reshape( img ) );
	}

	@Test
	public void testCursorJumpFwdCopyAndReset()
	{
		final long[] shape = { 8, 15 };
		final RandomAccessibleInterval< IntType > view = Views.reshape( img, shape );
		for ( final Cursor< IntType > c : Arrays.asList( view.cursor(), view.localizingCursor() ) )
		{
			final String what = c.getClass().getSimpleName();

			c.jumpFwd( 50 ); // equivalent to fifty fwd() calls from before-first
			assertAt( c, 49, shape, what + " after jumpFwd" );

			final Cursor< IntType > copy = c.copy();
			c.fwd();
			assertAt( c, 50, shape, what + " after fwd" );
			assertAt( copy, 49, shape, what + " copy is independent" );
			copy.fwd();
			assertAt( copy, 50, shape, what + " copy advances" );

			c.reset();
			assertTrue( what + " hasNext after reset", c.hasNext() );
			c.fwd();
			assertAt( c, 0, shape, what + " after reset" );
		}
	}

	private static void assertAt( final Cursor< IntType > c, final long flatIndex, final long[] shape, final String what )
	{
		final long[] expected = new long[ shape.length ];
		IntervalIndexer.indexToPosition( flatIndex, shape, expected );
		assertArrayEquals( what + ": position", expected, c.positionAsLongArray() );
		assertEquals( what + ": value", flatIndex, c.get().get() );
	}

	@Test
	public void testIterationOrderIsFlat()
	{
		final RandomAccessibleInterval< IntType > view = Views.reshape( img, 8, 15 );
		assertTrue( view.iterationOrder() instanceof FlatIterationOrder );
		assertEquals( ArrayImgs.ints( 8, 15 ).iterationOrder(), view.iterationOrder() );
		assertSame( view, Views.flatIterable( view ) );
		assertEquals( NUM_ELEMENTS, view.size() );
		assertEquals( 0, view.firstElement().get() );
		assertEquals( NUM_ELEMENTS, view.parallelStream().count() );
		assertEquals( NUM_ELEMENTS * ( NUM_ELEMENTS - 1 ) / 2, view.stream().mapToLong( IntType::get ).sum() );
	}

	@Test
	public void testSingletonOnlyReshapes()
	{
		final long[][] shapes = { { 5, 1, 4, 6 }, { 1, 5, 4, 6, 1 }, { 5, 4, 6, 1 } };
		for ( final long[] shape : shapes )
		{
			final RandomAccessibleInterval< IntType > view = Views.reshape( img, shape );
			assertReshapeOf( img, view, shape );
			assertTrue( ( ( IntervalView< IntType > ) view ).getSource() instanceof MixedTransformView );
			assertFalse( view.randomAccess() instanceof ReshapeRandomAccess );
		}

		// A source with singleton axes of its own, reshaped both to another
		// singleton-only shape and to a genuine split.
		final RandomAccessibleInterval< IntType > withSingletons = Views.reshape( img, 5, 1, 4, 6 );
		for ( final long[] shape : new long[][] { { 1, 5, 4, 6, 1 }, DIMS, { 20, 6 } } )
			assertReshapeOf( img, Views.reshape( withSingletons, shape ), shape );
	}

	@Test
	public void testIdentityReshape()
	{
		assertSame( img, Views.reshape( img, DIMS ) );
		final RandomAccessibleInterval< IntType > translated = Views.translate( img, 100, -7, 3 );
		final RandomAccessibleInterval< IntType > view = Views.reshape( translated, DIMS );
		assertReshapeOf( img, view, DIMS );
		assertTrue( view.randomAccess() instanceof ArrayRandomAccess );
		assertTrue( view.cursor() instanceof ArraySubIntervalCursor );
	}

	@Test
	public void testExtensionAboveReshape()
	{
		final RandomAccess< IntType > ra = Views.extendBorder( Views.reshape( img, 8, 15 ) ).randomAccess();
		ra.setPosition( new long[] { -1, 3 } );
		assertEquals( 24, ra.get().get() );
		ra.setPosition( new long[] { 9, 14 } );
		assertEquals( 119, ra.get().get() );

		// extension is dropped when the requested interval is inside
		final RandomAccessibleInterval< IntType > inside = Views.interval( Views.extendBorder( Views.reshape( img, 8, 15 ) ), new FinalInterval( 8, 15 ) );
		assertTrue( inside.randomAccess() instanceof ReshapeRandomAccess );
		assertReshapeOf( img, inside, new long[] { 8, 15 } );
	}

	@Test
	public void testExtensionBelowReshape()
	{
		final RandomAccessibleInterval< IntType > extended = Views.interval( Views.extendValue( img, new IntType( -1 ) ),
				new long[] { -1, 0, 0 }, new long[] { 5, 3, 5 } );
		final long[] shape = { 28, 6 };
		assertReshapeOf( extended, Views.reshape( extended, shape ), shape );
	}

	@Test
	public void testFluentApi()
	{
		assertReshapeOf( img, img.view().reshape( 8, 15 ), new long[] { 8, 15 } );
	}

	@Test
	public void testLoopBuilder()
	{
		for ( final long[] shape : SHAPES )
		{
			final RandomAccessibleInterval< IntType > view = Views.reshape( img, shape );
			final Img< IntType > out = ArrayImgs.ints( shape );
			LoopBuilder.setImages( view, out ).forEachPixel( ( a, b ) -> b.set( a ) );
			assertReshapeOf( img, out, shape );

			final Img< IntType > out2 = ArrayImgs.ints( shape );
			LoopBuilder.setImages( view, out2 ).multiThreaded().forEachChunk( chunk -> {
				chunk.forEachPixel( ( a, b ) -> b.set( a ) );
				return null;
			} );
			assertReshapeOf( img, out2, shape );
		}
	}

	/**
	 * Exposes the transforms that {@link TransformBuilder} collects.
	 */
	private static class InspectTransformBuilder< T > extends TransformBuilder< T >
	{
		InspectTransformBuilder( final Interval interval, final RandomAccessible< T > randomAccessible )
		{
			super( interval, randomAccessible );
		}
	}

	@Test
	public void testTransformBuilder()
	{
		final RandomAccessibleInterval< IntType > view = Views.reshape( img, 8, 15 );
		final InspectTransformBuilder< IntType > b = new InspectTransformBuilder<>( view, view );
		assertEquals( 1, b.transforms.size() );
		assertTrue( b.transforms.get( 0 ) instanceof ReshapeTransform );
		assertNotNull( b.boundingBox );
		assertSame( img, b.source );

		// Two reshapes separated by an identity MixedTransformView are
		// concatenated once the identity is removed.
		final ReshapeTransform r1 = new ReshapeTransform( new long[] { 8, 15 }, DIMS );
		final ReshapeTransform r2 = new ReshapeTransform( new long[] { 2, 60 }, new long[] { 8, 15 } );
		final RandomAccessible< IntType > chain = new ReshapeTransformView<>(
				new MixedTransformView<>( new ReshapeTransformView<>( img, r1 ), new MixedTransform( 2, 2 ) ), r2 );
		final InspectTransformBuilder< IntType > b2 = new InspectTransformBuilder<>( new FinalInterval( 2, 60 ), chain );
		final List< Transform > transforms = b2.transforms;
		assertEquals( 1, transforms.size() );
		assertEquals( new ReshapeTransform( new long[] { 2, 60 }, DIMS ), transforms.get( 0 ) );
		assertReshapeOf( img, Views.interval( chain, new FinalInterval( 2, 60 ) ), new long[] { 2, 60 } );

		// ... and if they compose to a singleton-only reshape, that becomes a MixedTransform
		final ReshapeTransform r3 = new ReshapeTransform( new long[] { 5, 1, 4, 6 }, new long[] { 8, 15 } );
		final RandomAccessible< IntType > chain2 = new ReshapeTransformView<>(
				new MixedTransformView<>( new ReshapeTransformView<>( img, r1 ), new MixedTransform( 2, 2 ) ), r3 );
		final InspectTransformBuilder< IntType > b3 = new InspectTransformBuilder<>( new FinalInterval( 5, 1, 4, 6 ), chain2 );
		assertEquals( 1, b3.transforms.size() );
		assertTrue( b3.transforms.get( 0 ) instanceof MixedTransform );
		assertReshapeOf( img, Views.interval( chain2, new FinalInterval( 5, 1, 4, 6 ) ), new long[] { 5, 1, 4, 6 } );
	}
}
