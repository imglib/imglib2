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
import Jama.Matrix;

/**
 * An abstract implementation of an affine transformation that returns default
 * values referring to the identity transformation for all fields.  This
 * implementation is not thread safe.  Create a {@link #copy()} for each
 * consumer.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public abstract class AbstractAffineTransform implements AffineGet, AffineSet
{
	final protected int n;

	final protected Matrix a;

	final protected double[] t, tmp;

	final protected RealPoint[] ds;

	protected AbstractAffineTransform( final Matrix a, final double[] t )
	{
		assert a.getRowDimension() == t.length &&
				a.getColumnDimension() == t.length: "The passed arrays must be n*n and the t-vector n.";

		this.n = t.length;
		this.a = a;
		this.t = t;
		this.tmp = new double[ n ];
		ds = new RealPoint[ n ];
		for ( int r = 0; r < n; ++r )
			ds[ r ] = new RealPoint( n );

		updateDs();
	}

	public AbstractAffineTransform( final Matrix matrix )
	{
		assert matrix.getRowDimension() == matrix.getColumnDimension() - 1: "The passed affine matrix must be of the format (n-1)*n.";

		n = matrix.getRowDimension();
		a = new Matrix( n, n );
		t = new double[ n ];
		tmp = new double[ n ];
		ds = new RealPoint[ n ];

		a.setMatrix( 0, n - 1, 0, n - 1, matrix );
		for ( int r = 0; r < n; ++r )
		{
			t[ r ] = matrix.get( r, n );
			ds[ r ] = new RealPoint( n );
		}
		updateDs();
	}

	public AbstractAffineTransform( final int n )
	{
		this.n = n;
		a = new Matrix( n, n );
		t = new double[ n ];
		tmp = new double[ n ];
		ds = new RealPoint[ n ];

		for ( int r = 0; r < n; ++r )
		{
			final RealPoint d = new RealPoint( n );
			a.set( r, r, 1.0 );
			d.setPosition( 1.0, r );
			ds[ r ] = d;
		}
	}

	protected void updateDs()
	{
		for ( int c = 0; c < n; ++c )
		{
			final RealPoint d = ds[ c ];
			for ( int r = 0; r < n; ++r )
				d.setPosition( a.get( r, c ), r );
		}
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public int numSourceDimensions()
	{
		return n;
	}

	@Override
	public int numTargetDimensions()
	{
		return n;
	}
	
	@Override
	public void apply( final double[] source, final double[] target )
	{
		assert source.length >= n && target.length >= n: "Source or target vector dimensions do not match with the transformation.";

		for ( int r = 0; r < n; ++r )
		{
			tmp[ r ] = 0;
			for ( int c = 0; c < n; ++c )
				tmp[ r ] += source[ c ] * a.get( r, c );
		}
		
		for ( int r = 0; r < n; ++r )
			target[ r ] = tmp[ r ] + t[ r ];
	}
	
	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= n && target.length >= n: "Source or target vector dimensions do not match with the transformation.";

		for ( int r = 0; r < n; ++r )
		{
			tmp[ r ] = 0;
			for ( int c = 0; c < n; ++c )
				tmp[ r ] += source[ c ] * a.get( r, c );
		}
		
		for ( int r = 0; r < n; ++r )
			target[ r ] = ( float )( tmp[ r ] + t[ r ] );
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= n && target.numDimensions() >= n: "Source or target vector dimensions do not match with the transformation.";

		for ( int r = 0; r < n; ++r )
		{
			tmp[ r ] = 0;
			for ( int c = 0; c < n; ++c )
				tmp[ r ] += source.getDoublePosition( c ) * a.get( r, c );
		}
		
		for ( int r = 0; r < n; ++r )
			target.setPosition( tmp[ r ] + t[ r ], r );
	}

	@Override
	public double get( final int row, final int column )
	{
		assert row >= 0 && row < n && column >= 0 && column <= n: "Row or column out of bounds.";

		if ( column == n )
			return t[ row ];
		return a.get( row, column );
	}

	@Override
	public double[] getRowPackedCopy()
	{
		final double[] copy = new double[ n * n + n ];
		for ( int r = 0, i = 0; r < n; ++r, ++i )
		{
			for ( int c = 0; c < n; ++c, ++i )
				copy[ i ] = a.get( r, c );
			copy[ i ] = t[ r ];
		}
		return copy;
	}

	@Override
	public RealLocalizable d( final int d )
	{
		assert d >= 0 && d < n: "Dimension out of bounds.";

		return ds[ d ];
	}
}
