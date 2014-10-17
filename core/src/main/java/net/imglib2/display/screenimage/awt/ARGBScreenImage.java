/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.display.screenimage.awt;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.util.Fraction;

/**
 * 
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class ARGBScreenImage extends ArrayImg< ARGBType, IntArray > implements AWTScreenImage
{
	final protected int[] data;

	final protected BufferedImage image;

	static final public ColorModel ARGB_COLOR_MODEL = new DirectColorModel( 32, 0xff0000, 0xff00, 0xff, 0xff000000 );

	public ARGBScreenImage( final int width, final int height )
	{
		this( width, height, new int[ width * height ] );
	}

	/**
	 * Create an {@link Image} with {@code data}. Writing to the {@code data}
	 * array will update the {@link Image}.
	 */
	public ARGBScreenImage( final int width, final int height, final IntArray data )
	{
		this( width, height, data.getCurrentStorageArray() );
	}

	/**
	 * Create an {@link Image} with {@code data}. Writing to the {@code data}
	 * array will update the {@link Image}.
	 */
	public ARGBScreenImage( final int width, final int height, final int[] data )
	{
		super( new IntArray( data ), new long[]{ width, height }, new Fraction() );
		setLinkedType( new ARGBType( this ) );
		this.data = data;

		final SampleModel sampleModel = ARGB_COLOR_MODEL.createCompatibleWritableRaster( 1, 1 ).getSampleModel()
				.createCompatibleSampleModel( width, height );
		final DataBuffer dataBuffer = new DataBufferInt( data, width * height, 0 );
		final WritableRaster rgbRaster = Raster.createWritableRaster( sampleModel, dataBuffer, null );
		image = new BufferedImage( ARGB_COLOR_MODEL, rgbRaster, false, null );
	}

	@Override
	public BufferedImage image()
	{
		return image;
	}

	/**
	 * The underlying array holding the data. Writing to this array will change
	 * the content of the {@link Image} returned by
	 * {@link ARGBScreenImage#image()}
	 */
	public int[] getData()
	{
		return data;
	}
}
