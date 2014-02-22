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

import java.util.HashMap;
import java.util.Map;

import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

import ucar.units.ConversionException;
import ucar.units.NoSuchUnitException;
import ucar.units.PrefixDBException;
import ucar.units.SpecificationException;
import ucar.units.Unit;
import ucar.units.UnitDBException;
import ucar.units.UnitFormat;
import ucar.units.UnitFormatManager;
import ucar.units.UnitParseException;
import ucar.units.UnitSystemException;
import ucar.units.UnknownUnit;

/**
 * Service for defining units and making unit conversions.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = Service.class)
public class DefaultUnitService extends AbstractService implements UnitService {

	// -- fields --

	@Parameter
	private LogService log;

	private Map<String, UnitDef> userDefinedUnits =
		new HashMap<String, DefaultUnitService.UnitDef>();
	private UnitFormat unitFormatter = UnitFormatManager.instance();

	// -- UnitService methods --

	@Override
	public double value(double inputValue, String inputUnit, String outputUnit)
	{
		return findConversion(inputValue, inputUnit, outputUnit);
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
			final Unit u1 = parseUnit(unit1);
			final Unit u2 = parseUnit(unit2);
			return u1.convertTo(measure, u2);
		}
		catch (final ConversionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private boolean definedInternally(String unitName) {
		final Unit o = parseUnit(unitName);
		if (o == null || o instanceof UnknownUnit) return false;
		if (o.getDerivedUnit() instanceof UnknownUnit) return false;
		return true;
	}

	private Unit parseUnit(final String unitName) {
		try {
			return unitFormatter.parse(unitName);
		}
		catch (final NoSuchUnitException exc) {
			log.error("Cannot parse unit: " + unitName, exc);
		}
		catch (final UnitParseException exc) {
			log.error("Cannot parse unit: " + unitName, exc);
		}
		catch (final SpecificationException exc) {
			log.error("Cannot parse unit: " + unitName, exc);
		}
		catch (final UnitDBException exc) {
			log.error("Cannot parse unit: " + unitName, exc);
		}
		catch (final PrefixDBException exc) {
			log.error("Cannot parse unit: " + unitName, exc);
		}
		catch (final UnitSystemException exc) {
			log.error("Cannot parse unit: " + unitName, exc);
		}
		return null;
	}

	private class UnitDef {

		UnitDef(String baseUnit, Calibrator calibrator) {
			this.baseUnit = baseUnit;
			this.calibrator = calibrator;
		}

		String baseUnit;
		Calibrator calibrator;
	}

}
