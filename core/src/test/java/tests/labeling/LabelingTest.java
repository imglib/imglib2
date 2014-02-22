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

package tests.labeling;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.DefaultROIStrategyFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * TODO
 * 
 * @author Lee Kamentsky
 */
public class LabelingTest
{
	protected < T extends Comparable< T >> Labeling< T > makeLabeling( final T exemplar, final long[] dimensions )
	{
		final Labeling< T > labeling = new NativeImgLabeling< T, IntType >( new ArrayImgFactory< IntType >().create( dimensions, new IntType() ) );
		return labeling;
	}

	protected < T extends Comparable< T >> Labeling< T > makeLabeling( final long[][] coordinates, final T[] labels, final long[] dimensions )
	{
		assertTrue( labels.length > 0 );
		assertEquals( labels.length, coordinates.length );
		final Labeling< T > labeling = makeLabeling( labels[ 0 ], dimensions );
		final RandomAccess< LabelingType< T >> a = labeling.randomAccess();
		for ( int i = 0; i < coordinates.length; i++ )
		{
			a.setPosition( coordinates[ i ] );
			final List< T > currentLabels = new ArrayList< T >( a.get().getLabeling() );
			if ( !currentLabels.contains( labels[ i ] ) )
				currentLabels.add( labels[ i ] );
			a.get().setLabeling( currentLabels );
		}
		return labeling;
	}

	protected < T extends Comparable< T >> void labelSphere( final Labeling< T > labeling, final T label, final double[] center, final double radius )
	{
		final Cursor< LabelingType< T >> c = labeling.localizingCursor();
		final long[] position = new long[ labeling.numDimensions() ];
		while ( c.hasNext() )
		{
			final LabelingType< T > t = c.next();
			c.localize( position );
			double distance2 = 0;
			double distance = 0;
			for ( int i = 0; i < position.length; i++ )
			{
				distance = ( position[ i ] - center[ i ] );
				distance2 += distance * distance;
			}
			distance = Math.sqrt( distance2 );
			if ( distance <= radius )
			{
				final List< T > labels = new ArrayList< T >( t.getLabeling() );
				if ( !labels.contains( label ) )
					labels.add( label );
				t.setLabeling( labels );
			}
		}
	}

	@Test
	public void testDefaultConstructor()
	{
		final long[] dimensions = { 5, 6, 7 };
		final Labeling< String > labeling = new NativeImgLabeling< String, IntType >( new ArrayImgFactory< IntType >().create( dimensions, new IntType() ) );
		assertEquals( 3, labeling.numDimensions() );
	}

	@Test
	public void testFactoryConstructor()
	{
		final long[] dimensions = { 5, 6, 7 };

		final Labeling< String > labeling = new NativeImgLabeling< String, IntType >( new DefaultROIStrategyFactory< String >(), new ArrayImgFactory< IntType >().create( dimensions, new IntType() ) );

		assertEquals( 3, labeling.numDimensions() );
	}

	@Test
	public void testEmptyImage()
	{
		final Labeling< String > labeling = makeLabeling( "Foo", new long[] { 5, 6, 7 } );
		assertTrue( labeling.getLabels().isEmpty() );
		int iterations = 0;
		for ( final LabelingType< String > t : labeling )
		{
			assertTrue( t.getLabeling().size() == 0 );
			iterations++;
		}
		assertTrue( iterations == 5 * 6 * 7 );
	}

