/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.imglib2.realtransform;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import Jama.Matrix;

/**
 * An abstract implementation of an affine transformation that returns
 * default values referring tot the identity transformation for all fields.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public abstract class AbstractAffineTransform implements Affine
{
	final protected int n;
	final protected Matrix a, i;
	final protected double[] t;
	
	public AbstractAffineTransform( final int n )
	{
		this.n = n;
		a = new Matrix( n, n );
		
		for ( int r = 0; r < n; ++r )
			a.set( r, r, 1.0 );
		
		t = new double[ n ];
		i = a.copy();
	}
	
	public AbstractAffineTransform( final Matrix matrix )
	{
		assert matrix.getRowDimension() == matrix.getColumnDimension() - 1 : "The passed affine matrix must be of the format (n-1)*n.";
		
		n = matrix.getRowDimension();
		a = new Matrix( n, n );
		a.setMatrix( 0, n - 1, 0, n - 1, matrix );
		i = a.inverse();
		t = new double[ n ];
		for ( int r = 0; r < n; ++r )
			t[ r ] = a.get( r, n );
	}
	
	protected AbstractAffineTransform( final Matrix a, final Matrix i, final double[] t )
	{
		assert
			a.getRowDimension() == t.length &&
			a.getColumnDimension() == t.length &&
			i.getRowDimension() == t.length &&
			i.getColumnDimension() == t.length : "The passed arrays must be n*n and the t-vector n*1.";
		
		this.n = t.length;
		this.a = a;
		this.i = i;
		this.t = t;
	}
	
	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length == n && target.length == n : "Source or target vector dimensions do not match with the transformation.";
		
		for ( int r = 0; r < n; ++r )
		{
			double ar = 0;
			for ( int c = 0; c < n; ++c )
				ar += ( target[ c ] - t[ c ] ) * i.get( r, c );
			
			source[ r ] = ar;
		}
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length == n && target.length == n : "Source or target vector dimensions do not match with the transformation.";
		
		for ( int r = 0; r < n; ++r )
		{
			double ar = 0;
			for ( int c = 0; c < n; ++c )
				ar += ( target[ c ] - t[ c ] ) * i.get( r, c );
			
			source[ r ] = ( float )ar;
		}
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() == n && target.numDimensions() == n : "Source or target vector dimensions do not match with the transformation.";
		
		for ( int r = 0; r < n; ++r )
		{
			double ar = 0;
			for ( int c = 0; c < n; ++c )
				ar += ( target.getDoublePosition( c ) - t[ c ] ) * i.get( r, c );
			
			source.setPosition( ar, r );
		}
			
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
		assert source.length == n && target.length == n : "Source or target vector dimensions do not match with the transformation.";
		
		for ( int r = 0; r < n; ++r )
		{
			double ar = 0;
			for ( int c = 0; c < n; ++c )
				ar += source[ c ] * a.get( r, c ) + t[ c ];
			
			target[ r ] = ar;
		}
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length == n && target.length == n : "Source or target vector dimensions do not match with the transformation.";

		for ( int r = 0; r < n; ++r )
		{
			double ar = 0;
			for ( int c = 0; c < n; ++c )
				ar += source[ c ] * a.get( r, c ) + t[ c ];
			
			target[ r ] = ( float )ar;
		}
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() == n && target.numDimensions() == n : "Source or target vector dimensions do not match with the transformation.";
		
		for ( int r = 0; r < n; ++r )
		{
			double ar = 0;
			for ( int c = 0; c < n; ++c )
				ar += source.getDoublePosition( c ) * a.get( r, c ) + t[ c ];
			
			target.setPosition( ar, r );
		}
	}

	@Override
	public double get( final int row, final int column )
	{
		if ( column == n )
			return t[ row ];
		else
			return a.get( row, column );
	}
}
