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
 * 3-d arbitrary scaling.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class Scale3D extends AbstractScale implements Concatenable< ScaleGet >, PreConcatenable< ScaleGet >
{
	final protected Scale3D inverse;

	protected Scale3D( final double[] s, final Scale3D inverse, final RealPoint[] ds )
	{
		super( s, ds );

		assert s.length == numDimensions(): "Input dimensions do not match or are not 3.";

		this.inverse = inverse;
	}

	public Scale3D( final double sx, final double sy, final double sz )
	{
		super( new double[ 3 ], new RealPoint[ 3 ] );

		s[ 0 ] = sx;
		s[ 1 ] = sy;
		s[ 2 ] = sz;

		ds[ 0 ] = new RealPoint( sx, 0, 0 );
		ds[ 1 ] = new RealPoint( 0, sy, 0 );
		ds[ 2 ] = new RealPoint( 0, 0, sz );

		final double[] si = new double[ 3 ];
		si[ 0 ] = 1.0 / s[ 0 ];
		si[ 1 ] = 1.0 / s[ 1 ];
		si[ 2 ] = 1.0 / s[ 2 ];

		final RealPoint[] dis = new RealPoint[ 3 ];
		dis[ 0 ] = new RealPoint( si[ 0 ], 0, 0 );
		dis[ 1 ] = new RealPoint( 0, si[ 1 ], 0 );
		dis[ 2 ] = new RealPoint( 0, 0, si[ 2 ] );

		inverse = new Scale3D( si, this, dis );
	}

	public Scale3D( final double... s )
	{
		super( s.clone(), new RealPoint[ s.length ] );

		assert s.length == numDimensions(): "Input dimensions do not match or are not 3.";

		ds[ 0 ] = new RealPoint( s[ 0 ], 0, 0 );
		ds[ 1 ] = new RealPoint( 0, s[ 1 ], 0 );
		ds[ 2 ] = new RealPoint( 0, 0, s[ 2 ] );

		final double[] si = new double[ 3 ];
		si[ 0 ] = 1.0 / s[ 0 ];
		si[ 1 ] = 1.0 / s[ 1 ];
		si[ 2 ] = 1.0 / s[ 2 ];

		final RealPoint[] dis = new RealPoint[ 3 ];
		dis[ 0 ] = new RealPoint( si[ 0 ], 0, 0 );
		dis[ 1 ] = new RealPoint( 0, si[ 1 ], 0 );
		dis[ 2 ] = new RealPoint( 0, 0, si[ 2 ] );

		inverse = new Scale3D( si, this, dis );
	}

	public void set( final double sx, final double sy, final double sz )
	{
		s[ 0 ] = sx;
		s[ 1 ] = sy;
		s[ 2 ] = sz;

		inverse.s[ 0 ] = 1.0 / sx;
		inverse.s[ 1 ] = 1.0 / sy;
		inverse.s[ 2 ] = 1.0 / sz;

		ds[ 0 ].setPosition( sx, 0 );
		ds[ 1 ].setPosition( sy, 1 );
		ds[ 2 ].setPosition( sz, 2 );

		inverse.ds[ 0 ].setPosition( inverse.s[ 0 ], 0 );
		inverse.ds[ 1 ].setPosition( inverse.s[ 1 ], 1 );
		inverse.ds[ 2 ].setPosition( inverse.s[ 2 ], 2 );
	}

	@Override
	public void set( final double... s )
	{
		assert s.length == numDimensions(): "Input dimensions do not match or are not 3.";

		this.s[ 0 ] = s[ 0 ];
		this.s[ 1 ] = s[ 1 ];
		this.s[ 2 ] = s[ 2 ];

		inverse.s[ 0 ] = 1.0 / s[ 0 ];
		inverse.s[ 1 ] = 1.0 / s[ 1 ];
		inverse.s[ 2 ] = 1.0 / s[ 2 ];

		ds[ 0 ].setPosition( s[ 0 ], 0 );
		ds[ 1 ].setPosition( s[ 1 ], 1 );
		ds[ 2 ].setPosition( s[ 2 ], 2 );

		inverse.ds[ 0 ].setPosition( inverse.s[ 0 ], 0 );
		inverse.ds[ 1 ].setPosition( inverse.s[ 1 ], 1 );
		inverse.ds[ 2 ].setPosition( inverse.s[ 2 ], 2 );
	}

	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		source[ 0 ] = target[ 0 ] / s[ 0 ];
		source[ 1 ] = target[ 1 ] / s[ 1 ];
		source[ 2 ] = target[ 2 ] / s[ 2 ];
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		source[ 0 ] = ( float )( target[ 0 ] / s[ 0 ] );
		source[ 1 ] = ( float )( target[ 1 ] / s[ 1 ] );
		source[ 2 ] = ( float )( target[ 2 ] / s[ 2 ] );
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= numDimensions() && target.numDimensions() >= numDimensions(): "Input dimensions too small.";

		source.setPosition( target.getDoublePosition( 0 ) / s[ 0 ], 0 );
		source.setPosition( target.getDoublePosition( 1 ) / s[ 1 ], 1 );
		source.setPosition( target.getDoublePosition( 2 ) / s[ 2 ], 2 );
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		target[ 0 ] = source[ 0 ] * s[ 0 ];
		target[ 1 ] = source[ 1 ] * s[ 1 ];
		target[ 2 ] = source[ 2 ] * s[ 2 ];
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		target[ 0 ] = ( float )( source[ 0 ] * s[ 0 ] );
		target[ 1 ] = ( float )( source[ 1 ] * s[ 1 ] );
		target[ 2 ] = ( float )( source[ 2 ] * s[ 2 ] );
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= numDimensions() && target.numDimensions() >= numDimensions(): "Input dimensions too small.";

		target.setPosition( source.getDoublePosition( 0 ) * s[ 0 ], 0 );
		target.setPosition( source.getDoublePosition( 1 ) * s[ 1 ], 1 );
		target.setPosition( source.getDoublePosition( 2 ) * s[ 2 ], 2 );
	}

	@Override
	public double get( final int row, final int column )
	{
		assert row >= 0 && row < numDimensions(): "Dimension index out of bounds.";

		return row == column ? s[ row ] : 0;
	}

	@Override
	public double[] getRowPackedCopy()
	{
		final double[] matrix = new double[ 12 ];
		matrix[ 0 ] = s[ 0 ];
		matrix[ 5 ] = s[ 1 ];
		matrix[ 10 ] = s[ 2 ];

		return matrix;
	}

	@Override
	public Scale3D inverse()
	{
		return inverse;
	}

	@Override
	public Scale3D copy()
	{
		return new Scale3D( s );
	}

	@Override
	public Scale3D preConcatenate( final ScaleGet a )
	{
		set(
				s[ 0 ] * a.getScale( 0 ),
				s[ 1 ] * a.getScale( 1 ),
				s[ 2 ] * a.getScale( 2 ) );

		return this;
	}

	@Override
	public Class< ScaleGet > getPreConcatenableClass()
	{
		return ScaleGet.class;
	}

	@Override
	public Scale3D concatenate( final ScaleGet a )
	{
		return preConcatenate( a );
	}

	@Override
	public Class< ScaleGet > getConcatenableClass()
	{
		return ScaleGet.class;
	}
}
