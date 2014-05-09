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
package net.imglib2.ops.img;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;

/**
 * @author Christian Dietz (University of Konstanz)
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public abstract class UnaryBinaryOperationAdapter< A, B, C, D > implements UnaryOperation< A, D >
{

	private final BinaryOperation< B, C, D > binaryOp;

	private final UnaryOperation< A, B > unaryOp1;

	private final UnaryOperation< A, C > unaryOp2;

	public UnaryBinaryOperationAdapter( UnaryOperation< A, B > op1, UnaryOperation< A, C > op2, BinaryOperation< B, C, D > binaryOp )
	{
		this.binaryOp = binaryOp;
		this.unaryOp1 = op1;
		this.unaryOp2 = op2;
	}

	@Override
	public D compute( A input, D output )
	{
		return binaryOp.compute( unaryOp1.compute( input, getOp1Buffer() ), unaryOp2.compute( input, getOp2Buffer() ), output );
	}

	@Override
	public UnaryOperation< A, D > copy()
	{
		return new UnaryBinaryOperationAdapter< A, B, C, D >( unaryOp1.copy(), unaryOp2.copy(), binaryOp.copy() )
		{

			@Override
			protected B getOp1Buffer()
			{
				return this.getOp1Buffer();
			}

			@Override
			protected C getOp2Buffer()
			{
				return this.getOp2Buffer();
			}

		};
	}

	protected abstract B getOp1Buffer();

	protected abstract C getOp2Buffer();

}
