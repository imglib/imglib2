/**
 * Copyright (c) 2009--2010, Stephan Saalfeld
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
package mpicbg.imglib.location;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Iterator;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.iterator.ZeroMinIntervalIterator;
import mpicbg.imglib.util.Util;

/**
 * Use this class to iterate a virtual rectangular raster in flat order, that
 * is: row by row, plane by plane, cube by cube, ...  This is useful for
 * iterating an arbitrary {@link Img} in a defined order.  For that,
 * connect a {@link LocalizingFlatIntegerIntervalIterator} to a
 * {@link ImgRandomAccess}.
 * 
 * <pre>
 * ...
 * LocalizingFlatRasterIterator i = new LocalizingFlatRasterIterator(image);
 * PositionableRasterSampler s = image.createPositionableRasterSampler();
 * while (i.hasNext()) {
 *   i.fwd();
 *   s.setPosition(i);
 *   s.type().performOperation(...);
 *   ...
 * }
 * ...
 * </pre>
 * 
 * Note that {@link LocalizingFlatIntegerIntervalIterator} is the right choice in
 * situations where, for <em>each</em> pixel, you want to localize and/or set
 * the {@link ImgRandomAccess}, that is in a dense sampling
 * situation.  For localizing sparsely (e.g. under an external condition),
 * use {@link ZeroMinIntervalIterator} instead.
 * 
 * TODO implement it, it's still the basic FlatRasterIterator!!!!!!
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class LocalizingFlatIntegerIntervalIterator implements Iterator, Localizable
{
	final protected int[] dimensions;
	final protected int[] steps;
	final protected int n;
	final protected int lastIndex;
	protected int index = -1;
	
	public LocalizingFlatIntegerIntervalIterator( final int[] dimensions )
	{
		n = dimensions.length;
		final int m = n - 1;
		this.dimensions = dimensions.clone();
		steps = new int[ n ];
		int k = steps[ 0 ] = 1;
		for ( int i = 0; i < m; )
		{
			k *= dimensions[ i ];
			steps[ ++i ] = k;
		}
		lastIndex = k * dimensions[ m ] - 1;
	}

	public LocalizingFlatIntegerIntervalIterator( final Image< ? > image ) { this( image.getDimensions() ); }
	
	final public static void indexToPosition( final int i, final int[] steps, final int[] l )
	{
		int x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final int ld = x / steps[ d ];
			l[ d ] = ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = i;
	}

	final public static void indexToPosition( final int i, final int[] steps, final long[] l )
	{
		int x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final int ld = x / steps[ d ];
			l[ d ] = ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = i;
	}
	
	final public static void indexToPosition( final int i, final int[] steps, final float[] l )
	{
		int x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final int ld = x / steps[ d ];
			l[ d ] = ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = i;
	}
	
	final public static void indexToPosition( final int i, final int[] steps, final double[] l )
	{
		int x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final int ld = x / steps[ d ];
			l[ d ] = ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = i;
	}
	
	final public static int indexToPosition( final int i, final int[] steps, final int dim )
	{
		int x = i;
		for ( int d = steps.length - 1; d > dim; --d )
			x %= steps[ d ];

		return x / steps[ dim ];
	}
	
	/* Iterator */

	@Override
	final public void jumpFwd( final long i ) { index += i; }

	@Override
	final public void fwd() { ++index; }

	@Override
	final public void reset() { index = -1; }
	
	
	/* RasterLocalizable */

	@Override
	final public long getLongPosition( final int dim ) { return indexToPosition( index, steps, dim ); }
	
	@Override
	final public void localize( final long[] position ) { indexToPosition( index, steps, position ); }

	@Override
	final public int getIntPosition( final int dim ) { return indexToPosition( index, steps, dim ); }

	@Override
	final public void localize( final int[] position ) { indexToPosition( index, steps, position ); }

	@Override
	final public double getDoublePosition( final int dim ) { return indexToPosition( index, steps, dim ); }
	
	
	/* Localizable */

	@Override
	final public float getFloatPosition( final int dim ) { return indexToPosition( index, steps, dim ); }

	@Override
	final public void localize( final float[] position ) { indexToPosition( index, steps, position ); }

	@Override
	final public void localize( final double[] position ) { indexToPosition( index, steps, position ); }

	
	/* Dimensionality */
	
	@Override
	final public int numDimensions() { return n; }
	
	
	/* Object */
	
	@Override
	final public String toString()
	{
		final int[] l = new int[ dimensions.length ];
		localize( l );
		return Util.printCoordinates( l );
	}
}
