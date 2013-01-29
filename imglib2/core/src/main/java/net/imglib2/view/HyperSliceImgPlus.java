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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.view;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgPlus;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.transform.integer.MixedTransform;

/**
 * This class offers access to a <code>n-1</code>-dimensional view of a source {@link ImgPlus},
 * obtained by fixing a target dimension to a target position. The source image is wrapped
 * so there is data duplication.
 * <p>
 * Its result is exactly similar to the {@link Views#hyperSlice(RandomAccessible, int, long)}
 * static method, except that the returned class preserves the {@link ImgPlus} capabilities, 
 * namely the metadata and iterability.
 * <p>
 * Internally, we strongly rely on the fantastic {@link Views} class. 
 *  
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 */
public class HyperSliceImgPlus <T> extends ImgPlus<T> {

	/** The number of dimension in the target {@link ImgPlus}. Equals the number of dimensions
	 * in the source image minus one.  */
	protected final int nDimensions;
	/** The source {@link ImgPlus}. */
	protected final ImgPlus< T > source;
	/** The iterable built by wrapping the {@link #fullViewRandomAccessible}. */
	protected final IterableInterval<T> fullViewIterable;
	/** An optimized RandomAccess over the transformed source. */
	protected RandomAccessible< T > fullViewRandomAccessible;
	/** The transformed source. */
	protected final  MixedTransformView<T> mtv;
	/** The dimension to freeze. */
	protected final int targetDimension;
	/** The target freeze-dimension position. */ 
	protected final long dimensionPosition;

	/*
	 * CONSTRUCTOR
	 */

	/**
	 * Wraps a source {@link ImgPlus} in a <code>n-1</code>-dimensional view, by
	 * fixing dimension <code>d</code> at the position <code>pos</code>.
	 * <p>
	 * Errors will be generated if <code>d</code> and/or <code>pos</code> are incorrect. 
	 * 
	 * @param source  the source {@link ImgPlus}
	 * @param d  the dimension to freeze
	 * @param pos  the position at which to hold the target dimension
	 */
	public HyperSliceImgPlus( ImgPlus< T > source, final int d, final long pos ) {
		super(source);

		final int m = source.numDimensions();
		this.nDimensions = m - 1;
		this.targetDimension = d;
		this.dimensionPosition = pos;

		// Prepare reslice
		final long[] min = new long[ nDimensions ];
		final long[] max = new long[ nDimensions ];

		final MixedTransform t = new MixedTransform( nDimensions, m );
		final long[] translation = new long[ m ];
		translation[ d ] = pos;
		final boolean[] zero = new boolean[ m ];
		final int[] component = new int[ m ];

		/* Determine transform component & iterable bounds
		 * and defines calibration of the target ImgPlus	 */
		for ( int e = 0; e < m; ++e ) {
			if ( e < d ) {

				zero[ e ] = false;
				component[ e ] = e;
				min[ e ] = source.min( e );
				max[ e ] = source.max( e );
				setCalibration( source.calibration(e), e);
				setAxis( source.axis(e), e);

			} else if ( e > d ) {

				zero[ e ] = false;
				component[ e ] = e - 1;
				min[ e - 1] = source.min( e );
				max[ e - 1] = source.max( e );
				setCalibration( source.calibration(e), e-1);
				setAxis( source.axis(e), e-1);

			} else {

				zero[ e ] = true;
				component[ e ] = 0;

			}
		}

		// Set target name
		setName("Hypserslice of "+source.getName()+" at dim "+d+"="+pos);

		// Create transform and transformed view
		t.setTranslation( translation );
		t.setComponentZero( zero );
		t.setComponentMapping( component );
		this.mtv = new MixedTransformView<T>(source, t);

		// Copy calibration and axes
		int index = 0;
		for (int i = 0; i < m; i++) {
			if (i != d) {
				setCalibration( source.calibration(i) , index );
				setAxis( source.axis(i), index);
				index++;
			}
		}

		this.source = source;
		this.fullViewRandomAccessible = TransformBuilder.getEfficientRandomAccessible( null, mtv );
		this.fullViewIterable =  Views.iterable( Views.interval(fullViewRandomAccessible, min, max) );
	}

	/*
	 * METHODS
	 */
	
