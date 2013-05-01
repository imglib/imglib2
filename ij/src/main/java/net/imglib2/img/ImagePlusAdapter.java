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

package net.imglib2.img;

import ij.ImagePlus;
import ij.measure.Calibration;
import net.imglib2.Cursor;
import net.imglib2.converter.Converter;
import net.imglib2.img.imageplus.ByteImagePlus;
import net.imglib2.img.imageplus.FloatImagePlus;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.img.imageplus.IntImagePlus;
import net.imglib2.img.imageplus.ShortImagePlus;
import net.imglib2.meta.Axes;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ImagePlusAdapter
{
	@SuppressWarnings( "unchecked" )
	public static < T extends NumericType< T > & NativeType< T > > ImagePlusImg< T, ? > wrap( final ImagePlus imp )
	{
		return ( ImagePlusImg< T, ? > ) wrapLocal( imp );
	}

	@SuppressWarnings( { "rawtypes" } )
	public static ImagePlusImg wrapReal( final ImagePlus imp )
	{
		return wrapLocalReal( imp );
	}

	@SuppressWarnings( { "rawtypes" } )
	public static ImagePlusImg wrapNumeric( final ImagePlus imp )
	{
		return wrapLocal( imp );
	}

	public static < T extends NumericType< T > & NativeType< T > > ImgPlus< T > wrapImgPlus( final ImagePlus imp )
	{
		Img< T > img = wrap( imp );
		ImgPlus< T > image = new ImgPlus< T >( img );

		// set calibration
		setCalibrationFromImagePlus1( image, imp );

		// set title
		image.setName( imp.getTitle() );

		// set axes
		setAxesFromImagePlus( image, imp );

		return image;
	}

	protected static ImagePlusImg< ?, ? > wrapLocal( final ImagePlus imp )
	{
		switch( imp.getType() )
		{		
		case ImagePlus.GRAY8 : 
		{
			return wrapByte( imp );
		}
		case ImagePlus.GRAY16 : 
		{
			return wrapShort( imp );
		}
		case ImagePlus.GRAY32 : 
		{
			return wrapFloat( imp );
		}
		case ImagePlus.COLOR_RGB : 
		{
			return wrapRGBA( imp );
		}
		default :
		{
			throw new RuntimeException("Only 8, 16, 32-bit and RGB supported!");
		}
		}
	}

	protected static ImagePlusImg< ?, ? > wrapLocalReal( final ImagePlus imp )
	{
		switch( imp.getType() )
		{		
		case ImagePlus.GRAY8 : 
		{
			return wrapByte( imp );
		}
		case ImagePlus.GRAY16 : 
		{
			return wrapShort( imp );
		}
		case ImagePlus.GRAY32 : 
		{
			return wrapFloat( imp );
		}
		default :
		{
			throw new RuntimeException("Only 8, 16 and 32-bit supported!");
		}
		}
	}

	protected static < T extends NumericType< T > & NativeType< T > > void setAxesFromImagePlus( final ImgPlus<T> image, final ImagePlus imp ) 
	{

		int currentDim = 2;

		if (imp.getNChannels() > 1) {
			image.axis(currentDim).setType(Axes.CHANNEL);
			currentDim++;
		}

		if (imp.getNSlices() > 1) {
			image.axis(currentDim).setType(Axes.Z);
			currentDim++;
		}

		if (imp.getNFrames() > 1) {
			image.axis(currentDim).setType(Axes.TIME);
		}

	}

	protected static < T extends NumericType< T > & NativeType< T > > void setCalibrationFromImagePlus1( final ImgPlus<T> image, final ImagePlus imp ) 
	{
		final int d = image.numDimensions();
		final float [] spacing = new float[d];

		for( int i = 0; i < d; ++i )
			spacing[i] = 1f;

		final Calibration c = imp.getCalibration();

		/* Fill out calibration array. We must make sure that the element
		 * matches the dimension; the resulting ImgPlus skips singleton dimensions. */
		if( c != null ) 
		{
			if( d >= 1 )
				spacing[0] = (float)c.pixelWidth;

			if( d >= 2 )
				spacing[1] = (float)c.pixelHeight;

			/* Extra dimensions. We must take  care of the dimensions order and
			 * of singleton dimensions. */
			int currentDim = 2;

			if (imp.getNChannels() > 1) {
				spacing[currentDim] = 1;
				currentDim++;
			}

			if (imp.getNSlices() > 1) {
				spacing[currentDim] = (float) c.pixelDepth;
				currentDim++;
			}

			if (imp.getNFrames() > 1) {
				spacing[currentDim] = (float) c.frameInterval;
			}

		}

		for (int i = 0; i < spacing.length; i++) {
			image.axis(i).setCalibration(spacing[i]);
		}
	}

	public static ByteImagePlus<UnsignedByteType> wrapByte( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY8)
			return null;

		final ByteImagePlus< UnsignedByteType > container = new ByteImagePlus< UnsignedByteType >( imp );

		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	public static ShortImagePlus<UnsignedShortType> wrapShort( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY16)
			return null;

		final ShortImagePlus< UnsignedShortType > container = new ShortImagePlus< UnsignedShortType >( imp );

		// create a Type that is linked to the container
		final UnsignedShortType linkedType = new UnsignedShortType( container );

		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		return container;						
	}

	public static IntImagePlus<ARGBType> wrapRGBA( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.COLOR_RGB)
			return null;

		final IntImagePlus< ARGBType > container = new IntImagePlus< ARGBType >( imp );

		// create a Type that is linked to the container
		final ARGBType linkedType = new ARGBType( container );

		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		return container;				
	}	

	public static FloatImagePlus<FloatType> wrapFloat( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY32)
			return null;

		final FloatImagePlus<FloatType> container = new FloatImagePlus<FloatType>( imp );

		// create a Type that is linked to the container
		final FloatType linkedType = new FloatType( container );

		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		return container;				
	}	

	public static Img< FloatType > convertFloat( final ImagePlus imp )
	{

		switch( imp.getType() )
		{		
		case ImagePlus.GRAY8 : 
		{
			return convertToFloat( wrapByte( imp ), new NumberToFloatConverter<UnsignedByteType>() );
		}
		case ImagePlus.GRAY16 : 
		{
			return convertToFloat( wrapShort( imp ), new NumberToFloatConverter<UnsignedShortType>() );
		}
		case ImagePlus.GRAY32 : 
		{
			return wrapFloat( imp );
		}
		case ImagePlus.COLOR_RGB : 
		{
			return convertToFloat( wrapRGBA( imp ), new ARGBtoFloatConverter() );
		}
		default :
		{
			throw new RuntimeException("Only 8, 16, 32-bit and RGB supported!");
		}
		}
	}

	static private class ARGBtoFloatConverter implements Converter< ARGBType, FloatType >
	{
		/** Luminance times alpha. */
		@Override
		public void convert(final ARGBType input, final FloatType output) {
			final int v = input.get();
			output.setReal( ((v >> 24) & 0xff) * (((v >> 16) & 0xff) * 0.299 + ((v >> 8) & 0xff) * 0.587 + (v & 0xff) * 0.144));
		}
	}

	static private class NumberToFloatConverter< T extends ComplexType< T > > implements Converter< T, FloatType >
	{
		@Override
		public void convert(final T input, final FloatType output) {
			output.setReal( input.getRealFloat() );
		}		
	}

	protected static < T extends Type< T > > Img< FloatType > convertToFloat(
			final Img< T > input, final Converter< T, FloatType > c )
			{		
		final ImagePlusImg< FloatType, ? > output = new ImagePlusImgFactory< FloatType >().create( input, new FloatType() );

		final Cursor< T > in = input.cursor();
		final Cursor< FloatType > out = output.cursor();

		while ( in.hasNext() )
		{
			in.fwd();
			out.fwd();

			c.convert(in.get(), out.get());
		}

		return output;
			}
}
