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

package net.imglib2.script.algorithm;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.script.algorithm.fn.AbstractAffine3D;
import net.imglib2.script.algorithm.fn.AbstractAffine3D.Mode;
import net.imglib2.script.algorithm.fn.ImgProxy;
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
import net.imglib2.view.Views;

/** Resample an image in all its dimensions by a given scaling factor and interpolation mode.
 *  
 *  An image of 2000x2000 pixels, when resampled by 2, will result in an image of dimensions 4000x4000.
 *  
 *  Mathematically this is not a scaling operation and can be proved to be wrong.
 *  For proper scaling, see {@link Scale2D} and {@link Scale3D}. */
/**
 * TODO
 *
 */
public class Resample<N extends NumericType<N>> extends ImgProxy<N>
{
	static public final Mode LINEAR = Affine3D.Mode.LINEAR;
	static public final Mode NEAREST_NEIGHBOR = Affine3D.Mode.NEAREST_NEIGHBOR;
	static public final Mode BEST = AbstractAffine3D.BEST;

	/** Resample an {@link Img} with the best possible mode. */
	public Resample(final Img<N> img, final Number scale) throws Exception {
		this(img, asDimArray(img, scale), BEST);
	}

	public Resample(final Img<N> img, final Number scale, final Mode mode) throws Exception {
		this(img, asDimArray(img, scale), mode);
	}

	public Resample(final Img<N> img, final long[] dimensions) throws Exception {
		this(img, dimensions, BEST);
	}

	public Resample(final Img<N> img, final long[] dimensions, final Mode mode) throws Exception {
		super(process(img, dimensions, mode));
	}

	static private final long[] asDimArray(final Img<?> img, final Number scale) {
		final long[] dim = new long[img.numDimensions()];
		final double s = scale.doubleValue();
		for (int i=0; i<dim.length; i++) {
			dim[i] = (int)((img.dimension(i) * s) + 0.5);
		}
		return dim;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static private final <N extends NumericType<N>> Img<N> process(final Img<N> img, long[] dim, final Mode mode) throws Exception {
		// Pad dim array with missing dimensions
		if (dim.length != img.numDimensions()) {
			long[] d = new long[img.numDimensions()];
			int i = 0;
			for (; i<dim.length; i++) d[i] = dim[i];
			for (; i<img.numDimensions(); i++) d[i] = img.dimension(i);
			dim = d;
		}
		final Type<?> type = img.firstElement().createVariable();
		if (ARGBType.class.isAssignableFrom(type.getClass())) { // type instanceof RGBALegacyType fails to compile
			return (Img)processRGBA((Img)img, dim, mode);
		} else if (type instanceof RealType<?>) {
			return processReal((Img)img, dim, mode);
		} else {
			throw new Exception("Affine transform: cannot handle type " + type.getClass());
		}
	}

	static private final Img<ARGBType> processRGBA(final Img<ARGBType> img, final long[] dim, final Mode mode) throws Exception {
		// Process each channel independently and then compose them back
		return new RGBA(processReal(Compute.inFloats(new Red(img)), dim, mode),
						processReal(Compute.inFloats(new Green(img)), dim, mode),
						processReal(Compute.inFloats(new Blue(img)), dim, mode),
						processReal(Compute.inFloats(new Alpha(img)), dim, mode)).asImage();
	}

	static private final <T extends RealType<T>> Img<T> processReal(final Img<T> img, final long[] dim, final Mode mode) throws Exception {

		final Img<T> res = img.factory().create(dim, img.firstElement().createVariable());

		InterpolatorFactory<T,RandomAccessible<T>> ifac;
		switch (mode) {
		case LINEAR:
			ifac = new NLinearInterpolatorFactory<T>();
			break;
		case NEAREST_NEIGHBOR:
			ifac = new NearestNeighborInterpolatorFactory<T>();
			break;
		default:
			throw new Exception("Resample: unknown mode!");
		}

		final RealRandomAccess<T> inter = ifac.create(Views.extend(img, new OutOfBoundsMirrorFactory<T,Img<T>>(OutOfBoundsMirrorFactory.Boundary.SINGLE)));
		final Cursor<T> c2 = res.localizingCursor();
		final float[] s = new float[dim.length];
		for (int i=0; i<s.length; i++) s[i] = (float)img.dimension(i) / dim[i];
		final long[] d = new long[dim.length];
		final float[] p = new float[dim.length];
		while (c2.hasNext()) {
			c2.fwd();
			c2.localize(d); // TODO "localize" seems to indicate the opposite of what it does
			for (int i=0; i<d.length; i++) p[i] = d[i] * s[i];
			inter.move(p);
			c2.get().set(inter.get());			
		}
		return res;
	}
}
