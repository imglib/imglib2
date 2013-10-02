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

import net.imglib2.meta.axis.LinearAxis;

/**
 * TODO
 * 
 * @author Barry DeZonia
 */
public class CombinedCalibratedRealInterval<A extends CalibratedAxis, S extends CalibratedRealInterval<A>>
	extends CombinedRealInterval<A, S> implements CalibratedRealInterval<A>
{

	@Override
	public double averageScale(final int d) {
		return axis(d).averageScale(realMin(d), realMax(d));
	}

	// FIXME - these methods need some TLC. Maybe this class will store its
	// own copy of calibration values and units. And then setUnit() and
	// setCalibration() on an axis does a unit converted scaling of existing axes
	// cal values. Pulling values out of this interval will use views and sampling
	// as needed to get values along unit/calibration converted points of the
	// underlying axes.

	@Override
	public double calibration(final int d) {
		return linearAxis(d).scale();
	}

	@Override
	public void calibration(final double[] cal) {
		for (int d = 0; d < numDimensions(); d++) {
			cal[d] = calibration(d);
		}
	}

	@Override
	public void calibration(final float[] cal) {
		for (int d = 0; d < numDimensions(); d++) {
			cal[d] = (float) calibration(d);
		}
	}

	@Override
	public void setCalibration(final double cal, final int d) {
		linearAxis(d).setScale(cal);
	}

	@Override
	public void setCalibration(final double[] cal) {
		for (int d = 0; d < numDimensions(); d++) {
			setCalibration(cal[d], d);
		}
	}

	@Override
	public void setCalibration(final float[] cal) {
		for (int d = 0; d < numDimensions(); d++) {
			setCalibration(cal[d], d);
		}
	}

	@Override
	public void setUnit(final String unit, final int d) {
		axis(d).setUnit(unit);
	}

	@Override
	public String unit(final int d) {
		return axis(d).unit();
	}

	// -- Helper methods --

	private LinearAxis linearAxis(final int d) {
		final A axis = axis(d);
		if (axis instanceof LinearAxis) {
			return (LinearAxis) axis;
		}
		throw new IllegalArgumentException("Unsupported axis: " +
			axis.getClass().getName());
	}

}
