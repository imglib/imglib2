/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import net.imglib2.Interval;
import net.imglib2.transform.integer.Mixed;
import net.imglib2.transform.integer.MixedTransform;

/**
 * Utility methods to create mixed transforms for common operations. Used by {@link Views}.
 * 
 * @author Tobias Pietzsch
 * @author Carsten Haubold, KNIME GmbH, Konstanz, Germany
 */
public class MixedTransforms
{
	/**
	 * Create a {@link MixedTransform} that rotates by 90 degrees. The rotation
	 * is specified by two axis indices, such that the {@code fromAxis} is
	 * rotated to the {@code toAxis}.
	 *
	 * For example: {@code rotate(0, 1, 3)} creates a transform
	 * that rotates the X axis (of a XYZ space) to the Y axis. Applying the
	 * transform to <em>(1,2,3)</em> yields <em>(2,-1,3)</em>.
	 *
	 * @param fromAxis
	 *            axis index.
	 * @param toAxis
	 *            axis index.
	 * @param n
	 *            number of dimensions of the space.
	 * @return a transform that rotates the {@code fromAxis} to the
	 *         {@code toAxis}.
	 */
	public static Mixed rotate( final int fromAxis, final int toAxis, final int n )
	{
		if ( fromAxis == toAxis )
			return new MixedTransform(n, n);

		final MixedTransform t = new MixedTransform( n, n );
		if ( fromAxis != toAxis )
		{
			final int[] component = new int[ n ];
			final boolean[] inv = new boolean[ n ];
			for ( int e = 0; e < n; ++e )
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
	 * Create a transformation that permutes axes. fromAxis and toAxis are swapped.
	 *
	 * If fromAxis=0 and toAxis=2, this means that the X-axis of the source view
	 * is mapped to the Z-Axis of the permuted view and vice versa. For a XYZ
	 * source, a ZYX view would be created.
	 * 
	 * @param fromAxis
	 * @param toAxis
	 * @param n
	 * @return
	 */
	public static Mixed permute( final int fromAxis, final int toAxis, final int n )
	{
		if ( fromAxis == toAxis )
			return new MixedTransform(n, n);

		final int[] component = new int[ n ];
		for ( int e = 0; e < n; ++e )
			component[ e ] = e;
		component[ fromAxis ] = toAxis;
		component[ toAxis ] = fromAxis;
		final MixedTransform t = new MixedTransform( n, n );
		t.setComponentMapping( component );
		return t;
	}

	/**
	 * Create a transform that takes a (n-1)-dimensional slice of a n-dimensional view, fixing
	 * d-component of coordinates to pos.
	 *
	 * @param d
	 * @param pos
	 * @param m
	 * @return
	 */
	public static MixedTransform hyperSlice( final int d, final long pos, final int m )
	{
		final int n = m - 1;
		final MixedTransform t = new MixedTransform( n, m );
		final long[] translation = new long[ m ];
		translation[ d ] = pos;
		final boolean[] zero = new boolean[ m ];
		final int[] component = new int[ m ];
		for ( int e = 0; e < m; ++e )
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
	 * Create a {@link MixedTransform} that describes the translation vector. When applied to a View, each pixel
	 * <em>x</em> in the source view has coordinates <em>(x + translation)</em>
	 * in the resulting view.
	 *
	 * @param translation
	 *            translation vector of the source view. The pixel at <em>x</em>
	 *            in the source view becomes <em>(x + translation)</em> in the
	 *            resulting view.
	 */
	public static MixedTransform translate( final long... translation )
	{
		final int n = translation.length;
		final MixedTransform t = new MixedTransform( n, n );
		t.setInverseTranslation( translation );
		return t;
	}

	/**
	 * Return a transformation such that a pixel at offset in a randomAccessible is at the origin
	 * in the resulting view. This is equivalent to translating by -offset.
	 * 
	 * @param offset
	 *            offset of the source view. The pixel at offset becomes the
	 *            origin of resulting view.
	 * @return transformation
	 */
	public static MixedTransform offset(final long... offset )
	{
		final int n = offset.length;
		final MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( offset );
		return t;
	}

	/**
	 * Create a transformation that moves an axis. fromAxis is moved to toAxis. While the
	 * order of the other axes is preserved.
	 *
	 * If fromAxis=2 and toAxis=4, and axis order of image is XYCZT, then
	 * a view to the image with axis order XYZTC would be created.
	 */
	public static MixedTransform moveAxis(final int fromAxis, final int toAxis, final int n) {
		if ( fromAxis == toAxis )
			return new MixedTransform(n, n);

		List<Integer> axisIndices = new ArrayList<>();
		IntStream.rangeClosed(0, n - 1).forEach(axisIndices::add);
		axisIndices.remove(fromAxis);
		axisIndices.add(toAxis, fromAxis);

		int components[] = new int[n];
		for(int i = 0; i < n; i++) {
			components[axisIndices.get(i)] = i;
		}

		final MixedTransform t = new MixedTransform(n, n);
		t.setComponentMapping(components);
		return t;
	}

	/**
	 * Create a transformation that moves the min coordinate of the given interval to the origin
	 *
	 * @param interval
	 *            the source.
	 * @return transformation
	 */
	public static MixedTransform zeroMin( final Interval interval ) {
		final int n = interval.numDimensions();
		final long[] offset = new long[ n ];
		interval.min( offset );
		return MixedTransforms.offset(offset);
	}

	/**
	 * Create a transformation that adds a new dimension at the end
	 * @param currentNumDims
	 * @return 
	 */
	public static MixedTransform addDimension( final int currentNumDims ) {
		final int newNumDims = currentNumDims + 1;
		return new MixedTransform(newNumDims, currentNumDims);
	}

	/**
	 * Create a transform that inverts the d'th axis of an n-dimensional space.
	 * @param d
	 * @param n
	 * @return
	 */
	public static MixedTransform invertAxis( final int d , final int n ) {
		final boolean[] inv = new boolean[ n ];
		inv[ d ] = true;
		final MixedTransform t = new MixedTransform( n, n );
		t.setComponentInversion( inv );
		return t;
	}
}
