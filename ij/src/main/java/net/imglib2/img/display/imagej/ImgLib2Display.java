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

package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import net.imglib2.converter.Converter;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.img.Img;
import net.imglib2.sampler.special.OrthoSliceCursor;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class ImgLib2Display
{
	final public static int GRAY8 = ImagePlus.GRAY8;
	final public static int GRAY32 = ImagePlus.GRAY32;
	final public static int COLOR_RGB = ImagePlus.COLOR_RGB;

	public static ImagePlus copyToImagePlus( final Img<FloatType> container, final int[] dim )
	{
		return createImagePlus( container, new TypeIdentity<FloatType>(), "image", GRAY32, getDim3( dim ), new long[ container.numDimensions() ] ); 		
	}
	
	public static ImagePlus copyToImagePlus( final Img<FloatType> container )
	{
		return createImagePlus( container, new TypeIdentity<FloatType>(), "image", GRAY32, getDim3( getStandardDimensions() ), new long[ container.numDimensions() ] ); 
	}
	
	public static <T extends Type<T>> ImagePlus copyToImagePlus( final Img<T> container, final Converter<T, FloatType> converter )
	{
		return createImagePlus( container, converter, "image", GRAY32, getDim3( getStandardDimensions() ), new long[ container.numDimensions() ] ); 
	}
	
	protected static <T extends Type<T>>ImagePlus createImagePlus( final Img<T> container, final Converter<T, FloatType> converter, 
			final String name, final int type, final int[] dim, final long[] dimensionPositions )
	{	      
		final int n = container.numDimensions();
		
		final int[] size = new int[ 3 ];		
		size[ 0 ] = (int) container.dimension( dim[ 0 ] );
		size[ 1 ] = (int) container.dimension( dim[ 1 ] );
		size[ 2 ] = (int) container.dimension( dim[ 2 ] );
        
        final ImageStack stack = new ImageStack( size[ 0 ], size[ 1 ] );
        
        final long dimPos[] = dimensionPositions.clone();
        final int dimX = dim[ 0 ];
        final int dimY = dim[ 1 ];
        final int dimZ = dim[ 2 ];
 		
		for (int z = 0; z < size[ 2 ]; z++)
		{
			if ( dimZ < n )
				dimPos[ dimZ ] = z;
			
			FloatProcessor bp = new FloatProcessor( size[ 0 ], size[ 1 ] );        			
			bp.setPixels( extractSliceFloat( container, converter, dimX, dimY, dimPos  ) );
			//bp.setMinAndMax( display.getMin(), display.getMax() );
			stack.addSlice(""+z, bp);
		}        		
        
        ImagePlus imp =  new ImagePlus( name, stack );
        //imp.getProcessor().setMinAndMax( img.getDisplay().getMin(), img.getDisplay().getMax() );
        
        return imp;
	}		
	
    public static <T extends Type<T>> float[] extractSliceFloat( final Img<T> container, final Converter<T, FloatType> converter,
    		final int dimX, final int dimY, final long[] dimensionPositions )
    {
		final int sizeX = (int) container.dimension( dimX );
		final int sizeY = (int) container.dimension( dimY );
    	
    	final OrthoSliceCursor< T > cursor = new OrthoSliceCursor<T>( container, dimX, dimY, dimensionPositions);
    		//new OrthoSliceCursor<T>( container, dimX, dimY, dimensionPositions ); 
		final FloatType out = new FloatType();
		
		// store the slice image
    	float[] sliceImg = new float[ sizeX * sizeY ];
    	
    	if ( dimY < container.numDimensions() )
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();
	    		
	    		converter.convert( cursor.get(), out );	    		
	    		sliceImg[ cursor.getIntPosition( dimX ) + cursor.getIntPosition( dimY ) * sizeX ] = out.get();  
	    	}
    	}
    	else // only a 1D image
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();

	    		converter.convert( cursor.get(), out );	    		
	    		sliceImg[ cursor.getIntPosition( dimX ) ] = out.get();    		
	    	}    		
    	}

    	return sliceImg;
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
	
}
