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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.image;

import ij.ImagePlus;
import ij.measure.Calibration;
import mpicbg.imglib.container.imageplus.ByteImagePlus;
import mpicbg.imglib.container.imageplus.FloatImagePlus;
import mpicbg.imglib.container.imageplus.ImagePlusContainerFactory;
import mpicbg.imglib.container.imageplus.IntImagePlus;
import mpicbg.imglib.container.imageplus.ShortImagePlus;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.TypeConverter;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class ImagePlusAdapter
{
	@SuppressWarnings("unchecked")
	public static <T extends RealType<T>> Image< T > wrap( final ImagePlus imp )
	{
		return (Image<T>) wrapLocal(imp);
	}
	
	protected static Image<?> wrapLocal( final ImagePlus imp )
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
				System.out.println( "mpi.imglib.container.imageplus.ImagePlusAdapter(): Cannot handle type " + imp.getType() );
				return null;
			}
		}
	}

	protected static void setCalibrationFromImagePlus( final Image<?> image, final ImagePlus imp ) 
	{
		final int d = image.getNumDimensions();
		final float [] spacing = new float[d];
		
		for( int i = 0; i < d; ++i )
			spacing[i] = 1f;
		
		final Calibration c = imp.getCalibration();
		
		if( c != null ) 
		{
			if( d >= 1 )
				spacing[0] = (float)c.pixelWidth;
			if( d >= 2 )
				spacing[1] = (float)c.pixelHeight;
			if( d >= 3 )
				spacing[2] = (float)c.pixelDepth;
			if( d >= 4 )
				spacing[3] = (float)c.frameInterval;
		}

		image.setCalibration( spacing );
	}
	
	public static Image<UnsignedByteType> wrapByte( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY8)
			return null;
		
		final ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		final ByteImagePlus<UnsignedByteType> container = new ByteImagePlus<UnsignedByteType>( imp,  containerFactory );

		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		final Image<UnsignedByteType> image = new Image<UnsignedByteType>( container, new UnsignedByteType(), imp.getTitle() );

		setCalibrationFromImagePlus( image, imp );
		
		return image;		
	}
	
	public static Image<UnsignedShortType> wrapShort( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY16)
			return null;

		final ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		final ShortImagePlus<UnsignedShortType> container = new ShortImagePlus<UnsignedShortType>( imp,  containerFactory );

		// create a Type that is linked to the container
		final UnsignedShortType linkedType = new UnsignedShortType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		Image<UnsignedShortType> image = new Image<UnsignedShortType>( container, new UnsignedShortType(), imp.getTitle() );

		setCalibrationFromImagePlus( image, imp );
		
		return image;						
	}

	public static Image<RGBALegacyType> wrapRGBA( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.COLOR_RGB)
			return null;

		final ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		final IntImagePlus<RGBALegacyType> container = new IntImagePlus<RGBALegacyType>( imp,  containerFactory );

		// create a Type that is linked to the container
		final RGBALegacyType linkedType = new RGBALegacyType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		final Image<RGBALegacyType> image = new Image<RGBALegacyType>( container, new RGBALegacyType(), imp.getTitle() );

		setCalibrationFromImagePlus( image, imp );
		
		return image;				
	}	
	
	public static Image<FloatType> wrapFloat( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY32)
			return null;

		final ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		final FloatImagePlus<FloatType> container = new FloatImagePlus<FloatType>( imp,  containerFactory );

		// create a Type that is linked to the container
		final FloatType linkedType = new FloatType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		final Image<FloatType> image = new Image<FloatType>( container, new FloatType(), imp.getTitle() );

		setCalibrationFromImagePlus( image, imp );
		
		return image;				
	}	
	
	public static Image<FloatType> convertFloat( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY32)
		{
			Image<?> img = wrapLocal( imp );
			
			if ( img == null )
				return null;				
			
			return convertToFloat( img );
			
		}
		else
		{
			return wrapFloat( imp );
		}
	}
	
	protected static <T extends Type<T> > Image<FloatType> convertToFloat( Image<T> input )
	{		
		ImageFactory<FloatType> factory = new ImageFactory<FloatType>( new FloatType(), new ImagePlusContainerFactory() );
		Image<FloatType> output = factory.createImage( input.getDimensions(), input.getName() );
	
		Cursor<T> in = input.createCursor();
		Cursor<FloatType> out = output.createCursor();
		
		TypeConverter tc = TypeConverter.getTypeConverter( in.getType(), out.getType() );
		
		if ( tc == null )
		{
			System.out.println( "Cannot convert from " + in.getType().getClass() + " to " + out.getType().getClass() );
			output.close();
			return null;
		}
		
		while ( in.hasNext() )
		{
			in.fwd();
			out.fwd();
			
			tc.convert();			
		}
		
		return output;
	}
}
