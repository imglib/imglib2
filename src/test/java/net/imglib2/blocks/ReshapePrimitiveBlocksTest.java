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

package net.imglib2.blocks;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.blocks.PrimitiveBlocks.OnFallback;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.nio.IntBufferAccess;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.junit.Test;

/**
 * Tests {@link PrimitiveBlocks} on views that contain a reshape, against
 * {@link FallbackPrimitiveBlocks}.
 */
public class ReshapePrimitiveBlocksTest
{
	/** 6 * 5 * 8 = 240 elements. */
	private static final long[] DIMS = { 6, 5, 8 };

	private static final long[][] SHAPES = {
			{ 240 }, // merge everything
			{ 30, 8 }, // merge
			{ 6, 5, 2, 4 }, // split
			{ 3, 2, 5, 8 }, // split
			{ 10, 3, 8 }, // general group
			{ 4, 15, 4 }, // general
			{ 3, 10, 2, 4 }, // mixed
			{ 1, 30, 1, 8 }, // with singletons
	};

	private static < I extends RandomAccessibleInterval< IntType > > I fill( final I img )
	{
		int i = 0;
		for ( final IntType t : Views.flatIterable( img ) )
			t.set( i++ );
		return img;
	}

	private static List< RandomAccessibleInterval< IntType > > roots()
	{
		final ArrayImg< IntType, IntBufferAccess > buffer = new ArrayImg<>( new IntBufferAccess( 240 ), DIMS, new Fraction() );
		buffer.setLinkedType( new IntType( buffer ) );
		return Arrays.asList(
				fill( ArrayImgs.ints( DIMS ) ),
				fill( new PlanarImgFactory<>( new IntType() ).create( DIMS ) ),
				fill( new CellImgFactory<>( new IntType(), 4, 3, 3 ).create( DIMS ) ),
				fill( buffer ) );
	}

	private static boolean usesReshapeCopier( final RandomAccessible< IntType > view )
	{
		final ViewPropertiesOrError< IntType, ? > props = ViewAnalyzer.getViewProperties( view );
		assertTrue( props.getErrorMessage(), props.isFullySupported() );
		return props.getViewProperties().hasReshape();
	}

	/**
	 * Copy random blocks (including the full interval, and single elements)
	 * of {@code view} using {@code blocks}, and compare to
	 * {@link FallbackPrimitiveBlocks}.
	 */
	private static void checkBlocks( final RandomAccessibleInterval< IntType > view, final PrimitiveBlocks< IntType > blocks, final String what )
	{
		final PrimitiveBlocks< IntType > expectedBlocks = new FallbackPrimitiveBlocks<>( view, new IntType() );
		final Random random = new Random( 11 );
		final int n = view.numDimensions();
		final List< Interval > intervals = new ArrayList<>();
		intervals.add( view );
		intervals.add( FinalInterval.createMinSize( view.minAsLongArray(), ones( n ) ) );
		for ( int k = 0; k < 50; ++k )
		{
			final long[] min = new long[ n ];
			final long[] max = new long[ n ];
			for ( int d = 0; d < n; ++d )
			{
				final long a = view.min( d ) + random.nextInt( ( int ) view.dimension( d ) );
				final long b = view.min( d ) + random.nextInt( ( int ) view.dimension( d ) );
				min[ d ] = Math.min( a, b );
				max[ d ] = Math.max( a, b );
			}
			intervals.add( new FinalInterval( min, max ) );
		}
		for ( final Interval interval : intervals )
		{
			final int size = ( int ) Intervals.numElements( interval );
			final int[] expected = new int[ size ];
			final int[] actual = new int[ size ];
			expectedBlocks.copy( interval, expected );
			blocks.copy( interval, actual );
			assertArrayEquals( what + " block " + Intervals.toString( interval ), expected, actual );
		}
	}

	private static long[] ones( final int n )
	{
		final long[] a = new long[ n ];
		Arrays.fill( a, 1 );
		return a;
	}

