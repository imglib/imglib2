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
package net.imglib2.ops.operation.iterable.binary.localthresholder;

import java.util.Iterator;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
/**
 * @author Markus Friedrich (University of Konstanz)
 */
public class Sauvola< T extends RealType< T >> implements BinaryOperation< Iterator<T>, T, BitType >
{

	private double m_r;

	private double m_k;

	public Sauvola( double k, double r )
	{
		m_r = r;
		m_k = k;
	}

	@Override
	public BitType compute( Iterator<T> input, T px, BitType output )
	{
		double sum = 0;
		double sumSqr = 0;
		int n = 0; 

		while ( input.hasNext() )
		{
			double val = input.next().getRealDouble();
			++n;
			sum += val;
			sumSqr += val * val;
		}

		double mean = sum / n;
		double variance = ( sumSqr - ( sum * mean ) ) / ( n - 1 );

		output.set( px.getRealDouble() > mean * ( 1 + m_k * ( Math.sqrt( variance ) / m_r - 1 ) ) );

		return output;
	}

	@Override
	public BinaryOperation<Iterator<T>, T, BitType > copy()
	{
		return new Sauvola< T >( m_k, m_r );
	}

}
