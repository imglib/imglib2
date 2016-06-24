/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import java.util.Arrays;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.integer.Slicing;

/**
 * Wrap a {@code source} RandomAccess which is related to this by a
 * {@link Slicing} {@code transformToSource}.
 *
 * @param <T>
 * @author Tobias Pietzsch (tobias.pietzsch@gmail.com)
 */
public class SlicingRandomAccess< T > extends AbstractLocalizable implements RandomAccess< T >
{
	/**
	 * source RandomAccess. note that this is the <em>target</em> of the
	 * transformToSource.
	 */
	private final RandomAccess< T > s;

	/**
	 * number of dimensions of source RandomAccess, respectively
	 * numTargetDimensions of the Slicing transform.
	 */
	private final int m;

	/**
	 * for each component of the source vector: should the value be taken to a
	 * target vector component (false) or should it be discarded (true).
	 */
	private final boolean[] sourceZero;

	/**
	 * for each component of the source vector: to which target vector component
	 * should it be taken.
	 */
	private final int[] sourceComponent;

	private final long[] tmpPosition;

	private final long[] tmpDistance;

	SlicingRandomAccess( final RandomAccess< T > source, final Slicing transformToSource )
	{
		super( transformToSource.numSourceDimensions() );
		// n == transformToSource.numSourceDimensions()
		// m == transformToSource.numTargetDimensions()

		assert source.numDimensions() == transformToSource.numTargetDimensions();

		s = source;
		m = transformToSource.numTargetDimensions();
		final boolean[] targetZero = new boolean[ m ];
		final int[] targetComponent = new int[ m ];
		transformToSource.getComponentZero( targetZero );
		transformToSource.getComponentMapping( targetComponent );

		sourceZero = new boolean[ n ];
		sourceComponent = new int[ n ];
		Arrays.fill( sourceZero, true );
		for ( int d = 0; d < m; ++d )
			if ( targetZero[ d ] )
				s.setPosition( transformToSource.getTranslation( d ), d );
			else
			{
				final int e = targetComponent[ d ];
				sourceZero[ e ] = false;
				sourceComponent[ e ] = d;
			}

		tmpPosition = new long[ m ];
		transformToSource.getTranslation( tmpPosition );

		tmpDistance = new long[ m ];
	}

	protected SlicingRandomAccess( final SlicingRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );
		s = randomAccess.s.copyRandomAccess();
		m = randomAccess.m;
		sourceZero = randomAccess.sourceZero.clone();
		sourceComponent = randomAccess.sourceComponent.clone();
		tmpPosition = randomAccess.tmpPosition.clone();
		tmpDistance = randomAccess.tmpDistance.clone();
	}

	@Override
	public void fwd( final int d )
	{
		assert d < n;
		++position[ d ];
		if ( !sourceZero[ d ] )
			s.fwd( sourceComponent[ d ] );
	}

	@Override
	public void bck( final int d )
	{
		assert d < n;
		--position[ d ];
		if ( !sourceZero[ d ] )
			s.bck( sourceComponent[ d ] );
	}

	@Override
	public void move( final int distance, final int d )
	{
		assert d < n;
		position[ d ] += distance;
		if ( !sourceZero[ d ] )
			s.move( distance, sourceComponent[ d ] );
	}

	@Override
	public void move( final long distance, final int d )
	{
		assert d < n;
		position[ d ] += distance;
		if ( !sourceZero[ d ] )
			s.move( distance, sourceComponent[ d ] );
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
				tmpDistance[ sourceComponent[ d ] ] = distance;
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
				tmpDistance[ sourceComponent[ d ] ] = distance[ d ];
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
				tmpDistance[ sourceComponent[ d ] ] = distance[ d ];
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
			position[ d ] = p;
			if ( !sourceZero[ d ] )
				tmpPosition[ sourceComponent[ d ] ] = p;
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
				tmpPosition[ sourceComponent[ d ] ] = p;
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
				tmpPosition[ sourceComponent[ d ] ] = p;
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		assert d < n;
		this.position[ d ] = position;
		if ( !sourceZero[ d ] )
			s.setPosition( position, sourceComponent[ d ] );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		assert d < n;
		this.position[ d ] = position;
		if ( !sourceZero[ d ] )
			s.setPosition( position, sourceComponent[ d ] );
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public SlicingRandomAccess< T > copy()
	{
		return new SlicingRandomAccess< T >( this );
	}

	@Override
	public SlicingRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
