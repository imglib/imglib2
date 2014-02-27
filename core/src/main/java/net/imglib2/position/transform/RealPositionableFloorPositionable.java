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

package net.imglib2.position.transform;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * Moves a {@link RealLocalizable} & {@link RealPositionable} and a
 * {@link Positionable} in synchrony. The position of the latter is at the floor
 * coordinates of the former. For practical useage, the floor operation is
 * defined as the integer smaller than the real value:
 * 
 * f = r < 0 ? (long)r - 1 : (long)r
 * 
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealPositionableFloorPositionable< P extends RealLocalizable & RealPositionable > extends AbstractEuclideanSpace implements RealPositionable, RealLocalizable
{
	final protected P source;

	final protected Positionable target;

	/* temporary floor position register */
	final protected long[] floor;

	public RealPositionableFloorPositionable( final P source, final Positionable target )
	{
		super( source.numDimensions() );

		this.source = source;
		this.target = target;

		floor = new long[ n ];
	}

	/* RealPositionable */

	@Override
	public void move( final float distance, final int d )
	{
		source.move( distance, d );
		target.setPosition( Floor.floor( source.getDoublePosition( d ) ), d );
	}

	@Override
	public void move( final double distance, final int d )
	{
		source.move( distance, d );
		target.setPosition( Floor.floor( source.getDoublePosition( d ) ), d );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		source.move( localizable );
		Floor.floor( source, floor );
		target.setPosition( floor );
	}

	@Override
	public void move( final float[] distance )
	{
		source.move( distance );
		Floor.floor( source, floor );
		target.setPosition( floor );
	}

	@Override
	public void move( final double[] distance )
	{
		source.move( distance );
		Floor.floor( source, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		source.setPosition( localizable );
		Floor.floor( localizable, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final float[] position )
	{
		source.setPosition( position );
		Floor.floor( position, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final double[] position )
	{
		source.setPosition( position );
		Floor.floor( position, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( Floor.floor( position ), d );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( Floor.floor( position ), d );
	}

	/* Positionable */

	@Override
	public void bck( final int d )
	{
		source.bck( d );
		target.bck( d );
	}

	@Override
	public void fwd( final int d )
	{
		source.fwd( d );
		target.fwd( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		source.move( distance, d );
		target.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		source.move( distance, d );
		target.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		source.move( localizable );
		target.move( localizable );
	}

	@Override
	public void move( final int[] position )
	{
		source.move( position );
		target.move( position );
	}

	@Override
	public void move( final long[] position )
	{
		source.move( position );
		target.move( position );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		source.setPosition( localizable );
		target.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		source.setPosition( position );
		target.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		source.setPosition( position );
		target.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( position, d );
	}

	/* RealLocalizable */

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public void localize( final float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		source.localize( position );
	}
}
