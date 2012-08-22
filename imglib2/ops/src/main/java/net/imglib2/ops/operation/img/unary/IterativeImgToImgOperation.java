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

package net.imglib2.ops.operation.img.unary;

import net.imglib2.img.Img;
import net.imglib2.ops.img.ConcatenatedBufferedUnaryOperation;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class IterativeImgToImgOperation< TT extends RealType< TT >> extends ConcatenatedBufferedUnaryOperation< Img< TT >>
{

	private final UnaryOperation< Img< TT >, Img< TT >> m_op;

	private final int m_numIterations;

	public IterativeImgToImgOperation( UnaryOperation< Img< TT >, Img< TT >> op, int numIterations )
	{
		super( getOpArray( op, numIterations ) );
		m_op = op;
		m_numIterations = numIterations;

	}

	private static < TTT extends RealType< TTT >> UnaryOperation< Img< TTT >, Img< TTT >>[] getOpArray( UnaryOperation< Img< TTT >, Img< TTT >> op, int numIterations )
	{

		@SuppressWarnings( "unchecked" )
		UnaryOperation< Img< TTT >, Img< TTT >>[] ops = new UnaryOperation[ numIterations ];

		for ( int i = 0; i < numIterations; i++ )
		{
			ops[ i ] = op.copy();
		}

		return ops;
	}

	@Override
	protected Img< TT > getBuffer( Img< TT > input )
	{
		return input.factory().create( input, input.firstElement().createVariable() );
	}

	@Override
	public UnaryOperation< Img< TT >, Img< TT >> copy()
	{
		return new IterativeImgToImgOperation< TT >( m_op.copy(), m_numIterations );
	}
}
