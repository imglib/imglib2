/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch
 */
package mpicbg.imglib.algorithm;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.type.Type;

public class CanvasImage<T extends Type<T>> implements OutputAlgorithm<T>, Benchmark
{
	final Image<T> input;
	final Image<T> output;
	final OutOfBoundsStrategyFactory<T> outOfBoundsFactory;
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
	 * @param outOfBoundsFactory - what to do when extending the image
	 */
	public CanvasImage( final Image<T> input, final int[] newSize, final int[] offset, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory )
	{
		this.input = input;
		this.outOfBoundsFactory = outOfBoundsFactory;
		this.numDimensions = input.numDimensions();
		
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
				if ( outOfBoundsFactory == null && offset[ d ] < 0 )
					errorMessage = "no OutOfBoundsStrategyFactory given but image size should increase, that is not possible";

			if ( errorMessage.length() == 0 )
				this.output = input.createNewImage( newSize );
			else
				this.output = null;
		}
	}
	
	public int[] getOffset() { return offset.clone(); }
	
	public CanvasImage( final Image<T> input, final int[] newSize, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory )
	{		
		this( input, newSize, computeOffset(input, newSize), outOfBoundsFactory ); 
	}
	
	private static int[] computeOffset( final Image<?> input, final int[] newSize )
	{
		final int offset[] = new int[ input.numDimensions() ];
		
		for ( int d = 0; d < input.numDimensions(); ++d )
			offset[ d ] = ( input.getDimension( d ) - newSize[ d ] ) / 2;
		
		return offset;
	}
	
	
	/**
	 * This constructor can be called if the image is only cropped, then there is no {@link OutOfBoundsStrategyFactory} necessary.
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

		final RasterIterator<T> outputCursor = output.createLocalizingRasterIterator();
		final PositionableRasterSampler<T> inputCursor;
		
		if ( outOfBoundsFactory == null)
			inputCursor = input.createPositionableRasterSampler( );
		else
			inputCursor = input.createPositionableRasterSampler( outOfBoundsFactory );

		while ( outputCursor.hasNext() )
		{
			outputCursor.fwd();
			outputCursor.localize( location );
			
			for ( int d = 0; d < numDimensions; ++d )
				location[ d ] += offset[ d ];
			
			inputCursor.setPosition( location );
			outputCursor.type().set( inputCursor.type() );
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
