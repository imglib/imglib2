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

package net.imglib2.img.cell;

import static org.junit.Assert.assertTrue;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.ImgTestHelper;
import net.imglib2.util.Util;

import org.junit.Test;

/**
 * Unit tests for {@link CellImg}.
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Curtis Rueden
 */
public class CellImgTest
{
	@Test
	public void testCellImg()
	{
		final long[][] dim = ImgTestHelper.dims();
		for ( int i = 0; i < dim.length; ++i )
		{
			if ( dim[ i ].length > 1 )
			{
				assertTrue( "ArrayImg vs CellImg failed for dim = " + Util.printCoordinates( dim[ i ] ),
						ImgTestHelper.testImg( dim[ i ], new ArrayImgFactory<>( new FloatType() ), new CellImgFactory<>( new FloatType(), 10 ) ) );
				assertTrue( "CellImg vs ArrayImg failed for dim = " + Util.printCoordinates( dim[ i ] ),
						ImgTestHelper.testImg( dim[ i ], new CellImgFactory<>( new FloatType() ), new ArrayImgFactory<>( new FloatType() ) ) );
				assertTrue( "CellImg vs CellImg failed for dim = " + Util.printCoordinates( dim[ i ] ),
						ImgTestHelper.testImg( dim[ i ], new CellImgFactory<>( new FloatType(), 5 ), new CellImgFactory<>( new FloatType() ) ) );
			}
		}
	}
}
