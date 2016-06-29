/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */

package net.imglib2.display.screenimage.awt;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import net.imglib2.Dimensions;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * An {@link AWTScreenImage} that is an {@link ArrayImg}.
 * 
 * @author Curtis Rueden
 */
public abstract class ArrayImgAWTScreenImage< T extends NativeType< T >, A > extends ArrayImg< T, A > implements AWTScreenImage
{

	private final BufferedImage bufferedImage;

	public ArrayImgAWTScreenImage( final ArrayImg< T, A > img )
	{
		this( img.firstElement(), img.update( null ), dimensions( img ) );
	}

	public ArrayImgAWTScreenImage( final T type, final A data, final long[] dim )
	{
		super( data, dim, type.getEntitiesPerPixel() );
		bufferedImage = createBufferedImage( type, data, ( int ) dim[ 0 ], ( int ) dim[ 1 ] );
	}

	@Override
	public BufferedImage image()
	{
		return bufferedImage;
	}

	protected int getBitsPerPixel( final T type )
	{
		if ( type instanceof RealType ) { return ( ( RealType< ? > ) type ).getBitsPerPixel(); }
		throw new IllegalStateException( "Unknown bits per pixel: " + type );
	}

	protected abstract DataBuffer createDataBuffer( A data );

	private BufferedImage createBufferedImage( final T type, final A data, final int width, final int height )
	{
		final DataBuffer buffer = createDataBuffer( data );
		final SampleModel model = new PixelInterleavedSampleModel( buffer.getDataType(), width, height, 1, width, new int[] { 0 } );
		final ColorModel colorModel = createColorModel( type, buffer );
		final WritableRaster raster = Raster.createWritableRaster( model, buffer, null );
		return new BufferedImage( colorModel, raster, false, null );
	}

	private ColorModel createColorModel( final T type, final DataBuffer buffer )
	{
		final ColorSpace cs = ColorSpace.getInstance( ColorSpace.CS_GRAY );
		final int[] bits = { getBitsPerPixel( type ) };
		return new ComponentColorModel( cs, bits, false, false, Transparency.OPAQUE, buffer.getDataType() );
	}

	private static long[] dimensions( final Dimensions img )
	{
		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		return dimensions;
	}
}
