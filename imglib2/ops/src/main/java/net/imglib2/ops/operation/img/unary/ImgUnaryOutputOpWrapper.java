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
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;

public class ImgUnaryOutputOpWrapper< T extends RealType< T >> implements UnaryOutputOperation< Img< T >, Img< T >>
{

	private final UnaryOperation< Img< T >, Img< T >> m_op;

	public ImgUnaryOutputOpWrapper( UnaryOperation< Img< T >, Img< T >> op )
	{
		m_op = op;
	}

	@Override
	public Img< T > compute( Img< T > input, Img< T > output )
	{
		return m_op.compute( input, output );
	}

	@Override
	public UnaryOutputOperation< Img< T >, Img< T >> copy()
	{
		return new ImgUnaryOutputOpWrapper< T >( m_op.copy() );
	}

	@Override
	public Img< T > createEmptyOutput( Img< T > in )
	{
		return in.factory().create( in, in.firstElement() );
	}

	@Override
	public Img< T > compute( Img< T > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}

	public < C > UnaryOutputOperation< C, Img< T >> concatenate( final UnaryOutputOperation< C, Img< T >> op )
	{
		return new ConcatenatedUnaryOutputOperation< C, Img< T >, Img< T >>( op, this );
	}

	public < C > UnaryOutputOperation< Img< T >, C > preConcatenate( final UnaryOutputOperation< Img< T >, C > op )
	{
		return new ConcatenatedUnaryOutputOperation< Img< T >, Img< T >, C >( this, op );
	}
}

class ConcatenatedUnaryOutputOperation< I, B, O > implements UnaryOutputOperation< I, O >
{

	private final UnaryOutputOperation< I, B > m_first;

	private final UnaryOutputOperation< B, O > m_rest;

	public ConcatenatedUnaryOutputOperation( UnaryOutputOperation< I, B > first, UnaryOutputOperation< B, O > rest )
	{
		super();
		m_first = first;
		m_rest = rest;
	}

	@Override
	public O compute( I input, O output )
	{
		return m_rest.compute( m_first.compute( input ), output );
	}

	@Override
	public UnaryOutputOperation< I, O > copy()
	{
		return new ConcatenatedUnaryOutputOperation< I, B, O >( m_first.copy(), m_rest.copy() );
	}

	@Override
	public O createEmptyOutput( I in )
	{
		return m_rest.createEmptyOutput( m_first.createEmptyOutput( in ) );
	}

	@Override
	public O compute( I in )
	{
		return m_rest.compute( m_first.compute( in ) );
	}
};
