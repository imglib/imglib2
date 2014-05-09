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

package net.imglib2.algorithm.labeling;

import java.util.List;
import java.util.PriorityQueue;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingOutOfBoundsRandomAccessFactory;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

/**
 * Watershed algorithms. The watershed algorithm segments and labels an image
 * using an analogy to a landscape. The image intensities are turned into the
 * z-height of the landscape and the landscape is "filled with water" and the
 * bodies of water label the landscape's pixels. Here is the reference for the
 * original paper:
 * 
 * Lee Vincent, Pierre Soille, Watersheds in digital spaces: An efficient
 * algorithm based on immersion simulations, IEEE Trans. Pattern Anal. Machine
 * Intell., 13(6) 583-598 (1991)
 * 
 * Watersheds are often performed on the gradient of an intensity image or one
 * where the edges of the object boundaries have been enhanced. The resulting
 * image has a depressed object interior and a ridge which constrains the
 * watershed boundary.
 * 
 * @author Lee Kamentsky
 */
public class Watershed< T extends RealType< T >, L extends Comparable< L >> implements OutputAlgorithm< Labeling< L >>
{

	protected static class PixelIntensity< U extends Comparable< U >> implements Comparable< PixelIntensity< U >>
	{
		protected final long index;

		protected final long age;

		protected final double intensity;

		protected final List< U > labeling;

		public PixelIntensity( final long[] position, final long[] dimensions, final double intensity, final long age, final List< U > labeling )
		{
			long index = position[ 0 ];
			long multiplier = dimensions[ 0 ];
			for ( int i = 1; i < dimensions.length; i++ )
			{
				index += position[ i ] * multiplier;
				multiplier *= dimensions[ i ];
			}

			this.index = index;
			this.intensity = intensity;
			this.labeling = labeling;
			this.age = age;
		}

		@Override
		public int compareTo( final PixelIntensity< U > other )
		{
			int result = Double.compare( intensity, other.intensity );
			if ( result == 0 )
				result = Double.compare( age, other.age );
			return result;
		}

		void getPosition( final long[] position, final long[] dimensions )
		{
			long idx = index;
			for ( int i = 0; i < dimensions.length; i++ )
			{
				position[ i ] = ( int ) ( idx % dimensions[ i ] );
				idx /= dimensions[ i ];
			}
		}

		List< U > getLabeling()
		{
			return labeling;
		}
	}

	protected RandomAccessibleInterval< T > image;

	protected Labeling< L > seeds;

	long[][] structuringElement;

	protected Labeling< L > output;

	String errorMessage;

	/**
	 * Provide the intensity image to be watershedded.
	 * 
	 * @param image
	 *            the intensity image that defines the watershed landscape.
	 *            Lower values will be labeled first.
	 */
	public void setIntensityImage( final RandomAccessibleInterval< T > image )
	{
		this.image = image;
	}

	/**
	 * Provide the seeds that mark the watersheds.
	 * 
	 * @param seeds
	 *            a labeling of the space, defining the first pixels in the
	 *            space to be labeled. The seeded pixels will be similarly
	 *            labeled in the output as will be their watershed neighbors.
	 */
	public void setSeeds( final Labeling< L > seeds )
	{
		this.seeds = seeds;
	}

	/**
	 * Set the structuring element that defines the connectivity
	 * 
	 * @param structuringElement
	 *            an array of offsets where each element of the array gives the
	 *            offset of a connected pixel from a pixel of interest. You can
	 *            use AllConnectedComponents.getStructuringElement to get an
	 *            8-connected (or N-dimensional equivalent) structuring element
	 *            (all adjacent pixels + diagonals).
	 */
	public void setStructuringElement( final long[][] structuringElement )
	{
		this.structuringElement = structuringElement;
	}

	/**
	 * Set the output labeling where the results will be stored. The class will
	 * provide one if none is supplied.
	 * 
	 * @param outputLabeling
	 */
	public void setOutputLabeling( final Labeling< L > outputLabeling )
	{
		output = outputLabeling;
	}

