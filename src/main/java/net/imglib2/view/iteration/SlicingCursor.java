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
package net.imglib2.view.iteration;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.transform.integer.Slicing;

/**
 * Wrap a cursor that runs on a hyperslice and apply coordinate transform on
 * localize calls.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class SlicingCursor< T > extends AbstractEuclideanSpace implements Cursor< T >
{
	/**
	 * source Cursor. note that this is the <em>target</em> of the
	 * transformToSource.
	 */
	private final Cursor< T > s;

	/**
	 * number of dimensions of source Cursor, respectively numTargetDimensions
	 * of the Slicing transform.
	 */
	private final int m;

	/**
	 * for each component of the source vector: to which target vector component
	 * should it be taken.
	 */
	private final int[] sourceComponent;

	private final long[] tmpPosition;

	/**
	 * Create a Cursor that forwards all {@link Cursor} methods to
	 * {@code source}, except {@link Localizable} methods. Localize calls are
	 * propagated through {@code transformToSource}.
	 */
	SlicingCursor( final Cursor< T > source, final Slicing transformToSource )
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
			if ( !transformToSource.getComponentZero( d ) )
				sourceComponent[ transformToSource.getComponentMapping( d ) ] = d;

		tmpPosition = new long[ m ];
		transformToSource.getTranslation( tmpPosition );
	}

	protected SlicingCursor( final SlicingCursor< T > cursor )
	{
		super( cursor.numDimensions() );
		s = cursor.s;
		m = cursor.m;
		sourceComponent = cursor.sourceComponent.clone();
		tmpPosition = cursor.tmpPosition.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void localize( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getIntPosition( d );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void localize( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getLongPosition( d );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIntPosition( final int d )
	{
		assert d < n;
		return s.getIntPosition( sourceComponent[ d ] );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLongPosition( final int d )
	{
		assert d < n;
		return s.getLongPosition( sourceComponent[ d ] );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void localize( final float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getFloatPosition( d );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void localize( final double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getDoublePosition( d );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getFloatPosition( final int d )
	{
		assert d < n;
		return s.getFloatPosition( sourceComponent[ d ] );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDoublePosition( final int d )
	{
		assert d < n;
		return s.getDoublePosition( sourceComponent[ d ] );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get()
	{
		return s.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SlicingCursor< T > copy()
	{
		return new SlicingCursor< T >( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SlicingCursor< T > copyCursor()
	{
		return copy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jumpFwd( final long steps )
	{
		s.jumpFwd( steps );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fwd()
	{
		s.fwd();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset()
	{
		s.reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext()
	{
		return s.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T next()
	{
		return s.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove()
	{
		return;
	}
}
