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
package mpicbg.imglib.display;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Iterator;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableInterval;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class UnsignedByteScreenImage implements ScreenImage, IterableInterval< UnsignedByteType >
{
	final protected byte[] data; 
	final protected Array< UnsignedByteType, ByteArray > argbArray;
	final Image image;
	
	public UnsignedByteScreenImage( final int width, final int height )
	{
		data = new byte[ width * height ];
		argbArray = new Array< UnsignedByteType, ByteArray >( new ByteArray( data ), new long[]{ width, height }, 1 );
		argbArray.setLinkedType( new UnsignedByteType( argbArray ) );

		final MemoryImageSource source = new MemoryImageSource( width, height, ColorModel.getRGBdefault(), data, 0, width );
		source.setAnimated( true );
		
		/* TOOO check if this is actually required */
		source.setFullBufferUpdates( true );
		image = Toolkit.getDefaultToolkit().createImage( source );
	}
	
	@Override
	public Image image()
	{
		return image;
	}

	@Override
	public Cursor< UnsignedByteType > cursor()
	{
		return argbArray.cursor();
	}

	@Override
	public Cursor< UnsignedByteType > localizingCursor()
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
	public int numDimensions()
	{
		return 2;
	}

	@Override
	public Iterator< UnsignedByteType > iterator()
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
	public UnsignedByteType firstElement()
	{
		return iterator().next();
	}
}
