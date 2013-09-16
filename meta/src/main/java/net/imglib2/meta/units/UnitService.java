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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
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
	 * Returns a conversion factor between two compatible types of units. Each
	 * unit can be a compound unit string. For instance "inch" or "kg*m/s" or
	 * "pounds/inch^2", "km/hour**2", etc. Returns the multiplication factor that
	 * will convert values in the input unit space to values in the output unit
	 * space. Returns Double.NaN if the unit types are incompatible.
	 * 
	 * @param inputUnit The string representing the input unit.
	 * @param outputUnit The string representing the output unit.
	 * @return The conversion factor which is used to multiply input numbers into
	 *         output space.
	 */
	public double factor(String inputUnit, String outputUnit);

	/**
	 * Return the last internal error message if any. Each time factor() is called
	 * the internal message is initially set to null. When factor() determines a
	 * desired unit conversion is invalid it returns Double.NaN and sets the
	 * internal failure message.
	 */
	public String failureMessage();

	/**
	 * Defines a unit conversion that can be referred to via the factor() method.
	 * Note that baseUnit is not necessarily a name. It could be a compound unit
	 * string like "m/s^2" etc. Imagine we define a unit called "fliggs" that is
	 * 14.2 m/s^2.
	 * 
	 * @param unitName The name of the unit being defined e.g. "fliggs".
	 * @param baseUnit The unit the defined unit is based upon e.g. "m/s^2".
	 * @param factor The ratio of defined units to base units e.g. 14.2.
	 */
	public void defineUnit(String unitName, String baseUnit, double factor);
}
