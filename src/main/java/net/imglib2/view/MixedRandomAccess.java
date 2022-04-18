/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.view;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.integer.Mixed;

/**
 * Wrap a {@code source} RandomAccess which is related to this by a
 * {@link Mixed} {@code transformToSource}.
 *
 * @param <T>
 * @author Tobias Pietzsch
 */
public final class MixedRandomAccess< T > extends AbstractLocalizable implements RandomAccess< T >
{
	/**
	 * source RandomAccess. note that this is the <em>target</em> of the
	 * transformToSource.
	 */
	private final RandomAccess< T > s;

	/**
	 * number of dimensions of source RandomAccess, respectively
	 * numTargetDimensions of the Mixed transform.
	 */
	private final int m;

	private final long[] translation;

	/**
	 * for each component of the source vector: should the value be taken to a
	 * target vector component (false) or should it be discarded (true).
	 */
	private final boolean[] sourceZero;

	/**
	 * for each component of the source vector: should the target vector
	 * component be inverted (true).
	 */
	private final boolean[] sourceInv;

	/**
	 * for each component of the source vector: to which target vector component
	 * should it be taken.
	 */
	private final int[] sourceComponent;

	private final long[] tmpPosition;

	private final long[] tmpDistance;

	MixedRandomAccess( final RandomAccess< T > source, final Mixed transformToSource )
	{
		super( transformToSource.numSourceDimensions() );
		// n == transformToSource.numSourceDimensions()
		// m == transformToSource.numTargetDimensions()

		assert source.numDimensions() == transformToSource.numTargetDimensions();

		s = source;
		m = transformToSource.numTargetDimensions();
		translation = new long[ m ];
		final boolean[] targetZero = new boolean[ m ];
		final boolean[] targetInv = new boolean[ m ];
		final int[] targetComponent = new int[ m ];
		transformToSource.getTranslation( translation );
		transformToSource.getComponentZero( targetZero );
		transformToSource.getComponentMapping( targetComponent );
		transformToSource.getComponentInversion( targetInv );

		sourceZero = new boolean[ n ];
		sourceInv = new boolean[ n ];
		sourceComponent = new int[ n ];
		for ( int e = 0; e < n; ++e )
		{
			sourceZero[ e ] = true;
		}
		for ( int d = 0; d < m; ++d )
		{
			if ( targetZero[ d ] )
			{
				s.setPosition( translation[ d ], d );
			}
			else
			{
				final int e = targetComponent[ d ];
				sourceZero[ e ] = false;
				sourceInv[ e ] = targetInv[ d ];
				sourceComponent[ e ] = d;
			}
		}

		tmpPosition = translation.clone();
		tmpDistance = new long[ m ];
	}

	protected MixedRandomAccess( final MixedRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.s = randomAccess.s.copy();
		this.m = randomAccess.m;

		this.translation = randomAccess.translation.clone();
		this.sourceZero = randomAccess.sourceZero.clone();
		this.sourceInv = randomAccess.sourceInv.clone();
		this.sourceComponent = randomAccess.sourceComponent.clone();

		tmpPosition = translation.clone();
		tmpDistance = new long[ m ];
	}

	@Override
	public void fwd( final int d )
	{
		assert d < n;
		position[ d ] += 1;
		if ( !sourceZero[ d ] )
		{
			if ( sourceInv[ d ] )
			{
				s.bck( sourceComponent[ d ] );
			}
			else
			{
				s.fwd( sourceComponent[ d ] );
			}
		}
	}

	@Override
	public void bck( final int d )
	{
		assert d < n;
		position[ d ] -= 1;
		if ( !sourceZero[ d ] )
		{
			if ( sourceInv[ d ] )
			{
				s.fwd( sourceComponent[ d ] );
			}
			else
			{
				s.bck( sourceComponent[ d ] );
			}
		}
	}

	@Override
	public void move( final int distance, final int d )
	{
		assert d < n;
		position[ d ] += distance;
		if ( !sourceZero[ d ] )
			s.move( sourceInv[ d ] ? -distance : distance, sourceComponent[ d ] );
	}

	@Override
	public void move( final long distance, final int d )
	{
		assert d < n;
		position[ d ] += distance;
		if ( !sourceZero[ d ] )
			s.move( sourceInv[ d ] ? -distance : distance, sourceComponent[ d ] );
	}

	@Override
	public void move( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in
		// tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			position[ d ] += distance;
			if ( !sourceZero[ d ] )
			{
				final int td = sourceComponent[ d ];
				tmpDistance[ td ] = sourceInv[ d ] ? -distance : distance;
			}
		}
		s.move( tmpDistance );
	}

	@Override
	public void move( final int[] distance )
	{
		assert distance.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in
		// tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			if ( !sourceZero[ d ] )
			{
				final int td = sourceComponent[ d ];
				tmpDistance[ td ] = sourceInv[ d ] ? -distance[ d ] : distance[ d ];
			}
		}
		s.move( tmpDistance );
	}

	@Override
	public void move( final long[] distance )
	{
		assert distance.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in
		// tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			if ( !sourceZero[ d ] )
			{
				final int td = sourceComponent[ d ];
				tmpDistance[ td ] = sourceInv[ d ] ? -distance[ d ] : distance[ d ];
			}
		}
		s.move( tmpDistance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
		{
			final long p = localizable.getLongPosition( d );
			this.position[ d ] = p;
			if ( !sourceZero[ d ] )
			{
				final int td = sourceComponent[ d ];
				tmpPosition[ td ] = translation[ td ] + ( sourceInv[ d ] ? -p : p );
			}
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final int[] position )
	{
		assert position.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
		{
			final long p = position[ d ];
			this.position[ d ] = p;
			if ( !sourceZero[ d ] )
			{
				final int td = sourceComponent[ d ];
				tmpPosition[ td ] = translation[ td ] + ( sourceInv[ d ] ? -p : p );
			}
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final long[] position )
	{
		assert position.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
		{
			final long p = position[ d ];
			this.position[ d ] = p;
			if ( !sourceZero[ d ] )
			{
				final int td = sourceComponent[ d ];
				tmpPosition[ td ] = translation[ td ] + ( sourceInv[ d ] ? -p : p );
			}
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		assert d < n;
		this.position[ d ] = position;
		if ( !sourceZero[ d ] )
		{
			final int td = sourceComponent[ d ];
			final long targetPos = translation[ td ] + ( sourceInv[ d ] ? -position : position );
			s.setPosition( targetPos, td );
		}
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		assert d < n;
		this.position[ d ] = position;
		if ( !sourceZero[ d ] )
		{
			final int td = sourceComponent[ d ];
			final long targetPos = translation[ td ] + ( sourceInv[ d ] ? -position : position );
			s.setPosition( targetPos, td );
		}
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public MixedRandomAccess< T > copy()
	{
		return new MixedRandomAccess< T >( this );
	}
}
