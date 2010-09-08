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
package mpicbg.imglib.sampler.special;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.sampler.RasterSampler;
import mpicbg.imglib.type.Type;

/**
 * 
 * 
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class LocalNeighborhoodCursorFactory 
{
	public static < T extends Type< T > > LocalNeighborhoodCursor<T> createLocalNeighborhoodCursor(
			final RasterLocalizable localizable,
			final Image< T > image,
			final OutOfBoundsStrategyFactory< T > outsideFactory )
	{
		if ( image.numDimensions() == 3 )
		{
			return new LocalNeighborhoodCursor3D< T >( localizable, image, outsideFactory );
		}
		else
		{
			return new LocalNeighborhoodCursor< T >( localizable, image, outsideFactory );
		}
	}

	public static < T extends Type< T > >LocalNeighborhoodCursor<T> createLocalNeighborhoodCursor( final RasterLocalizable localizable, final Image< T > image )
	{
		return createLocalNeighborhoodCursor( localizable, image, null ); 
	}
	
	public static < T extends Type< T >, S extends RasterLocalizable & RasterSampler< T > >LocalNeighborhoodCursor< T > createLocalNeighborhoodCursor( final S cursor, final OutOfBoundsStrategyFactory< T > outsideFactory )
	{
		return createLocalNeighborhoodCursor( cursor, cursor.getImage(), outsideFactory ); 
	}
	
	public static < T extends Type< T >, S extends RasterLocalizable & RasterSampler< T > >LocalNeighborhoodCursor< T > createLocalNeighborhoodCursor( final S cursor )
	{
		return createLocalNeighborhoodCursor( cursor, cursor.getImage(), null ); 
	}
}
