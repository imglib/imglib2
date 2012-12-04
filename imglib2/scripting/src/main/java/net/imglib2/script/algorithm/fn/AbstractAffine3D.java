/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.script.algorithm.fn;

import mpicbg.models.AffineModel2D;
import mpicbg.models.AffineModel3D;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.transformation.ImageTransform;
import net.imglib2.img.Img;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.script.color.Alpha;
import net.imglib2.script.color.Blue;
import net.imglib2.script.color.Green;
import net.imglib2.script.color.RGBA;
import net.imglib2.script.color.Red;
import net.imglib2.script.math.Compute;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/** Convenient intermediate class to be able to operate directly on an {@link Image} argument in the constructor. */
/**
 * TODO
 *
 */
public abstract class AbstractAffine3D<T extends NumericType<T>> extends ImgProxy<T>
{
	static public enum Mode { LINEAR, NEAREST_NEIGHBOR }

	//static public final Mode LINEAR = Mode.LINEAR;
	//static public final Mode NEAREST_NEIGHBOR = Mode.NEAREST_NEIGHBOR;
	static public final Mode BEST = Mode.LINEAR;

	/** With a default {@link OutOfBoundsStrategyValueFactory} with @param outside. */
	@SuppressWarnings("unchecked")
	public AbstractAffine3D(final Img<T> img, final float[] matrix, final Mode mode, final Number outside) throws Exception {
		this(img, matrix, mode, new OutOfBoundsConstantValueFactory<T,Img<T>>((T)withValue(img, img.firstElement().createVariable(), outside))); // default value is zero
	}

	public AbstractAffine3D(final Img<T> img, final float[] matrix, final Mode mode, final OutOfBoundsFactory<T,Img<T>> oobf) throws Exception {
		super(process(img, matrix, mode, oobf));
	}

	/** With a default {@link OutOfBoundsStrategyValueFactory} with @param outside. */
	@SuppressWarnings("unchecked")
	public AbstractAffine3D(final Img<T> img,
			final float scaleX, final float shearX,
			final float shearY, final float scaleY,
			final float translateX, final float translateY,
			final Mode mode, final Number outside) throws Exception {
		this(img, new float[]{scaleX, shearX, 0, translateX,
				  			   shearY, scaleY, 0, translateY,
				  			   0, 0, 1, 0}, mode, new OutOfBoundsConstantValueFactory<T,Img<T>>((T)withValue(img, img.firstElement().createVariable(), outside)));
	}

