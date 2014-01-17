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
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.legacy.transform;

import mpicbg.models.AffineModel2D;
import mpicbg.models.AffineModel3D;
import mpicbg.models.Boundable;
import mpicbg.models.InvertibleCoordinateTransform;
import mpicbg.models.NoninvertibleModelException;
import mpicbg.models.RigidModel2D;
import mpicbg.models.TranslationModel2D;
import mpicbg.models.TranslationModel3D;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ImageTransform<T extends Type<T>> implements OutputAlgorithm<Img<T>>
{
	final protected InvertibleCoordinateTransform transform;
	final protected Boundable transformAsBoundable;
	final protected Img<T> img;
	final protected int numDimensions;
	protected InterpolatorFactory<T,RandomAccessible<T>> interpolatorFactory;
	protected OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBoundsFactory;
	final protected boolean isAffine;

	ImgFactory<T> outputImageFactory;

	final int[] newDim;
	final float[] offset;

	Img<T> transformed;
	String errorMessage = "";

	public < BT extends InvertibleCoordinateTransform & Boundable >ImageTransform( final Img<T> img, final BT transform, final InterpolatorFactory<T,RandomAccessible<T>> interpolatorFactory, final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBoundsFactory )
	{
		this.img = img;
		this.interpolatorFactory = interpolatorFactory;
		this.outOfBoundsFactory = outOfBoundsFactory;
		this.numDimensions = img.numDimensions();
		this.transform = transform;
		this.transformAsBoundable = transform;
		this.outputImageFactory = img.factory();

		if ( transform instanceof AffineModel3D ||
			 transform instanceof AffineModel2D ||
			 transform instanceof TranslationModel3D ||
			 transform instanceof TranslationModel2D ||
			 transform instanceof RigidModel2D )
				isAffine = true;
			else
				isAffine = false;

		// get image dimensions
		final int[] dimensions = new int[ numDimensions ];
		for ( int d = 0; d < numDimensions; ++d )
			dimensions[ d ] = (int)img.dimension( d );

		//
		// first determine new min-max in all dimensions of the image
		// by transforming all the corner-points
		//
		final float[] min = new float[ numDimensions ];
		final float[] max = new float[ numDimensions ];
		for ( int i = 0; i < numDimensions; ++i )
			max[ i ] = dimensions[ i ] - 1;
		
		transformAsBoundable.estimateBounds( min, max );
		
		offset = new float[ numDimensions ];

		// get the final size for the new image
		newDim = new int[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			newDim[ d ] = ( int )( max[ d ] - min[ d ] + 1.0f );
			offset[ d ] = min[ d ];
		}
	}

	public float[] getOffset() { return offset.clone(); }
	public float getOffset( final int dim ) { return offset[ dim ]; }
	public void setOffset( final int dim, final int size ) { offset[ dim ] = size; }
	public void setOffset( final float[] offset )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.offset[ d ] = offset[ d ];
	}

	public int[] getNewImageSize() { return newDim.clone(); }
	public float getNewImageSize( final int dim ) { return newDim[ dim ]; }
	public void setNewImageSize( final int dim, final int size ) { newDim[ dim ] = size; }
	public void setNewImageSize( final int[] newDim )
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
		else if ( img == null )
		{
			errorMessage = "AffineTransform: [Image<T> img] is null.";
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
		transformed = outputImageFactory.create( newDim, img.firstElement() );

		final Cursor<T> transformedIterator = transformed.localizingCursor();

		final RandomAccessible< T > r1 = Views.extend( img, outOfBoundsFactory );
		final RealRandomAccessible< T > r2 = Views.interpolate( r1, interpolatorFactory );
		final RealRandomAccess< T > interpolator = r2.realRandomAccess(); 

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

				// does the same, but for affine typically slower
				// interpolator.setPosition( tmp );

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
