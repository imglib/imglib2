/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
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
package tests;


import java.awt.Rectangle;

//import ij.ImageJ;
//import ij.ImagePlus;
//import mpicbg.imglib.image.display.imagej.ImageJFunctions;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.cell.CellContainerFactory;
import mpicbg.imglib.container.list.ListContainerFactory;
import mpicbg.imglib.container.shapelist.ShapeList;
import mpicbg.imglib.container.shapelist.ShapeListContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import mpicbg.imglib.type.numeric.integer.IntType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class OutOfBoundsMirrorSingleBoundaryTest
{
	final private int[] dim = new int[]{ 5, 4, 3 };

	static private Image< IntType > arrayImage;
	static private Image< IntType > cellImage;
	static private Image< IntType > dynamicImage;
	static private Image< IntType > shapeListImage;
	
	static private ImgRandomAccess< IntType > cArray;
	static private ImgRandomAccess< IntType > cCell;
	static private ImgRandomAccess< IntType > cDynamic;
	static private ImgRandomAccess< IntType > cShapeList;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
//		new ImageJ();
//		arrayImage.getDisplay().setMinMax();
//		final ImagePlus impArray = ImageJFunctions.displayAsVirtualStack( arrayImage );
//		impArray.show();
//		
//		cellImage.getDisplay().setMinMax();
//		final ImagePlus impCell = ImageJFunctions.displayAsVirtualStack( cellImage );
//		impCell.show();
//		
//		dynamicImage.getDisplay().setMinMax();
//		final ImagePlus impDynamic = ImageJFunctions.displayAsVirtualStack( dynamicImage );
//		impDynamic.show();
//		
//		shapeListImage.getDisplay().setMinMax();
//		final ImagePlus impShapeList = ImageJFunctions.displayAsVirtualStack( shapeListImage );
//		impShapeList.show();
//		
//		try
//		{
//			Thread.sleep( 100000 );
//		}
//		catch ( final InterruptedException e ){}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		arrayImage = new ImageFactory< IntType >( new IntType(), new ArrayContainerFactory() ).createImage( dim );
		cellImage = new ImageFactory< IntType >( new IntType(), new CellContainerFactory( 2 ) ).createImage( dim );
		dynamicImage = new ImageFactory< IntType >( new IntType(), new ListContainerFactory() ).createImage( dim );
		shapeListImage = new ImageFactory< IntType >( new IntType(), new ShapeListContainerFactory() ).createImage( dim );
		
		int i = 0;
		for ( final IntType t : arrayImage )
			t.set( i++ );

		final int[] position = new int[ dim.length ];
		for ( final ImgCursor< IntType > c = cellImage.createLocalizingRasterIterator(); c.hasNext(); )
		{
			c.fwd();
			c.localize( position );
			
			i = 0;
			for ( int d = dim.length - 1; d >= 0; --d )
				i = i * dim[ d ] + position[ d ];
			
			c.get().setInteger( i );
		}

		i = 0;
		for ( final IntType t : dynamicImage )
			t.set( i++ );

		i = 0;
		final ShapeList< IntType > shapeList = ( ShapeList< IntType > )shapeListImage.getContainer();
		for ( int z = 0; z < dim[ 2 ]; ++z )
			for ( int y = 0; y < dim[ 1 ]; ++y )
				for ( int x = 0; x < dim[ 0 ]; ++x )
					shapeList.addShape( new Rectangle( x, y, 1, 1 ), new IntType( i++ ), new int[]{ z } );
		
		cArray = arrayImage.createPositionableRasterSampler( new OutOfBoundsMirrorFactory< IntType >( Boundary.SINGLE ) );
		cCell = cellImage.createPositionableRasterSampler( new OutOfBoundsMirrorFactory< IntType >( Boundary.SINGLE ) );
		cDynamic = dynamicImage.createPositionableRasterSampler( new OutOfBoundsMirrorFactory< IntType >( Boundary.SINGLE ) );
		cShapeList = shapeListImage.createPositionableRasterSampler( new OutOfBoundsMirrorFactory< IntType >( Boundary.SINGLE ) );
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	final private boolean isOutOfBounds( final Localizable l )
	{
		for ( int i = 0; i < dim.length; ++i )
			if ( l.getIntPosition( i ) < 0 || l.getIntPosition( i ) >= dim[ i ] ) return true;
		return false;
	}
	
	@Test
	public void fwd()
	{
		final int[] expectedX = new int[]{ 0, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3 };
		final int[] expectedY = new int[]{ 0, 5, 10, 15, 10, 5, 0, 5, 10, 15, 10, 5, 0, 5, 10, 15, 10, 5, 0, 5 };
		final int[] expectedZ = new int[]{ 0, 20, 40, 20, 0, 20, 40, 20, 0, 20, 40, 20, 0, 20, 40, 20, 0, 20, 40, 20 };
		
		cArray.setPosition( -8, 0 );
		cCell.setPosition( -8, 0 );
		cDynamic.setPosition( -8, 0 );
		cShapeList.setPosition( -8, 0 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer x failed at iteration " + i + ".", expectedX[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer x failed at iteration " + i + ".", expectedX[ i ], cCell.get().getInteger() );
			assertEquals( "DynamicContainer x failed at iteration " + i + ".", expectedX[ i ], cDynamic.get().getInteger() );
			assertEquals( "ShapeListContainer x failed at iteration " + i + ".", expectedX[ i ], cShapeList.get().getInteger() );
			
			assertEquals( "ArrayContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "DynamicContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cDynamic.isOutOfBounds() );
			assertEquals( "ShapeListContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cShapeList.isOutOfBounds() );
			
			cArray.fwd( 0 );
			cCell.fwd( 0 );
			cDynamic.fwd( 0 );
			cShapeList.fwd( 0 );
		}
		
		cArray.setPosition( 0, 0 );
		cCell.setPosition( 0, 0 );
		cDynamic.setPosition( 0, 0 );
		cShapeList.setPosition( 0, 0 );
		
		cArray.setPosition( -6, 1 );
		cCell.setPosition( -6, 1 );
		cDynamic.setPosition( -6, 1 );
		cShapeList.setPosition( -6, 1 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer y failed at iteration " + i + ".", expectedY[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer y failed at iteration " + i + ".", expectedY[ i ], cCell.get().getInteger() );
			assertEquals( "DynamicContainer y failed at iteration " + i + ".", expectedY[ i ], cDynamic.get().getInteger() );
			assertEquals( "ShapeListContainer y failed at iteration " + i + ".", expectedY[ i ], cShapeList.get().getInteger() );
			
			assertEquals( "ArrayContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "DynamicContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cDynamic.isOutOfBounds() );
			assertEquals( "ShapeListContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cShapeList.isOutOfBounds() );
			
			cArray.fwd( 1 );
			cCell.fwd( 1 );
			cDynamic.fwd( 1 );
			cShapeList.fwd( 1 );
		}
		
		cArray.setPosition( 0, 1 );
		cCell.setPosition( 0, 1 );
		cDynamic.setPosition( 0, 1 );
		cShapeList.setPosition( 0, 1 );
		
		cArray.setPosition( -4, 2 );
		cCell.setPosition( -4, 2 );
		cDynamic.setPosition( -4, 2 );
		cShapeList.setPosition( -4, 2 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer z failed at iteration " + i + ".", expectedZ[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer z failed at iteration " + i + ".", expectedZ[ i ], cCell.get().getInteger() );
			assertEquals( "DynamicContainer z failed at iteration " + i + ".", expectedZ[ i ], cDynamic.get().getInteger() );
			assertEquals( "ShapeListContainer z failed at iteration " + i + ".", expectedZ[ i ], cShapeList.get().getInteger() );
			
			assertEquals( "ArrayContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "DynamicContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cDynamic.isOutOfBounds() );
			assertEquals( "ShapeListContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cShapeList.isOutOfBounds() );
			
			cArray.fwd( 2 );
			cCell.fwd( 2 );
			cDynamic.fwd( 2 );
			cShapeList.fwd( 2 );
		}
	}
	
	@Test
	public void bck()
	{
		final int[] expectedX = new int[] { 0, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3 };
		final int[] expectedY = new int[] { 0, 5, 10, 15, 10, 5, 0, 5, 10, 15, 10, 5, 0, 5, 10, 15, 10, 5, 0, 5 };
		final int[] expectedZ = new int[] { 0, 20, 40, 20, 0, 20, 40, 20, 0, 20, 40, 20, 0, 20, 40, 20, 0, 20, 40, 20 };

		cArray.setPosition( 8, 0 );
		cCell.setPosition( 8, 0 );
		cDynamic.setPosition( 8, 0 );
		cShapeList.setPosition( 8, 0 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer x failed at iteration " + i + ".", expectedX[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer x failed at iteration " + i + ".", expectedX[ i ], cCell.get().getInteger() );
			assertEquals( "DynamicContainer x failed at iteration " + i + ".", expectedX[ i ], cDynamic.get().getInteger() );
			assertEquals( "ShapeListContainer x failed at iteration " + i + ".", expectedX[ i ], cShapeList.get().getInteger() );
			
			assertEquals( "ArrayContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "DynamicContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cDynamic.isOutOfBounds() );
			assertEquals( "ShapeListContainer x failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cShapeList.isOutOfBounds() );			
			
			cArray.bck( 0 );
			cCell.bck( 0 );
			cDynamic.bck( 0 );
			cShapeList.bck( 0 );
		}

		cArray.setPosition( 0, 0 );
		cCell.setPosition( 0, 0 );
		cDynamic.setPosition( 0, 0 );
		cShapeList.setPosition( 0, 0 );
		
		cArray.setPosition( 6, 1 );
		cCell.setPosition( 6, 1 );
		cDynamic.setPosition( 6, 1 );
		cShapeList.setPosition( 6, 1 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer y failed at iteration " + i + ".", expectedY[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer y failed at iteration " + i + ".", expectedY[ i ], cCell.get().getInteger() );
			assertEquals( "DynamicContainer y failed at iteration " + i + ".", expectedY[ i ], cDynamic.get().getInteger() );
			assertEquals( "ShapeListContainer y failed at iteration " + i + ".", expectedY[ i ], cShapeList.get().getInteger() );
			
			assertEquals( "ArrayContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "DynamicContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cDynamic.isOutOfBounds() );
			assertEquals( "ShapeListContainer y failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cShapeList.isOutOfBounds() );
			
			cArray.bck( 1 );
			cCell.bck( 1 );
			cDynamic.bck( 1 );
			cShapeList.bck( 1 );
		}

		cArray.setPosition( 0, 1 );
		cCell.setPosition( 0, 1 );
		cDynamic.setPosition( 0, 1 );
		cShapeList.setPosition( 0, 1 );

		cArray.setPosition( 4, 2 );
		cCell.setPosition( 4, 2 );
		cDynamic.setPosition( 4, 2 );
		cShapeList.setPosition( 4, 2 );
		for ( int i = 0; i < 20; ++i )
		{
			assertEquals( "ArrayContainer z failed at iteration " + i + ".", expectedZ[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer z failed at iteration " + i + ".", expectedZ[ i ], cCell.get().getInteger() );
			assertEquals( "DynamicContainer z failed at iteration " + i + ".", expectedZ[ i ], cDynamic.get().getInteger() );
			assertEquals( "ShapeListContainer z failed at iteration " + i + ".", expectedZ[ i ], cShapeList.get().getInteger() );
			
			assertEquals( "ArrayContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "DynamicContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cDynamic.isOutOfBounds() );
			assertEquals( "ShapeListContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cShapeList.isOutOfBounds() );
			
			cArray.bck( 2 );
			cCell.bck( 2 );
			cDynamic.bck( 2 );
			cShapeList.bck( 2 );
		}
	}
	
	@Test
	public void move()
	{
		final int[] distance = new int[]{ 0, 10, 6, -12, -5 };
		final int[] d = new int[]{ 0, 0, 1, 0, 1 };
		final int[] v = new int[]{ 33, 33, 33, 31, 36 };
		
		final int[] start = new int[]{ 3, 2, 1 };
		
		cArray.setPosition( start );
		cCell.setPosition( start );
		cDynamic.setPosition( start );
		cShapeList.setPosition( start );
		
		for ( int i = 0; i < d.length; ++i )
		{
			cArray.move( distance[ i ], d[ i ] );
			cCell.move( distance[ i ], d[ i ] );
			cDynamic.move( distance[ i ], d[ i ] );
			cShapeList.move( distance[ i ], d[ i ] );
			
			assertEquals( "ArrayContainer move failed at iteration " + i + ".", v[ i ], cArray.get().getInteger() );
			assertEquals( "CellContainer move failed at iteration " + i + ".", v[ i ], cCell.get().getInteger() );
			assertEquals( "DynamicContainer move failed at iteration " + i + ".", v[ i ], cDynamic.get().getInteger() );
			assertEquals( "ShapeListContainer move failed at iteration " + i + ".", v[ i ], cShapeList.get().getInteger() );
			
			assertEquals( "ArrayContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cArray ), cArray.isOutOfBounds() );
			assertEquals( "CellContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cCell.isOutOfBounds() );
			assertEquals( "DynamicContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cDynamic.isOutOfBounds() );
			assertEquals( "ShapeListContainer z failed isOutOfBounds() at iteration " + i + ".", isOutOfBounds( cCell ), cShapeList.isOutOfBounds() );
		}
	}
	
	
	@Test
	public void setPosition()
	{
		final int[] x = new int[]{ 0, 1, 2, 3, 18, -9 };
		final int[] y = new int[]{ 0, 0, 2, 3, 11, 12 };
		final int[] z = new int[]{ 0, 0, 1, 2, 10, -13 };
		final int[] t = new int[]{ 0, 1, 32, 58, 47, 21 };
		
		for ( int i = 0; i < x.length; ++ i )
		{
			cArray.setPosition( x[ i ], 0 );
			cArray.setPosition( y[ i ], 1 );
			cArray.setPosition( z[ i ], 2 );
			cCell.setPosition( x[ i ], 0 );
			cCell.setPosition( y[ i ], 1 );
			cCell.setPosition( z[ i ], 2 );
			cDynamic.setPosition( x[ i ], 0 );
			cDynamic.setPosition( y[ i ], 1 );
			cDynamic.setPosition( z[ i ], 2 );
			cShapeList.setPosition( x[ i ], 0 );
			cShapeList.setPosition( y[ i ], 1 );
			cShapeList.setPosition( z[ i ], 2 );
			
			assertEquals( "ArrayContainer failed at " + cArray, cArray.get().getInteger(), t[ i ] );
			assertEquals( "CellContainer failed at " + cCell, cCell.get().getInteger(), t[ i ] );
			assertEquals( "DynamicContainer failed at " + cDynamic, cDynamic.get().getInteger(), t[ i ] );
			assertEquals( "ShapeListContainer failed at " + cShapeList, cShapeList.get().getInteger(), t[ i ] );
		}
	}
}
