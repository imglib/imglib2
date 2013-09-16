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

import java.util.HashMap;
import java.util.Map;

import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

import ucar.nc2.units.SimpleUnit;

/**
 * Service for defining units and making unit conversions.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = Service.class)
public class DefaultUnitService extends AbstractService {

	// -- fields --

	private String failureMsg = null;
	private Map<String, UnitDef> userDefinedUnits =
		new HashMap<String, DefaultUnitService.UnitDef>();

	// -- constructors --

	public DefaultUnitService() {
	}

//	 * Returns a conversion factor between two compatible types of units. Each
//	 * unit can be a compound unit string. For instance "inch" or "kg*m/s" or
//	 * "pounds/inch^2", "km/hour**2", etc. Returns the multiplication factor that
//	 * will convert values in the input unit space to values in the output unit
//	 * space. Returns Double.NaN if the unit types are incompatible.
//	 * 
//	 * @param inputUnit The string representing the input unit.
//	 * @param outputUnit The string representing the output unit.
//	 * @return The conversion factor which is used to multiply input numbers into
//	 *         output space.

	public double factor(String inputUnit, String outputUnit)
	{
		failureMsg = null;
		return findConversion(inputUnit, outputUnit, 1);
	}

//	 * Return the last internal error message if any. Each time factor() is called
//	 * the internal message is initially set to null. When factor() determines a
//	 * desired unit conversion is invalid it returns Double.NaN and sets the
//	 * internal failure message.

	public String failureMessage() {
		return failureMsg;
	}

	// TODO - we are relying on using NetCDF which has limited API for units. The
	// udunits package is also released as its own maven artifact in ucar's own
	// nexus. We could rely on that subproject and maybe have better api for
	// user defined units. This code below was quick to write and is a minimal
	// implementation. Note: ucar's nexus link is this:
	// https://artifacts.unidata.ucar.edu/content/groups/public/edu/ucar/udunits/

//	 * Defines a unit conversion that can be referred to via the factor() method.
//	 * Note that baseUnit is not necessarily a name. It could be a compound unit
//	 * string like "m/s^2" etc. Imagine we define a unit called "fliggs" that is
//	 * 14.2 m/s^2.
//	 * 
//	 * @param unitName The name of the unit being defined e.g. "fliggs".
//	 * @param baseUnit The unit the defined unit is based upon e.g. "m/s^2".
//	 * @param factor The ratio of defined units to base units e.g. 14.2.

	public void defineUnit(String unitName, String baseUnit, double factor) {
		if (!Double.isNaN(factor(unitName, baseUnit))) {
			throw new IllegalArgumentException("unit defined already " + unitName);
		}
		UnitDef unitDef = new UnitDef(baseUnit, factor);
		userDefinedUnits.put(unitName, unitDef);
	}

	// -- helpers --

	private double findConversion(String unit1, String unit2, double accum) {
		UnitDef unit = userDefinedUnits.get(unit1);
		if (unit != null) {
			return findConversion(unit.baseUnit, unit2, accum / unit.amount);
		}
		unit = userDefinedUnits.get(unit2);
		if (unit != null) {
			return findConversion(unit1, unit.baseUnit, accum * unit.amount);
		}
		// Here is the only reliance on underlying library that handles unit
		// conversion.
		// Initial release: ucar udunits as shipped in the netcdf library
		try {
			return accum * SimpleUnit.getConversionFactor(unit1, unit2);
		}
		catch (Exception e) {
			failureMsg = e.getMessage();
			return Double.NaN;
		}
	}

	private class UnitDef {

		UnitDef(String baseUnit, double amount) {
			this.baseUnit = baseUnit;
			this.amount = amount;
		}

		double amount;
		String baseUnit;
	}

	public static void main(String[] args) {
		DefaultUnitService c = new DefaultUnitService();
		// a peeb is 5 meters
		c.defineUnit("peeb", "m", 5);
		System.out.println("peebs per meter = " + c.factor("peeb", "m"));
		System.out.println("meters per peeb = " + c.factor("m", "peeb"));
		// a dub is 2 peebs
		c.defineUnit("dub", "peeb", 2);
		System.out.println("dubs per meter = " + c.factor("dub", "m"));
		System.out.println("meters per dub = " + c.factor("m", "dub"));
		System.out.println("dubs per peeb = " + c.factor("dub", "peeb"));
		System.out.println("peebs per dub = " + c.factor("peeb", "dub"));
		// a plook is 7 dubs
		c.defineUnit("plook", "dub", 7);
		System.out.println("dubs per plook = " + c.factor("dub", "plook"));
		System.out.println("plooks per dub = " + c.factor("plook", "dub"));
		System.out.println("plook per meter = " + c.factor("m", "plook"));
		System.out.println("meter per plook = " + c.factor("plook", "m"));
		// a korch is 4 m/s^2
		c.defineUnit("korch", "m/s^2", 4);
		System.out.println("korch per m/s^2 = " + c.factor("m/s^2", "korch"));
		System.out.println("korch per km/s^2 = " + c.factor("km/s^2", "korch"));
	}
}
