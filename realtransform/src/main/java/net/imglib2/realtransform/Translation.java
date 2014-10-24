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

package net.imglib2.realtransform;

import net.imglib2.concatenate.Concatenable;
import net.imglib2.concatenate.PreConcatenable;

/**
 * 
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class Translation extends AbstractTranslation implements Concatenable< TranslationGet >, PreConcatenable< TranslationGet >
{
	final protected Translation inverse;

	protected Translation( final double[] t, final Translation inverse )
	{
		super( t, inverse.ds );
		this.inverse = inverse;
	}

	public Translation( final int n )
	{
		super( n );
		this.inverse = new Translation( new double[ n ], this );
	}

	public Translation( final double... t )
	{
		this( t.length );
		set( t );
	}

	/**
	 * Set the translation vector.
	 * 
	 * @param t
	 *            t.length <= the number of dimensions of this
	 *            {@link Translation}
	 */
	@Override
	public void set( final double... t )
	{
		assert t.length <= this.t.length : "Too many inputs.";
		
		for ( int d = 0; d < t.length; ++d )
		{
			this.t[ d ] = t[ d ];
			inverse.t[ d ] = -t[ d ];
		}
	}

	/**
	 * Set one value of the translation vector.
	 * 
	 * @param t
	 *            t.length <= the number of dimensions of this
	 *            {@link Translation}
	 */
	@Override
	public void set( final double t, final int d )
	{
		assert d >= 0 && d < numDimensions() : "Dimension index out of bounds.";

		this.t[ d ] = t;
		inverse.t[ d ] = -t;
	}

	@Override
	public Translation copy()
	{
		return new Translation( t );
	}

	@Override
	public Translation inverse()
	{
		return inverse;
	}

	@Override
	public Translation preConcatenate( final TranslationGet a )
	{
		for ( int d = 0; d < numDimensions(); ++d )
			set( t[ d ] + a.getTranslation( d ) );

		return this;
	}

	@Override
	public Class< TranslationGet > getPreConcatenableClass()
	{
		return TranslationGet.class;
	}

	@Override
	public Translation concatenate( final TranslationGet a )
	{
		return preConcatenate( a );
	}

	@Override
	public Class< TranslationGet > getConcatenableClass()
	{
		return TranslationGet.class;
	}
}
