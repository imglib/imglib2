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
 * A Euclidean space whose dimensions have units and calibrations.
 * 
 * @author Curtis Rueden
 * @see CalibratedAxis
 */
public interface CalibratedSpace<A extends CalibratedAxis> extends
	TypedSpace<A>
{
	/** Gets the space's physical calibration at the given dimension. */
	double calibration(int d);

	/** Copies the space's physical calibration into the given array. */
	void calibration(double[] cal);

	/** Copies the space's physical calibration into the given array. */
	void calibration(float[] cal);

	/** Sets the physical calibration for the given dimension. */
	void setCalibration(double cal, int d);

	/** Sets the physical calibration for all dimensions. */
	void setCalibration(double[] cal);

	/** Sets the physical calibration for all dimensions. */
	void setCalibration(float[] cal);

	/** Gets the physical unit for the given dimension. */
	String unit(int d);

	/** Sets the physical unit for the given dimension. */
	void setUnit(String unit, int d);

}
