/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.componenttree.pixellist;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.pixellist.PixelListComponent;
import net.imglib2.algorithm.componenttree.pixellist.PixelListComponentTree;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;

/**
 * Example of computing the {@link PixelListComponentTree} of an image.
 *
 *
 * @author Tobias Pietzsch
 */
public class PixelListComponentTreeExample
{
	public static final int[][] testData = new int[][] {
		{ 8, 7, 6, 7, 1 },
		{ 8, 8, 5, 8, 1 },
		{ 2, 3, 4, 3, 2 },
		{ 1, 8, 3, 8, 1 },
		{ 1, 2, 2, 2, 1 } };

	static final long[] dimensions = new long[] { testData[ 0 ].length, testData.length };

	public static void print( PixelListComponentTree< IntType > tree )
	{
		for ( PixelListComponent< IntType > component : tree )
		{
			System.out.println( component );

			for ( int r = 0; r < dimensions[1]; ++r )
			{
				System.out.print("| ");
				for ( int c = 0; c < dimensions[0]; ++c )
				{
					boolean set = false;
					for ( Localizable l : component )
						if( l.getIntPosition( 0 ) == c && l.getIntPosition( 1 ) == r )
							set = true;
					System.out.print( set ? "x " : ". " );
				}
				System.out.println("|");
			}

			System.out.println();
		}
	}

	public static void main( String[] args )
	{
		ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
		Img< IntType > input = imgFactory.create( dimensions, new IntType() );

		// fill input image with test data
		int[] pos = new int[ 2 ];
		Cursor< IntType > c = input.localizingCursor();
		while ( c.hasNext() )
		{
			c.fwd();
			c.localize( pos );
			c.get().set( testData[ pos[ 1 ] ][ pos[ 0 ] ] );
		}

		System.out.println("== dark to bright ==");
		PixelListComponentTree< IntType > tree = PixelListComponentTree.buildComponentTree( input, new IntType(), true );
		print( tree );

		System.out.println("== bright to dark ==");
		tree = PixelListComponentTree.buildComponentTree( input, new IntType(), false );
		print( tree );
	}
}
