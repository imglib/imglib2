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

package net.imglib2.ops.operation.iterableinterval.unary.multilevelthresholder;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;

/**
 * @author Markus Friedrich (University of Konstanz)
 */
public class MultilevelThresholderOp< T extends RealType< T >, IN extends IterableInterval< T >, OUT extends IterableInterval< T >> implements UnaryOperation< IN, OUT >
{

	private final UnaryOutputOperation< IN, ThresholdValueCollection > m_op;

	public MultilevelThresholderOp( UnaryOutputOperation< IN, ThresholdValueCollection > op )
	{
		m_op = op;
	}

	@Override
	public UnaryOperation< IN, OUT > copy()
	{
		return new MultilevelThresholderOp< T, IN, OUT >( m_op.copy() );
	}

	@Override
	public OUT compute( IN input, OUT out )
	{

		if ( !input.iterationOrder().equals( out.iterationOrder() ) ) { throw new IllegalArgumentException( "IterationOrders not the same in StandardMultilevelThresholder" ); }

		Cursor< T > outputCursor = out.cursor();
		Cursor< T > inputCursor = input.cursor();

		ThresholdValueCollection thresholdValues = m_op.compute( input );

		double[] sortedValues = thresholdValues.getSortedVector();

		while ( inputCursor.hasNext() )
		{
			outputCursor.fwd();

			double value = inputCursor.next().getRealDouble();

			int idx = 0;
			for ( int d = 0; d < sortedValues.length; d++ )
			{
				if ( value > sortedValues[ d ] )
				{
					idx++;
				}
				else
				{
					break;
				}
			}
			outputCursor.get().setReal( idx );
		}
		return out;
	}

}