	private static void checkViews( final Function< RandomAccessibleInterval< IntType >, RandomAccessibleInterval< IntType > > createView, final String what )
	{
		for ( final RandomAccessibleInterval< IntType > root : roots() )
		{
			final RandomAccessibleInterval< IntType > view = createView.apply( root );
			final String w = what + " of " + root.getClass().getSimpleName();
			checkBlocks( view, PrimitiveBlocks.of( view, OnFallback.FAIL ), w );
			// force many splits
			checkBlocks( view, viewPrimitiveBlocks( view, 1, 0, Long.MAX_VALUE ), w + " (split)" );
		}
	}

	private static PrimitiveBlocks< IntType > viewPrimitiveBlocks( final RandomAccessible< IntType > view, final int wasteFactor, final long minSplitSize, final long maxBufferSize )
	{
		final ViewPropertiesOrError< IntType, ? > props = ViewAnalyzer.getViewProperties( view );
		assertTrue( props.getErrorMessage(), props.isFullySupported() );
		return new ViewPrimitiveBlocks<>( props.getViewProperties(), wasteFactor, minSplitSize, maxBufferSize );
	}

	@Test
	public void testReshapes()
	{
		for ( final long[] shape : SHAPES )
			checkViews( root -> Views.reshape( root, shape ), "reshape to " + Arrays.toString( shape ) );
	}

	@Test
	public void testArrayImgIsReshapedDirectly()
	{
		final Img< IntType > img = fill( ArrayImgs.ints( DIMS ) );
		for ( final long[] shape : SHAPES )
			assertFalse( usesReshapeCopier( Views.reshape( img, shape ) ) );
		assertFalse( usesReshapeCopier( Views.permute( Views.reshape( img, 30, 8 ), 0, 1 ) ) );
		assertFalse( usesReshapeCopier( Views.reshape( Views.translate( img, 1, 2, 3 ), 30, 8 ) ) );

		assertTrue( usesReshapeCopier( Views.reshape( Views.permute( img, 0, 1 ), 30, 8 ) ) );
		assertTrue( usesReshapeCopier( Views.reshape( Views.interval( img, new long[] { 0, 0, 1 }, new long[] { 5, 4, 6 } ), 30, 6 ) ) );
		final Img< IntType > cells = fill( new CellImgFactory<>( new IntType(), 4, 3, 3 ).create( DIMS ) );
		assertTrue( usesReshapeCopier( Views.reshape( cells, 30, 8 ) ) );
	}

	@Test
	public void testTransformsAboveReshape()
	{
		checkViews( root -> Views.permute( Views.reshape( root, 30, 8 ), 0, 1 ), "permute of reshape" );
		checkViews( root -> Views.moveAxis( Views.reshape( root, 3, 10, 2, 4 ), 0, 3 ), "moveAxis of reshape" );
		checkViews( root -> Views.invertAxis( Views.reshape( root, 10, 3, 8 ), 1 ), "invertAxis of reshape" );
		checkViews( root -> Views.hyperSlice( Views.reshape( root, 4, 6, 10 ), 1, 2 ), "hyperSlice of reshape" );
		checkViews( root -> Views.translate( Views.reshape( root, 30, 8 ), 5, -3 ), "translate of reshape" );
		checkViews( root -> Views.interval( Views.reshape( root, 30, 8 ), new long[] { 3, 2 }, new long[] { 20, 6 } ), "interval of reshape" );
	}

	@Test
	public void testTransformsBelowReshape()
	{
		checkViews( root -> Views.reshape( Views.translate( root, 3, 4, 5 ), 30, 8 ), "reshape of translate" );
		checkViews( root -> Views.reshape( Views.permute( root, 0, 2 ), 40, 6 ), "reshape of permute" );
		checkViews( root -> Views.reshape( Views.invertAxis( root, 1 ), 3, 10, 8 ), "reshape of invertAxis" );
		checkViews( root -> Views.reshape( Views.hyperSlice( root, 2, 3 ), 3, 10 ), "reshape of hyperSlice" );
		checkViews( root -> Views.reshape( Views.hyperSlice( root, 0, 3 ), 2, 20 ), "reshape of hyperSlice" );
		checkViews( root -> Views.reshape( Views.interval( root, new long[] { 1, 1, 1 }, new long[] { 4, 3, 6 } ), 12, 6 ), "reshape of interval" );
		checkViews( root -> Views.reshape( Views.permute( Views.translate( root, 3, 4, 5 ), 1, 2 ), 3, 16, 5 ), "reshape of permute of translate" );
	}

