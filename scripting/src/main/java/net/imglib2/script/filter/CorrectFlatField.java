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

import net.imglib2.script.filter.fn.AbstractFilterFn;
import net.imglib2.script.math.Divide;
import net.imglib2.script.math.Multiply;
import net.imglib2.script.math.Subtract;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.Util;

/**
 * 
 *
 * @author Albert Cardona
 */
public class CorrectFlatField extends AbstractFilterFn
{
	protected final Object img, brightfield, darkfield;
	
	/**
	 * 
	 * @param img An IFunction, IterableRealInterval or Number.
	 * @param brightfield An IFunction, IterableRealInterval or Number.
	 * @param darkfield An IFunction, IterableRealInterval or Number.
	 * @throws Exception
	 */
	public CorrectFlatField(final Object img, final Object brightfield, final Object darkfield) throws Exception {
		super(new Multiply(new Divide(new Subtract(Util.wrap(img), Util.wrap(brightfield)),
		                              new Subtract(Util.wrap(brightfield), Util.wrap(darkfield)))));
		this.img = img;
		this.brightfield = brightfield;
		this.darkfield = darkfield;
	}
	
	@SuppressWarnings("boxing")
	public CorrectFlatField(final Object img, final Object brightfield) throws Exception {
		this(img, brightfield, 0);
	}

	@Override
	public IFunction duplicate() throws Exception {
		return new CorrectFlatField(Util.wrap(this.img).duplicate(),
		                            Util.wrap(this.brightfield).duplicate(),
		                            Util.wrap(this.darkfield).duplicate());
	}
}
