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
package mpi.imglib.algorithm.transformation;

import mpi.imglib.algorithm.OutputAlgorithm;
import mpi.imglib.algorithm.math.MathLib;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.interpolation.Interpolator;
import mpi.imglib.interpolation.InterpolatorFactory;
import mpi.imglib.type.Type;
import mpicbg.models.AffineModel2D;
import mpicbg.models.AffineModel3D;
import mpicbg.models.InvertibleCoordinateTransform;
import mpicbg.models.NoninvertibleModelException;
import mpicbg.models.RigidModel2D;
import mpicbg.models.TranslationModel2D;
import mpicbg.models.TranslationModel3D;

public class ImageTransform<T extends Type<T>> implements OutputAlgorithm<T>
{
	final InvertibleCoordinateTransform transform;
	final Image<T> img;
	final int numDimensions;
	final InterpolatorFactory<T> interpolatorFactory;
	final float[] location;
	final boolean isAffine;
	
	Image<T> transformed;
	String errorMessage = "";
	
	public ImageTransform( final Image<T> img, final InvertibleCoordinateTransform transform, final InterpolatorFactory<T> interpolatorFactory )
	{
		this.img = img;
		this.interpolatorFactory = interpolatorFactory;
		this.numDimensions = img.getNumDimensions();
		this.location = new float[ numDimensions ];
		this.transform = transform;		

		if ( transform instanceof AffineModel3D ||
			 transform instanceof AffineModel2D ||
			 transform instanceof TranslationModel3D ||
			 transform instanceof TranslationModel2D || 
			 transform instanceof RigidModel2D )
				isAffine = true;
			else
				isAffine = false;
	}
	
	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( img == null )
		{
			errorMessage = "AffineTransform: [Image<T> img] is null.";
			return false;
		}
		else if ( interpolatorFactory.getOutsideStrategyFactory() == null )
		{
			errorMessage = "AffineTransform: [OutsideStrategyFactory<T> of interpolatorFactory] is null.";
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
	public Image<T> getResult() { return transformed; }
	

	@Override
	public boolean process()
	{
		if ( !checkInput() )
			return false;

		// get image dimensions
		final int[] dimensions = img.getDimensions();

		//
		// first determine new min-max in all dimensions of the image
		// by transforming all the corner-points
		//	
		final float[][] minMaxDim = MathLib.getMinMaxDim( dimensions, transform );
		
		// get the final size for the new image
		final int[] newDim = new int[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			newDim[ d ] = Math.round( minMaxDim[ d ][ 1 ] ) - Math.round( minMaxDim[ d ][ 0 ] );
		
		// create the new output image
		transformed = img.createNewImage( newDim );

		final LocalizableCursor<T> transformedIterator = transformed.createLocalizableCursor();
		final Interpolator<T> interpolator = img.createInterpolator( interpolatorFactory );

		final T transformedValue = transformedIterator.getType();		
		final T interpolatedValue = interpolator.getType();		
		
		try
		{
			final float[] tmp = new float[ numDimensions ];

			if ( isAffine )
			{
				while (transformedIterator.hasNext())
				{
					transformedIterator.fwd();
		
					// we have to add the offset of our new image
					// relative to it's starting point (0,0,0)
					for ( int d = 0; d < numDimensions; ++d )
						tmp[ d ] = transformedIterator.getPosition( d ) + minMaxDim[ d ][ 0 ];
					
					// transform back into the original image
					// 
					// in order to compute the voxels in the new object we have to apply
					// the inverse transform to all voxels of the new array and interpolate
					// the position in the original image
					transform.applyInverseInPlace( tmp );
					
					interpolator.moveTo( tmp );
					
					// does the same, but for affine typically slower
					// interpolator.setPosition( tmp );
		
					transformedValue.set( interpolatedValue );
				}
			}
			else
			{				
				while (transformedIterator.hasNext())
				{
					transformedIterator.fwd();
		
					// we have to add the offset of our new image
					// relative to it's starting point (0,0,0)
					for ( int d = 0; d < numDimensions; ++d )
						tmp[ d ] = transformedIterator.getPosition( d ) + minMaxDim[ d ][ 0 ];
					
					// transform back into the original image
					// 
					// in order to compute the voxels in the new object we have to apply
					// the inverse transform to all voxels of the new array and interpolate
					// the position in the original image
					transform.applyInverseInPlace( tmp );
					
					interpolator.setPosition( tmp );
		
					transformedValue.set( interpolatedValue );
				}
			}
		
		} 
		catch ( NoninvertibleModelException e )
		{
			transformedIterator.close();
			interpolator.close();
			transformed.close();
			
			errorMessage = "ImageTransform.process(): " + e.getMessage();
			return false;
		}

		transformedIterator.close();
		interpolator.close();
		return true;
	}	
}
