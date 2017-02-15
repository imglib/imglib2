/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.outofbounds;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;

/**
 *
 *
 * @author Stephan Saalfeld
 */
public class OutOfBoundsPeriodicTest
{
	final private long[] dim = new long[] { 5, 4, 3 };

	static private ArrayImg< IntType, ? > arrayImage;

	static private CellImg< IntType, ? > cellImage;

	static private Img< IntType > listImage;

	static private OutOfBounds< IntType > cArray;

	static private OutOfBounds< IntType > cCell;

	static private OutOfBounds< IntType > cList;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		arrayImage = new ArrayImgFactory< IntType >().create( dim, new IntType() );
		cellImage = new CellImgFactory< IntType >( 2 ).create( dim, new IntType() );
		listImage = new ListImgFactory< IntType >().create( dim, new IntType() );

		int i = 0;
		for ( final IntType t : arrayImage )
			t.set( i++ );

		final int[] position = new int[ dim.length ];
		for ( final Cursor< IntType > c = arrayImage.cursor(); c.hasNext(); )
		{
			c.fwd();
			c.localize( position );

			i = 0;
			for ( int d = dim.length - 1; d >= 0; --d )
				i = i * ( int ) dim[ d ] + position[ d ];

			c.get().setInteger( i );
		}

		for ( final Cursor< IntType > c = cellImage.cursor(); c.hasNext(); )
		{
			c.fwd();
			c.localize( position );

			i = 0;
			for ( int d = dim.length - 1; d >= 0; --d )
				i = i * ( int ) dim[ d ] + position[ d ];

			c.get().setInteger( i );
		}

		i = 0;
		for ( final IntType t : listImage )
			t.set( i++ );

		cArray = new ExtendedRandomAccessibleInterval< IntType, Img< IntType > >( arrayImage, new OutOfBoundsPeriodicFactory< IntType, Img< IntType > >() ).randomAccess();
		cCell = new ExtendedRandomAccessibleInterval< IntType, Img< IntType > >( cellImage, new OutOfBoundsPeriodicFactory< IntType, Img< IntType > >() ).randomAccess();
		cList = new ExtendedRandomAccessibleInterval< IntType, Img< IntType > >( listImage, new OutOfBoundsPeriodicFactory< IntType, Img< IntType > >() ).randomAccess();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{}

	final private boolean isOutOfBounds( final Localizable l )
	{
		for ( int i = 0; i < dim.length; ++i )
			if ( l.getIntPosition( i ) < 0 || l.getIntPosition( i ) >= dim[ i ] )
				return true;
		return false;
	}

	@Test
	public void fwd()
	{
		final int[] expectedX = new int[] { 2, 3, 4, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4 };
		final int[] expectedY = new int[] { 10, 15, 0, 5, 10, 15, 0, 5, 10, 15, 0, 5, 10, 15, 0, 5, 10, 15, 0, 5 };
		final int[] expectedZ = new int[] { 40, 0, 20, 40, 0, 20, 40, 0, 20, 40, 0, 20, 40, 0, 20, 40, 0, 20, 40, 0, 20 };

		cArray.setPosition( -8, 0 );
		cCell.setPosition( -8, 0 );
		cList.setPosition( -8, 0 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer x failed at iteration " + i + ".", expectedX[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer x failed at iteration " + i + ".", expectedX[ i ], cCell.get().getInteger() );
			assertEquals( "ListContainer x failed at iteration " + i + ".", expectedX[ i ], cList.get().getInteger() );

			assertEquals( "ArrayContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "ListContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cList ), cList.isOutOfBounds() );

