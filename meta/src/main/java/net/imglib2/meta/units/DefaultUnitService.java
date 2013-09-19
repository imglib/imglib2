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

import ucar.units.Unit;
import ucar.units.UnitFormat;
import ucar.units.UnitFormatManager;
import ucar.units.UnknownUnit;

/**
 * Service for defining units and making unit conversions.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = Service.class)
public class DefaultUnitService extends AbstractService implements UnitService {

	// -- fields --

	private String failureMsg = null;
	private Map<String, UnitDef> userDefinedUnits =
		new HashMap<String, DefaultUnitService.UnitDef>();
	private UnitFormat unitFormatter = UnitFormatManager.instance();

	// -- constructors --

	public DefaultUnitService() {
	}

	// -- UnitService methods --

	@Override
	public double value(double inputValue, String inputUnit, String outputUnit)
	{
		failureMsg = null;
		return findConversion(inputValue, inputUnit, outputUnit);
	}

	@Override
	public String failureMessage() {
		return failureMsg;
	}

	@Override
	public void defineUnit(String unitName, String baseUnit, double scale) {
		defineUnit(unitName, baseUnit, scale, 0);
	}

	@Override
	public void defineUnit(String unitName, String baseUnit, double scale,
		double offset)
	{
		defineUnit(unitName, baseUnit, new LinearCalibrator(scale, offset));
	}

	@Override
	public void
		defineUnit(String unitName, String baseUnit, Calibrator calibrator)
	{
		String err = null;
		if (userDefinedUnits.get(baseUnit) == null) {
			// make sure that base unit is defined internally
			if (!definedInternally(baseUnit)) {
				err = "base unit has not been defined: " + baseUnit;
			}
		}
		else { // base unit is one of our user defined ones
			// make sure our new unit name is not already in use
			if (userDefinedUnits.get(unitName) != null) {
				err = "unit has already been defined: " + unitName;
			}
			else if (definedInternally(unitName)) {
				err = "can't redefine internal unit " + unitName;
			}
		}
		if (err != null) {
			throw new IllegalArgumentException(err);
		}
		UnitDef unitDef = new UnitDef(baseUnit, calibrator);
		userDefinedUnits.put(unitName, unitDef);
	}

	// -- helpers --

	private double findConversion(double measure, String unit1, String unit2) {
		UnitDef unit = userDefinedUnits.get(unit1);
		if (unit != null) {
			return findConversion(unit.calibrator.toOutput(measure), unit.baseUnit,
				unit2);
		}
		unit = userDefinedUnits.get(unit2);
		if (unit != null) {
			return findConversion(unit.calibrator.toInput(measure), unit1,
				unit.baseUnit);
		}
		try {
			Unit u1 = unitFormatter.parse(unit1);
			Unit u2 = unitFormatter.parse(unit2);
			return u1.convertTo(measure, u2);
		}
		catch (Exception e) {
			failureMsg = e.getMessage();
			return Double.NaN;
		}
	}

	private boolean definedInternally(String unitName) {
		try {
			Unit o = unitFormatter.parse(unitName);
			if (o instanceof UnknownUnit) return false;
			if (o.getDerivedUnit() instanceof UnknownUnit) return false;
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	private class UnitDef {

		UnitDef(String baseUnit, Calibrator calibrator) {
			this.baseUnit = baseUnit;
			this.calibrator = calibrator;
		}

		String baseUnit;
		Calibrator calibrator;
	}

	public static void main(String[] args) {
		DefaultUnitService c = new DefaultUnitService();
		// a peeb is 5 meters
		c.defineUnit("peeb", "m", 5);
		System.out.println("peebs per meter = " + c.value(1, "peeb", "m"));
		System.out.println("meters per peeb = " + c.value(1, "m", "peeb"));
		// a wuzpang is 2 peebs
		c.defineUnit("wuzpang", "peeb", 2);
		System.out.println("wuzpangs per meter = " + c.value(1, "wuzpang", "m"));
		System.out.println("meters per wuzpang = " + c.value(1, "m", "wuzpang"));
		System.out.println("wuzpangs per peeb = " + c.value(1, "wuzpang", "peeb"));
		System.out.println("peebs per wuzpang = " + c.value(1, "peeb", "wuzpang"));
		// a plook is 7 wuzpangs
		c.defineUnit("plook", "wuzpang", 7);
		System.out
			.println("wuzpangs per plook = " + c.value(1, "wuzpang", "plook"));
		System.out
			.println("plooks per wuzpang = " + c.value(1, "plook", "wuzpang"));
		System.out.println("plook per meter = " + c.value(1, "m", "plook"));
		System.out.println("meter per plook = " + c.value(1, "plook", "m"));
		// a korch is 4 m/s^2
		c.defineUnit("korch", "m/s^2", 4);
		System.out.println("korch per m/s^2 = " + c.value(1, "m/s^2", "korch"));
		System.out.println("korch per km/s^2 = " + c.value(1, "km/s^2", "korch"));
		// define a scale/offset unit
		c.defineUnit("MyCel", "K", 1, 273.15);
		System.out.println("1 degree C to kelvin " + c.value(1, "Cel", "K"));
		System.out.println("1 degree MyCel to kelvin " + c.value(1, "MyCel", "K"));
		// complain about a bad conversion
		System.out.println("Trying bad conversion: kelvin to meter : " +
			c.value(1, "kelvin", "meter"));
		System.out.println(c.failureMessage());
	}
}
