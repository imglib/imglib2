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

package net.imglib2.type.numeric;

/**
 * TODO
 * 
 * @author Barry DeZonia
 */
public interface FloatingType<T extends FloatingType<T>>
{

	/**
	 * Fills self with the representation of pi for the given type.
	 */
	void PI();

	/**
	 * Fills self with the representation of e for the given type.
	 */
	void E();

	/**
	 * Fills self with result of raising e to the power of the passed as an input.
	 */
	void exp(T input);

	/**
	 * Fills self with result of taking the sqrt of the passed in input.
	 */
	void sqrt(T input);

	/**
	 * Fills self with result of taking the log of the passed in input.
	 */
	void log(T input);

	/**
	 * Fills self with result of raising the passed in input to the given power.
	 */
	void pow(T input, T power);

	/**
	 * Fills self with result of taking the log (of provided base) of the passed
	 * in input.
	 */
	void logBase(T input, T base);

	/**
	 * Fills self with result of taking the sin of the passed in input.
	 */
	void sin(T input);

	/**
	 * Fills self with result of taking the cos of the passed in input.
	 */
	void cos(T input);

	/**
	 * Fills self with result of taking the tan of the passed in input.
	 */
	void tan(T input);

	/**
	 * Fills self with result of taking the asin of the passed in input.
	 */
	void asin(T input);

	/**
	 * Fills self with result of taking the acos of the passed in input.
	 */
	void acos(T input);

	/**
	 * Fills self with result of taking the atan of the passed in input.
	 */
	void atan(T input);

	/**
	 * Fills self with result of taking the sinh of the passed in input.
	 */
	void sinh(T input);

	/**
	 * Fills self with result of taking the cosh of the passed in input.
	 */
	void cosh(T input);

	/**
	 * Fills self with result of taking the tanh of the passed in input.
	 */
	void tanh(T input);

	/**
	 * Fills self with result of taking the asinh of the passed in input.
	 */
	void asinh(T input);

	/**
	 * Fills self with result of taking the acosh of the passed in input.
	 */
	void acosh(T input);

	/**
	 * Fills self with result of taking the atanh of the passed in input.
	 */
	void atanh(T input);

}