	public AbstractAffine3D(final Img<T> img,
			final float scaleX, final float shearX,
			final float shearY, final float scaleY,
			final float translateX, final float translateY,
			final Mode mode, final OutOfBoundsFactory<T,Img<T>> oobf) throws Exception {
		this(img, new float[]{scaleX, shearX, 0, translateX,
				  			   shearY, scaleY, 0, translateY,
				  			   0, 0, 1, 0}, mode, oobf);
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final NumericType<?> withValue(final Img<? extends NumericType<?>> img, final NumericType<?> type, final Number val) {
		final NumericType t = img.firstElement().createVariable();
		if (ARGBType.class.isAssignableFrom(t.getClass())) {
			int i = val.intValue();
			t.set(new ARGBType(i));
		} else {
			((RealType)t).setReal(val.doubleValue());
		}
		return t;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static private final <N extends NumericType<N>>
						Img<N> process(final Img<N> img, final float[] matrix,
						final Mode mode, final OutOfBoundsFactory<N,Img<N>> oobf) throws Exception {
		if (matrix.length < 12) {
			throw new IllegalArgumentException("Affine transform in 2D requires a matrix array of 12 elements.");
		}
		final Type<?> type = img.firstElement().createVariable();
		if (ARGBType.class.isAssignableFrom(type.getClass())) { // type instanceof RGBALegacyType fails to compile
			return (Img)processRGBA((Img)img, matrix, mode, (OutOfBoundsFactory)oobf);
		} else if (type instanceof RealType<?>) {
			return processReal((Img)img, matrix, mode, (OutOfBoundsFactory)oobf);
		} else {
			throw new Exception("Affine transform: cannot handle type " + type.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	static private final Img<ARGBType> processRGBA(final Img<ARGBType> img, final float[] m,
			final Mode mode, final OutOfBoundsFactory<ARGBType,Img<ARGBType>> oobf) throws Exception {
		// Process each channel independently and then compose them back
		OutOfBoundsFactory<FloatType,Img<FloatType>> ored, ogreen, oblue, oalpha;
		if (OutOfBoundsConstantValueFactory.class.isAssignableFrom(oobf.getClass())) { // can't use instanceof
			final int val = ((OutOfBoundsConstantValueFactory<ARGBType,Img<ARGBType>>)oobf).getValue().get();
			ored = new OutOfBoundsConstantValueFactory<FloatType,Img<FloatType>>(new FloatType((val >> 16) & 0xff));
			ogreen = new OutOfBoundsConstantValueFactory<FloatType,Img<FloatType>>(new FloatType((val >> 8) & 0xff));
			oblue = new OutOfBoundsConstantValueFactory<FloatType,Img<FloatType>>(new FloatType(val & 0xff));
			oalpha = new OutOfBoundsConstantValueFactory<FloatType,Img<FloatType>>(new FloatType((val >> 24) & 0xff));
		} else {
			// Jump into the pool!
			try {
				ored = oobf.getClass().newInstance();
			} catch (Exception e) {
				System.out.println("Affine3D for RGBA: oops -- using a black OutOfBoundsStrategyValueFactory");
				ored = new OutOfBoundsConstantValueFactory<FloatType,Img<FloatType>>(new FloatType());
			}
			ogreen = ored;
			oblue = ored;
			oalpha = ored;
		}
		return new RGBA(processReal(Compute.inFloats(new Red(img)), m, mode, ored),
						processReal(Compute.inFloats(new Green(img)), m, mode, ogreen),
						processReal(Compute.inFloats(new Blue(img)), m, mode, oblue),
						processReal(Compute.inFloats(new Alpha(img)), m, mode, oalpha)).asImage();
	}

	static private final <R extends RealType<R>> Img<R> processReal(final Img<R> img, final float[] m,
			final Mode mode, final OutOfBoundsFactory<R,Img<R>> oobf) throws Exception {
		final InterpolatorFactory<R,RandomAccessible<R>> inter;
		switch (mode) {
		case LINEAR:
			inter = new NLinearInterpolatorFactory<R>();
			break;
		case NEAREST_NEIGHBOR:
			inter = new NearestNeighborInterpolatorFactory<R>();
			break;
		default:
			throw new IllegalArgumentException("Scale: don't know how to scale with mode " + mode);
		}

		final ImageTransform<R> transform;
		final ExtendedRandomAccessibleInterval<R, Img<R>> imgExt = Views.extend(img, oobf);

		if (2 == img.numDimensions()) {
			// Transform the single-plane image in 2D
			AffineModel2D aff = new AffineModel2D();
			aff.set(m[0], m[4], m[1], m[5], m[3], m[7]);
			transform = new ImageTransform<R>(imgExt, aff, (InterpolatorFactory) inter);
		} else if (3 == img.numDimensions()) {
			// Transform the image in 3D, or each plane in 2D
			if (m.length < 12) {
				throw new IllegalArgumentException("Affine transform in 3D requires a matrix array of 12 elements.");
			}
			AffineModel3D aff = new AffineModel3D();
			aff.set(m[0], m[1], m[2], m[3],
					m[4], m[5], m[6], m[7],
					m[8], m[9], m[10], m[11]);
			transform = new ImageTransform<R>(imgExt, aff, (InterpolatorFactory) inter);
			// Ensure Z dimension is not altered if scaleZ is 1:
			if (Math.abs(m[10] - 1.0f) < 0.000001 && 0 == m[8] && 0 == m[9]) {
				long[] d = transform.getNewImageSize();
				d[2] = img.dimension(2); // 0-based: '2' is the third dimension
				transform.setNewImageSize(d);
			}
		} else {
			throw new Exception("Affine transform: only 2D and 3D images are supported.");
		}

		if (!transform.checkInput() || !transform.process()) {
			throw new Exception("Could not affine transform the image: " + transform.getErrorMessage());
		}

		return transform.getResult();
	}
}
