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
package net.imglib2.ops.operation.real.unary;

import net.imglib2.converter.Converter;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * Scales the value of the input type (supposed to be lying within the origin
 * interval [oldMin,oldMax]) to the a value within the interval [newMin,newMax]
 * by a certain factor. If the the new value exceeds the interval
 * [newMin,newMax] it will be clipped.
 * 
 * @author Christian Dietz (University of Konstanz)
 * @author Martin Horn (University of Konstanz)
 * 
 * @param <T>
 */
public class Normalize< T extends RealType< T >> implements UnaryOperation< T, T >, Converter< T, T >
{

	private final double oldMin;

	private final double oldMax;

	private final double newMin;

	private final double newMax;

	private final double factor;

	/**
	 * The normalization factor is automatically determined from the input
	 * parameters types.
	 * 
	 * @param oldMin
	 * @param oldMax
	 * @param newMin
	 * @param newMax
	 */
	public Normalize( T oldMin, T oldMax, T newMin, T newMax )
	{
		this( oldMin.getRealDouble(), oldMax.getRealDouble(), newMin.getRealDouble(), newMax.getRealDouble() );
	}

	/**
	 * @param factor
	 * @param oldMin
	 * @param oldMax
	 * @param newMin
	 * @param newMax
	 */
	protected Normalize( double factor, double oldMin, double oldMax, double newMin, double newMax )
	{
		this.oldMin = oldMin;
		this.oldMax = oldMax;
		this.newMin = newMin;
		this.newMax = newMax;
		this.factor = factor;
	}

	/**
	 * The normalization factor is automatically determined from the input
	 * parameters.
	 * 
	 * @param oldMin
	 * @param oldMax
	 * @param newMin
	 * @param newMax
	 */
	public Normalize( double oldMin, double oldMax, double newMin, double newMax )
	{
		this( normalizationFactor( oldMin, oldMax, newMin, newMax ), oldMin, oldMax, newMin, newMax );
	}

	@Override
	public T compute( T input, T output )
	{
		output.setReal( Math.min( newMax, +Math.max( newMin, ( input.getRealDouble() - oldMin ) * factor + newMin ) ) );
		return output;
	}

	@Override
	public UnaryOperation< T, T > copy()
	{
		return new Normalize< T >( factor, oldMin, oldMax, newMin, newMax );
	}

	@Override
	public void convert( T input, T output )
	{
		compute( input, output );
	}

	/**
	 * Determines the factor to map the interval [oldMin, oldMax] to
	 * [newMin,newMax].
	 * 
	 * @param oldMin
	 * @param oldMax
	 * @param newMin
	 * @param newMax
	 * @return the normalization factor
	 */
	public synchronized static double normalizationFactor( double oldMin, double oldMax, double newMin, double newMax )
	{
		return 1.0d / ( oldMax - oldMin ) * ( ( newMax - newMin ) );
	}

}
