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

package net.imglib2.view;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.integer.Slicing;

/**
 * 
 * @param <T>
 * @author Tobias Pietzsch
 */
public class SlicingRandomAccess< T > extends AbstractEuclideanSpace implements RandomAccess< T >
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

		sourceComponent = new int[ n ];
		for ( int d = 0; d < m; ++d )
			if ( transformToSource.getComponentZero( d ) )
				s.setPosition( transformToSource.getTranslation( d ), d );
			else
				sourceComponent[ transformToSource.getComponentMapping( d ) ] = d;

		tmpPosition = new long[ m ];
		transformToSource.getTranslation( tmpPosition );

		tmpDistance = new long[ m ];
	}

	protected SlicingRandomAccess( final SlicingRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );
		s = randomAccess.s.copyRandomAccess();
		m = randomAccess.m;
		sourceComponent = randomAccess.sourceComponent.clone();
		tmpPosition = randomAccess.tmpPosition.clone();
		tmpDistance = randomAccess.tmpDistance.clone();
	}

	@Override
	public void localize( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getIntPosition( d );
	}

	@Override
	public void localize( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getLongPosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		assert d < n;
		return s.getIntPosition( sourceComponent[ d ] );
	}

	@Override
	public long getLongPosition( final int d )
	{
		assert d < n;
		return s.getLongPosition( sourceComponent[ d ] );
	}

	@Override
	public void localize( final float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getFloatPosition( d );
	}

	@Override
	public void localize( final double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		assert d < n;
		return s.getFloatPosition( sourceComponent[ d ] );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		assert d < n;
		return s.getDoublePosition( sourceComponent[ d ] );
	}

	@Override
	public void fwd( final int d )
	{
		s.fwd( sourceComponent[ d ] );
	}

	@Override
	public void bck( final int d )
	{
		s.bck( sourceComponent[ d ] );
	}

	@Override
	public void move( final int distance, final int d )
	{
		assert d < n;
		s.move( distance, sourceComponent[ d ] );
	}

	@Override
	public void move( final long distance, final int d )
	{
		assert d < n;
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
			tmpDistance[ sourceComponent[ d ] ] = localizable.getLongPosition( d );
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
			tmpDistance[ sourceComponent[ d ] ] = distance[ d ];
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
			tmpDistance[ sourceComponent[ d ] ] = distance[ d ];
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
			tmpPosition[ sourceComponent[ d ] ] = localizable.getLongPosition( d );
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
			tmpPosition[ sourceComponent[ d ] ] = position[ d ];
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
			tmpPosition[ sourceComponent[ d ] ] = position[ d ];
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		assert d < n;
		s.setPosition( position, sourceComponent[ d ] );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		assert d < n;
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
