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

package net.imglib2.ops.operation;

/**
 * Class should only be used if the output can't be known by the user in
 * advance. For example if the operation produces an double[] of unknown size,
 * this algorithm should know what to do.
 * <p>
 * If the output can be known, e.g. {@link net.imglib2.img.Img} to
 * {@link net.imglib2.img.Img} of same dimensionality {@link UnaryOperation}
 * should be used.
 * </p>
 * 
 * @author Christian Dietz (University of Konstanz)
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
	@Override
	UnaryOutputOperation< INPUT_TYPE, OUTPUT_TYPE > copy();
}
