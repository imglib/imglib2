/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.transform.integer.Mixed;
import net.imglib2.transform.integer.MixedTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility methods to create transformation for the common
 * operations that are provided in {@link Views}.
 * <p>
 * Warning: The transformations used in {@link Views} are always
 * inverse to the operations that are performed by the views.
 *
 * @author Tobias Pietzsch
 * @author Carsten Haubold, KNIME GmbH, Konstanz, Germany
 */
public class ViewTransforms
{
	/**
	 * Returns the transformation that is used by
	 * {@link Views#rotate(RandomAccessible, int, int)}.
	 * <p>
	 * Warning: The transformations used in {@link Views} are always
	 * inverse to the operations that are performed by the views.
	 * <p>
	 * The rotation returned by this operation therefore is inverse to the
	 * rotation described in {@link Views#rotate(RandomAccessible, int, int)}
	 */
	public static Mixed rotate( final int numDimensions, final int fromAxis, final int toAxis )
	{
		if ( fromAxis == toAxis )
			return new MixedTransform( numDimensions, numDimensions );

		final MixedTransform t = new MixedTransform( numDimensions, numDimensions );
		if ( fromAxis != toAxis )
		{
			final int[] component = new int[ numDimensions ];
			final boolean[] inv = new boolean[ numDimensions ];
			for ( int e = 0; e < numDimensions; ++e )
			{
				if ( e == toAxis )
				{
					component[ e ] = fromAxis;
					inv[ e ] = true;
				}
				else if ( e == fromAxis )
				{
					component[ e ] = toAxis;
				}
				else
				{
					component[ e ] = e;
				}
			}
			t.setComponentMapping( component );
			t.setComponentInversion( inv );
		}
		return t;
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#permute(RandomAccessible, int, int)}.
	 * <p>
	 * Warning: The transformations used in {@link Views} are always
	 * inverse to the operations that are performed by the views.
	 * But that's not important for this type of permutation. As
	 * the permutation stays the same when inverted.
	 */
	public static Mixed permute( final int numDimensions, final int fromAxis, final int toAxis )
	{
		if ( fromAxis == toAxis )
			return new MixedTransform( numDimensions, numDimensions );

		final int[] component = new int[ numDimensions ];
		for ( int e = 0; e < numDimensions; ++e )
			component[ e ] = e;
		component[ fromAxis ] = toAxis;
		component[ toAxis ] = fromAxis;
		final MixedTransform t = new MixedTransform( numDimensions, numDimensions );
		t.setComponentMapping( component );
		return t;
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#hyperSlice(RandomAccessible, int, long)}.
	 * <p>
	 * Warning: The transformations used in {@link Views} are always
	 * inverse to the operations that are performed by the views.
	 * <p>
	 *
	 * @param numDimensions Number of dimensions including that dimension
	 *                      that is sliced / inserted.
	 * @param d             Index of that dimension that is sliced / inserted.
	 * @param pos           Position of the slice / value of the coordinate that's
	 *                      inserted.
	 * @return Transformation that inserts a coordinate at the given index.
	 */
	public static MixedTransform hyperSlice( final int numDimensions, final int d, final long pos )
	{
		final int n = numDimensions - 1;
		final MixedTransform t = new MixedTransform( n, numDimensions );
		final long[] translation = new long[ numDimensions ];
		translation[ d ] = pos;
		final boolean[] zero = new boolean[ numDimensions ];
		final int[] component = new int[ numDimensions ];
		for ( int e = 0; e < numDimensions; ++e )
		{
			if ( e < d )
			{
				zero[ e ] = false;
				component[ e ] = e;
			}
			else if ( e > d )
			{
				zero[ e ] = false;
				component[ e ] = e - 1;
			}
			else
			{
				zero[ e ] = true;
				component[ e ] = 0;
			}
		}
		t.setTranslation( translation );
		t.setComponentZero( zero );
		t.setComponentMapping( component );
		return t;
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#translate(RandomAccessible, long...)}.
	 * <p>
	 * Warning: The transformation used by a view in {@link Views} is always
	 * inverse to the operation that is performed by the View.
	 * <p>
	 * Therefore this method actually returns the inverse translation.
	 */
	public static MixedTransform translate( final long... translation )
	{
		final int n = translation.length;
		final MixedTransform t = new MixedTransform( n, n );
		t.setInverseTranslation( translation );
		return t;
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#translateInverse(RandomAccessible, long...)}.
	 * <p>
	 * Warning: The transformation used by a view in {@link Views} is always
	 * inverse to the operation that is performed by the View.
	 * <p>
	 * Therefore this method actually returns the (not inverse) translation.
	 */
	public static MixedTransform translateInverse( final long... translation )
	{
		final int n = translation.length;
		final MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( translation );
		return t;
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#moveAxis(RandomAccessible, int, int)}.
	 * <p>
	 * Warning: The transformation used by a view in {@link Views} is always
	 * inverse to the operation that is performed by the View.
	 * <p>
	 * Therefore the axis permutation return by this method
	 * is actually inverse as described in {@link Views#moveAxis(RandomAccessible, int, int)}.
	 */
	public static MixedTransform moveAxis( final int numDimensions, final int fromAxis, final int toAxis )
	{
		if ( fromAxis == toAxis )
			return new MixedTransform( numDimensions, numDimensions );

		final List< Integer > axisIndices = new ArrayList<>();
		IntStream.rangeClosed( 0, numDimensions - 1 ).forEach( axisIndices::add );
		axisIndices.remove( fromAxis );
		axisIndices.add( toAxis, fromAxis );

		final int components[] = new int[ numDimensions ];
		for ( int i = 0; i < numDimensions; i++ )
		{
			components[ axisIndices.get( i ) ] = i;
		}

		final MixedTransform t = new MixedTransform( numDimensions, numDimensions );
		t.setComponentMapping( components );
		return t;
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#zeroMin(RandomAccessibleInterval)}.
	 * <p>
	 * Warning: The transformation used by a view in {@link Views} is always
	 * inverse to the operation that is performed by the View.
	 */
	public static MixedTransform zeroMin( final Interval interval )
	{
		final int n = interval.numDimensions();
		final long[] offset = new long[ n ];
		interval.min( offset );
		final long[] translation = Arrays.stream( offset ).map( o -> -o ).toArray();
		return ViewTransforms.translate( translation );
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#addDimension(RandomAccessible)}.
	 * <p>
	 * Warning: The transformation used by a view in {@link Views} is always
	 * inverse to the operation that is performed by the View.
	 *
	 * @param numDimensions Number of dimensions without
	 *                       the coordinate that's added/removed.
	 * @return A transformation that removes the last coordinate.
	 */
	public static MixedTransform addDimension( final int numDimensions )
	{
		final int newNumDims = numDimensions + 1;
		return new MixedTransform( newNumDims, numDimensions );
	}

	/**
	 * Returns the transformation that is used by
	 * {@link Views#invertAxis(RandomAccessible, int)}.
	 * <p>
	 * Warning: The transformation used by a view in {@link Views} is always
	 * inverse to the operation that is performed by the View.
	 * But that's not relevant here, because inverse of an axis inversion
	 * is the axis inversion itself.
	 * <p>
	 *
	 * @param numDimensions Number of dimensions of the coordinate space.
	 * @param d Index of the coordinate that's inverted.
	 * @return Transformation that inverts the specified coordinate.
	 */
	public static MixedTransform invertAxis( final int numDimensions, final int d )
	{
		final boolean[] inv = new boolean[ numDimensions ];
		inv[ d ] = true;
		final MixedTransform t = new MixedTransform( numDimensions, numDimensions );
		t.setComponentInversion( inv );
		return t;
	}
}
