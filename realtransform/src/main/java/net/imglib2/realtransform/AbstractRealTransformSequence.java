/**
 * Copyright (c) 2009--2013, ImgLib2 developers
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

import java.util.ArrayList;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;

/**
 * Shared properties of {@link RealTransformSequence} and
 * {@link InvertibleRealTransformSequence}, sequences of
 * something that extends {@link RealTransform RealTransforms}.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class AbstractRealTransformSequence< R extends RealTransform > implements RealTransform
{
	final protected ArrayList< R > transforms = new ArrayList< R >();;

	protected double[] a = new double[ 0 ];
	protected double[] b = new double[ 0 ];
	protected RealPoint pa = RealPoint.wrap( a );
	protected RealPoint pb = RealPoint.wrap( b );
	protected int nSource = 0;
	protected int nTarget = 0;
	
	final protected void switchAB()
	{
		final double[] c = a;
		a = b;
		b = c;
		final RealPoint pc = pa;
		pa = pb;
		pb = pc;
	}

	/**
	 * Append a {@link RealTransform} to the sequence.
	 *  
	 * @param transform
	 */
	public void add( final R transform )
	{
		transforms.add( transform );
		nTarget = transform.numTargetDimensions();
		if ( transforms.size() == 1 )
		{
			nSource = transform.numSourceDimensions();
			if ( nTarget > nSource )
			{
				a = new double[ nTarget ];
				b = new double[ nTarget ];
				pa = RealPoint.wrap( a );
				pb = RealPoint.wrap( b );
			}
			else
			{
				a = new double[ nSource ];
				b = new double[ nSource ];
				pa = RealPoint.wrap( a );
				pb = RealPoint.wrap( b );
			}	
		}
		else if ( nTarget > a.length )
		{
			a = new double[ nTarget ];
			b = new double[ nTarget ];
			pa = RealPoint.wrap( a );
			pb = RealPoint.wrap( b );
		}
	}

	@Override
	public int numSourceDimensions()
	{
		return nSource;
	}

	@Override
	public int numTargetDimensions()
	{
		return nTarget;
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		assert source.length >= nSource && target.length >= nTarget : "Input dimensions too small.";
		
		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			if ( s > 0 )
			{
				transforms.get( 0 ).apply( source, a );
				for ( int i = 1; i < s; ++i )
				{
					transforms.get( i ).apply( a, b );
					switchAB();
				}
				transforms.get( s ).apply( a, target );
			}
			else
				transforms.get( 0 ).apply( source, target );
		}
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= nSource && target.length >= nTarget : "Input dimensions too small.";
		
		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			for ( int d = Math.min( source.length, a.length ) - 1; d >= 0; --d )
				a[ d ] = source[ d ];
			
			for ( final RealTransform t : transforms )
			{
				t.apply( a, b );
				switchAB();
			}
			
			for ( int d = Math.min( target.length, a.length ) - 1; d >= 0; --d )
				target[ d ] = ( float )a[ d ];
		}
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= nSource && target.numDimensions() >= nTarget : "Input dimensions too small.";
		
		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			if ( s > 0 )
			{
				transforms.get( 0 ).apply( source, pa );
				for ( int i = 1; i < s; ++i )
				{
					transforms.get( i ).apply( a, b );
					switchAB();
				}
				transforms.get( s ).apply( pa, target );
			}
			else
				transforms.get( 0 ).apply( source, target );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	public AbstractRealTransformSequence< R > copy()
	{
		final AbstractRealTransformSequence< R > copy = new AbstractRealTransformSequence< R >();
		for ( final R t : transforms )
			copy.add( ( R )t.copy() );
		return copy;
	}
}