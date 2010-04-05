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
	
	public static Image<UnsignedByteType> wrapByte( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY8)
			return null;
		
		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		ByteImagePlus<UnsignedByteType> container = new ByteImagePlus<UnsignedByteType>( imp, containerFactory, new UnsignedByteType() );
		Image<UnsignedByteType> image = new Image<UnsignedByteType>( container, imp.getTitle() );
		
		return image;		
	}
	
	public static Image<UnsignedShortType> wrapShort( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY16)
			return null;

		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		ShortImagePlus<UnsignedShortType> container = new ShortImagePlus<UnsignedShortType>( imp, containerFactory, new UnsignedShortType() );
		Image<UnsignedShortType> image = new Image<UnsignedShortType>( container, imp.getTitle() );
		
		return image;						
	}

	public static Image<RGBALegacyType> wrapRGBA( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.COLOR_RGB)
			return null;

		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		IntImagePlus<RGBALegacyType> container = new IntImagePlus<RGBALegacyType>( imp,  containerFactory, new RGBALegacyType() );
		Image<RGBALegacyType> image = new Image<RGBALegacyType>( container, imp.getTitle() );
		
		return image;				
	}	
	
	public static Image<FloatType> wrapFloat( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY32)
			return null;

		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		FloatImagePlus<FloatType> container = new FloatImagePlus<FloatType>( imp,  containerFactory, new FloatType() );
		Image<FloatType> image = new Image<FloatType>( container, imp.getTitle() );
		
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
