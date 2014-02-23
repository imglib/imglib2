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

package net.imglib2.meta.view;

import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedAxis;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * This class offers access to a <code>n-1</code>-dimensional view of a source
 * {@link ImgPlus}, obtained by fixing a target dimension to a target position.
 * The source image is wrapped so there is no data duplication.
 * <p>
 * Its result is exactly similar to the
 * {@link Views#hyperSlice(RandomAccessible, int, long)} static method, except
 * that the returned class preserves the {@link ImgPlus} capabilities, namely
 * the metadata and iterability.
 * <p>
 * Internally, we strongly rely on the fantastic {@link Views} class.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 * @author Christian Dietz <christian.dietz@uni-konstanz.de>
 */
public class HyperSliceImgPlus<T extends Type<T>> extends ImgPlus<T> {

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
	 * Errors will be generated if <code>d</code> and/or <code>pos</code> are
	 * incorrect.
	 * 
	 * @param source
	 *            the source {@link ImgPlus}
	 * @param d
	 *            the dimension to freeze
	 * @param pos
	 *            the position at which to hold the target dimension
	 */
	public HyperSliceImgPlus(final ImgPlus<T> sourceImgPlus, final int d,
			final long pos) {
		super(setUpSlice(sourceImgPlus, d, pos), sourceImgPlus.getName(),
				setUpAxis(sourceImgPlus, d));

		setSource(sourceImgPlus.getSource());

		this.targetDimension = d;
		this.dimensionPosition = pos;
	}

	private static <T extends Type<T>> CalibratedAxis[] setUpAxis(
			ImgPlus<T> source, int d) {

		CalibratedAxis[] copiedAxes = new CalibratedAxis[source.numDimensions() - 1];

		int offset = 0;
		for (int i = 0; i < source.numDimensions(); i++) {
			if (i == d) {
				offset = 1;
				continue;
			}
			copiedAxes[i - offset] = source.axis(i).copy();
		}

		return copiedAxes;
	}

	private static <T extends Type<T>> Img<T> setUpSlice(
			ImgPlus<T> sourceImgPlus, int d, long pos) {
		return new ImgView<T>(Views.hyperSlice(sourceImgPlus, d, pos),
				sourceImgPlus.factory());
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
	public HyperSliceImgPlus<T> copy() {
		return new HyperSliceImgPlus<T>(new ImgPlus<T>(getImg().copy(), this),
				targetDimension, dimensionPosition);
	}

	/*
	 * STATIC UTILITIES
	 */

	/**
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}
	 *         , obtained by fixing the target {@link AxisType} to a target
	 *         position. The source image is wrapped so there is data
	 *         duplication.
	 *         <p>
	 *         If the axis type is not found in the source image, then the
	 *         source image is returned.
	 */
	public static final <T extends Type<T>> ImgPlus<T> fixAxis(
			final ImgPlus<T> source, final AxisType axis, final long pos) {
		// Determine target axis dimension
		int targetDim = -1;
		for (int d = 0; d < source.numDimensions(); d++) {
			if (source.axis(d).type() == axis) {
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
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}
	 *         , obtained by fixing the time axis to a target position. The
	 *         source image is wrapped so there is data duplication.
	 *         <p>
	 *         If the time axis is not found in the source image, then the
	 *         source image is returned.
	 */
	public static final <T extends Type<T>> ImgPlus<T> fixTimeAxis(
			final ImgPlus<T> source, final long pos) {
		return fixAxis(source, Axes.TIME, pos);
	}

	/**
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}
	 *         , obtained by fixing the Z axis to a target position. The source
	 *         image is wrapped so there is data duplication.
	 *         <p>
	 *         If the Z axis is not found in the source image, then the source
	 *         image is returned.
	 */
	public static final <T extends Type<T>> ImgPlus<T> fixZAxis(
			final ImgPlus<T> source, final long pos) {
		return fixAxis(source, Axes.Z, pos);
	}

	/**
	 * @return a <code>n-1</code>-dimensional view of the source {@link ImgPlus}
	 *         , obtained by fixing the channel axis to a target position. The
	 *         source image is wrapped so there is data duplication.
	 *         <p>
	 *         If the channel axis is not found in the source image, then the
	 *         source image is returned.
	 */
	public static final <T extends Type<T>> ImgPlus<T> fixChannelAxis(
			final ImgPlus<T> source, final long pos) {
		return fixAxis(source, Axes.CHANNEL, pos);
	}

}
