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
 * 2-d arbitrary scaling.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class Scale2D extends AbstractScale
{
	final protected Scale2D inverse;

	protected Scale2D( final double[] s, final Scale2D inverse, final RealPoint[] ds )
	{
		super( s, ds );

		assert s.length == numDimensions(): "Input dimensions do not match or are not 2.";

		this.inverse = inverse;
	}

	public Scale2D( final double sx, final double sy )
	{
		super( new double[ 2 ], new RealPoint[ 2 ] );

		s[ 0 ] = sx;
		s[ 1 ] = sy;

		ds[ 0 ] = new RealPoint( sx, 0 );
		ds[ 1 ] = new RealPoint( 0, sy );

		final double[] si = new double[ 2 ];
		si[ 0 ] = 1.0 / s[ 0 ];
		si[ 1 ] = 1.0 / s[ 1 ];

		final RealPoint[] dis = new RealPoint[ 2 ];
		dis[ 0 ] = new RealPoint( si[ 0 ], 0 );
		dis[ 1 ] = new RealPoint( 0, si[ 1 ] );

		inverse = new Scale2D( si, this, dis );
	}

	public Scale2D( final double... s )
	{
		super( s.clone(), new RealPoint[ s.length ] );

		assert s.length == numDimensions(): "Input dimensions do not match or are not 2.";

		ds[ 0 ] = new RealPoint( s[ 0 ], 0 );
		ds[ 1 ] = new RealPoint( 0, s[ 1 ] );

		final double[] si = new double[ 2 ];
		si[ 0 ] = 1.0 / s[ 0 ];
		si[ 1 ] = 1.0 / s[ 1 ];

		final RealPoint[] dis = new RealPoint[ 2 ];
		dis[ 0 ] = new RealPoint( si[ 0 ], 0 );
		dis[ 1 ] = new RealPoint( 0, si[ 1 ] );

		inverse = new Scale2D( si, this, dis );
	}

	public void set( final double sx, final double sy )
	{
		s[ 0 ] = sx;
		s[ 1 ] = sy;

		inverse.s[ 0 ] = 1.0 / sx;
		inverse.s[ 1 ] = 1.0 / sy;

		ds[ 0 ].setPosition( sx, 0 );
		ds[ 1 ].setPosition( sy, 1 );

		inverse.ds[ 0 ].setPosition( inverse.s[ 0 ], 0 );
		inverse.ds[ 1 ].setPosition( inverse.s[ 1 ], 1 );
	}

	@Override
	public void set( final double... s )
	{
		assert s.length == numDimensions(): "Input dimensions do not match or are not 2.";

		this.s[ 0 ] = s[ 0 ];
		this.s[ 1 ] = s[ 1 ];

		inverse.s[ 0 ] = 1.0 / s[ 0 ];
		inverse.s[ 1 ] = 1.0 / s[ 1 ];

		ds[ 0 ].setPosition( s[ 0 ], 0 );
		ds[ 1 ].setPosition( s[ 1 ], 1 );

		inverse.ds[ 0 ].setPosition( inverse.s[ 0 ], 0 );
		inverse.ds[ 1 ].setPosition( inverse.s[ 1 ], 1 );
	}

	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		source[ 0 ] = target[ 0 ] / s[ 0 ];
		source[ 1 ] = target[ 1 ] / s[ 1 ];
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		source[ 0 ] = ( float ) ( target[ 0 ] / s[ 0 ] );
		source[ 1 ] = ( float ) ( target[ 1 ] / s[ 1 ] );
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= numDimensions() && target.numDimensions() >= numDimensions(): "Input dimensions too small.";

		source.setPosition( target.getDoublePosition( 0 ) / s[ 0 ], 0 );
		source.setPosition( target.getDoublePosition( 1 ) / s[ 1 ], 1 );
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		target[ 0 ] = source[ 0 ] * s[ 0 ];
		target[ 1 ] = source[ 1 ] * s[ 1 ];
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= numDimensions() && target.length >= numDimensions(): "Input dimensions too small.";

		target[ 0 ] = ( float ) ( source[ 0 ] * s[ 0 ] );
		target[ 1 ] = ( float ) ( source[ 1 ] * s[ 1 ] );
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= numDimensions() && target.numDimensions() >= numDimensions(): "Input dimensions too small.";

		target.setPosition( source.getDoublePosition( 0 ) * s[ 0 ], 0 );
		target.setPosition( source.getDoublePosition( 1 ) * s[ 1 ], 1 );
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
		final double[] matrix = new double[ 6 ];
		matrix[ 0 ] = s[ 0 ];
		matrix[ 4 ] = s[ 1 ];

		return matrix;
	}

	@Override
	public Scale2D inverse()
	{
		return inverse;
	}

	@Override
	public Scale2D copy()
	{
		return new Scale2D( s );
	}
}
