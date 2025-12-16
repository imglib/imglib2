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
package net.imglib2.blocks;

import static net.imglib2.blocks.Ranges.Direction.BACKWARD;
import static net.imglib2.blocks.Ranges.Direction.CONSTANT;
import static net.imglib2.blocks.Ranges.Direction.FORWARD;
import static net.imglib2.blocks.Ranges.Direction.STAY;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.blocks.Ranges.Range;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;

public class CompactRangesPlayground
{
	public static void main( String[] args )
	{
		final CellImg< IntType, ? > img = new CellImgFactory<>( new IntType(), 2, 4, 4 ).create( 2, 9, 7 );
		System.out.println( "img.getCellGrid() = " + img.getCellGrid() );

		final Cursor< IntType > c = img.view().flatIterable().cursor();
		int i = 0;
		while ( c.hasNext() )
			c.next().set( i++ );

		final PrimitiveBlocks< IntType > blocks = PrimitiveBlocks.of( img );

//		final BlockInterval interval = BlockInterval.wrap(
//				new long[] { 0, 1, 3 },
//				new int[] { 2, 2, 4 } );
		final BlockInterval interval = BlockInterval.wrap(
				new long[] { 0, 4, 3 },
				new int[] { 2, 4, 4 } );

		int[] dest = new int[ ( int ) Intervals.numElements( interval ) ];
		blocks.copy( interval, dest );

		System.out.println( Arrays.toString( dest ) );
	}


	public static void main1( String[] args )
	{
		final CellImg< IntType, ? > img = new CellImgFactory<>( new IntType(), 2, 16, 16 ).create( 2, 55, 35 );

		System.out.println( "img.getCellGrid() = " + img.getCellGrid() );

		final PrimitiveBlocks< IntType > blocks = PrimitiveBlocks.of( img );

		final BlockInterval interval = BlockInterval.wrap(
				new long[] { 0, 4, 10 },
				new int[] { 2, 18, 10 } );

		int[] dest = new int[ ( int ) Intervals.numElements( interval ) ];
		blocks.copy( interval, dest );
	}
}
