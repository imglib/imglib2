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
package mpicbg.imglib.image.display.imagej;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.Collection;

import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImagePlusAdapter;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class ImageJFunctions
{
	final public static int GRAY8 = ImagePlus.GRAY8;
	final public static int GRAY32 = ImagePlus.GRAY32;
	final public static int COLOR_RGB = ImagePlus.COLOR_RGB;
	
	public static <T extends Type<T>> ImagePlus displayAsVirtualStack( final Collection<InverseTransformDescription<T>> interpolators,
																	   final int type, int[] dim, final int[] dimensionPositions )
	{
		return displayAsVirtualStack( interpolators, type, dim, dimensionPositions, null );
	}

	public static <T extends Type<T>> ImagePlus displayAsVirtualStack( final Collection<InverseTransformDescription<T>> interpolators,
																	   final int type, int[] dim, final int[] dimensionPositions,
																	   float[][] minMaxDim )
	{
		dim = getDim3(dim);

		if ( minMaxDim == null || minMaxDim.length != 3 ||
			 minMaxDim[0].length != 2 || minMaxDim[1].length != 2 || minMaxDim[2].length != 2 )
		{
			minMaxDim = new float[3][2];

			minMaxDim[0][0] = Float.MAX_VALUE;
			minMaxDim[1][0] = Float.MAX_VALUE;
			minMaxDim[2][0] = Float.MAX_VALUE;

			minMaxDim[0][1] = -Float.MAX_VALUE;
			minMaxDim[1][1] = -Float.MAX_VALUE;
			minMaxDim[2][1] = -Float.MAX_VALUE;

			for ( InverseTransformDescription<T> ti : interpolators )
			{
				float[] min = new float[ 3 ];
				float[] max = new float[ 3 ];
				
				for ( int d = 0; d < 3; ++d )
					max[ d ] = ti.getImage().getDimension( d );
				
				ti.getTransform().estimateBounds( min, max );
				//float[][] minMaxDimLocal = Util.getMinMaxDim( ti.getImage().getDimensions(), ti.getTransform() );

				for ( int i = 0; i < dim.length; i++ )
				{
					if ( dim[i] < min.length )
					{
						if ( min[ dim[i] ] < minMaxDim[ dim[i] ][ 0 ] )
							minMaxDim[ dim[i] ][ 0 ] = min[ dim[i] ];

						if ( max[ dim[i] ] > minMaxDim[ dim[i] ][ 1 ] )
							minMaxDim[ dim[i] ][ 1 ] = max[ dim[i] ];
					}
				}
			}
		}

		// get the final image dimensions
		final int[] dimensions = new int[ 3 ];
		dimensions[ 0 ] = 1;
		dimensions[ 1 ] = 1;
		dimensions[ 2 ] = 1;

		for ( int d = 0; d < dim.length; d++ )
			if ( minMaxDim[ d ][ 0 ] < Float.MAX_VALUE && minMaxDim[ d ][ 1 ] > -Float.MAX_VALUE )
				dimensions[ d ] = Math.round( minMaxDim[ d ][ 1 ] ) - Math.round( minMaxDim[ d ][ 0 ] );

		// set the offset for all InverseTransformableIterators
		for ( InverseTransformDescription<T> ti : interpolators )
		{
			final float[] offset = new float[ ti.getImage().getNumDimensions() ];

			for ( int d = 0; d < dim.length; ++d )
				offset[ dim[d] ] = minMaxDim[ d ][ 0 ];

			ti.setOffset( offset );
		}

		final ImageJVirtualDisplay<T> virtualDisplay = new ImageJVirtualDisplay<T>( interpolators, dimensions, type, dim, dimensionPositions );

		final ImagePlus imp = new ImagePlus( virtualDisplay.toString(), virtualDisplay );
		virtualDisplay.setParent( imp );

		imp.setSlice( dim[2]/2 );

		return imp;
	}
	
	public static <T extends RealType<T>> Image< T > wrap( final ImagePlus imp ) { return ImagePlusAdapter.wrap( imp ); }
	
	public static Image<UnsignedByteType> wrapByte( final ImagePlus imp ) { return ImagePlusAdapter.wrapByte( imp ); }
	
	public static Image<UnsignedShortType> wrapShort( final ImagePlus imp ) { return ImagePlusAdapter.wrapShort( imp ); }

	public static Image<RGBALegacyType> wrapRGBA( final ImagePlus imp ) { return ImagePlusAdapter.wrapRGBA( imp ); }
	
	public static Image<FloatType> wrapFloat( final ImagePlus imp ) { return ImagePlusAdapter.wrapFloat( imp ); }
	
	public static Image<FloatType> convertFloat( final ImagePlus imp ) { return ImagePlusAdapter.convertFloat( imp ); }	
	
	public static <T extends Type<T>> ImagePlus displayAsVirtualStack( final Image<T> img )
	{
		if ( RGBALegacyType.class.isInstance( img.createType() ) )
			return new ImagePlus( img.getName(), new ImageJVirtualStack<T>( img, COLOR_RGB, getDim3( getStandardDimensions() ), new int[ img.getNumDimensions() ] ) );
		else
			return new ImagePlus( img.getName(), new ImageJVirtualStack<T>( img, GRAY32, getDim3( getStandardDimensions() ), new int[ img.getNumDimensions() ] ) );
	}

	public static <T extends Type<T>> ImagePlus displayAsVirtualStack( final Image<T> img, final int type )
	{
		return new ImagePlus( img.getName(), new ImageJVirtualStack<T>( img, type, getDim3( getStandardDimensions() ), new int[ img.getNumDimensions() ] ) );
	}

	public static <T extends Type<T>> ImagePlus displayAsVirtualStack( final Image<T> img, final int type, final int[] dim )
	{
		return new ImagePlus( img.getName(), new ImageJVirtualStack<T>( img, type, getDim3(dim), new int[ img.getNumDimensions() ] ) );
	}

	public static <T extends Type<T>> ImagePlus displayAsVirtualStack( final Image<T> img, final int[] dim )
	{
		if ( RGBALegacyType.class.isInstance( img.createType() ) )
			return new ImagePlus( img.getName(), new ImageJVirtualStack<T>( img, COLOR_RGB, getDim3(dim), new int[ img.getNumDimensions() ] ) );
		else
			return new ImagePlus( img.getName(), new ImageJVirtualStack<T>( img, GRAY32, getDim3(dim), new int[ img.getNumDimensions() ] ) );
	}

	public static <T extends Type<T>> ImagePlus displayAsVirtualStack( final Image<T> img, final int type, final int[] dim, final int[] dimensionPositions )
	{
		return new ImagePlus( img.getName(), new ImageJVirtualStack<T>( img, type, getDim3(dim), dimensionPositions ) );
	}

	public static <T extends Type<T>> ImagePlus show( final Image<T> img ) 
	{ 
		img.getDisplay().setMinMax();
		ImagePlus imp = displayAsVirtualStack( img );
		
		imp.show();
		return imp;
	}

	public static <T extends Type<T>> ImagePlus copyToImagePlus( final Image<T> img )
	{
		if ( RGBALegacyType.class.isInstance( img.createType() ) )
			return createImagePlus( img, img.getName(), COLOR_RGB, getDim3( getStandardDimensions() ), new int[ img.getNumDimensions() ] );
		else
			return createImagePlus( img, img.getName(), GRAY32, getDim3( getStandardDimensions() ), new int[ img.getNumDimensions() ] );
	}

	public static <T extends Type<T>> ImagePlus copyToImagePlus( final Image<T> img, final int type )
	{
		return createImagePlus( img, img.getName(), type, getDim3( getStandardDimensions() ), new int[ img.getNumDimensions() ] );
	}

	public static <T extends Type<T>> ImagePlus copyToImagePlus( final Image<T> img, final int[] dim )
	{
		return createImagePlus( img, img.getName(), GRAY32, getDim3(dim), new int[ img.getNumDimensions() ] );
	}

	public static <T extends Type<T>> ImagePlus copyToImagePlus( final Image<T> img, final int type, final int[] dim )
	{
		return createImagePlus( img, img.getName(), type, getDim3(dim), new int[ img.getNumDimensions() ] );
	}

	public static <T extends Type<T>> ImagePlus copyToImagePlus( final Image<T> img, final int type, final int[] dim, final int[] dimensionPositions )
	{
		return createImagePlus( img, img.getName(), type, getDim3(dim), dimensionPositions );
	}

	protected static int[] getStandardDimensions()
	{
		final int[] dim = new int[ 3 ];
		dim[ 0 ] = 0;
		dim[ 1 ] = 1;
		dim[ 2 ] = 2;

		return dim;
	}

	protected static int[] getDim3( int[] dim )
	{
		int[] dimReady = new int[ 3 ];

		dimReady[ 0 ] = -1;
		dimReady[ 1 ] = -1;
		dimReady[ 2 ] = -1;

		for ( int d = 0; d < Math.min( dim.length, dimReady.length ) ; d++ )
			dimReady[ d ] = dim[ d ];

		return dimReady;
	}

	public static <T extends Type<T>> boolean saveAsTiffs( final Image<T> img, String directory, final int type )
	{
		return saveAsTiffs( img, directory, img.getName(), type );
	}

	public static <T extends Type<T>> boolean saveAsTiffs( final Image<T> img, String directory, final String name, final int type )
	{
		final Display<T> display = img.getDisplay();
		boolean everythingOK = true;

		if ( directory == null )
			directory = "";

		directory = directory.replace('\\', '/');
		directory = directory.trim();
		if (directory.length() > 0 && !directory.endsWith("/"))
			directory = directory + "/";

		final int numDimensions = img.getNumDimensions();

		final int[] dimensionPositions = new int[ numDimensions ];

		// x dimension for save is x
		final int dimX = 0;
		// y dimensins for save is y
		final int dimY = 1;

		if ( numDimensions <= 2 )
		{
			final ImageProcessor ip = extract2DSlice( img, display,type, dimX, dimY, dimensionPositions );
			final ImagePlus slice = new ImagePlus( name + ".tif", ip);
        	final FileSaver fs = new FileSaver( slice );
        	everythingOK = everythingOK && fs.saveAsTiff(directory + slice.getTitle());

        	slice.close();
		}
		else // n dimensions
		{
			final int extraDimensions[] = new int[ numDimensions - 2 ];
			final int extraDimPos[] = new int[ extraDimensions.length ];

			for ( int d = 2; d < numDimensions; ++d )
				extraDimensions[ d - 2 ] = img.getDimension( d );

			// the max number of digits for each dimension
			final int maxLengthDim[] = new int[ extraDimensions.length ];

			for ( int d = 2; d < numDimensions; ++d )
			{
				final String num = "" + (img.getDimension( d ) - 1);
				maxLengthDim[ d - 2 ] = num.length();
			}

			//
			// Here we "misuse" a ArrayLocalizableCursor to iterate through the dimensions (d > 2),
			// he will iterate all dimensions as we want ( iterate through d=3, inc 4, iterate through 3, inc 4, ... )
			//
			final ArrayLocalizableCursor<FakeType> cursor = ArrayLocalizableCursor.createLinearCursor( extraDimensions );

			while ( cursor.hasNext() )
			{
				cursor.fwd();
				cursor.getPosition( extraDimPos );

				for ( int d = 2; d < numDimensions; ++d )
					dimensionPositions[ d ] = extraDimPos[ d - 2 ];

				final ImageProcessor ip = extract2DSlice( img, display,type, dimX, dimY, dimensionPositions );

	        	String desc = "";

				for ( int d = 2; d < numDimensions; ++d )
				{
		        	String descDim = "" + dimensionPositions[ d ];
		        	while( descDim.length() < maxLengthDim[ d - 2 ] )
		        		descDim = "0" + descDim;

		        	desc = desc + "_" + descDim;
				}

	        	final ImagePlus slice = new ImagePlus( name + desc + ".tif", ip);

	        	final FileSaver fs = new FileSaver( slice );
	        	everythingOK = everythingOK && fs.saveAsTiff(directory + slice.getTitle());

	        	slice.close();

			}
		}

		return everythingOK;
	}


	protected static <T extends Type<T>> ImageProcessor extract2DSlice( final Image<T> img, final Display<T> display, final int type, final int dimX, final int dimY, final int[] dimensionPositions )
	{
		final ImageProcessor ip;

        switch(type)
        {
        	case ImagePlus.GRAY8:
        		ip = new ByteProcessor( img.getDimension( dimX ), img.getDimension( dimY ), ImageJVirtualStack.extractSliceByte( img, display, dimX, dimY, dimensionPositions ), null); break;
        	case ImagePlus.COLOR_RGB:
        		ip = new ColorProcessor( img.getDimension( dimX ), img.getDimension( dimY ), ImageJVirtualStack.extractSliceRGB( img, display, dimX, dimY, dimensionPositions )); break;
        	default:
        		ip = new FloatProcessor( img.getDimension( dimX ), img.getDimension( dimY ), ImageJVirtualStack.extractSliceFloat( img, display, dimX, dimY, dimensionPositions ), null);
        		ip.setMinAndMax( display.getMin(), display.getMax() );
        		break;
        }

        return ip;
	}


	protected static <T extends Type<T>>ImagePlus createImagePlus( final Image<T> img, final String name, final int type, final int[] dim, final int[] dimensionPositions )
	{
		final Display<T> display = img.getDisplay();

		int[] size = new int[ 3 ];
		size[ 0 ] = img.getDimension( dim[ 0 ] );
		size[ 1 ] = img.getDimension( dim[ 1 ] );
		size[ 2 ] = img.getDimension( dim[ 2 ] );

        final ImageStack stack = new ImageStack( size[ 0 ], size[ 1 ] );

        final int dimPos[] = dimensionPositions.clone();
        final int dimX = dim[ 0 ];
        final int dimY = dim[ 1 ];
        final int dimZ = dim[ 2 ];

        switch(type)
        {
        	case ImagePlus.GRAY8:
        		for (int z = 0; z < size[ 2 ]; z++)
        		{
        			if ( dimZ < img.getNumDimensions() )
        				dimPos[ dimZ ] = z;

        			ByteProcessor bp = new ByteProcessor( size[ 0 ], size[ 1 ] );
        			bp.setPixels( ImageJVirtualStack.extractSliceByte( img, display, dimX, dimY, dimPos ) );
        			stack.addSlice(""+z, bp);
        		}
        		break;
        	case ImagePlus.COLOR_RGB:
        		for (int z = 0; z < size[ 2 ]; z++)
        		{
        			if ( dimZ < img.getNumDimensions() )
        				dimPos[ dimZ ] = z;

        			ColorProcessor bp = new ColorProcessor( size[ 0 ], size[ 1 ] );
        			bp.setPixels( ImageJVirtualStack.extractSliceRGB( img, display, dimX, dimY, dimPos ) );
        			stack.addSlice(""+z, bp);
        		}
        		break;
        	default:
        		for (int z = 0; z < size[ 2 ]; z++)
        		{
        			if ( dimZ < img.getNumDimensions() )
        				dimPos[ dimZ ] = z;

	    			FloatProcessor bp = new FloatProcessor( size[ 0 ], size[ 1 ] );
	    			bp.setPixels( ImageJVirtualStack.extractSliceFloat( img, display, dimX, dimY, dimPos  ) );
	    			bp.setMinAndMax( display.getMin(), display.getMax() );
	    			stack.addSlice(""+z, bp);
        		}
        		break;
        }

        ImagePlus imp =  new ImagePlus( name, stack );
        //imp.getProcessor().setMinAndMax( img.getDisplay().getMin(), img.getDisplay().getMax() );

        return imp;
	}
}
