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

package net.imglib2.meta;

/**
 * @author Barry DeZonia
 */
public class LinearAxis extends AbstractCalibratedAxis {

	private double scale, origin;

	public LinearAxis() {
		this(Axes.unknown());
	}

	public LinearAxis(double scale) {
		this(Axes.unknown(), scale);
	}

	public LinearAxis(double scale, double origin) {
		this(Axes.unknown(), scale, origin);
	}

	public LinearAxis(AxisType type) {
		this(type, 1, 0);
	}

	public LinearAxis(AxisType type, double scale) {
		this(type, scale, 0);
	}

	public LinearAxis(AxisType type, double scale, double origin) {
		super(type);
		this.scale = scale;
		this.origin = origin;
	}

	public LinearAxis(AxisType type, String unit) {
		this(type, unit, 1, 0);
	}

	public LinearAxis(AxisType type, String unit, double scale) {
		this(type, unit, scale, 0);
	}

	public LinearAxis(AxisType type, String unit, double scale, double origin) {
		super(type);
		setUnit(unit);
		this.scale = scale;
		this.origin = origin;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double scale() {
		return scale;
	}

	public void setOrigin(double origin) {
		this.origin = origin;
	}

	public double origin() {
		return origin;
	}

	@Override
	public double calibratedValue(double rawValue) {
		return scale * rawValue + origin;
	}

	@Override
	public double rawValue(double calibratedValue) {
		return (calibratedValue - origin) / scale;
	}

	@Override
	public String equation() {
		return "y = a + b*x";
	}

	@Override
	public String calibratedEquation() {
		return "y = (" + origin + ") + (" + scale + ") * x";
	}

	@Override
	public boolean update(CalibratedAxis other) {
		if (other instanceof LinearAxis) {
			setOrigin(((LinearAxis) other).origin());
			setScale(((LinearAxis) other).scale());
			setType(other.type());
			setUnit(other.unit());
			return true;
		}
		return false;
	}
}
