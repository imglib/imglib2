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
import net.imglib2.RealPositionable;
import net.imglib2.concatenate.Concatenable;
import net.imglib2.concatenate.PreConcatenable;
import Jama.Matrix;

/**
 * An <em>n</em>-dimensional affine transformation.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class AffineTransform extends AbstractAffineTransform implements Concatenable< AffineGet >, PreConcatenable< AffineGet >
{
	final protected AffineTransform inverse;

	public AffineTransform( final int n )
	{
		super( n );
		inverse = new AffineTransform( this );
		invert();
		inverse.updateDs();
	}

	protected AffineTransform( final Matrix a, final double[] t )
	{
		super( a, t );

		inverse = new AffineTransform( this );
		invert();
		inverse.updateDs();
	}

	public AffineTransform( final Matrix matrix )
	{
		super( matrix );

		inverse = new AffineTransform( this );
		invert();
		inverse.updateDs();
	}

	protected AffineTransform( final AffineTransform inverse )
	{
		super( inverse.n );
		this.inverse = inverse;
	}

	protected void invertT()
	{
		for ( int r = 0; r < n; ++r )
		{
			double tir = -inverse.a.get( r, 0 ) * t[ 0 ];
			for ( int c = 1; c < n; ++c )
				tir -= inverse.a.get( r, c ) * t[ c ];
			inverse.t[ r ] = tir;
		}
	}

	protected void invert()
	{
		final Matrix ii = a.inverse();
		inverse.a.setMatrix( 0, n - 1, 0, n - 1, ii );
		invertT();
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= n && target.length >= n: "Source or target vector dimensions do not match with the transformation.";

		for ( int r = 0; r < n; ++r )
		{
			double ar = 0;
			for ( int c = 0; c < n; ++c )
				ar += source[ c ] * a.get( r, c );

			target[ r ] = ( float ) ( ar + t[ r ] );
		}
	}

	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= n && target.length >= n: "Source or target vector dimensions do not match with the transformation.";

		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= n && target.length >= n: "Source or target vector dimensions do not match with the transformation.";

		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= n && target.numDimensions() >= n: "Source or target vector dimensions do not match with the transformation.";

		inverse.apply( target, source );
	}

	@Override
	public AffineTransform inverse()
	{
		return inverse;
	}

	public void set( final AffineGet affine )
	{
		assert n == affine.numSourceDimensions(): "Dimensions do not match.";

		for ( int r = 0; r < n; ++r )
		{
			for ( int c = 0; c < n; ++c )
				a.set( r, c, affine.get( r, c ) );
			t[ r ] = affine.get( r, n );
		}
		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public void set( final double[][] affine )
	{
		assert n == affine.length: "Dimensions do not match.";

		for ( int r = 0; r < n; ++r )
		{
			assert n + 1 == affine[ r ].length: "Dimensions do not match.";

			for ( int c = 0; c < n; ++c )
				a.set( r, c, affine[ r ][ c ] );
			t[ r ] = affine[ r ][ n ];
		}

		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public AffineTransform concatenate( final AffineGet affine )
	{
		assert affine.numSourceDimensions() == n: "Dimensions do not match.";

		final Matrix matrix = new Matrix( n, n );
		final double[] translation = new double[ n ];
		for ( int r = 0; r < n; ++r )
		{
			for ( int c = 0; c < n; ++c )
			{
				double ar = get( r, 0 ) * affine.get( 0, c );
				for ( int k = 1; k < n; ++k )
					ar += get( r, k ) * affine.get( k, c );
				matrix.set( r, c, ar );
			}
			double tr = get( r, n ) + get( r, 0 ) * affine.get( 0, n );
			for ( int k = 1; k < n; ++k )
				tr += get( r, k ) * affine.get( k, n );
			translation[ r ] = tr;
		}
		a.setMatrix( 0, n - 1, 0, n - 1, matrix );
		System.arraycopy( translation, 0, t, 0, t.length );

		updateDs();
		invert();
		inverse.updateDs();

		return this;
	}

	@Override
	public Class< AffineGet > getConcatenableClass()
	{
		return AffineGet.class;
	}

	@Override
	public AffineTransform preConcatenate( final AffineGet affine )
	{
		assert affine.numSourceDimensions() == n: "Dimensions do not match.";

		final Matrix matrix = new Matrix( n, n );
		final double[] translation = new double[ n ];
		for ( int r = 0; r < n; ++r )
		{
			for ( int c = 0; c < n; ++c )
			{
				double ar = affine.get( r, 0 ) * get( 0, c );
				for ( int k = 1; k < n; ++k )
					ar += affine.get( r, k ) * get( k, c );
				matrix.set( r, c, ar );
			}
			double tr = affine.get( r, n ) + affine.get( r, 0 ) * get( 0, n );
			for ( int k = 1; k < n; ++k )
				tr += affine.get( r, k ) * get( k, n );
			translation[ r ] = tr;
		}
		a.setMatrix( 0, n - 1, 0, n - 1, matrix );
		System.arraycopy( translation, 0, t, 0, t.length );

		updateDs();
		invert();
		inverse.updateDs();

		return this;
	}

	@Override
	public Class< AffineGet > getPreConcatenableClass()
	{
		return AffineGet.class;
	}

	@Override
	public void set( final double value, final int row, final int column )
	{
		if ( column == n )
			t[ row ] = value;
		else
			a.set( row, column, value );

		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public void set( final double... values )
	{
		assert values.length == n * n + n: "Input dimensions do not match dimensions of this affine transform.";

		int i = 0;
		for ( int r = 0; r < n; ++r )
		{
			for ( int c = 0; c < n; ++c, ++i )
				a.set( r, c, values[ i ] );
			t[ r ] = values[ i++ ];
		}

		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public AffineTransform copy()
	{
		final AffineTransform copy = new AffineTransform( n );
		copy.set( this );
		return copy;
	}
}
