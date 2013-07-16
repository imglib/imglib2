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
	 * Fills result with the representation of pi for the given type.
	 */
	void PI(T result);

	/**
	 * Fills result with the representation of e for the given type.
	 */
	void E(T result);

	/**
	 * Fills result by raising e to the power represented by the current variable.
	 */
	void exp(T result);

	/**
	 * Fills result by taking the sqrt of the current variable.
	 */
	void sqrt(T result);

	/**
	 * Fills result by taking the log of the current variable.
	 */
	void log(T result);

	/**
	 * Fills result by raising the current variable to the b power.
	 */
	void pow(T b, T result);

	/**
	 * Fills result by taking the b-based log of the current variable.
	 */
	void logBase(T b, T result);

	/**
	 * Fills result by taking the sin of the current variable.
	 */
	void sin(T result);

	/**
	 * Fills result by taking the cos of the current variable.
	 */
	void cos(T result);

	/**
	 * Fills result by taking the tan of the current variable.
	 */
	void tan(T result);

	/**
	 * Fills result by taking the asin of the current variable.
	 */
	void asin(T result);

	/**
	 * Fills result by taking the acos of the current variable.
	 */
	void acos(T result);

	/**
	 * Fills result by taking the atan of the current variable.
	 */
	void atan(T result);

	/**
	 * Fills result by taking the sinh of the current variable.
	 */
	void sinh(T result);

	/**
	 * Fills result by taking the cosh of the current variable.
	 */
	void cosh(T result);

	/**
	 * Fills result by taking the tanh of the current variable.
	 */
	void tanh(T result);

	/**
	 * Fills result by taking the asinh of the current variable.
	 */
	void asinh(T result);

	/**
	 * Fills result by taking the acosh of the current variable.
	 */
	void acosh(T result);

	/**
	 * Fills result by taking the atanh of the current variable.
	 */
	void atanh(T result);

}
