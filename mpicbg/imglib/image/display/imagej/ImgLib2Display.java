package mpicbg.imglib.image.display.imagej;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.converter.Converter;
import mpicbg.imglib.converter.VoidConverter;
import mpicbg.imglib.sampler.special.OrthoSliceIterator;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.real.FloatType;

public class ImgLib2Display
{
	public static ImagePlus copyToImagePlus( final Container<FloatType> container, final int[] dim )
	{
		return createImagePlus( container, new VoidConverter<FloatType>(), "image", ImageJFunctions.GRAY32, getDim3( dim ), new int[ container.numDimensions() ] ); 		
	}
	
	public static ImagePlus copyToImagePlus( final Container<FloatType> container )
	{
		return createImagePlus( container, new VoidConverter<FloatType>(), "image", ImageJFunctions.GRAY32, getDim3( getStandardDimensions() ), new int[ container.numDimensions() ] ); 
	}
	
	public static <T extends Type<T>> ImagePlus copyToImagePlus( final Container<T> container, final Converter<T, FloatType> converter )
	{
		return createImagePlus( container, converter, "image", ImageJFunctions.GRAY32, getDim3( getStandardDimensions() ), new int[ container.numDimensions() ] ); 
	}
	
	protected static <T extends Type<T>>ImagePlus createImagePlus( final Container<T> container, final Converter<T, FloatType> converter, 
			final String name, final int type, final int[] dim, final int[] dimensionPositions )
	{	      
		final int n = container.numDimensions();
		
		final int[] size = new int[ 3 ];		
		size[ 0 ] = (int) container.size( dim[ 0 ] );
		size[ 1 ] = (int) container.size( dim[ 1 ] );
		size[ 2 ] = (int) container.size( dim[ 2 ] );
        
        final ImageStack stack = new ImageStack( size[ 0 ], size[ 1 ] );
        
        final int dimPos[] = dimensionPositions.clone();
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
	
    public static <T extends Type<T>> float[] extractSliceFloat( final Container<T> container, final Converter<T, FloatType> converter,
    		final int dimX, final int dimY, final int[] dimensionPositions )
    {
		final int sizeX = (int) container.size( dimX );
		final int sizeY = (int) container.size( dimY );
    	
    	final OrthoSliceIterator< T > cursor = new OrthoSliceIterator<T>( container, dimX, dimY, dimensionPositions ); 
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
