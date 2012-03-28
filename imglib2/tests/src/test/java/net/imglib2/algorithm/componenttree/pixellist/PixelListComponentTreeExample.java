/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
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
