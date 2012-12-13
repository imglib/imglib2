/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
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

package script.imglib.algorithm;

import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.type.numeric.NumericType;

/** Scale a 2D or 3D image, resizing only the 2D planes as necessary. The Z axis is left untouched.
 * 
 *  The constructors accept either an {@link Image} or an {@link IFunction} from which an {@link Image} is generated. */
/**
 * TODO
 *
 */
public class Scale2D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Scale the given image in 2D using the best interpolation available. */
	public Scale2D(final Object fn, final Number scale) throws Exception {
		this(fn, scale.floatValue(), scale.floatValue(), Affine3D.BEST);
	}

	public Scale2D(final Object fn, final Number scale, final Mode mode) throws Exception {
		this(fn, scale.floatValue(), scale.floatValue(), mode);
	}

	public Scale2D(final Object fn, final Number scaleX, Number scaleY) throws Exception {
		this(fn, scaleX.floatValue(), scaleY.floatValue(), Affine3D.BEST);
	}

	public Scale2D(final Object fn, final Number scaleX, Number scaleY, final Mode mode) throws Exception {
		this(fn, scaleX.floatValue(), scaleY.floatValue(), mode);
	}

	public Scale2D(final Object fn, final float scaleX, final float scaleY, final Mode mode) throws Exception {
		super(fn, new float[]{scaleX, 0, 0, 0,
							   0, scaleY, 0, 0,
							   0, 0, 1, 0},
			  mode, new OutOfBoundsStrategyMirrorFactory<N>());
	}
}
