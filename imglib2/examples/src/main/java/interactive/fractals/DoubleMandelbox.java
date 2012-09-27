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
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class DoubleMandelbox extends AbstractMandelbox< DoubleType >
{
	public class DoubleMandelboxRealRandomAccess extends RealPoint implements RealRandomAccess< DoubleType >
	{
		final DoubleType t = new DoubleType();

		public DoubleMandelboxRealRandomAccess()
		{
			super( DoubleMandelbox.this.n );
		}

		@Override
		public DoubleType get()
		{
			for ( int d = 0; d < n; ++d )
				z[ d ] = position[ d ];
			double sum = 0;
			double f = 0;
			for ( long i = 0; i < maxIterations; ++i )
			{
				f = scale;
				sum = 0;
				for ( int d = 0; d < n; ++d )
				{
					double p = z[ d ];
					if ( p > 1 )
						p = 2 - p;
					else if ( p < -1 )
						p = -2 - p;
					z[ d ] = p;
					sum += p * p;
				}
				
			    if ( sum < 0.25 )
			    {
			    	for ( int d = 0; d < n; ++d )
			    		z[ d ] = z[ d ] * 4 * scale + position[ d ];
			    	f *= scale / 0.25;
			    }
			    else if ( sum < 1 )
			    {
			    	for ( int d = 0; d < n; ++d )
			    		z[ d ] = ( z[ d ] - sum ) * scale + position[ d ];
			    	f *= scale / sum;
			    }
			}
		    
			t.set( Math.sqrt( sum ) / Math.abs( f ) );
			return t;
		}

		@Override
		public DoubleMandelboxRealRandomAccess copyRealRandomAccess()
		{
			return copy();
		}

		@Override
		public DoubleMandelboxRealRandomAccess copy()
		{
			final DoubleMandelboxRealRandomAccess a = new DoubleMandelboxRealRandomAccess();
			a.setPosition( this );
			return a;
		}
	}
	
	public DoubleMandelbox( final int n, final double scale, final long maxIterations )
	{
		super( n, scale, maxIterations );
	}
	
	public DoubleMandelbox( final int n, final long maxIterations )
	{
		this( n, -1.5, maxIterations );
	}
	
	public DoubleMandelbox( final int n )
	{
		this( n, -1.5, 10 );
	}
	
	@Override
	public DoubleMandelboxRealRandomAccess realRandomAccess()
	{
		return new DoubleMandelboxRealRandomAccess();
	}
}
