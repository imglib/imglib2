/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package interactive.fractals;

import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.type.numeric.integer.LongType;

/**
 * A RealRandomAccess that procedurally generates values (iteration count)
 * for the mandelbulb set as described by Daniel White and Paul Nylander
 * <a href="http://www.skytopia.com/project/fractal/mandelbulb.html">http://www.skytopia.com/project/fractal/mandelbulb.html</a>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class MandelbulbRealRandomAccess extends RealPoint implements RealRandomAccess< LongType >
{
	final LongType t;
	final static private int m = 8;
	final private long maxIterations;

	public MandelbulbRealRandomAccess( final long maxIterations )
	{
		super( 3 );
		this.maxIterations = maxIterations;
		t = new LongType();
	}
	
	final static private double pow8( final double x )
	{
		final double x2 = x * x;
		final double x4 = x2 * x2;
		return x4 * x4;
	}

	public static final long mandelbrot( final double x0, final double y0, final double z0, final long maxIterations )
	{
		double x = x0;
		double y = y0;
		double z = z0;
		long i = 0;
		for ( ; i < maxIterations; ++i )
		{
			final double x2 = x * x;
			final double y2 = y * y;
			final double z2 = z * z;
			if ( x2 + y2 + z2 > 4 )
				break;
			
			final double xxyy = x2 + y2;
			final double r = Math.sqrt( xxyy + z2 );
			final double theta = Math.atan2( Math.sqrt( xxyy ), z );
			final double phi = Math.atan2( y, x );
			
			final double rm = pow8( r );
			final double thetam = theta * m;
			final double phim = phi * m;
			
			x = rm * Math.cos( thetam ) * Math.cos( phim );
			y = rm * Math.sin( thetam ) * Math.sin( phim );
			z = rm * Math.sin( phim );
			
//			final double dx = x - x0;
//			final double dy = y - y0;
//			final double dz = z - z0;
//			
//			final double dx2 = dx * dx;
//			final double dy2 = dy * dy;
//			final double dz2 = dz * dz;
//			if ( dx2 + dy2 + dz2 >= 4 )
//				break;
		}
		return i;
	}

	@Override
	public LongType get()
	{
		t.set( mandelbrot( position[ 0 ], position[ 1 ], position[ 2 ], maxIterations ) );
		return t;
	}

	@Override
	public MandelbulbRealRandomAccess copyRealRandomAccess()
	{
		return copy();
	}

	@Override
	public MandelbulbRealRandomAccess copy()
	{
		final MandelbulbRealRandomAccess a = new MandelbulbRealRandomAccess( maxIterations );
		a.setPosition( this );
		return a;
	}
}
