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

package net.imglib2.script.filter;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.filter.fn.AbstractFilterFn;
import net.imglib2.script.math.Divide;
import net.imglib2.script.math.Exp;
import net.imglib2.script.math.Log;
import net.imglib2.script.math.Max;
import net.imglib2.script.math.Min;
import net.imglib2.script.math.Multiply;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.ImageFunction;
import net.imglib2.type.numeric.RealType;

/**
 * Return a function that, when evaluated, computes the gamma of the given image.
 * If 'i' was the pixel value, and the image was 8-bit, then this function would do: 
      double v = Math.exp(Math.log(i/255.0) * gamma) * 255.0); 
      if (v < 0) v = 0; 
      if (v >255) v = 255;
 * 
 *
 * @author Albert Cardona
 */
public class Gamma extends AbstractFilterFn
{
	protected final IFunction fn;
	protected final Number gamma, min, max;

	public Gamma(final IFunction fn, final Number gamma, final Number min, final Number max) {
		super(new Min(max,
				      new Max(min,
				              new Multiply(new Exp(new Multiply(gamma,
				                                                new Log(new Divide(fn, max)))),
				                           max))));
		this.fn = fn;
		this.gamma = gamma;
		this.min = min;
		this.max = max;
	}

	@SuppressWarnings("boxing")
	public <R extends RealType<R>> Gamma(final IterableRealInterval<R> img, final Number val) {
		this(new ImageFunction<R>(img), val, 0, img.firstElement().getMaxValue());
	}

	@Override
	public IFunction duplicate() throws Exception {
		return new Gamma(fn.duplicate(), gamma, min, max);
	}
}
