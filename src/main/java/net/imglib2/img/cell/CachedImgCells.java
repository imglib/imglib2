/*
 * #%L
 * BigDataViewer core classes with minimal dependencies
 * %%
 * Copyright (C) 2012 - 2016 Tobias Pietzsch, Stephan Saalfeld, Stephan Preibisch,
 * Jean-Yves Tinevez, HongKee Moon, Johannes Schindelin, Curtis Rueden, John Bogovic
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

import net.imglib2.cache.Cache;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.AbstractLongListImg;
import net.imglib2.util.Fraction;

public class CachedImgCells< A > extends AbstractCells< A, CacheCell< A >, CachedImgCells< A >.Cells >
{
	protected final Cells cells;

	protected final Cache< Long, CacheCell< A > > cache;

	public CachedImgCells(
			final Cache< Long, CacheCell< A > > cache,
			final long[] dimensions,
			final int[] cellDimensions,
			final Fraction entitiesPerPixel )
	{
		super( entitiesPerPixel, dimensions, cellDimensions );
		this.cache = cache;
		cells = new Cells( numCells );
	}

	@Override
	protected Cells cells()
	{
		return cells;
	}

	public class Cells extends AbstractLongListImg< CacheCell< A > >
	{
		protected Cells( final long[] dim )
		{
			super( dim );
		}

		@Override
		protected CacheCell< A > get( final long index )
		{
			return cache.get( index );
		}

		@Override
		public Img< CacheCell< A > > copy()
		{
			throw new UnsupportedOperationException( "Not supported" );
		}

		@Override
		protected void set( final long index, final CacheCell< A > value )
		{
			throw new UnsupportedOperationException( "Not supported" );
		}

		@Override
		public ImgFactory< CacheCell< A > > factory()
		{
			throw new UnsupportedOperationException( "Not supported" );
		}
	}
}