	@Test
	public void testLabelOne()
	{
		final long[][] coordinates = { { 1, 1, 1 } };
		final String[] labels = { "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		assertEquals( labeling.getLabels().size(), 1 );
		assertTrue( labeling.getLabels().contains( "Foo" ) );
	}

	@Test
	public void testGetAreaOne()
	{
		final long[][] coordinates = { { 1, 1, 1 } };
		final String[] labels = { "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		assertEquals( labeling.getArea( "Foo" ), 1 );
	}

	@Test
	public void testExtentsOne()
	{
		final long[][] coordinates = { { 1, 3, 5 } };
		final String[] labels = { "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		final long[] minExtents = new long[ 3 ];
		final long[] maxExtents = new long[ 3 ];
		assertFalse( labeling.getExtents( "Bar", minExtents, maxExtents ) );
		assertTrue( labeling.getExtents( "Foo", minExtents, maxExtents ) );
		assertArrayEquals( coordinates[ 0 ], minExtents );
		assertArrayEquals( coordinates[ 0 ], maxExtents );
	}

	@Test
	public void testExtentsMany()
	{
		final long[][] coordinates = { { 1, 4, 5 }, { 2, 3, 6 } };
		final long[] expectedMinExtents = { 1, 3, 5 };
		final long[] expectedMaxExtents = { 2, 4, 6 };
		final String[] labels = { "Foo", "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		final long[] minExtents = new long[ 3 ];
		final long[] maxExtents = new long[ 3 ];
		assertFalse( labeling.getExtents( "Bar", minExtents, maxExtents ) );
		assertTrue( labeling.getExtents( "Foo", minExtents, maxExtents ) );
		assertArrayEquals( expectedMinExtents, minExtents );
		assertArrayEquals( expectedMaxExtents, maxExtents );
	}

	@Test
	public void testRasterStartOne()
	{
		final long[][] coordinates = { { 1, 3, 5 } };
		final String[] labels = { "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		final long[] rasterStart = new long[ 3 ];
		assertFalse( labeling.getRasterStart( "Bar", rasterStart ) );
		assertTrue( labeling.getRasterStart( "Foo", rasterStart ) );
		assertArrayEquals( coordinates[ 0 ], rasterStart );
	}

	@Test
	public void testRandomAccessible()
	{
		final long[][] coordinates = { { 1, 3, 5 } };
		final String[] labels = { "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		final RandomAccess< LabelingType< String >> a = labeling.randomAccess();
		a.setPosition( coordinates[ 0 ] );
		List< String > list = a.get().getLabeling();
		assertEquals( list.size(), 1 );
		assertEquals( list.get( 0 ), "Foo" );
		a.setPosition( new long[] { 1, 2, 3 } );
		list = a.get().getLabeling();
		assertEquals( list.size(), 0 );
	}

	@Test
	public void testROIRandomAccess()
	{
		final long[][] coordinates = { { 1, 3, 5 } };
		final String[] labels = { "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		final RealRandomAccess< BitType > a = labeling.getRegionOfInterest( "Foo" ).realRandomAccess();
		a.setPosition( coordinates[ 0 ] );
		assertTrue( a.get().get() );
		a.setPosition( new long[] { 1, 2, 3 } );
		assertFalse( a.get().get() );
	}

	@Test
	public void testLocalizableCursorOne()
	{
		final long[][] coordinates = { { 1, 3, 2 } };
		final int expected = 132;
		final String[] labels = { "Foo" };
		final long[] dimensions = new long[] { 5, 6, 7 };
		final Labeling< String > labeling = makeLabeling( coordinates, labels, dimensions );
		final Img< IntType > img = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		final RandomAccess< IntType > a = img.randomAccess();
		for ( int i = 0; i < dimensions[ 0 ]; i++ )
		{
			a.setPosition( i, 0 );
			for ( int j = 0; j < dimensions[ 1 ]; j++ )
			{
				a.setPosition( j, 1 );
				for ( int k = 0; k < dimensions[ 2 ]; k++ )
				{
					a.setPosition( k, 2 );
					a.get().set( i * 100 + j * 10 + k );
				}
			}
		}
		final Cursor< IntType > c = labeling.getIterableRegionOfInterest( "Foo" ).getIterableIntervalOverROI( img ).cursor();
		int iterations = 0;
		while ( c.hasNext() )
		{
			final IntType t = c.next();
			iterations++;
			assertEquals( c.getLongPosition( 0 ), 1 );
			assertEquals( c.getLongPosition( 1 ), 3 );
			assertEquals( c.getLongPosition( 2 ), 2 );
			assertEquals( expected, t.get() );
		}
		assertEquals( 1, iterations );
	}

	@Test
	public void testSphere()
	{
		final long[] dimensions = new long[] { 20, 20, 20 };
		final Labeling< String > labeling = makeLabeling( "MyLabels", dimensions );
		labelSphere( labeling, "Foo", new double[] { 10, 9, 8 }, 5 );
		/*
		 * Test the extents
		 */
		final long[] minExtents = new long[ 3 ];
		final long[] maxExtents = new long[ 3 ];
		assertTrue( labeling.getExtents( "Foo", minExtents, maxExtents ) );
		assertArrayEquals( new long[] { 5, 4, 3 }, minExtents );
		assertArrayEquals( new long[] { 15, 14, 13 }, maxExtents );
		/*
		 * Test the raster start which should be 5, 9, 8
		 */
		final long[] start = new long[ 3 ];
		assertTrue( labeling.getRasterStart( "Foo", start ) );
		assertArrayEquals( new long[] { 5, 9, 8 }, start );
		final double expectedVolumeLow = 4. / 3. * Math.PI * Math.pow( 4.5, 3 );
		final double expectedVolumeHigh = 4. / 3. * Math.PI * Math.pow( 5.5, 3 );
		assertTrue( labeling.getArea( "Foo" ) > expectedVolumeLow );
		assertTrue( labeling.getArea( "Foo" ) < expectedVolumeHigh );
		final RandomAccess< LabelingType< String >> a = labeling.randomAccess();
		final Img< DoubleType > img = new ArrayImgFactory< DoubleType >().create( dimensions, new DoubleType() );
		final RandomAccess< DoubleType > img_a = img.randomAccess();
		for ( int i = 0; i < dimensions[ 0 ]; i++ )
		{
			img_a.setPosition( i, 0 );
			for ( int j = 0; j < dimensions[ 1 ]; j++ )
			{
				img_a.setPosition( j, 1 );
				for ( int k = 0; k < dimensions[ 2 ]; k++ )
				{
					img_a.setPosition( k, 2 );
					img_a.get().set( Math.sqrt( ( i - 10 ) * ( i - 10 ) + ( j - 9 ) * ( j - 9 ) + ( k - 8 ) * ( k - 8 ) ) );
				}
			}
		}
		final Cursor< DoubleType > c = labeling.getIterableRegionOfInterest( "Foo" ).getIterableIntervalOverROI( img ).cursor();
		int iterations = 0;
		final long[] position = new long[ 3 ];
		while ( c.hasNext() )
		{
			iterations++;
			final DoubleType t = c.next();
			assertTrue( t.get() <= 5 );
			c.localize( position );
			a.setPosition( position );
			assertEquals( a.get().getLabeling().size(), 1 );
		}
		assertEquals( iterations, labeling.getArea( "Foo" ) );
	}

	@Test
	public void testTwoLabels()
	{
		final long[] dimensions = new long[] { 20, 20, 40 };
		final Labeling< String > labeling = makeLabeling( "MyLabels", dimensions );
		final String[] labels = { "Foo", "Bar" };
		final double[][] centers = { { 10, 9, 8 }, { 8, 9, 30 } };
		for ( int i = 0; i < 2; i++ )
		{
			labelSphere( labeling, labels[ i ], centers[ i ], 5 );
		}
		final long[] temp = new long[ 3 ];
		for ( int i = 0; i < 2; i++ )
		{
			final double[] coords = new double[ 3 ];
			Arrays.fill( coords, 0 );
			// oooooo
			// it's a cursor iterating over the labels themselves for one label
			// oooooo

			final Cursor< LabelingType< String >> c = labeling.getIterableRegionOfInterest( labels[ i ] ).getIterableIntervalOverROI( labeling ).cursor();
			while ( c.hasNext() )
			{
				final LabelingType< String > t = c.next();
				c.localize( temp );
				for ( int j = 0; j < temp.length; j++ )
				{
					coords[ j ] += temp[ j ];
				}
				assertEquals( t.getLabeling().size(), 1 );
				assertEquals( t.getLabeling().get( 0 ), labels[ i ] );
			}
			for ( int j = 0; j < coords.length; j++ )
			{
				coords[ j ] /= labeling.getArea( labels[ i ] );
				assertTrue( Math.abs( coords[ j ] - centers[ i ][ j ] ) < .5 );
			}
		}
	}

	@Test
	public void testOverlappingLabels()
	{
		final long[] dimensions = new long[] { 20, 20, 30 };
		final Labeling< String > labeling = makeLabeling( "MyLabels", dimensions );
		final String[] labels = { "Foo", "Bar" };
		final double[][] centers = { { 10, 9, 8 }, { 8, 9, 12 } };
		for ( int i = 0; i < 2; i++ )
		{
			labelSphere( labeling, labels[ i ], centers[ i ], 5 );
		}
		final Collection< String > foundLabels = labeling.getLabels();
		assertEquals( foundLabels.size(), 2 );
		for ( int i = 0; i < 2; i++ )
		{
			assertTrue( foundLabels.contains( labels[ i ] ) );
		}
		final long[] temp = new long[ 3 ];
		for ( int i = 0; i < 2; i++ )
		{
			final double[] coords = new double[ 3 ];
			Arrays.fill( coords, 0 );
			final Cursor< LabelingType< String >> c = labeling.getIterableRegionOfInterest( labels[ i ] ).getIterableIntervalOverROI( labeling ).cursor();
			while ( c.hasNext() )
			{
				final LabelingType< String > t = c.next();
				c.localize( temp );
				final long[] d = new long[] { 0, 0 };
				for ( int j = 0; j < temp.length; j++ )
				{
					coords[ j ] += temp[ j ];
					for ( int k = 0; k < d.length; k++ )
					{
						d[ k ] += ( temp[ j ] - centers[ k ][ j ] ) * ( temp[ j ] - centers[ k ][ j ] );
					}
				}
				final boolean in_both = ( ( d[ 0 ] <= 25 ) & ( d[ 1 ] <= 25 ) );
				assertEquals( t.getLabeling().size(), in_both ? 2 : 1 );
				if ( in_both )
				{
					// Canonical order is alphabetical, but order of placement
					// would reverse.
					assertEquals( t.getLabeling().get( 0 ), "Bar" );
					assertEquals( t.getLabeling().get( 1 ), "Foo" );
				}
				assertTrue( t.getLabeling().contains( labels[ i ] ) );
			}
			for ( int j = 0; j < coords.length; j++ )
			{
				coords[ j ] /= labeling.getArea( labels[ i ] );
				assertTrue( Math.abs( coords[ j ] - centers[ i ][ j ] ) < .5 );
			}
		}
	}

	@Test
	public void TestCopy()
	{
		final long[] dimensions = new long[] { 20, 30 };
		final Labeling< Integer > labeling = makeLabeling( 1, dimensions );
		final Random r = new Random( 202030 );
		for ( final LabelingType< Integer > t : labeling )
		{
			t.setLabel( r.nextInt( 10 ) + 1 );
		}
		final Labeling< Integer > copy = labeling.copy();
		final Cursor< LabelingType< Integer >> c = copy.cursor();
		final RandomAccess< LabelingType< Integer >> ra = labeling.randomAccess();
		while ( c.hasNext() )
		{
			final LabelingType< Integer > t = c.next();
			final List< Integer > y = t.getLabeling();
			ra.setPosition( c );
			final List< Integer > x = ra.get().getLabeling();
			assertEquals( y.size(), 1 );
			assertEquals( x.get( 0 ), y.get( 0 ) );
		}
	}

	@Test
	public void testCopyCursor()
	{
		final long[] dimensions = new long[] { 2, 2 };
		final Labeling< Integer > labeling = makeLabeling( 1, dimensions );

		final Cursor< LabelingType< Integer >> c = labeling.cursor();
		c.fwd();
		c.get().setLabel( 1 );
		c.fwd();
		c.get().setLabel( 2 );

		final Cursor< LabelingType< Integer >> c2 = labeling.cursor().copyCursor();
		c2.fwd();
		assertTrue( c2.get().getLabeling().contains( 1 ) );
		c2.fwd();
		assertTrue( c2.get().getLabeling().contains( 2 ) );
	}

	@Test
	public void testCopyRandomAccess()
	{
		final long[] dimensions = new long[] { 2, 2 };
		final Labeling< Integer > labeling = makeLabeling( 1, dimensions );

		final RandomAccess< LabelingType< Integer >> a = labeling.randomAccess();
		a.setPosition( new long[] { 0, 0 } );
		a.get().setLabel( 1 );
		a.setPosition( new long[] { 1, 1 } );
		a.get().setLabel( 2 );

		final RandomAccess< LabelingType< Integer >> a2 = labeling.randomAccess().copyRandomAccess();
		a2.setPosition( new long[] { 0, 0 } );
		assertTrue( a2.get().getLabeling().contains( 1 ) );
		a2.setPosition( new long[] { 1, 1 } );
		assertTrue( a2.get().getLabeling().contains( 2 ) );
	}

	@Test
	public void testPerformance()
	{
		final int rounds = 10;
		final long[] dimensions = new long[] { 1000, 1000, 40 };
		final Labeling< Integer > labeling = makeLabeling( 1, dimensions );

		for ( int r = 0; r < rounds; r++ )
		{
			final Cursor< LabelingType< Integer >> c = labeling.cursor();
			while ( c.hasNext() )
			{
				c.fwd();
				c.get().getLabeling();
			}
		}
	}

}
