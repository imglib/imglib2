/**
 * Copyright (c) 2009--2012, ImgLib2 developers
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
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
package net.imglib2.tutorial.t02;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Load an image and find the maximum value and its location.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FindMaximumValueAndLocation
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< UnsignedByteType > img = new ImgOpener().openImg( "graffiti.tif", new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		final Cursor< UnsignedByteType > cursor = img.cursor();
		int max = 0;
		final long[] pos = new long[2];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			final UnsignedByteType t = cursor.get();
			if ( t.get() > max )
			{
				max = t.get();
				cursor.localize( pos );
			}
		}
		System.out.println( "max = " + max );
		System.out.println( "found at ( " + pos[0] + ", " + pos[1] + ")" );
	}
}
