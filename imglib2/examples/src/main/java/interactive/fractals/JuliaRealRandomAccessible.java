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

import net.imglib2.RealInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.LongType;

/**
 * A RealRandomAccess that procedurally generates values (iteration count)
 * for the mandelbrot set.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class JuliaRealRandomAccessible implements RealRandomAccessible< LongType >
{
	final protected LongType t;
	final protected ComplexDoubleType a;
	final protected ComplexDoubleType c;
	long maxIterations;
	double maxAmplitude;

	public JuliaRealRandomAccessible()
	{
		t = new LongType();
		a = new ComplexDoubleType();
		c = new ComplexDoubleType();
		maxIterations = 50;
		maxAmplitude = 4096;
	}
	
	public JuliaRealRandomAccessible(
			final ComplexDoubleType c,
			final int maxIterations,
			final int maxAmplitude )
	{
		t = new LongType();
		a = new ComplexDoubleType();
		this.c = c;
		this.maxIterations = maxIterations;
		this.maxAmplitude = maxAmplitude;
	}
	
	public void setC( final ComplexDoubleType c )
	{
		this.c.set( c );
	}
	
	public void setC( final double r, final double i )
	{
		c.set( r, i );
	}
	
	public void setMaxIterations( final long maxIterations )
	{
		this.maxIterations = maxIterations;
	}
	
	public void setMaxAmplitude( final double maxAmplitude )
	{
		this.maxAmplitude = maxAmplitude;
	}
	
	public class JuliaRealRandomAccess extends RealPoint implements RealRandomAccess< LongType >
	{
		public JuliaRealRandomAccess()
		{
			super( 2 );
		}
		
		final private long julia( final double x, final double y )
		{
			long i = 0;
			double v = 0;
			a.set( x, y );
			while ( i < maxIterations && v < 4096 )
			{
				a.mul( a );
				a.add( c );
				v = a.getPowerDouble();
				++i;
			}
			return i < 0 ? 0 : i > 255 ? 255 : i;
		}

		@Override
		public LongType get()
		{
			t.set( julia( position[ 0 ], position[ 1 ] ) );
			return t;
		}

		@Override
		public JuliaRealRandomAccess copyRealRandomAccess()
		{
			return copy();
		}

		@Override
		public JuliaRealRandomAccess copy()
		{
			final JuliaRealRandomAccess copy = new JuliaRealRandomAccess();
			copy.setPosition( this );
			return copy;
		}
	}

	@Override
	public int numDimensions()
	{
		return 2;
	}

	@Override
	public JuliaRealRandomAccess realRandomAccess()
	{
		return new JuliaRealRandomAccess();
	}

	@Override
	public JuliaRealRandomAccess realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
	}
}
