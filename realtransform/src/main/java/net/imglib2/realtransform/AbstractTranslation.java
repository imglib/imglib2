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

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;

/**
 * 
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
abstract public class AbstractTranslation implements InvertibleRealTransform, TranslationGet
{
	final protected double[] t;

	final protected RealPoint[] ds;

	protected AbstractTranslation( final double[] t, final RealPoint[] ds )
	{
		assert t.length == ds.length: "Input dimensions do not match.";

		this.t = t;
		this.ds = ds;
	}

	public AbstractTranslation( final int n )
	{
		t = new double[ n ];

		ds = new RealPoint[ n ];
		for ( int d = 0; d < n; ++d )
		{
			ds[ d ] = new RealPoint( n );
			ds[ d ].setPosition( 1, d );
		}
	}

	public AbstractTranslation( final double... t )
	{
		this( t.length );
		set( t );
	}

	/**
	 * Set the translation vector.
	 * 
	 * @param t
	 *            t.length <= the number of dimensions of this
	 *            {@link AbstractTranslation}
	 */
	abstract public void set( final double... t );

	/**
	 * Set one value of the translation vector.
	 * 
	 * @param t
	 *            t.length <= the number of dimensions of this
	 *            {@link AbstractTranslation}
	 */
	abstract public void set( final double t, final int d );

	@Override
	public int numDimensions()
	{
		return t.length;
	}

	@Override
	public int numSourceDimensions()
	{
		return t.length;
	}

	@Override
	public int numTargetDimensions()
	{
		return t.length;
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		assert source.length >= t.length && target.length >= t.length: "Input dimensions too small.";

		for ( int d = 0; d < t.length; ++d )
			target[ d ] = source[ d ] + t[ d ];
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= t.length && target.length >= t.length: "Input dimensions too small.";

		for ( int d = 0; d < t.length; ++d )
			target[ d ] = ( float ) ( source[ d ] + t[ d ] );
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= t.length && target.numDimensions() >= t.length: "Input dimensions too small.";

		for ( int d = 0; d < t.length; ++d )
			target.setPosition( source.getDoublePosition( d ) + t[ d ], d );
	}

	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= t.length && target.length >= t.length: "Input dimensions too small.";

		for ( int d = 0; d < t.length; ++d )
			source[ d ] = target[ d ] - t[ d ];
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= t.length && target.length >= t.length: "Input dimensions too small.";

		for ( int d = 0; d < t.length; ++d )
			source[ d ] = ( float ) ( target[ d ] - t[ d ] );
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= t.length && target.numDimensions() >= t.length: "Input dimensions too small.";

		for ( int d = 0; d < t.length; ++d )
			source.setPosition( target.getDoublePosition( d ) - t[ d ], d );

	}

	@Override
	abstract public AbstractTranslation copy();

	@Override
	public double get( final int row, final int column )
	{
		return column == t.length ? t[ row ] : row == column ? 1 : 0;
	}

	@Override
	public double[] getRowPackedCopy()
	{
		final int stepT = t.length + 1;
		final int stepI = stepT + 1;
		final double[] matrix = new double[ t.length * t.length + t.length ];
		for ( int d = 0; d < t.length; ++d )
		{
			matrix[ d * stepI ] = 1;
			matrix[ d * stepT + t.length ] = t[ d ];
		}
		return matrix;
	}

	@Override
	public RealLocalizable d( final int d )
	{
		assert d >= 0 && d < numDimensions(): "Dimension index out of bounds.";

		return ds[ d ];
	}

	@Override
	public double getTranslation( final int d )
	{
		assert d >= 0 && d < numDimensions(): "Dimension index out of bounds.";

		return t[ d ];
	}

	@Override
	public double[] getTranslationCopy()
	{
		return t.clone();
	}

	@Override
	abstract public AbstractTranslation inverse();
}