	/**
	 * Return the <code>n</code>-dimensional source of this view.
	 */
	@Override
	public Img<T> getImg() {
		return super.getImg();
	}
	
	
	@Override
	public RandomAccess< T > randomAccess( final Interval interval ) {
		return TransformBuilder.getEfficientRandomAccessible( interval, mtv ).randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess() {
		return fullViewRandomAccessible.randomAccess();
	}

	@Override
	public int numDimensions() {
		return nDimensions;
	}

	@Override
	public long min(final int d) {
		if (d < targetDimension)
			return source.min( d );
		return source.min( d + 1 );
	}

	@Override
	public void min(final long[] min) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				min[d] = source.min( d );
			else 
				min[d] = source.min( d + 1 );
		}
	}

	@Override
	public void min(final Positionable min) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				min.setPosition( source.min( d ), d);
			else 
				min.setPosition( source.min( d + 1 ), d);
		}
	}

	@Override
	public long max(final int d) {
		if (d < targetDimension)
			return source.max( d );
		return source.max( d + 1 );
	}

	@Override
	public void max(final long[] max) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				max[d] = source.max( d );
			else 
				max[d] = source.max( d + 1 );
		}
	}

	@Override
	public void max(final Positionable max) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				max.setPosition( source.max( d ), d);
			else 
				max.setPosition( source.max( d + 1 ), d);
		}
	}

	@Override
	public void dimensions(final long[] dimensions) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				dimensions[ d ] = source.dimension( d);
			else 
				dimensions[ d ] = source.dimension( d + 1 );
		}
	}

	@Override
	public long dimension(final int d) {
		if (d < targetDimension)
			return source.dimension( d );
		return source.dimension(d + 1);
	}

	@Override
	public double realMin(final int d) {
		if (d < targetDimension)
			return source.realMin( d );
		return source.realMin( d + 1 );
	}

	@Override
	public void realMin(final double[] min) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				min[ d ] = source.realMin( d );
			else 
				min[ d ] = source.realMin( d + 1 );
		}
	}

	@Override
	public void realMin(final RealPositionable min) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				min.setPosition( source.realMin( d ), d);
			else 
				min.setPosition( source.realMin( d + 1 ), d);
		}
	}

	@Override
	public double realMax(final int d) {
		if (d < targetDimension)
			return source.realMax( d );
		return source.realMax( d + 1 );
	}

	@Override
	public void realMax(final double[] max) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				max[ d ] = source.realMax( d );
			else 
				max[ d ] = source.realMax( d + 1 );
		}
	}

	@Override
	public void realMax(final RealPositionable max) {
		for (int d = 0; d < nDimensions; d++) {
			if (d < targetDimension)
				max.setPosition( source.realMax( d ), d );
			else 
				max.setPosition( source.realMax( d + 1 ), d );
		}
	}

	@Override
	public Cursor<T> cursor() {
		return fullViewIterable.cursor();
	}

	@Override
	public Cursor<T> localizingCursor() {
		return fullViewIterable.localizingCursor();
	}

	@Override
	public long size() {
		long size = 1;
		for (int d = 0; d < nDimensions; d++) {
			size *= dimension(d);
		}
		return size;
	}

	@Override
	public T firstElement() {
		return source.firstElement();
	}

	@Override
	public Object iterationOrder() {
		return fullViewIterable;
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f ) {
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public Iterator<T> iterator() {
		return fullViewIterable.iterator();
	}

	@Override
	public ImgFactory<T> factory() {
		return source.factory();
	}

	@Override
	public HyperSliceImgPlus<T> copy() {
		return new HyperSliceImgPlus<T>(source, targetDimension, dimensionPosition);
	}
	
	/*
	 * STATIC UTILITIES
	 */
	
	/**
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}, obtained by 
	 * fixing the target {@link AxisType} to a target position. The source image is wrapped
	 * so there is data duplication.
	 * <p>
	 * If the axis type is not found in the source image, then the source image is returned.
	 */
	public static final <T> ImgPlus<T> fixAxis(final ImgPlus<T> source, final AxisType axis, final long pos) {
		// Determine target axis dimension
		int targetDim = -1;
		for (int d = 0; d < source.numDimensions(); d++) {
			if (source.axis(d).equals(axis)) {
				targetDim = d;
				break;
			}
		}
		if (targetDim < 0) {
			// not found
			return source;
		}
		return new HyperSliceImgPlus<T>(source, targetDim, pos);
	}
	
	/**
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}, obtained by 
	 * fixing the time axis to a target position. The source image is wrapped
	 * so there is data duplication.
	 * <p>
	 * If the time axis is not found in the source image, then the source image is returned.
	 */
	public static final <T> ImgPlus<T> fixTimeAxis(final ImgPlus<T> source, final long pos) {
		return fixAxis(source, Axes.TIME, pos);
	}

	/**
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}, obtained by 
	 * fixing the Z axis to a target position. The source image is wrapped
	 * so there is data duplication.
	 * <p>
	 * If the Z axis is not found in the source image, then the source image is returned.
	 */
	public static final <T> ImgPlus<T> fixZAxis(final ImgPlus<T> source, final long pos) {
		return fixAxis(source, Axes.Z, pos);
	}
	
	/**
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}, obtained by 
	 * fixing the channel axis to a target position. The source image is wrapped
	 * so there is data duplication.
	 * <p>
	 * If the channel axis is not found in the source image, then the source image is returned.
	 */
	public static final <T> ImgPlus<T> fixChannelAxis(final ImgPlus<T> source, final long pos) {
		return fixAxis(source, Axes.CHANNEL, pos);
	}

}

