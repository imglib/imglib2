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
package mpicbg.imglib.algorithm.math;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.function.Function;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.Type;

public class ImageCalculator<S extends Type<S>, T extends Type<T>, U extends Type<U>> implements OutputAlgorithm<U>, Benchmark
{
	final Image<S> image1; 
	final Image<T> image2; 
	final Image<U> output;
	final Function<S,T,U> function;

	long processingTime;
	String errorMessage = "";
	
	public ImageCalculator( final Image<S> image1, final Image<T> image2, final Image<U> output, final Function<S,T,U> function )
	{
		this.image1 = image1;
		this.image2 = image2;
		this.output = output;
		this.function = function;
	}
	
	public ImageCalculator( final Image<S> image1, final Image<T> image2, final ImageFactory<U> factory, final Function<S,T,U> function )
	{
		this( image1, image2, createFromFactory( factory, image1.getDimensions() ), function );
	}
	
	@Override
	public Image<U> getResult() { return output; }

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image1 == null )
		{
			errorMessage = "ImageCalculator: [Image<S> image1] is null.";
			return false;
		}
		else if ( image2 == null )
		{
			errorMessage = "ImageCalculator: [Image<T> image2] is null.";
			return false;
		}
		else if ( output == null )
		{
			errorMessage = "ImageCalculator: [Image<U> output] is null.";
			return false;
		}
		else if ( function == null )
		{
			errorMessage = "ImageCalculator: [Function<S,T,U>] is null.";
			return false;
		}
		else if ( !image1.getContainer().compareStorageContainerDimensions( image2.getContainer() ) || 
				  !image1.getContainer().compareStorageContainerDimensions( output.getContainer() ) )
		{
			errorMessage = "ImageCalculator: Images have different dimensions, not supported:" + 
				" Image1: " + MathLib.printCoordinates( image1.getDimensions() ) + 
				" Image2: " + MathLib.printCoordinates( image2.getDimensions() ) +
				" Output: " + MathLib.printCoordinates( output.getDimensions() );
			return false;
		}
		else
			return true;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
        
		// check if all container types are comparable so that we can use simple iterators
		// we assume transivity here
		if ( image1.getContainer().compareStorageContainerCompatibility( image2.getContainer() ) &&
			 image1.getContainer().compareStorageContainerCompatibility( output.getContainer() ) )
		{
			// we can simply use iterators
			final Cursor<S> cursor1 = image1.createCursor();
			final Cursor<T> cursor2 = image2.createCursor();
			final Cursor<U> cursorOut = output.createCursor();
			
			while ( cursor1.hasNext() )
			{
				cursor1.fwd();
				cursor2.fwd();
				cursorOut.fwd();
				
				function.compute( cursor1.getType(), cursor2.getType(), cursorOut.getType() );
			}
			
			cursor1.close();
			cursor2.close();
			cursorOut.close();
		}
		else
		{
			// we need a combination of Localizable and LocalizableByDim
			final LocalizableByDimCursor<S> cursor1 = image1.createLocalizableByDimCursor();
			final LocalizableByDimCursor<T> cursor2 = image2.createLocalizableByDimCursor();
			final LocalizableCursor<U> cursorOut = output.createLocalizableCursor();
			
			while ( cursorOut.hasNext() )
			{
				cursorOut.fwd();
				cursor1.setPosition( cursorOut );
				cursor2.setPosition( cursorOut );
				
				function.compute( cursor1.getType(), cursor2.getType(), cursorOut.getType() );
			}
			
			cursor1.close();
			cursor2.close();
			cursorOut.close();			
		}
		
		processingTime = System.currentTimeMillis() - startTime;
        
		return false;
	}

	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public long getProcessingTime() { return processingTime; }
	
	protected static <U extends Type<U>> Image<U> createFromFactory( final ImageFactory<U> factory, final int[] size )
	{
		if ( factory == null || size == null )
			return null;
		else 
			return factory.createImage( size );			
	}
}
