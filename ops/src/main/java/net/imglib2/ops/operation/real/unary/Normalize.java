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
package net.imglib2.ops.operation.real.unary;

import net.imglib2.converter.Converter;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * @author Christian Dietz (University of Konstanz)
 * @author Martin Horn (University of Konstanz)
 * 
 * @param <I>
 * @param <O>
 */
public class Normalize< T extends RealType< T >> implements UnaryOperation< T, T >, Converter< T, T >
{

	private final double m_newMin;

	private final double m_factor;

	private final double m_oldMin;

	public Normalize( T oldMin, T oldMax, T newMin, T newMax )
	{
		this( oldMin.getRealDouble(), oldMax.getRealDouble(), newMin.getRealDouble(), newMax.getRealDouble() );
	}

	protected Normalize( double factor, double oldMin, double newMin )
	{
		m_oldMin = oldMin;
		m_newMin = newMin;
		m_factor = factor;
	}

	public Normalize( double oldMin, double oldMax, double newMin, double newMax )
	{
		this( normalizationFactor( oldMin, oldMax, newMin, newMax ), oldMin, newMin );
	}

	@Override
	public T compute( T input, T output )
	{
		output.setReal( ( input.getRealDouble() - m_oldMin ) * m_factor + m_newMin );
		return output;
	}

	@Override
	public UnaryOperation< T, T > copy()
	{
		return new Normalize< T >( m_factor, m_oldMin, m_newMin );
	}

	@Override
	public void convert( T input, T output )
	{
		compute( input, output );
	}

	public synchronized static double normalizationFactor( double oldMin, double oldMax, double newMin, double newMax )
	{
		return 1.0d / ( oldMax - oldMin ) * ( ( newMax - newMin ) );
	}

}
