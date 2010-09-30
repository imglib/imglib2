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
import mpicbg.imglib.algorithm.math.function.Converter;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.Type;

public class ImageConverter< S extends Type<S>, T extends Type<T> > implements OutputAlgorithm<T>, Benchmark
{
	final Image<S> image; 
	final Image<T> output;
	final Converter<S,T> converter;

	long processingTime;
	String errorMessage = "";
	
	public ImageConverter( final Image<S> image, final Image<T> output, final Converter<S,T> converter )
	{
		this.image = image;
		this.output = output;
		this.converter = converter;
	}
	
	public ImageConverter( final Image<S> image, final ImageFactory<T> factory, final Converter<S,T> converter )
	{
		this( image, createImageFromFactory( factory, image.getDimensions() ), converter );
	}
	
	@Override
	public Image<T> getResult() { return output; }

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image == null )
		{
			errorMessage = "ImageCalculator: [Image<S> image1] is null.";
			return false;
		}
		else if ( output == null )
		{
			errorMessage = "ImageCalculator: [Image<T> output] is null.";
			return false;
		}
		else if ( converter == null )
		{
			errorMessage = "ImageCalculator: [Converter<S,T>] is null.";
			return false;
		}
		else if ( !image.getContainer().compareStorageContainerDimensions( output.getContainer() ) )
		{
			errorMessage = "ImageCalculator: Images have different dimensions, not supported:" + 
				" Image: " + MathLib.printCoordinates( image.getDimensions() ) + 
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
		if ( image.getContainer().compareStorageContainerCompatibility( output.getContainer() ) )
		{
			// we can simply use iterators
			final Cursor<S> cursorIn = image.createCursor();
			final Cursor<T> cursorOut = output.createCursor();
			
			while ( cursorOut.hasNext() )
			{
				cursorIn.fwd();
				cursorOut.fwd();
				
				converter.convert( cursorIn.getType(), cursorOut.getType() );
			}
			
			cursorIn.close();
			cursorOut.close();
		}
		else
		{
			// we need a combination of Localizable and LocalizableByDim
			final LocalizableByDimCursor<S> cursorIn = image.createLocalizableByDimCursor();
			final LocalizableCursor<T> cursorOut = output.createLocalizableCursor();
			
			while ( cursorOut.hasNext() )
			{
				cursorOut.fwd();
				cursorIn.setPosition( cursorOut );
				
				converter.convert( cursorIn.getType(), cursorOut.getType() );
			}
			
			cursorIn.close();
			cursorOut.close();			
		}
		
		processingTime = System.currentTimeMillis() - startTime;
        
		return true;
	}

	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public long getProcessingTime() { return processingTime; }
	
	protected static <T extends Type<T>> Image<T> createImageFromFactory( final ImageFactory<T> factory, final int[] size )
	{
		if ( factory == null || size == null )
			return null;
		else 
			return factory.createImage( size );			
	}
}
