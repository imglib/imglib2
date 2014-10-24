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
 * 2d-affine transformation.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class AffineTransform2D implements AffineGet, AffineSet, Concatenable< AffineGet >, PreConcatenable< AffineGet >
{
	final static protected class AffineMatrix2D
	{
		public double
				m00 = 1.0, m01 = 0.0, m02 = 0.0,
				m10 = 0.0, m11 = 1.0, m12 = 0.0;

		public AffineMatrix2D copy()
		{
			final AffineMatrix2D copy = new AffineMatrix2D();

			copy.m00 = m00;
			copy.m01 = m01;
			copy.m02 = m02;

			copy.m10 = m10;
			copy.m11 = m11;
			copy.m12 = m12;

			return copy;
		}

		final protected double det()
		{
			return m00 * m11 - m01 * m10;
		}
	}

	final protected AffineMatrix2D a;

	final protected RealPoint d0;

	final protected RealPoint d1;

	final protected RealPoint[] ds;

	final protected AffineTransform2D inverse;

	public AffineTransform2D()
	{
		this( new AffineMatrix2D() );
	}

	protected AffineTransform2D( final AffineMatrix2D a )
	{
		this.a = a;

		d0 = new RealPoint( 2 );
		d1 = new RealPoint( 2 );
		ds = new RealPoint[] { d0, d1 };
		updateDs();

		inverse = new AffineTransform2D( this );
		invert();
		inverse.updateDs();
	}

	protected AffineTransform2D( final AffineTransform2D inverse )
	{
		this.inverse = inverse;

		a = new AffineMatrix2D();

		d0 = new RealPoint( 2 );
		d1 = new RealPoint( 2 );
		ds = new RealPoint[] { d0, d1 };
	}

	protected void invert()
	{
		final double det = a.det();

		/* similar to Jama, throw a RunTimeException for singular matrices. */
		if ( det == 0 )
			throw new RuntimeException( "Matrix is singular." );

		final double idet = 1.0 / det;

		inverse.a.m00 = a.m11 * idet;
		inverse.a.m01 = -a.m01 * idet;
		inverse.a.m02 = ( a.m01 * a.m12 - a.m02 * a.m11 ) * idet;

		inverse.a.m10 = -a.m10 * idet;
		inverse.a.m11 = a.m00 * idet;
		inverse.a.m12 = ( a.m02 * a.m10 - a.m00 * a.m12 ) * idet;
	}

	protected void updateDs()
	{
		d0.setPosition( a.m00, 0 );
		d0.setPosition( a.m10, 1 );

		d1.setPosition( a.m01, 0 );
		d1.setPosition( a.m11, 1 );
	}

	@Override
	final public void apply( final double[] source, final double[] target )
	{
		assert source.length >= 2 && target.length >= 2: "2d affine transformations can be applied to 2d coordinates only.";

		/* source and target may be the same vector, so do not write into target before done with source */
		final double tmp = source[ 0 ] * a.m00 + source[ 1 ] * a.m01 + a.m02;
		target[ 1 ] = source[ 0 ] * a.m10 + source[ 1 ] * a.m11 + a.m12;
		target[ 0 ] = tmp;
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= 2 && target.length >= 2: "2d affine transformations can be applied to 2d coordinates only.";

		/* source and target may be the same vector, so do not write into target before done with source */
		final float tmp = ( float )( source[ 0 ] * a.m00 + source[ 1 ] * a.m01 + a.m02 );
		target[ 1 ] = ( float )( source[ 0 ] * a.m10 + source[ 1 ] * a.m11 + a.m12 );
		target[ 0 ] = tmp;
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= 2 && target.numDimensions() >= 2: "2d affine transformations can be applied to 2d coordinates only.";

		/* source and target may be the same vector, so do not write into target before done with source */
		final double s0 = source.getDoublePosition( 0 );
		final double s1 = source.getDoublePosition( 1 );
		target.setPosition( s0 * a.m00 + s1 * a.m01 + a.m02, 0 );
		target.setPosition( s0 * a.m10 + s1 * a.m11 + a.m12, 1 );
	}

	@Override
	final public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= 2 && target.length >= 2: "2d affine transformations can be applied to 2d coordinates only.";

		/* source and target may be the same vector, so do not write into source before done with target */
		final double tmp = target[ 0 ] * inverse.a.m00 + target[ 1 ] * inverse.a.m01 + inverse.a.m02;
		source[ 1 ] = target[ 0 ] * inverse.a.m10 + target[ 1 ] * inverse.a.m11 + inverse.a.m12;
		source[ 0 ] = tmp;
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= 2 && target.length >= 2: "2d affine transformations can be applied to 2d coordinates only.";

		/* source and target may be the same vector, so do not write into source before done with target */
		final float tmp = ( float ) ( target[ 0 ] * inverse.a.m00 + target[ 1 ] * inverse.a.m01 + inverse.a.m02 );
		source[ 1 ] = ( float ) ( target[ 0 ] * inverse.a.m10 + target[ 1 ] * inverse.a.m11 + inverse.a.m12 );
		source[ 0 ] = tmp;
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= 2 && target.numDimensions() >= 2: "2d affine transformations can be applied to 2d coordinates only.";

		/* source and target may be the same vector, so do not write into source before done with target */
		final double t0 = target.getDoublePosition( 0 );
		final double t1 = target.getDoublePosition( 1 );
		source.setPosition( t0 * inverse.a.m00 + t1 * inverse.a.m01 + inverse.a.m02, 0 );
		source.setPosition( t0 * inverse.a.m10 + t1 * inverse.a.m11 + inverse.a.m12, 1 );
	}

	@Override
	final public AffineTransform2D concatenate( final AffineGet affine )
	{
		assert affine.numSourceDimensions() >= 2: "Only >=2d affine transformations can be concatenated to a 2d affine transformation.";

		final double am00 = affine.get( 0, 0 );
		final double am01 = affine.get( 0, 1 );
		final double am02 = affine.get( 0, 2 );

		final double am10 = affine.get( 1, 0 );
		final double am11 = affine.get( 1, 1 );
		final double am12 = affine.get( 1, 2 );

		final double a00 = a.m00 * am00 + a.m01 * am10;
		final double a01 = a.m00 * am01 + a.m01 * am11;
		final double a02 = a.m00 * am02 + a.m01 * am12 + a.m02;

		final double a10 = a.m10 * am00 + a.m11 * am10;
		final double a11 = a.m10 * am01 + a.m11 * am11;
		final double a12 = a.m10 * am02 + a.m11 * am12 + a.m12;

		a.m00 = a00;
		a.m01 = a01;
		a.m02 = a02;

		a.m10 = a10;
		a.m11 = a11;
		a.m12 = a12;

		invert();
		updateDs();
		inverse.updateDs();

		return this;
	}

	final public AffineTransform2D concatenate( final AffineTransform2D model )
	{
		final double a00 = a.m00 * model.a.m00 + a.m01 * model.a.m10;
		final double a01 = a.m00 * model.a.m01 + a.m01 * model.a.m11;
		final double a02 = a.m00 * model.a.m02 + a.m01 * model.a.m12 + a.m02;

		final double a10 = a.m10 * model.a.m00 + a.m11 * model.a.m10;
		final double a11 = a.m10 * model.a.m01 + a.m11 * model.a.m11;
		final double a12 = a.m10 * model.a.m02 + a.m11 * model.a.m12 + a.m12;

		a.m00 = a00;
		a.m01 = a01;
		a.m02 = a02;

		a.m10 = a10;
		a.m11 = a11;
		a.m12 = a12;

		invert();
		updateDs();
		inverse.updateDs();

		return this;
	}

	@Override
	public AffineTransform2D copy()
	{
		final AffineMatrix2D ma = new AffineMatrix2D();
		ma.m00 = a.m00;
		ma.m10 = a.m10;

		ma.m01 = a.m01;
		ma.m11 = a.m11;

		ma.m02 = a.m02;
		ma.m12 = a.m12;

		return new AffineTransform2D( ma );
	}

	@Override
	public RealLocalizable d( final int d )
	{
		return ds[ d ];
	}

	@Override
	public double get( final int row, final int column )
	{
		assert row >= 0 && row < 2 && column >= 0 && column < 3: "Index out of bounds, a 3d affine matrix is a 2x3 matrix.";

		switch ( row )
		{
		case 0:
			switch ( column )
			{
			case 0:
				return a.m00;
			case 1:
				return a.m01;
			default:
				return a.m02;
			}
		default:
			switch ( column )
			{
			case 0:
				return a.m10;
			case 1:
				return a.m11;
			default:
				return a.m12;
			}
		}
	}

	@Override
	public double[] getRowPackedCopy()
	{
		return new double[] {
				a.m00, a.m01, a.m02,
				a.m10, a.m11, a.m12,
		};
	}

	@Override
	public Class< AffineGet > getConcatenableClass()
	{
		return AffineGet.class;
	}

	@Override
	public Class< AffineGet > getPreConcatenableClass()
	{
		return AffineGet.class;
	}

	@Override
	public AffineTransform2D inverse()
	{
		return inverse;
	}

	@Override
	public int numDimensions()
	{
		return 2;
	}

	@Override
	public int numSourceDimensions()
	{
		return 2;
	}

	@Override
	public int numTargetDimensions()
	{
		return 2;
	}

	@Override
	final public AffineTransform2D preConcatenate( final AffineGet affine )
	{
		assert affine.numSourceDimensions() >= 2: "Only >=2d affine transformations can be pre-concatenated to a 2d affine transformation.";

		final double am00 = affine.get( 0, 0 );
		final double am01 = affine.get( 0, 1 );
		final double am02 = affine.get( 0, 2 );

		final double am10 = affine.get( 1, 0 );
		final double am11 = affine.get( 1, 1 );
		final double am12 = affine.get( 1, 2 );

		final double a00 = am00 * a.m00 + am01 * a.m10;
		final double a01 = am00 * a.m01 + am01 * a.m11;
		final double a02 = am00 * a.m02 + am01 * a.m12 + am02;

		final double a10 = am10 * a.m00 + am11 * a.m10;
		final double a11 = am10 * a.m01 + am11 * a.m11;
		final double a12 = am10 * a.m02 + am11 * a.m12 + am12;

		a.m00 = a00;
		a.m01 = a01;
		a.m02 = a02;

		a.m10 = a10;
		a.m11 = a11;
		a.m12 = a12;

		invert();
		updateDs();
		inverse.updateDs();

		return this;
	}

	final public AffineTransform2D preConcatenate( final AffineTransform2D affine )
	{
		final double a00 = affine.a.m00 * a.m00 + affine.a.m01 * a.m10;
		final double a01 = affine.a.m00 * a.m01 + affine.a.m01 * a.m11;
		final double a02 = affine.a.m00 * a.m02 + affine.a.m01 * a.m12 + affine.a.m02;

		final double a10 = affine.a.m10 * a.m00 + affine.a.m11 * a.m10;
		final double a11 = affine.a.m10 * a.m01 + affine.a.m11 * a.m11;
		final double a12 = affine.a.m10 * a.m02 + affine.a.m11 * a.m12 + affine.a.m12;

		a.m00 = a00;
		a.m01 = a01;
		a.m02 = a02;

		a.m10 = a10;
		a.m11 = a11;
		a.m12 = a12;

		invert();
		updateDs();
		inverse.updateDs();

		return this;
	}

	/**
	 * Rotate
	 * 
	 * @param d
	 *            angle in radians
	 */
	public void rotate( final double d )
	{
		final double dcos = Math.cos( d );
		final double dsin = Math.sin( d );

		final double a00 = dcos * a.m00 - dsin * a.m10;
		final double a01 = dcos * a.m01 - dsin * a.m11;
		final double a02 = dcos * a.m02 - dsin * a.m12;

		final double a10 = dsin * a.m00 + dcos * a.m10;
		final double a11 = dsin * a.m01 + dcos * a.m11;
		final double a12 = dsin * a.m02 + dcos * a.m12;
		
		a.m00 = a00;
		a.m01 = a01;
		a.m02 = a02;

		a.m10 = a10;
		a.m11 = a11;
		a.m12 = a12;

		invert();
		updateDs();
		inverse.updateDs();
	}

	/**
	 * Translate
	 * 
	 * @param t
	 *            2d translation vector
	 * 
	 */
	public void translate( final double... t )
	{
		assert t.length == 2: "2d affine transformations can be translated by 2d vector only.";
		a.m02 += t[ 0 ];
		a.m12 += t[ 1 ];

		invert();
		updateDs();
		inverse.updateDs();
	}

	/**
	 * Scale
	 * 
	 * @param d
	 *            scale factor
	 * 
	 */
	public void scale( final double d )
	{
		a.m00 *= d;
		a.m01 *= d;
		a.m02 *= d;
		a.m10 *= d;
		a.m11 *= d;
		a.m12 *= d;

		invert();
		updateDs();
		inverse.updateDs();
	}

	final public void set( final AffineTransform2D m )
	{
		a.m00 = m.a.m00;
		a.m10 = m.a.m10;
		a.m01 = m.a.m01;
		a.m11 = m.a.m11;
		a.m02 = m.a.m02;
		a.m12 = m.a.m12;

		inverse.a.m00 = m.inverse.a.m00;
		inverse.a.m10 = m.inverse.a.m10;
		inverse.a.m01 = m.inverse.a.m01;
		inverse.a.m11 = m.inverse.a.m11;
		inverse.a.m02 = m.inverse.a.m02;
		inverse.a.m12 = m.inverse.a.m12;

		updateDs();
		inverse.updateDs();
	}

	public void toArray( final double[] data )
	{
		data[ 0 ] = a.m00;
		data[ 1 ] = a.m01;
		data[ 3 ] = a.m02;
		data[ 4 ] = a.m10;
		data[ 6 ] = a.m11;
		data[ 7 ] = a.m12;
	}

	public void toMatrix( final double[][] data )
	{
		data[ 0 ][ 0 ] = a.m00;
		data[ 0 ][ 1 ] = a.m01;
		data[ 0 ][ 2 ] = a.m02;
		data[ 1 ][ 0 ] = a.m10;
		data[ 1 ][ 1 ] = a.m11;
		data[ 1 ][ 2 ] = a.m12;
	}

	@Override
	final public String toString()
	{
		return "2d-affine: (" +
				a.m00 + ", " + a.m01 + ", " + a.m02 + ", " +
				a.m10 + ", " + a.m11 + ", " + a.m12 + ")";
	}

	@Override
	public void set( final double value, final int row, final int column )
	{
		assert row >= 0 && row < 2 && column >= 0 && column < 3: "Index out of bounds, a 2d affine matrix is a 2x3 matrix.";

		switch ( row )
		{
		case 0:
			switch ( column )
			{
			case 0:
				a.m00 = value;
				break;
			case 1:
				a.m01 = value;
				break;
			default:
				a.m02 = value;
			}
			break;
		default:
			switch ( column )
			{
			case 0:
				a.m10 = value;
				break;
			case 1:
				a.m11 = value;
				break;
			default:
				a.m12 = value;
				break;
			}
		}

		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public void set( final double... values )
	{
		assert values.length >= 6: "Input dimensions do not match.  A 2d affine matrix is a 2x3 matrix.";

		a.m00 = values[ 0 ];
		a.m01 = values[ 1 ];
		a.m02 = values[ 2 ];

		a.m10 = values[ 3 ];
		a.m11 = values[ 4 ];
		a.m12 = values[ 5 ];

		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public void set( final double[][] values )
	{
		assert values.length >= 2 && values[ 0 ].length >= 3 && values[ 1 ].length == 3: "Input dimensions do not match.  A 2d affine matrix is a 2x3 matrix.";

		a.m00 = values[ 0 ][ 0 ];
		a.m01 = values[ 0 ][ 1 ];
		a.m02 = values[ 0 ][ 2 ];

		a.m10 = values[ 1 ][ 0 ];
		a.m11 = values[ 1 ][ 1 ];
		a.m12 = values[ 1 ][ 2 ];

		updateDs();
		invert();
		inverse.updateDs();
	}
}