	@Test
	public void testTransformsAboveAndBelowReshape()
	{
		checkViews( root -> Views.permute( Views.reshape( Views.permute( root, 0, 2 ), 40, 6 ), 0, 1 ), "permute of reshape of permute" );
	}

	@Test
	public void testConverters()
	{
		checkViews( root -> Converters.convert( Views.reshape( root, 30, 8 ), ( a, b ) -> b.set( 2 * a.get() + 1 ), new IntType() ), "convert of reshape" );
		checkViews( root -> Views.reshape( Converters.convert( root, ( a, b ) -> b.set( 2 * a.get() + 1 ), new IntType() ), 30, 8 ), "reshape of convert" );
		checkViews( root -> Views.reshape( Converters.convert( Views.permute( root, 0, 1 ), ( a, b ) -> b.set( 2 * a.get() + 1 ), new IntType() ), 30, 8 ), "reshape of convert of permute" );
	}

	@Test
	public void testExtensionBelowReshape()
	{
		checkViews( root -> Views.reshape( Views.interval( Views.extendBorder( root ), new long[] { -1, 0, -2 }, new long[] { 6, 4, 9 } ), 16, 30 ), "reshape of extendBorder" );
		checkViews( root -> Views.reshape( Views.interval( Views.extendMirrorSingle( root ), new long[] { -1, 0, -2 }, new long[] { 6, 4, 9 } ), 4, 10, 12 ), "reshape of extendMirrorSingle" );
		checkViews( root -> Views.reshape( Views.interval( Views.extendValue( root, 17 ), new long[] { -1, 0, -2 }, new long[] { 6, 4, 9 } ), 16, 30 ), "reshape of extendValue" );
	}

	@Test
	public void testExtensionAboveReshape()
	{
		// ArrayImg is reshaped directly, which supports extension
		final Img< IntType > img = fill( ArrayImgs.ints( DIMS ) );
		final RandomAccessibleInterval< IntType > view = Views.interval( Views.extendMirrorSingle( Views.reshape( img, 30, 8 ) ), new long[] { -5, -3 }, new long[] { 40, 12 } );
		assertFalse( usesReshapeCopier( view ) );
		checkBlocks( view, PrimitiveBlocks.of( view, OnFallback.FAIL ), "extension above reshape of ArrayImg" );

		// otherwise it's not supported
		final Img< IntType > cells = fill( new CellImgFactory<>( new IntType(), 4, 3, 3 ).create( DIMS ) );
		final RandomAccessibleInterval< IntType > cellView = Views.interval( Views.extendMirrorSingle( Views.reshape( cells, 30, 8 ) ), new long[] { -5, -3 }, new long[] { 40, 12 } );
		final ViewPropertiesOrError< IntType, ? > props = ViewAnalyzer.getViewProperties( cellView );
		assertFalse( props.isFullySupported() );
		assertTrue( props.getErrorMessage().contains( "reshape must not be applied to the out-of-bounds extension" ) );
		checkBlocks( cellView, PrimitiveBlocks.of( cellView, OnFallback.ACCEPT ), "extension above reshape of CellImg (fallback)" );
	}

	@Test
	public void testUnsupported()
	{
		final Img< IntType > cells = fill( new CellImgFactory<>( new IntType(), 4, 3, 3 ).create( DIMS ) );

		final ViewPropertiesOrError< IntType, ? > twoReshapes = ViewAnalyzer.getViewProperties(
				Views.reshape( Views.permute( Views.reshape( cells, 30, 8 ), 0, 1 ), 4, 60 ) );
		assertFalse( twoReshapes.isFullySupported() );
		assertTrue( twoReshapes.getErrorMessage().contains( "at most one reshape" ) );

		final ViewPropertiesOrError< IntType, ? > addDimension = ViewAnalyzer.getViewProperties(
				Views.reshape( Views.addDimension( cells, 0, 1 ), 30, 16 ) );
		assertFalse( addDimension.isFullySupported() );
		assertTrue( addDimension.getErrorMessage().contains( "addDimension" ) );
	}