	/**
	 * The seeded watershed uses a pre-existing labeling of the space where the
	 * labels act as seeds for the output watershed. The analogy would be to use
	 * dyed liquids emanating from the seeded pixels, flowing to the local
	 * minima and then filling individual watersheds until the liquids meet at
	 * the boundaries.
	 * 
	 * This implementation breaks ties by assigning the pixel to the label that
	 * occupied an adjacent pixel first.
	 */
	@Override
	public boolean process()
	{
		if ( !checkInput() )
			return false;

		if ( output == null )
		{
			final long[] dimensions = new long[ seeds.numDimensions() ];
			seeds.dimensions( dimensions );
			final NativeImgLabeling< L, IntType > o = new NativeImgLabeling< L, IntType >( new ArrayImgFactory< IntType >().create( dimensions, new IntType() ) );
			output = o;
		}
		/*
		 * Make an OutOfBounds for the labels that returns empty labels if out
		 * of bounds. Make an OutOfBounds for the intensities that returns the
		 * maximum intensity if out of bounds so that in-bounds will be in a
		 * deep valley.
		 */
		final OutOfBoundsFactory< LabelingType< L >, Labeling< L >> factory = new LabelingOutOfBoundsRandomAccessFactory< L, Labeling< L >>();
		final OutOfBounds< LabelingType< L >> outputAccess = factory.create( output );

		final T maxVal = Views.iterable( image ).firstElement().createVariable();
		maxVal.setReal( maxVal.getMaxValue() );
		final OutOfBoundsFactory< T, RandomAccessibleInterval< T >> oobImageFactory = new OutOfBoundsConstantValueFactory< T, RandomAccessibleInterval< T >>( maxVal );
		final OutOfBounds< T > imageAccess = oobImageFactory.create( image );

		/*
		 * Start by loading up a priority queue with the seeded pixels
		 */
		final PriorityQueue< PixelIntensity< L >> pq = new PriorityQueue< PixelIntensity< L >>();
		final Cursor< LabelingType< L >> c = seeds.localizingCursor();

		final long[] dimensions = new long[ image.numDimensions() ];
		output.dimensions( dimensions );
		final long[] position = new long[ image.numDimensions() ];
		final long[] destPosition = new long[ image.numDimensions() ];
		long age = 0;

		while ( c.hasNext() )
		{
			final LabelingType< L > tSrc = c.next();
			List< L > l = tSrc.getLabeling();
			if ( l.isEmpty() )
				continue;

			c.localize( position );
			imageAccess.setPosition( position );
			if ( imageAccess.isOutOfBounds() )
				continue;
			outputAccess.setPosition( position );
			if ( outputAccess.isOutOfBounds() )
				continue;
			final LabelingType< L > tDest = outputAccess.get();
			l = tDest.intern( l );
			tDest.setLabeling( l );
			final double intensity = imageAccess.get().getRealDouble();
			pq.add( new PixelIntensity< L >( position, dimensions, intensity, age++, l ) );
		}
		/*
		 * Rework the structuring element into a series of consecutive offsets
		 * so we can use Positionable.move to scan the image array.
		 */
		final long[][] strelMoves = new long[ structuringElement.length ][];
		final long[] currentOffset = new long[ image.numDimensions() ];
		for ( int i = 0; i < structuringElement.length; i++ )
		{
			strelMoves[ i ] = new long[ image.numDimensions() ];
			for ( int j = 0; j < image.numDimensions(); j++ )
			{
				strelMoves[ i ][ j ] = structuringElement[ i ][ j ] - currentOffset[ j ];
				if ( i > 0 )
					currentOffset[ j ] += structuringElement[ i ][ j ] - structuringElement[ i - 1 ][ j ];
				else
					currentOffset[ j ] += structuringElement[ i ][ j ];
			}
		}
		/*
		 * Pop the head of the priority queue, label and push all unlabeled
		 * connected pixels.
		 */
		while ( !pq.isEmpty() )
		{
			final PixelIntensity< L > currentPI = pq.remove();
			final List< L > l = currentPI.getLabeling();
			currentPI.getPosition( position, dimensions );
			outputAccess.setPosition( position );
			imageAccess.setPosition( position );
			for ( final long[] offset : strelMoves )
			{
				outputAccess.move( offset );
				imageAccess.move( offset );
				if ( outputAccess.isOutOfBounds() )
					continue;
				if ( imageAccess.isOutOfBounds() )
					continue;
				final LabelingType< L > outputLabelingType = outputAccess.get();
				if ( !outputLabelingType.getLabeling().isEmpty() )
					continue;
				outputLabelingType.setLabeling( l );
				final double intensity = imageAccess.get().getRealDouble();
				outputAccess.localize( destPosition );
				pq.add( new PixelIntensity< L >( destPosition, dimensions, intensity, age++, l ) );
			}
		}
		return true;
	}

	@Override
	public boolean checkInput()
	{
		if ( seeds == null )
		{
			errorMessage = "The seed labeling was not provided. Call \"setSeeds\" to do this";
			return false;
		}
		if ( image == null )
		{
			errorMessage = "The intensity image was not provided. Call \"setIntensityImage\" to do this";
			return false;
		}
		if ( seeds.numDimensions() != image.numDimensions() )
		{
			errorMessage = String.format( "The dimensionality of the seed labeling (%dD) does not match that of the intensity image (%dD)", seeds.numDimensions(), image.numDimensions() );
			return false;
		}
		if ( ( output != null ) && ( seeds.numDimensions() != output.numDimensions() ) )
		{
			errorMessage = String.format( "The dimensionality of the seed labeling (%dD) does not match that of the output labeling (%dD)", seeds.numDimensions(), output.numDimensions() );
			return false;
		}
		if ( structuringElement == null )
			structuringElement = AllConnectedComponents.getStructuringElement( image.numDimensions() );
		for ( int i = 0; i < structuringElement.length; i++ )
		{
			if ( structuringElement[ i ].length != seeds.numDimensions() )
			{
				errorMessage = "Some or all of the structuring element offsets do not have the same number of dimensions as the image";
				return false;
			}
		}
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public Labeling< L > getResult()
	{
		return output;
	}
}
