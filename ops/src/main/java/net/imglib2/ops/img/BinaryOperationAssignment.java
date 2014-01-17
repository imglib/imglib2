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

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.Type;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public final class BinaryOperationAssignment< I extends Type< I >, V extends Type< V >, O extends Type< O >> implements BinaryOperation< IterableInterval< I >, IterableInterval< V >, IterableInterval< O >>
{
	/* Operation to be wrapped */
	private final BinaryOperation< I, V, O > m_op;

	public BinaryOperationAssignment( final net.imglib2.ops.operation.BinaryOperation< I, V, O > op )
	{
		m_op = op;
	}

	@Override
	public IterableInterval< O > compute( IterableInterval< I > input1, IterableInterval< V > input2, IterableInterval< O > output )
	{

		if ( !input1.iterationOrder().equals( input2.iterationOrder() ) || !input1.iterationOrder().equals( output.iterationOrder() ) ) { throw new IllegalArgumentException( "Intervals are not compatible" ); }

		Cursor< I > c1 = input1.cursor();
		Cursor< V > c2 = input2.cursor();
		Cursor< O > resC = output.cursor();

		while ( c1.hasNext() )
		{
			c1.fwd();
			c2.fwd();
			resC.fwd();
			m_op.compute( c1.get(), c2.get(), resC.get() );
		}
		return output;
	}

	@Override
	public BinaryOperation< IterableInterval< I >, IterableInterval< V >, IterableInterval< O >> copy()
	{
		return new BinaryOperationAssignment< I, V, O >( m_op.copy() );
	}

}
