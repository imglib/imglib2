/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.ops.operation;


/**
 * Class should only be used if the output can't be known by the user in
 * advance. For example if the operation produces an double[] of unknown size,
 * this algorithm should know what to do.
 * 
 * If the output can be known, e.g. {@link Img<T>} to {@link Img<T>} of same
 * dimensionality {@link UnaryOperation} should be used.
 * 
 * @author dietzc (University of Konstanz)
 */
public interface UnaryOutputOperation< INPUT_TYPE, OUTPUT_TYPE > extends UnaryOperation< INPUT_TYPE, OUTPUT_TYPE >
{
	/**
	 * Creates an empty output for the given input.
	 * 
	 * @param in
	 *            Input to be processed
	 * @return Output object, which can be used to store the result
	 */
	OUTPUT_TYPE createEmptyOutput( INPUT_TYPE in );

	/**
	 * 
	 * @param in
	 *            Input to be processed
	 * @return Output object storing the result
	 * 
	 */
	OUTPUT_TYPE compute( INPUT_TYPE in );

	/**
	 * {@inheritDoc}
	 */
	UnaryOutputOperation< INPUT_TYPE, OUTPUT_TYPE > copy();
}
