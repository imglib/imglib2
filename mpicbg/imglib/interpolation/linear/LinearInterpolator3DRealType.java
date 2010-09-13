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
package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.location.Localizable;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.RealType;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class LinearInterpolator3DRealType< T extends RealType< T > > extends LinearInterpolator3D< T >
{
	protected LinearInterpolator3DRealType( final Image< T > img, final InterpolatorFactory< T > interpolatorFactory, final OutOfBoundsStrategyFactory< T > outOfBoundsStrategyFactory )
	{
		super( img, interpolatorFactory, outOfBoundsStrategyFactory );
	}

	@Override
	public T type()
	{
		// How to iterate the cube
		//
		// y7 y6
		// *------>*
		// ^ /|
		// y3 / y2 / v
		// *<------* * y5
		// | x ^ /
		// | |/
		// *------>*
		// y0 y1

		final float y0 = target.type().getRealFloat();
		target.fwd( 0 );
		final float y1 = target.type().getRealFloat();
		target.fwd( 1 );
		final float y2 = target.type().getRealFloat();
		target.bck( 0 );
		final float y3 = target.type().getRealFloat();
		target.fwd( 2 );
		final float y7 = target.type().getRealFloat();
		target.fwd( 0 );
		final float y6 = target.type().getRealFloat();
		target.bck( 1 );
		final float y5 = target.type().getRealFloat();
		target.bck( 0 );
		final float y4 = target.type().getRealFloat();
		target.bck( 2 );

		// weights
		final float t = x - target.getIntPosition( 0 );
		final float u = y - target.getIntPosition( 1 );
		final float v = z - target.getIntPosition( 2 );

		final float t1 = 1 - t;
		final float u1 = 1 - u;
		final float v1 = 1 - v;

		tmp1.setReal(
				t1 * u1 * v1 * y0 +
				t * u1 * v1 * y1 +
				t * u * v1 * y2 +
				t1 * u * v1 * y3 +
				
				t1 * u1 * v * y4 +
				t * u1 * v * y5 +
				t * u * v * y6 +
				t1 * u * v * y7 );

		return tmp1;
	}
}
