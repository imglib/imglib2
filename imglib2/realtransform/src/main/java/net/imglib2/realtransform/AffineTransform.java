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

import net.imglib2.concatenate.Concatenable;
import Jama.Matrix;

/**
 * An <em>n</em>-dimensional affine transformation.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class AffineTransform extends AbstractAffineTransform implements Concatenable< Affine >
{
	
	public AffineTransform( final int n )
	{
		super( n );
	}
	
	public AffineTransform( final Matrix matrix )
	{
		super( matrix );
	}
	
	protected AffineTransform( final Matrix a, final Matrix i, final double[] t )
	{
		super( a, i, t );
	}
	
	protected void invert()
	{
		final Matrix ii = a.inverse();
		i.setMatrix( 0, n - 1, 0, n - 1, ii );
	}
	
	@Override
	public AffineTransform inverse()
	{
		return new AffineTransform( a, i, t );
	}
	
	public void set( final Affine affine )
	{
		assert n == affine.numSourceDimensions() : "Dimensions do not match.";
		
		for ( int r = 0; r < n; ++r )
		{
			for ( int c = 0; c < n; ++c )
				a.set( r, c, affine.get( r, c ) );
			t[ r ] = affine.get( r, n );
		}
		invert();
	}
	
	public void set( final double[][] affine )
	{
		assert n == affine.length : "Dimensions do not match.";
		
		for ( int r = 0; r < n; ++r )
		{
			assert n + 1 == affine[ r ].length : "Dimensions do not match.";
			
			for ( int c = 0; c < n; ++c )
				a.set( r, c, affine[ r ][ c ] );
			t[ r ] = affine[ r ][ n ];
		}
		invert();
	}

	@Override
	public Concatenable< Affine > concatenate( final Affine affine )
	{
		assert affine.numSourceDimensions() == numSourceDimensions() : "Dimensions do not match.";
		
		final Matrix matrix = new Matrix( n, n );
		final double[] translation = new double[ n ];
		for ( int r = 0; r < n; ++r )
		{
			for ( int c = 0; c < n; ++c )
			{
				double ar = 0;
				for ( int k = 0; k < n; ++k )
					ar += get( r, k ) * affine.get( k, c );
				matrix.set( r, c, ar );
			}
			double tr = get( r, n );
			for ( int k = 0; k < n; ++k )
				tr += get( r, k ) * affine.get( k, n );
			translation[ r ] = tr;
		}
		final Matrix inverse = matrix.inverse();
		a.setMatrix( 0, n - 1, 0, n -  1, matrix );
		i.setMatrix( 0, n - 1, 0, n - 1, inverse );
		System.arraycopy( translation, 0, t, 0, t.length );
		
		return this;
	}

	@Override
	public Class< Affine > getConcatenableClass()
	{
		return Affine.class;
	}
	
}
