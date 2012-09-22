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

import java.util.Random;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Create an empty 8-bit image and set some pixels.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class DrawWhitePixels
{
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 400, 320 };
		final Img< UnsignedByteType > img = new ArrayImgFactory< UnsignedByteType >().create( dimensions, new UnsignedByteType() );

		final RandomAccess< UnsignedByteType > r = img.randomAccess();
		final Random random = new Random();
		for ( int i = 0; i < 1000; ++i )
		{
			final int x = ( int ) ( random.nextFloat() * img.max( 0 ) );
			final int y = ( int ) ( random.nextFloat() * img.max( 1 ) );
			r.setPosition( x, 0 );
			r.setPosition( y, 1 );
			final UnsignedByteType t = r.get();
			t.set( 255 );
		}

		ImageJFunctions.show( img );
	}
}
