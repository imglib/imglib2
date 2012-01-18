/**
 * Copyright (c) 2009--2011, Stephan Saalfeld
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
package net.imglib2.display;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Iterator;

import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayLocalizingCursor;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.ARGBType;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class ARGBScreenImage implements ScreenImage, IterableInterval< ARGBType >, RandomAccessibleInterval< ARGBType >
{
	final protected int[] data; 
	final protected ArrayImg< ARGBType, IntArray > argbArray;
	final protected Image image;
	
	static final public ColorModel ARGB_COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
	
	public ARGBScreenImage( final int width, final int height )
	{
		this( width, height, new int[ width * height ] );
	}
	
	/** Create an {@link Image} with {@param data}. Writing to the {@param data} array will update the {@link Image}. */
	public ARGBScreenImage( final int width, final int height, final IntArray data )
	{
		this( width, height, data.getCurrentStorageArray() );
	}

	/** Create an {@link Image} with {@param data}. Writing to the {@param data} array will update the {@link Image}. */
	public ARGBScreenImage( final int width, final int height, final int[] data )
	{
		this.data = data;
		argbArray = new ArrayImg< ARGBType, IntArray >( new IntArray( data ), new long[]{ width, height }, 1 );
		argbArray.setLinkedType( new ARGBType( argbArray ) );

		final SampleModel sampleModel = ARGB_COLOR_MODEL.createCompatibleWritableRaster( 1, 1 ).getSampleModel()
									.createCompatibleSampleModel( width, height );
		final DataBuffer dataBuffer = new DataBufferInt( data, width * height, 0 );
		final WritableRaster rgbRaster = Raster.createWritableRaster( sampleModel, dataBuffer, null );
		image = new BufferedImage( ARGB_COLOR_MODEL, rgbRaster, false, null );
	}
	
	@Override
	public Image image()
	{
		return image;
	}
	
	/** The underlying array holding the data. Writing to this array will change
	 * the content of the {@link Image} returned by {@link ARGBScreenImage#image() */
	public int[] getData()
	{
		return data;
	}

	@Override
	public ArrayCursor< ARGBType > cursor()
	{
		return argbArray.cursor();
	}

	@Override
	public ArrayLocalizingCursor< ARGBType > localizingCursor()
	{
		return argbArray.localizingCursor();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return argbArray.equalIterationOrder( f );
	}

	@Override
	public long size()
	{
		return argbArray.size();
	}

	@Override
	public double realMax( final int d )
	{
		return argbArray.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		argbArray.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		argbArray.realMax( max );
	}
	
	@Override
	public double realMin( final int d )
	{
		return 0;
	}

	@Override
	public void realMin( final double[] min )
	{
		argbArray.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		argbArray.realMin( min );
	}
	
	@Override
	public int numDimensions()
	{
		return 2;
	}

	@Override
	public Iterator< ARGBType > iterator()
	{
		return argbArray.iterator();
	}

	@Override
	public long max( final int d )
	{
		return argbArray.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		argbArray.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		argbArray.max( max );
	}

	@Override
	public long min( final int d )
	{
		return 0;
	}

	@Override
	public void min( final long[] min )
	{
		argbArray.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		argbArray.min( min );
	}

	@Override
	public void dimensions( final long[] size )
	{
		argbArray.dimensions( size );
	}

	@Override
	public long dimension( final int d )
	{
		return argbArray.dimension( d );
	}

	@Override
	public ARGBType firstElement()
	{
		return iterator().next();
	}

	@Override
	public ArrayRandomAccess< ARGBType > randomAccess()
	{
		return argbArray.randomAccess();
	}

	@Override
	public ArrayRandomAccess< ARGBType > randomAccess( final Interval interval )
	{
		return argbArray.randomAccess( interval );
	}
}
