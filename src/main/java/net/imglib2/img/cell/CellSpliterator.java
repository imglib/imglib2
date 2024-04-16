/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.cell;

import java.util.function.Consumer;
import net.imglib2.Cursor;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.stream.LocalizableSpliterator;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * LocalizableSpliterator for {@link CellImg}.
 * Localizes on demand.
 *
 * @param <T> pixel type
 */
class CellSpliterator< T extends NativeType< T >, C extends Cell< ? > > implements LocalizableSpliterator< T >
{
	private final AbstractCellImg< T, ?, C, ? > img;

	private final CellGrid grid;

	private final T type;

	private final Index index;

	private final CursorOnCells< C > currentCell;

	private int lastIndexInCurrentCell;

	private final long lastCell;

	private final int lastIndexInLastCell;

	private final long[] tmpIndices;

	CellSpliterator( AbstractCellImg< T, ?, C, ? > img, long origin, long fence )
	{
		this.img = img;
		grid = img.getCellGrid();
		type = img.createLinkedType();
		index = type.index();
		currentCell = new CursorOnCells<>( img.getCells().cursor() );

		tmpIndices = new long[ 2 ];

		grid.getCellAndPixelIndices( fence - 1, tmpIndices );
		lastCell = tmpIndices[ 0 ];
		lastIndexInLastCell = ( int ) tmpIndices[ 1 ];

		jumpBefore( origin );

		tmp = new long[ img.numDimensions() ];
	}

	private CellSpliterator( CellSpliterator< T, C > o )
	{
		img = o.img;
		grid = o.grid;
		type = o.type.duplicateTypeOnSameNativeImg();
		index = type.index();
		index.set( o.index.get() );
		currentCell = o.currentCell.copy();
		lastCell = o.lastCell;
		lastIndexInLastCell = o.lastIndexInLastCell;

		lastIndexInCurrentCell = o.lastIndexInCurrentCell;
		tmp = new long[ o.tmp.length ];
		tmpIndices = new long[ 2 ];
		type.updateContainer( currentCell );
	}

	private void jumpBefore( final long i )
	{
		grid.getCellAndPixelIndices( i, tmpIndices );
		currentCell.setIndex( tmpIndices[ 0 ] );
		cellUpdated();
		final int indexInCell = ( int ) tmpIndices[ 1 ];
		index.set( indexInCell - 1 );
	}

	private void cellUpdated()
	{
		final boolean isLastCell = currentCell.index() == lastCell;
		lastIndexInCurrentCell = isLastCell
				? lastIndexInLastCell
				: ( int ) ( currentCell.getCell().size() - 1 );
		type.updateContainer( currentCell );
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();

		if ( index.get() < lastIndexInCurrentCell )
		{
			index.inc();
			action.accept( type );
			return true;
		}
		else if ( currentCell.index() < lastCell )
		{
			currentCell.fwd();
			cellUpdated();
			index.set( 0 );
			action.accept( type );
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void forEachRemaining( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();

		while ( currentCell.index() < lastCell )
		{
			forEachRemainingInCell( action );
			currentCell.fwd();
			cellUpdated();
			index.set( -1 );
		}
		forEachRemainingInCell( action );
	}

	private void forEachRemainingInCell( final Consumer< ? super T > action )
	{
		final int len = lastIndexInCurrentCell - index.get();
		for ( int i = 0; i < len; i++ )
		{
			index.inc();
			action.accept( type );
		}
	}

	private long globalIndex( long cell, int index )
	{
		return grid.indexOfFirstPixelInCell( cell, tmp ) + index;
	}

	@Override
	public CellSpliterator< T, C > trySplit()
	{
		long lo = globalIndex( currentCell.index(), index.get() ) + 1;
		long mid = ( lo + globalIndex( lastCell, lastIndexInLastCell ) + 1 ) >>> 1;
		if ( lo >= mid )
			return null;
		else
		{
			final CellSpliterator< T, C > prefix = new CellSpliterator<>( img, lo, mid );
			jumpBefore( mid );
			return prefix;
		}
	}

	@Override
	public long estimateSize()
	{
		if ( currentCell.index() == lastCell )
			return lastIndexInLastCell - index.get();
		else
			return globalIndex( lastCell, lastIndexInLastCell ) - globalIndex( currentCell.index(), index.get() );
	}

	@Override
	public int characteristics()
	{
		return IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
	}

	@Override
	public CellSpliterator< T, C > copy()
	{
		return new CellSpliterator<>( this );
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public String toString()
	{
		long lo = globalIndex( currentCell.index(), index.get() ) + 1;
		long hi = globalIndex( lastCell, lastIndexInLastCell ) + 1;
		return "lo = " + lo + ", hi = " + hi;
	}

	// -- Localizable --

	@Override
	public long getLongPosition( final int d )
	{
		return currentCell.getCell().indexToGlobalPosition( index.get(), d );
	}

	@Override
	public void localize( final long[] pos )
	{
		currentCell.getCell().indexToGlobalPosition( index.get(), pos );
	}

	/**
	 * used internally to forward all localize() versions to the (abstract)
	 * long[] version.
	 */
	final private long[] tmp;

	@Override
	public int numDimensions()
	{
		return tmp.length;
	}

	@Override
	public void localize( final int[] pos )
	{
		localize( tmp );
		for ( int d = 0; d < tmp.length; d++ )
			pos[ d ] = ( int ) tmp[ d ];
	}

	@Override
	public void localize( final float[] pos )
	{
		localize( tmp );
		for ( int d = 0; d < tmp.length; d++ )
			pos[ d ] = tmp[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		localize( tmp );
		for ( int d = 0; d < tmp.length; d++ )
			pos[ d ] = tmp[ d ];
	}

	@Override
	public void localize( final Positionable position )
	{
		localize( tmp );
		position.setPosition( tmp );
	}

	@Override
	public void localize( final RealPositionable position )
	{
		localize( tmp );
		position.setPosition( tmp );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return getLongPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getLongPosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) getLongPosition( d );
	}

	private static class CursorOnCells< C extends Cell< ? > > implements AbstractCellImg.CellImgSampler< C >
	{
		private long index;

		private final Cursor< C > delegate;

		/**
		 * Delegate is assumed to be in initial state, such that the first fwd()
		 * will move to the first element.
		 */
		CursorOnCells( final Cursor< C > delegate )
		{
			this.delegate = delegate;
			index = -1;
		}

		CursorOnCells( final CursorOnCells< C > o )
		{
			delegate = o.delegate.copy();
			index = o.index;
		}

		public long index()
		{
			return index;
		}

		public void setIndex( long index )
		{
			final long steps = index - this.index;
			this.index = index;
			if ( steps != 0 )
				delegate.jumpFwd( steps );
		}

		@Override
		public C getCell()
		{
			return delegate.get();
		}

		public void fwd()
		{
			++index;
			delegate.fwd();
		}

		public CursorOnCells< C > copy()
		{
			return new CursorOnCells<>( this );
		}
	}
}
