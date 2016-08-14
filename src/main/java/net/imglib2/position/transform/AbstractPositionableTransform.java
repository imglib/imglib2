/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.position.transform;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * A {@link RealPositionable} that drives a {@link Positionable} to somehow
 * derived discrete coordinates.
 * 
 * @author Stephan Saalfeld
 */
public abstract class AbstractPositionableTransform< LocalizablePositionable extends Localizable & Positionable > extends AbstractEuclideanSpace implements RealPositionable, RealLocalizable
{
	final protected LocalizablePositionable target;

	/* current position, required for relative movement */
	final protected double[] position;

	/*
	 * current discrete position for temporary storage, this field does not
	 * necessarily contain the actual discrete position!
	 */
	final protected long[] discrete;

	public AbstractPositionableTransform( final LocalizablePositionable target )
	{
		super( target.numDimensions() );

		this.target = target;

		position = new double[ n ];
		discrete = new long[ n ];
	}

	/* RealLocalizable */

	@Override
	public double getDoublePosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		return ( float ) position[ dim ];
	}

	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < pos.length; ++d )
			pos[ d ] = ( float ) this.position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < pos.length; ++d )
			pos[ d ] = this.position[ d ];
	}

	/* Positionable */

	@Override
	public void bck( final int dim )
	{
		position[ dim ] -= 1;
		target.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		position[ dim ] += 1;
		target.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		position[ dim ] += distance;
		target.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		position[ dim ] += distance;
		target.move( distance, dim );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += localizable.getDoublePosition( d );

		target.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];

		target.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];

		target.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( position );
		target.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];

		target.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];

		target.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( position, d );
	}

	/* Object */

	@Override
	public String toString()
	{
		final StringBuffer pos = new StringBuffer( "(" );
		pos.append( position[ 0 ] );

		for ( int d = 1; d < n; d++ )
			pos.append( ", " ).append( position[ d ] );

		pos.append( ")" );

		return pos.toString();
	}
}
