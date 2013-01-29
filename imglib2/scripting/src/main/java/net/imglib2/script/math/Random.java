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

package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.NumberFunction;
import net.imglib2.script.math.fn.UnaryOperation;

/**
 * TODO
 *
 */
public class Random extends UnaryOperation
{
	private final java.util.Random rand;

	@SuppressWarnings("boxing")
	public Random() {
		this(System.currentTimeMillis());
	}

	public Random(final Number seed) {
		super(seed);
		rand = new java.util.Random(seed.longValue());
	}
	
	public Random(final NumberFunction seed) {
		super(seed);
		rand = new java.util.Random((long)seed.eval());
	}
	
	@SuppressWarnings("boxing")
	public Random(final java.util.Random rand) {
		super(0); // bogus
		this.rand = rand;
	}

	@Override
	public final double eval() {
		return rand.nextDouble();
	}
	
	/** Duplicates this IFunction but not the random number generator. */
	@Override
	public IFunction duplicate() {
		return new Random(this.rand);
	}
}
