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

package net.imglib2.script.algorithm;

import java.awt.geom.AffineTransform;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.script.algorithm.fn.AbstractAffine3D;
import net.imglib2.type.numeric.NumericType;

/** 
* Expects matrix values in the same order that {@link AffineTransform} uses.
* 
* The constructors accept either an {@link Image} or an {@link IFunction} from which an {@link Image} is generated. */
/**
 * TODO
 *
 */
public class Affine2D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Affine transform the image with the best interpolation mode available. */
	@SuppressWarnings("boxing")
	public Affine2D(final Object fn,
			final Number scaleX, final Number shearX,
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY) throws Exception {
		this(fn, scaleX, shearX,
				 shearY, scaleY,
				 translateX, translateY,
				 AbstractAffine3D.BEST, 0);
	}

	/** Affine transform the image with the best interpolation mode available. */
	public Affine2D(final Object fn,
			final Number scaleX, final Number shearX,
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY,
			final Mode mode, final Number outside) throws Exception {
		super(fn, scaleX.floatValue(), shearX.floatValue(),
				  shearY.floatValue(), scaleY.floatValue(),
				  translateX.floatValue(), translateY.floatValue(),
				  mode, outside);
	}

	public Affine2D(final Object fn, final AffineTransform aff) throws Exception {
		this(fn, aff, AbstractAffine3D.BEST);
	}

	@SuppressWarnings("boxing")
	public Affine2D(final Object fn, final AffineTransform aff, final Mode mode) throws Exception {
		this(fn, aff, mode, 0);
	}

	public Affine2D(final Object fn, final AffineTransform aff, final Number outside) throws Exception {
		this(fn, aff, AbstractAffine3D.BEST, outside);
	}

	public Affine2D(final Object fn, final AffineTransform aff, final Mode mode, final Number outside) throws Exception {
		super(fn, (float)aff.getScaleX(), (float)aff.getShearX(),
				  (float)aff.getShearY(), (float)aff.getScaleY(),
				  (float)aff.getTranslateX(), (float)aff.getTranslateY(),
				  mode, outside);
	}

	public Affine2D(final Object fn,
					final float scaleX, final float shearX,
					final float shearY, final float scaleY,
					final float translateX, final float translateY,
					final Mode mode, final OutOfBoundsFactory<N,Img<N>> oobf) throws Exception
	{
		super(fn, scaleX, shearX,
				  shearY, scaleY,
				  translateX, translateY,
				  mode, oobf);
	}
}
