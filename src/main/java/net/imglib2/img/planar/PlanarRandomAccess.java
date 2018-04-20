/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.planar;

import net.imglib2.AbstractLocalizableInt;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;

/**
 * {@link RandomAccess} on a {@link PlanarImg}.
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class PlanarRandomAccess< T extends NativeType< T > > extends AbstractLocalizableInt implements RandomAccess< T >, PlanarImg.PlanarContainerSampler
{
	final protected int[] sliceSteps;

	final protected int width;

	final protected T type;

	protected int sliceIndex;

	protected PlanarRandomAccess( final PlanarRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		sliceSteps = randomAccess.sliceSteps;
		width = randomAccess.width;
		sliceIndex = randomAccess.sliceIndex;

		for ( int d = 0; d < n; ++d )
			position[ d ] = randomAccess.position[ d ];

		type = randomAccess.type.duplicateTypeOnSameNativeImg();
		type.updateContainer( this );
		type.updateIndex( randomAccess.type.getIndex() );
	}

	public PlanarRandomAccess( final PlanarImg< T, ? > container )
	{
		super( container.numDimensions() );

		sliceSteps = container.sliceSteps;
		width = ( int ) container.dimension( 0 );

		type = container.createLinkedType();
		type.updateIndex( 0 );
		type.updateContainer( this );
	}

	@Override
	public int getCurrentSliceIndex()
	{
		return sliceIndex;
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public PlanarRandomAccess< T > copy()
	{
		return new PlanarRandomAccess< T >( this );
	}

	@Override
	public PlanarRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];

		if ( d == 0 )
			type.incIndex();
		else if ( d == 1 )
			type.incIndex( width );
		else
		{
			sliceIndex += sliceSteps[ d ];
			type.updateContainer( this );
		}
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];

		if ( d == 0 )
			type.decIndex();
		else if ( d == 1 )
			type.decIndex( width );
		else
		{
			sliceIndex -= sliceSteps[ d ];
			type.updateContainer( this );
		}
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;

		if ( d == 0 )
		{
			type.incIndex( distance );
		}
		else if ( d == 1 )
		{
			type.incIndex( distance * width );
		}
		else
		{
			sliceIndex += sliceSteps[ d ] * distance;
			type.updateContainer( this );
		}
	}

	@Override
	public void move( final long distance, final int d )
	{
		move( ( int ) distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		final int d0 = localizable.getIntPosition( 0 );
		final int d1 = localizable.getIntPosition( 1 );
		type.incIndex( d0 + d1 * width );
		position[ 0 ] += d0;
		position[ 1 ] += d1;

		for ( int d = 2; d < n; ++d )
		{
			final int dist = localizable.getIntPosition( d );
			if ( dist != 0 )
			{
				sliceIndex += dist * sliceSteps[ d ];
				position[ d ] += dist;

				for ( ++d; d < n; ++d )
				{
					if ( dist != 0 )
					{
						sliceIndex += dist * sliceSteps[ d ];
						position[ d ] += dist;
					}
				}
				type.updateContainer( this );
			}
		}
	}

	@Override
	public void move( final int[] distance )
	{
		type.incIndex( distance[ 0 ] + distance[ 1 ] * width );
		position[ 0 ] += distance[ 0 ];
		position[ 1 ] += distance[ 1 ];

		for ( int d = 2; d < n; ++d )
		{
			final int dist = distance[ d ];
			if ( dist != 0 )
			{
				sliceIndex += dist * sliceSteps[ d ];
				position[ d ] += dist;

				for ( ++d; d < n; ++d )
				{
					if ( dist != 0 )
					{
						sliceIndex += dist * sliceSteps[ d ];
						position[ d ] += dist;
					}
				}
				type.updateContainer( this );
			}
		}
	}

	@Override
	public void move( final long[] distance )
	{
		type.incIndex( ( int ) distance[ 0 ] + ( int ) distance[ 1 ] * width );
		position[ 0 ] += ( int ) distance[ 0 ];
		position[ 1 ] += ( int ) distance[ 1 ];

		for ( int d = 2; d < n; ++d )
		{
			final int dist = ( int ) distance[ d ];
			if ( dist != 0 )
			{
				sliceIndex += dist * sliceSteps[ d ];
				position[ d ] += dist;

				for ( ++d; d < n; ++d )
				{
					if ( dist != 0 )
					{
						sliceIndex += dist * sliceSteps[ d ];
						position[ d ] += dist;
					}
				}
				type.updateContainer( this );
			}
		}
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		if ( d == 0 )
		{
			type.incIndex( pos - position[ 0 ] );
		}
		else if ( d == 1 )
		{
			type.incIndex( ( pos - position[ 1 ] ) * width );
		}
		else
		{
			sliceIndex += ( pos - position[ d ] ) * sliceSteps[ d ];
			type.updateContainer( this );
		}

		position[ d ] = pos;
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		setPosition( ( int ) pos, d );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		final int p0 = localizable.getIntPosition( 0 );
		final int p1 = localizable.getIntPosition( 1 );
		type.updateIndex( p0 + p1 * width );
		position[ 0 ] = p0;
		position[ 1 ] = p1;

		for ( int d = 2; d < n; ++d )
		{
			final int pos = localizable.getIntPosition( d );
			if ( pos != position[ d ] )
			{
				sliceIndex += ( pos - position[ d ] ) * sliceSteps[ d ];
				position[ d ] = pos;

				for ( ++d; d < n; ++d )
				{
					final int pos2 = localizable.getIntPosition( d );
					if ( pos2 != position[ d ] )
					{
						sliceIndex += ( pos2 - position[ d ] ) * sliceSteps[ d ];
						position[ d ] = pos2;
					}
				}
				type.updateContainer( this );
			}
		}
	}

	@Override
	public void setPosition( final int[] pos )
	{
		type.updateIndex( pos[ 0 ] + pos[ 1 ] * width );
		position[ 0 ] = pos[ 0 ];
		position[ 1 ] = pos[ 1 ];

		for ( int d = 2; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
				position[ d ] = pos[ d ];

				for ( ++d; d < n; ++d )
				{
					if ( pos[ d ] != position[ d ] )
					{
						sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
						position[ d ] = pos[ d ];
					}
				}
				type.updateContainer( this );
			}
		}
	}

	@Override
	public void setPosition( final long[] pos )
	{
		type.updateIndex( ( int ) pos[ 0 ] + ( int ) pos[ 1 ] * width );
		position[ 0 ] = ( int ) pos[ 0 ];
		position[ 1 ] = ( int ) pos[ 1 ];

		for ( int d = 2; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
				position[ d ] = ( int ) pos[ d ];

				for ( ++d; d < n; ++d )
				{
					if ( pos[ d ] != position[ d ] )
					{
						sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
						position[ d ] = ( int ) pos[ d ];
					}
				}
				type.updateContainer( this );
			}
		}
	}
}
