package net.imglib2.ops.operation.iterableinterval.unary;

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

import java.util.Iterator;

import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.ValuePair;

/**
 * TODO
 * 
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <T>
 *            TODO
 */
public final class MinMax< T extends RealType< T >> implements UnaryOutputOperation< Iterable< T >, ValuePair< T, T >>
{

	public MinMax()
	{}

	@Override
	public ValuePair< T, T > createEmptyOutput( Iterable< T > op )
	{
		final T t = op.iterator().next();
		return new ValuePair< T, T >( t.createVariable(), t.createVariable() );
	}

	@Override
	public ValuePair< T, T > compute( Iterable< T > op, ValuePair< T, T > r )
	{

		final Iterator< T > it = op.iterator();
		r.a.setReal( r.a.getMaxValue() );
		r.b.setReal( r.b.getMinValue() );
		while ( it.hasNext() )
		{
			T i = it.next();
			if ( r.a.compareTo( i ) > 0 )
				r.a.set( i );
			if ( r.b.compareTo( i ) < 0 )
				r.b.set( i );
		}

		return r;
	}

	@Override
	public UnaryOutputOperation< Iterable< T >, ValuePair< T, T >> copy()
	{
		return new MinMax< T >();
	}

	@Override
	public ValuePair< T, T > compute( Iterable< T > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}
}