			cArray.fwd( 0 );
			cCell.fwd( 0 );
			cList.fwd( 0 );
		}

		cArray.setPosition( 0, 0 );
		cCell.setPosition( 0, 0 );
		cList.setPosition( 0, 0 );

		cArray.setPosition( -6, 1 );
		cCell.setPosition( -6, 1 );
		cList.setPosition( -6, 1 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer y failed at iteration " + i + ".", expectedY[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer y failed at iteration " + i + ".", expectedY[ i ], cCell.get().getInteger() );
			assertEquals( "ListContainer y failed at iteration " + i + ".", expectedY[ i ], cList.get().getInteger() );

			assertEquals( "ArrayContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "ListContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cList ), cList.isOutOfBounds() );

			cArray.fwd( 1 );
			cCell.fwd( 1 );
			cList.fwd( 1 );
		}

		cArray.setPosition( 0, 1 );
		cCell.setPosition( 0, 1 );
		cList.setPosition( 0, 1 );

		cArray.setPosition( -4, 2 );
		cCell.setPosition( -4, 2 );
		cList.setPosition( -4, 2 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer z failed at iteration " + i + ".", expectedZ[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer z failed at iteration " + i + ".", expectedZ[ i ], cCell.get().getInteger() );
			assertEquals( "LinkContainer z failed at iteration " + i + ".", expectedZ[ i ], cList.get().getInteger() );

			assertEquals( "ArrayContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "LinkContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cList ), cList.isOutOfBounds() );

			cArray.fwd( 2 );
			cCell.fwd( 2 );
			cList.fwd( 2 );
		}
	}

	@Test
	public void bck()
	{
		final int[] expectedX = new int[] { 3, 2, 1, 0, 4, 3, 2, 1, 0, 4, 3, 2, 1, 0, 4, 3, 2, 1, 0, 4 };
		final int[] expectedY = new int[] { 10, 5, 0, 15, 10, 5, 0, 15, 10, 5, 0, 15, 10, 5, 0, 15, 10, 5, 0, 15 };
		final int[] expectedZ = new int[] { 20, 0, 40, 20, 0, 40, 20, 0, 40, 20, 0, 40, 20, 0, 40, 20, 0, 40, 20, 0 };

		cArray.setPosition( 8, 0 );
		cCell.setPosition( 8, 0 );
		cList.setPosition( 8, 0 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer x failed at iteration " + i + ".", expectedX[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer x failed at iteration " + i + ".", expectedX[ i ], cCell.get().getInteger() );
			assertEquals( "ListContainer x failed at iteration " + i + ".", expectedX[ i ], cList.get().getInteger() );

			assertEquals( "ArrayContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "ListContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cList ), cList.isOutOfBounds() );

			cArray.bck( 0 );
			cCell.bck( 0 );
			cList.bck( 0 );
		}

		cArray.setPosition( 0, 0 );
		cCell.setPosition( 0, 0 );
		cList.setPosition( 0, 0 );

		cArray.setPosition( 6, 1 );
		cCell.setPosition( 6, 1 );
		cList.setPosition( 6, 1 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer y failed at iteration " + i + ".", expectedY[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer y failed at iteration " + i + ".", expectedY[ i ], cCell.get().getInteger() );
			assertEquals( "ListContainer y failed at iteration " + i + ".", expectedY[ i ], cList.get().getInteger() );

			assertEquals( "ArrayContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "ListContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cList ), cList.isOutOfBounds() );

			cArray.bck( 1 );
			cCell.bck( 1 );
			cList.bck( 1 );
		}

		cArray.setPosition( 0, 1 );
		cCell.setPosition( 0, 1 );
		cList.setPosition( 0, 1 );

		cArray.setPosition( 4, 2 );
		cCell.setPosition( 4, 2 );
		cList.setPosition( 4, 2 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer z failed at iteration " + i + ".", expectedZ[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer z failed at iteration " + i + ".", expectedZ[ i ], cCell.get().getInteger() );
			assertEquals( "ListContainer z failed at iteration " + i + ".", expectedZ[ i ], cList.get().getInteger() );

			assertEquals( "ArrayContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "ListContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cList ), cList.isOutOfBounds() );

			cArray.bck( 2 );
			cCell.bck( 2 );
			cList.bck( 2 );
		}
	}

	@Test
	public void move()
	{
		final int[] distance = new int[] { 0, 10, 6, -12, -5, 13, -5 };
		final int[] d = new int[] { 0, 0, 1, 0, 1, 2, 2 };
		final int[] v = new int[] { 33, 33, 23, 21, 36, 56, 16 };

		final int[] start = new int[] { 3, 2, 1 };

		cArray.setPosition( start );
		cCell.setPosition( start );
		cList.setPosition( start );

		for ( int i = 0; i < d.length; ++i )
		{
			cArray.move( distance[ i ], d[ i ] );
			cCell.move( distance[ i ], d[ i ] );
			cList.move( distance[ i ], d[ i ] );

			assertEquals( "ArrayContainer move failed at iteration " + i + ".", v[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer move failed at iteration " + i + ".", v[ i ], cCell.get().getInteger() );
			assertEquals( "ListContainer move failed at iteration " + i + ".", v[ i ], cList.get().getInteger() );

			assertEquals( "ArrayContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "ListContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cList ), cList.isOutOfBounds() );
		}
	}

	@Test
	public void setPosition()
	{
		final int[] x = new int[] { 0, 1, 2, 3, 18, -9, 20 };
		final int[] y = new int[] { 0, 0, 2, 3, 11, 12, -40 };
		final int[] z = new int[] { 0, 0, 1, 2, 10, -13, -15 };
		final int[] t = new int[] { 0, 1, 32, 58, 38, 41, 0 };

		for ( int i = 0; i < x.length; ++i )
		{
			cArray.setPosition( x[ i ], 0 );
			cArray.setPosition( y[ i ], 1 );
			cArray.setPosition( z[ i ], 2 );
			cCell.setPosition( x[ i ], 0 );
			cCell.setPosition( y[ i ], 1 );
			cCell.setPosition( z[ i ], 2 );
			cList.setPosition( x[ i ], 0 );
			cList.setPosition( y[ i ], 1 );
			cList.setPosition( z[ i ], 2 );

			assertEquals( "ArrayContainer failed at " + cArray, cArray.get().getInteger(), t[ i ] );
			assertEquals( "CellContainer failed at " + cCell, cCell.get().getInteger(), t[ i ] );
			assertEquals( "ListContainer failed at " + cList, cList.get().getInteger(), t[ i ] );
		}
	}
}
