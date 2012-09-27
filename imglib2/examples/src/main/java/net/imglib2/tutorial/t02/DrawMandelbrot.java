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
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Use {@link MandelbrotRealRandomAccess} to render a mandelbrot fractal.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class DrawMandelbrot
{
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 600, 400 };
		final Img< UnsignedByteType > img = new ArrayImgFactory< UnsignedByteType >().create( dimensions, new UnsignedByteType() );

		final RealRandomAccess< UnsignedByteType > mb = new MandelbrotRealRandomAccess();

		//final double scale = 0.000125;
		//final double[] offset = new double[] { -1.3875, 0.045 };

		final double scale = 0.005;
		final double[] offset = new double[] { -2, -1 };

		final Cursor< UnsignedByteType > cursor = img.localizingCursor();
		while( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int d = 0; d < 2; ++d )
				mb.setPosition( scale * cursor.getIntPosition( d ) + offset[ d ], d );
			cursor.get().set( mb.get() );
		}

		ImageJFunctions.show( img );
	}
}
