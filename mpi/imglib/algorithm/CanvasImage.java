package mpi.imglib.algorithm;

import mpi.imglib.algorithm.Benchmark;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.outside.OutsideStrategyFactory;
import mpi.imglib.type.Type;

public class CanvasImage<T extends Type<T>> implements OutputAlgorithm<T>, Benchmark
{
	final Image<T> input;
	final Image<T> output;
	final OutsideStrategyFactory<T> outsideFactory;
	final int numDimensions;
	final int[] newSize, offset, location;
	
	String errorMessage = "";
	int numThreads;
	long processingTime;
	
	/**
	 * Increase or decrease size of the image in all dimensions
	 * 
	 * @param input - the input image
	 * @param newSize - the size of the new image
	 * @param outsideFactory - what to do when extending the image
	 */
	public CanvasImage( final Image<T> input, final int[] newSize, final int[] offset, final OutsideStrategyFactory<T> outsideFactory )
	{
		this.input = input;
		this.outsideFactory = outsideFactory;
		this.numDimensions = input.getNumDimensions();
		
		this.newSize = newSize.clone();
		this.location = new int[ numDimensions ];
		this.offset = offset;
		this.processingTime = -1;
		
		if ( newSize == null || newSize.length != numDimensions )
		{
			errorMessage = "newSize is invalid: null or not of same dimensionality as input image";
			this.output = null;
		}
		else if ( offset == null || offset.length != numDimensions )
		{
			errorMessage = "offset is invalid: null or not of same dimensionality as input image";
			this.output = null;			
		}
		else
		{
			for ( int d = 0; d < numDimensions; ++d )
				if ( outsideFactory == null && offset[ d ] < 0 )
					errorMessage = "no OutsideStrategyFactory given but image size should increase, that is not possible";

			if ( errorMessage.length() == 0 )
				this.output = input.createNewImage( newSize );
			else
				this.output = null;
		}
	}
	
	public int[] getOffset() { return offset.clone(); }
	
	public CanvasImage( final Image<T> input, final int[] newSize, final OutsideStrategyFactory<T> outsideFactory )
	{		
		this( input, newSize, computeOffset(input, newSize), outsideFactory ); 
	}
	
	private static int[] computeOffset( final Image<?> input, final int[] newSize )
	{
		final int offset[] = new int[ input.getNumDimensions() ];
		
		for ( int d = 0; d < input.getNumDimensions(); ++d )
			offset[ d ] = ( input.getDimension( d ) - newSize[ d ] ) / 2;
		
		return offset;
	}
	
	
	/**
	 * This constructor can be called if the image is only cropped, then there is no {@link OutsideStrategyFactory} necessary.
	 * It will fail if the image size is increased.
	 *   
	 * @param input - the input image
	 * @param newSize - the size of the new image
	 */
	public CanvasImage( final Image<T> input, final int[] newSize )
	{
		this( input, newSize, null );
	}
	
	@Override
	public boolean process() 
	{
		final long startTime = System.currentTimeMillis();

		final LocalizableCursor<T> outputCursor = output.createLocalizableCursor();
		final LocalizableByDimCursor<T> inputCursor;
		
		if ( outsideFactory == null)
			inputCursor = input.createLocalizableByDimCursor( );
		else
			inputCursor = input.createLocalizableByDimCursor( outsideFactory );

		while ( outputCursor.hasNext() )
		{
			outputCursor.fwd();
			outputCursor.getPosition( location );
			
			for ( int d = 0; d < numDimensions; ++d )
				location[ d ] += offset[ d ];
			
			inputCursor.moveTo( location );
			outputCursor.getType().set( inputCursor.getType() );
		}

		outputCursor.close();
		inputCursor.close();

        processingTime = System.currentTimeMillis() - startTime;

        return true;		
	}
	
	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public Image<T> getResult() { return output; }

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( input == null )
		{
			errorMessage = "Input image is null";
			return false;
		}
		else if ( output == null )
		{
			errorMessage = "Output image is null, maybe not enough memory";
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getErrorMessage() 
	{
		if ( errorMessage.length() > 0 )
			errorMessage =  "CanvasImage(): " + errorMessage;
			
		return errorMessage;
	}
	
}
