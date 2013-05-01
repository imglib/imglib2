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
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class Mandelbox extends AbstractMandelbox< LongType >
{
	public class MandelboxRealRandomAccess extends RealPoint implements RealRandomAccess< LongType >
	{
		final LongType t = new LongType();

		public MandelboxRealRandomAccess()
		{
			super( Mandelbox.this.n );
		}

		@Override
		public LongType get()
		{
			for ( int d = 0; d < n; ++d )
				z[ d ] = position[ d ];
			
			long i = 0;
			double sum = 0;
			for ( ; i < maxIterations; ++i )
			{
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
			    	for ( int d = 0; d < n; ++d )
			    		z[ d ] = z[ d ] * 4 * scale + position[ d ];		
			    else if ( sum < 1 )
			    	for ( int d = 0; d < n; ++d )
			    		z[ d ] = ( z[ d ] - sum ) * scale + position[ d ];
			    else if ( sum > 2 )
			    	break;
			}
		    
			t.set( i );
			return t;
		}

		@Override
		public MandelboxRealRandomAccess copyRealRandomAccess()
		{
			return copy();
		}

		@Override
		public MandelboxRealRandomAccess copy()
		{
			final MandelboxRealRandomAccess a = new MandelboxRealRandomAccess();
			a.setPosition( this );
			return a;
		}
	}
	
	public Mandelbox( final int n, final double scale, final long maxIterations )
	{
		super( n, scale, maxIterations );
	}
	
	public Mandelbox( final int n, final long maxIterations )
	{
		this( n, -1.5, maxIterations );
	}
	
	public Mandelbox( final int n )
	{
		this( n, -1.5, 10 );
	}
	
	@Override
	public MandelboxRealRandomAccess realRandomAccess()
	{
		return new MandelboxRealRandomAccess();
	}
}
