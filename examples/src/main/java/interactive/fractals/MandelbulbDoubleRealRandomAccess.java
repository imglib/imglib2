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
 */
package interactive.fractals;

import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * A RealRandomAccess that procedurally generates values (iteration count)
 * for the mandelbulb set as described by Daniel White and Paul Nylander
 * <a href="http://www.skytopia.com/project/fractal/mandelbulb.html">http://www.skytopia.com/project/fractal/mandelbulb.html</a>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class MandelbulbDoubleRealRandomAccess extends RealPoint implements RealRandomAccess< DoubleType >
{
	final DoubleType t;
	final static private int m = 8;
	final private long maxIterations;

	public MandelbulbDoubleRealRandomAccess( final long maxIterations )
	{
		super( 3 );
		this.maxIterations = maxIterations;
		t = new DoubleType();
	}
	
	final static private double pow8( final double x )
	{
		final double x2 = x * x;
		final double x4 = x2 * x2;
		return x4 * x4;
	}

	public static final double mandelbrot( final double x0, final double y0, final double z0, final long maxIterations )
	{
		double x = x0;
		double y = y0;
		double z = z0;
		for ( long i = 0; i < maxIterations; ++i )
		{
			final double xxyy = x * x + y * y;
			final double r = Math.sqrt( xxyy + z * z );
			final double theta = Math.atan2( Math.sqrt( xxyy ), z );
			final double phi = Math.atan2( y, x );
			
			final double rm = pow8( r );
			final double thetam = theta * m;
			final double phim = phi * m;
			
			x = rm * Math.cos( thetam ) * Math.cos( phim );
			y = rm * Math.sin( thetam ) * Math.sin( phim );
			z = rm * Math.sin( phim );
		}
		final double dx = x - x0;
		final double dy = y - y0;
		final double dz = z - z0;
		
		final double dx2 = dx * dx;
		final double dy2 = dy * dy;
		final double dz2 = dz * dz;
		return Math.sqrt( dx2 + dy2 + dz2 );
	}

	@Override
	public DoubleType get()
	{
		t.set( mandelbrot( position[ 0 ], position[ 1 ], position[ 2 ], maxIterations ) );
		return t;
	}

	@Override
	public MandelbulbDoubleRealRandomAccess copyRealRandomAccess()
	{
		return copy();
	}

	@Override
	public MandelbulbDoubleRealRandomAccess copy()
	{
		final MandelbulbDoubleRealRandomAccess a = new MandelbulbDoubleRealRandomAccess( maxIterations );
		a.setPosition( this );
		return a;
	}
}