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

package net.imglib2.meta.axis;

import java.util.HashMap;
import java.util.Set;

import net.imglib2.meta.AbstractCalibratedAxis;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedAxis;

/**
 * An {@link CalibratedAxis} whose calibration is defined by an equation with
 * one or more variables.
 * 
 * @author Curtis Rueden
 */
public abstract class VariableAxis extends AbstractCalibratedAxis {

	private final HashMap<String, Double> vars = new HashMap<String, Double>();

	// -- Constructors --

	public VariableAxis(final AxisType type) {
		super(type);
	}

	public VariableAxis(final AxisType type, final String unit) {
		super(type, unit);
	}

	// -- VariableAxis methods --

	/** Gets the value of the variable with the given name, or null if none. */
	public Double get(final String name) {
		return vars.get(name);
	}

	/**
	 * Sets the specified value to a variable of the given name.
	 * 
	 * @param name The variable to assign.
	 * @param value The value to assign, or null to clear the variable.
	 */
	public void set(final String name, final Double value) {
		if (value == null) vars.remove(name);
		else vars.put(name, value);
	}

	/** Gets the number of variables with assigned values. */
	public int numVars() {
		return vars.size();
	}

	/** Gets the set of variables with assigned values. */
	public Set<String> vars() {
		return vars.keySet();
	}

	// -- CalibratedAxis methods --

	@Override
	public String particularEquation() {
		final StringBuilder sb = new StringBuilder();
		// NB: Split general equation on potential variables, including delimiters.
		// For an explanation, see: http://stackoverflow.com/a/279337
		final String[] tokens =
			generalEquation().split("(?<=\\w)(?=\\W)|(?<=\\W)(?=\\w)");
		for (final String token : tokens) {
			if (token.matches("\\w+")) {
				// token might be a variable; check the vars table
				final Double value = vars.get(token);
				if (value != null) {
					// token *is* a variable; substitute the value!
					sb.append("(");
					sb.append(value);
					sb.append(")");
					continue;
				}
			}
			sb.append(token);
		}
		return sb.toString();
	}

}
