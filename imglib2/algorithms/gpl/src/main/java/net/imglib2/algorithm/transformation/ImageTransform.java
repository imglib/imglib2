/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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

package net.imglib2.algorithm.transformation;

import mpicbg.models.InvertibleBoundable;
import mpicbg.models.NoninvertibleModelException;
import net.imglib2.Cursor;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.type.Type;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ImageTransform<T extends Type<T>> implements OutputAlgorithm<Img<T>>
{
	final InvertibleBoundable transform;
	final ExtendedRandomAccessibleInterval<T, Img<T>> container;
	final int numDimensions;
	final InterpolatorFactory<T,ExtendedRandomAccessibleInterval<T, Img<T>>> interpolatorFactory;
	
	ImgFactory<T> outputContainerFactory;
	
	final long[] newDim;
	final float[] offset;
	
	Img<T> transformed;
	String errorMessage = "";
		
	public ImageTransform( final ExtendedRandomAccessibleInterval<T, Img<T>> container, final InvertibleBoundable transform, final InterpolatorFactory<T,ExtendedRandomAccessibleInterval<T, Img<T>>> interpolatorFactory )
	{
		this.container = container;
		this.interpolatorFactory = interpolatorFactory;
		this.numDimensions = container.numDimensions();
		this.transform = transform;		

		//
		// first determine new min-max in all dimensions of the image
		// by transforming all the corner-points
		//	
		
		float[] min = new float[ numDimensions ];
		float[] max = new float[ numDimensions ];

		final Img<T> source = container.getSource();
		this.outputContainerFactory = source.factory();
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			min[ d ] = (float) source.realMin( d );
			max[ d ] = (float) source.realMax( d ); 
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
	
	public void setOutputContainerFactory( final ImgFactory<T> outputContainerFactory ) { this.outputContainerFactory = outputContainerFactory; }
	public ImgFactory<T> getOutputContainerFactory() { return this.outputContainerFactory; }
	
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
		transformed = outputContainerFactory.create( newDim, container.getSource().firstElement().createVariable() );

		final Cursor<T> transformedIterator = transformed.localizingCursor();
		final RealRandomAccess<T> interpolator = interpolatorFactory.create( container );
		
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
