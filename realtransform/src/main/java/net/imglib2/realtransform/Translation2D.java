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
import net.imglib2.concatenate.Concatenable;
import net.imglib2.concatenate.PreConcatenable;

/**
 * 2-dimensional translation.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class Translation2D extends AbstractTranslation implements Concatenable< TranslationGet >, PreConcatenable< TranslationGet >
{
	final static protected RealPoint[] constDs = new RealPoint[ 2 ];
	{
		constDs[ 0 ] = new RealPoint( 1.0, 0.0 );
		constDs[ 1 ] = new RealPoint( 0.0, 1.0 );
	}

	final protected Translation2D inverse;

	protected Translation2D( final double[] t, final Translation2D inverse )
	{
		super( t, constDs );

		assert t.length == numDimensions(): "Input dimensions do not match or are not 2.";

		this.inverse = inverse;
	}

	public Translation2D()
	{
		super( new double[ 2 ], constDs );
		inverse = new Translation2D( new double[ 2 ], this );
	}

	public Translation2D( final double tx, final double ty )
	{
		this();
		set( tx, ty );
	}

	public Translation2D( final double... t )
	{
		this();
		set( t );
	}

	public void set( final double tx, final double ty )
	{
		t[ 0 ] = tx;
		t[ 1 ] = ty;

		inverse.t[ 0 ] = -tx;
		inverse.t[ 1 ] = -ty;
	}

	/**
	 * Set the translation vector.
	 * 
	 * @param t
	 *            t.length <= the number of dimensions of this
	 *            {@link Translation2D}
	 */
	@Override
	public void set( final double... t )
	{
		assert t.length <= 2 : "Too many inputs.";
		
		try
		{
			this.t[ 0 ] = t[ 0 ];
			this.t[ 1 ] = t[ 1 ];

			inverse.t[ 0 ] = -t[ 0 ];
			inverse.t[ 1 ] = -t[ 1 ];
		}
		catch ( final ArrayIndexOutOfBoundsException e )
		{}
	}

	@Override
	public void set( final double t, final int d )
	{
		assert d >= 0 && d < numDimensions(): "Dimensions index out of bounds.";

		this.t[ d ] = t;
		inverse.t[ d ] = -t;
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		target[ 0 ] = source[ 0 ] + t[ 0 ];
		target[ 1 ] = source[ 1 ] + t[ 1 ];
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		target[ 0 ] = ( float ) ( source[ 0 ] + t[ 0 ] );
		target[ 1 ] = ( float ) ( source[ 1 ] + t[ 1 ] );
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= numDimensions() && target.numDimensions() >= numDimensions(): "Input dimensions too small.";

		target.setPosition( source.getDoublePosition( 0 ) + t[ 0 ], 0 );
		target.setPosition( source.getDoublePosition( 1 ) + t[ 1 ], 1 );
	}

	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		source[ 0 ] = target[ 0 ] - t[ 0 ];
		source[ 1 ] = target[ 1 ] - t[ 1 ];
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		source[ 0 ] = ( float ) ( target[ 0 ] - t[ 0 ] );
		source[ 1 ] = ( float ) ( target[ 1 ] - t[ 1 ] );
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= numDimensions() && target.numDimensions() >= numDimensions(): "Input dimensions too small.";

		source.setPosition( target.getDoublePosition( 0 ) - t[ 0 ], 0 );
		source.setPosition( target.getDoublePosition( 1 ) - t[ 1 ], 1 );
	}

	@Override
	public Translation2D copy()
	{
		return new Translation2D( t );
	}

	@Override
	public double[] getRowPackedCopy()
	{
		final double[] matrix = new double[ 6 ];

		matrix[ 0 ] = 1;
		matrix[ 4 ] = 1;

		matrix[ 2 ] = t[ 0 ];
		matrix[ 5 ] = t[ 1 ];

		return matrix;
	}

	@Override
	public Translation2D inverse()
	{
		return inverse;
	}

	@Override
	public Translation2D preConcatenate( final TranslationGet a )
	{
		set(
				t[ 0 ] + a.getTranslation( 0 ),
				t[ 1 ] + a.getTranslation( 1 ) );

		return this;
	}

	@Override
	public Class< TranslationGet > getPreConcatenableClass()
	{
		return TranslationGet.class;
	}

	@Override
	public Translation2D concatenate( final TranslationGet a )
	{
		return preConcatenate( a );
	}

	@Override
	public Class< TranslationGet > getConcatenableClass()
	{
		return TranslationGet.class;
	}
}
