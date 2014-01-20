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

package net.imglib2.meta;

/**
 * An axis with an associated {@link AxisType}, unit and calibration.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 * @see TypedAxis
 */
public interface CalibratedAxis extends TypedAxis {

	/** Gets the dimension's unit. */
	String unit();

	/** Sets the dimension's unit. */
	void setUnit(String unit);

	/** Returns a calibrated value given a raw position along the axis. */
	double calibratedValue(double rawValue);

	/**
	 * Returns a raw value given a calibrated position along the axis. Returns
	 * Double.NaN if the calibrated value maps to more than one point along axis.
	 */
	double rawValue(double calibratedValue);

	/**
	 * Gets the general equation representing values along this axis; for
	 * instance: {@code y = m*x + b}.
	 */
	String generalEquation();

	/**
	 * Gets the particular equation representing values along this axis; for
	 * instance: {@code y = (14)*x + (4)}.
	 */
	String particularEquation();

	/**
	 * Returns the average scale between two raw value coordinates along an axis.
	 * <p>
	 * In the limit this is actually the derivative at a point. For linear axes
	 * this value never varies, and there is no error. For nonlinear axes this
	 * returns the linear scale between the points and thus may be inaccurate.
	 * Calls to this method may point out areas of code that should be generalized
	 * to work with nonlinear axes.
	 * </p>
	 */
	double averageScale(double rawValue1, double rawValue2);

	/** Creates an exact duplicate of this axis. */
	CalibratedAxis copy();

}
