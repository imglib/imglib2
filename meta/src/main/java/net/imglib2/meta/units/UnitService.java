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

package net.imglib2.meta.units;

import org.scijava.service.Service;

/**
 * Service for defining units and making unit conversions.
 * 
 * @author Barry DeZonia
 */
public interface UnitService extends Service {

	/**
	 * Returns a value after conversion between two compatible types of units.
	 * Each unit can be a compound unit string. For instance "inch" or "kg*m/s" or
	 * "pounds/inch^2", "km/hour**2", etc. Returns the number of output unit
	 * values represented by the number of input values of input type. Returns
	 * Double.NaN if the unit types are incompatible.
	 * 
	 * @param inputValue The double representing the number of input units.
	 * @param inputUnit The string representing the input unit.
	 * @param outputUnit The string representing the output unit.
	 * @return The value in output units after converting the inputValue from
	 *         input units.
	 * @throws IllegalArgumentException if conversion fails with the given
	 *           arguments (e.g., if the units are incompatible).
	 */
	public double value(double inputValue, String inputUnit, String outputUnit);

	/**
	 * Defines a unit conversion that can be referred to via the value() method.
	 * Note that baseUnit is not necessarily a name. It could be a compound unit
	 * string like "m/s^2" etc. Imagine we define a unit called "fliggs" that is
	 * 14.2 m/s^2.
	 * 
	 * @param unitName The name of the unit being defined e.g. "fliggs".
	 * @param baseUnit The unit the defined unit is based upon e.g. "m/s^2".
	 * @param scale The ratio of defined units to base units e.g. 14.2.
	 */
	public void defineUnit(String unitName, String baseUnit, double scale);

	/**
	 * Defines a unit conversion that can be referred to via the value() method.
	 * Note that baseUnit is not necessarily a name. It could be a compound unit
	 * string like "m/s^2" etc. Imagine we define a unit called "fliggs" that is
	 * 14.2 m/s^2 offset by 3.3 from baseUnit. An example of an offset is the 32
	 * used in conversion between celsius and fahrenheit.
	 * 
	 * @param unitName The name of the unit being defined e.g. "fliggs".
	 * @param baseUnit The unit the defined unit is based upon e.g. "m/s^2".
	 * @param scale The ratio of defined units to base units e.g. 14.2.
	 * @param offset The offset of defined units to base units e.g. 3.3.
	 */
	public void defineUnit(String unitName, String baseUnit, double scale,
		double offset);

	/**
	 * Defines a unit conversion that can be referred to via the value() method.
	 * Note that baseUnit is not necessarily a name. It could be a compound unit
	 * string like "m/s^2" etc. Imagine we define a unit called "fliggs" that has
	 * a nonlinear conversion from baseUnit. One supplies a {@link Calibrator}
	 * that handles conversion. The Calibrator allows any kind of conversion
	 * between units including nonlinear ones (i.e. can support log units).
	 * 
	 * @param unitName The name of the unit being defined e.g. "fliggs".
	 * @param baseUnit The unit the defined unit is based upon e.g. "m/s^2".
	 * @param calibrator The Calibrator the maps between the two unit spaces.
	 */
	public void
		defineUnit(String unitName, String baseUnit, Calibrator calibrator);
}
