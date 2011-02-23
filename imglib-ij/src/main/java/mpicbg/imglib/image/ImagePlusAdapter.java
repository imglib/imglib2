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
import mpicbg.imglib.Cursor;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.imageplus.ByteImagePlus;
import mpicbg.imglib.container.imageplus.FloatImagePlus;
import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.container.imageplus.ImagePlusContainerFactory;
import mpicbg.imglib.container.imageplus.IntImagePlus;
import mpicbg.imglib.container.imageplus.ShortImagePlus;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.TypeConverter;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class ImagePlusAdapter
{
	@SuppressWarnings( "unchecked" )
	public static < T extends RealType< T > > Img< T > wrap( final ImagePlus imp )
	{
		return ( Img< T > ) wrapLocal( imp );
	}

	protected static Img< ? > wrapLocal( final ImagePlus imp )
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

	protected static void setCalibrationFromImagePlus( final Img<?> image, final ImagePlus imp ) 
	{
		final int d = image.numDimensions();
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

		// TODO calibration?
		System.out.println("WARNING: copying calibration from ImagePlus is not yet implemented");
		//image.setCalibration( spacing );
	}
	
	public static Img<UnsignedByteType> wrapByte( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY8)
			return null;
		
		final ByteImagePlus< UnsignedByteType > container = new ByteImagePlus< UnsignedByteType >( imp );

		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		setCalibrationFromImagePlus( container, imp );
		
		return container;
	}
	
	public static Img<UnsignedShortType> wrapShort( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY16)
			return null;

		final ShortImagePlus< UnsignedShortType > container = new ShortImagePlus< UnsignedShortType >( imp );

		// create a Type that is linked to the container
		final UnsignedShortType linkedType = new UnsignedShortType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		setCalibrationFromImagePlus( container, imp );
		
		return container;						
	}

	public static Img<ARGBType> wrapRGBA( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.COLOR_RGB)
			return null;

		final IntImagePlus< ARGBType > container = new IntImagePlus< ARGBType >( imp );

		// create a Type that is linked to the container
		final ARGBType linkedType = new ARGBType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		setCalibrationFromImagePlus( container, imp );
		
		return container;				
	}	
	
	public static Img<FloatType> wrapFloat( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY32)
			return null;

		final FloatImagePlus<FloatType> container = new FloatImagePlus<FloatType>( imp );

		// create a Type that is linked to the container
		final FloatType linkedType = new FloatType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		setCalibrationFromImagePlus( container, imp );
		
		return container;				
	}	
	
	public static Img< FloatType > convertFloat( final ImagePlus imp )
	{
		
		switch( imp.getType() )
		{		
			case ImagePlus.GRAY8 : 
			{
				return convertToFloat( wrapByte( imp ) );
			}
			case ImagePlus.GRAY16 : 
			{
				return convertToFloat( wrapShort( imp ) );
			}
			case ImagePlus.GRAY32 : 
			{
				return wrapFloat( imp );
			}
			case ImagePlus.COLOR_RGB : 
			{
				return convertToFloat( wrapRGBA( imp ) );
			}
			default :
			{
				throw new RuntimeException("Only 8, 16, 32-bit and RGB supported!");
			}
		}
	}
	
	protected static < T extends Type< T > > Img< FloatType > convertToFloat( Img< T > input )
	{		
		ImagePlusContainer< FloatType, ? > output = new ImagePlusContainerFactory< FloatType >().create( input, new FloatType() );
	
		Cursor< T > in = input.cursor();
		Cursor< FloatType > out = output.cursor();
	
		TypeConverter tc = TypeConverter.getTypeConverter( in.get(), out.get() );
		
		if ( tc == null )
		{
			System.out.println( "Cannot convert from " + in.get().getClass() + " to " + out.get().getClass() );
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
