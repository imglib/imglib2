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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.algorithm.transformation;

import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.ImgIterator;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.type.Type;
import mpicbg.models.InvertibleBoundable;
import mpicbg.models.NoninvertibleModelException;

public class ImageTransform<T extends Type<T>> implements OutputAlgorithm<T>
{
	final InvertibleBoundable transform;
	final Img<T> container;
	final int numDimensions;
	final InterpolatorFactory<T,Img<T>> interpolatorFactory;
	
	ImgFactory outputContainerFactory;
	
	final long[] newDim;
	final float[] offset;
	
	Img<T> transformed;
	String errorMessage = "";
		
	public ImageTransform( final Img<T> container, final InvertibleBoundable transform, final InterpolatorFactory<T,Img<T>> interpolatorFactory )
	{
		this.container = container;
		this.interpolatorFactory = interpolatorFactory;
		this.numDimensions = container.numDimensions();
		this.transform = transform;		
		this.outputContainerFactory = container.factory();

		// get image dimensions
		final long[] dimensions = new long[ numDimensions ]; 
		container.size( dimensions );

		//
		// first determine new min-max in all dimensions of the image
		// by transforming all the corner-points
		//	
		
		float[] min = new float[ numDimensions ];
		float[] max = new float[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			min[ d ] = (float)container.realMin( d ); 
			max[ d ] = (float)container.realMax( d ); 
		}
		transform.estimateBounds( min, max );
		
		offset = new float[ numDimensions ];
		
		// get the final size for the new image
		newDim = new long[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			newDim[ d ] = Math.round( max[ d ] ) - Math.round( min[ d ] );
			offset[ d ] = min[ d ];
		}		
	}
	
	public void setOutputContainerFactory( final ImgFactory outputContainerFactory ) { this.outputContainerFactory = outputContainerFactory; } 
	public ImgFactory getOutputImageFactory() { return this.outputContainerFactory; } 
	
	public float[] getOffset() { return offset; }
	public void setOffset( final float[] offset ) 
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.offset[ d ] = offset[ d ];
	}

	public long[] getNewImageSize() { return newDim; }
	public void setNewImageSize( final long[] newDim ) 
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.newDim[ d ] = newDim[ d ];
	}

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( container == null )
		{
			errorMessage = "AffineTransform: [Container<T> container] is null.";
			return false;
		}
		else if ( interpolatorFactory.getOutOfBoundsStrategyFactory() == null )
		{
			errorMessage = "AffineTransform: [OutOfBoundsStrategyFactory<T> of interpolatorFactory] is null.";
			return false;
		}
		else if ( interpolatorFactory == null )
		{
			errorMessage = "AffineTransform: [InterpolatorFactory<T> interpolatorFactory] is null.";
			return false;
		}
		else if ( transform == null )
		{
			errorMessage = "AffineTransform: [Transform3D transform] or [float[] transform] is null.";
			return false;
		}
		else
			return true;
	}

	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public Img<T> getResult() { return transformed; }
	

	@Override
	public boolean process()
	{
		if ( !checkInput() )
			return false;
		
		// create the new output image
		transformed = outputContainerFactory.create( newDim, container.createVariable() );

		final ImgIterator<T> transformedIterator = transformed.localizingCursor();
		final Interpolator<T,Img<T>> interpolator = interpolatorFactory.create( container );
		
		try
		{
			final float[] tmp = new float[ numDimensions ];

			while (transformedIterator.hasNext())
			{
				transformedIterator.fwd();
	
				// we have to add the offset of our new image
				// relative to it's starting point (0,0,0)
				for ( int d = 0; d < numDimensions; ++d )
					tmp[ d ] = transformedIterator.getIntPosition( d ) + offset[ d ];
				
				// transform back into the original image
				// 
				// in order to compute the voxels in the new object we have to apply
				// the inverse transform to all voxels of the new array and interpolate
				// the position in the original image
				transform.applyInverseInPlace( tmp );
				
				interpolator.setPosition( tmp );
	
				transformedIterator.get().set( interpolator.get() );
			}		
		} 
		catch ( NoninvertibleModelException e )
		{			
			errorMessage = "ImageTransform.process(): " + e.getMessage();
			return false;
		}

		return true;
	}	
}