	@Test
	public void testSplitPieces()
	{
		// A flat view with blocks straddling row and plane boundaries of the
		// root. With the default limits, small blocks are not split, but
		// large wasteful ones are.
		final Img< IntType > cells = fill( new CellImgFactory<>( new IntType(), 64, 64, 8 ).create( 300, 300, 4 ) );
		final RandomAccessibleInterval< IntType > view = Views.reshape( cells, 360000 );
		final PrimitiveBlocks< IntType > blocks = PrimitiveBlocks.of( view, OnFallback.FAIL );
		final PrimitiveBlocks< IntType > expectedBlocks = new FallbackPrimitiveBlocks<>( view, new IntType() );
		for ( final long[] minSize : new long[][] { { 89990, 20 }, { 89000, 2000 }, { 0, 90000 }, { 44950, 90100 }, { 299, 2 } } )
		{
			final Interval interval = Intervals.createMinSize( minSize );
			final int[] expected = new int[ ( int ) minSize[ 1 ] ];
			final int[] actual = new int[ ( int ) minSize[ 1 ] ];
			expectedBlocks.copy( interval, expected );
			blocks.copy( interval, actual );
			assertArrayEquals( expected, actual );
		}
	}

	@Test
	public void testMaxBufferSize()
	{
		final Img< IntType > cells = fill( new CellImgFactory<>( new IntType(), 4, 3, 3 ).create( DIMS ) );
		// every bounding box must fit into 16 elements
		for ( final RandomAccessibleInterval< IntType > view : Arrays.asList( Views.reshape( cells, 240 ), Views.reshape( cells, 20, 12 ) ) )
			checkBlocks( view, viewPrimitiveBlocks( view, 1000, 1000, 16 ), "maxBufferSize=16" );
	}

	@Test
	public void testThreadSafe() throws Exception
	{
		final Img< IntType > cells = fill( new CellImgFactory<>( new IntType(), 4, 3, 3 ).create( DIMS ) );
		final RandomAccessibleInterval< IntType > view = Views.permute( Views.reshape( Views.permute( cells, 0, 2 ), 40, 6 ), 0, 1 );
		final PrimitiveBlocks< IntType > blocks = PrimitiveBlocks.of( view, OnFallback.FAIL ).threadSafe();
		final ExecutorService es = Executors.newFixedThreadPool( 8 );
		try
		{
			final List< Future< ? > > futures = new ArrayList<>();
			for ( int t = 0; t < 32; ++t )
				futures.add( es.submit( () -> checkBlocks( view, blocks, "threadSafe" ) ) );
			for ( final Future< ? > f : futures )
				f.get();
		}
		finally
		{
			es.shutdown();
		}
		checkBlocks( view, blocks.independentCopy(), "independentCopy" );
	}

	@Test
	public void testBufferAccess()
	{
		final ArrayImg< IntType, IntBufferAccess > buffer = new ArrayImg<>( new IntBufferAccess( 240 ), DIMS, new Fraction() );
		buffer.setLinkedType( new IntType( buffer ) );
		fill( buffer );

		// regression test: PermuteInvert used a MemCopy from buffer to array
		final RandomAccessibleInterval< IntType > permuted = Views.permute( buffer, 0, 2 );
		checkBlocks( permuted, PrimitiveBlocks.of( permuted, OnFallback.FAIL ), "permute of buffer ArrayImg" );

		// regression test: the constant oob value was copied with a MemCopy from buffer to array
		final RandomAccessibleInterval< IntType > extended = Views.interval( Views.extendValue( buffer, 17 ), new long[] { -2, -1, -3 }, new long[] { 7, 6, 9 } );
		checkBlocks( extended, PrimitiveBlocks.of( extended, OnFallback.FAIL ), "extendValue of buffer ArrayImg" );
	}
}
