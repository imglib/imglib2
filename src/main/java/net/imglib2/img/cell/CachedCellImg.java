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

import java.lang.ref.ReferenceQueue;

import net.imglib2.cache.Cache;
import net.imglib2.cache.CacheEntry;
import net.imglib2.cache.Loader;
import net.imglib2.cache.SoftReferenceCache;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;

final public class CachedCellImg< T extends NativeType< T >, A >
		extends AbstractCellImg< T, A, CacheCell< A >, CellImgFactory< T > >
{
	final private Cache< Long, CacheCell< A > > cache;

	private Cache< Long, CacheCell< A > > createSoftReferenceCache(
			final Loader< Long, CacheCell< A > > loader,
			final ReferenceQueue< ? super CacheEntry< Long, CacheCell< A > > > referenceQueue )
	{
		return new SoftReferenceCache< Long, CacheCell< A > >( loader, referenceQueue );
	}

	private Cache< Long, CacheCell< A > > createSoftReferenceCache(
			final Loader< Long, CacheCell< A > > loader )
	{
		return new SoftReferenceCache< Long, CacheCell< A > >( loader, ReferenceQueue. );
	}

	public CachedCellImg(
			final Fraction entitiesPerPixel,
			final long[] dimensions,
			final int[] cellDimensions,
			final Cache< Long, CacheCell< A > > cache )
	{
		/** TODO this is not right, CellImgFactory cannot create
		 * {@link CachedCellImg} but only {@link CellImg} with memory backed
		 * {@link DefaultCell}s.  It is also final, which should may be change.
		 *
		 * Options:
		 *
		 * (1) Make a CachedCellImgFactory that generates CachedCellImg, that
		 * currently would not work together with implementing AbstractCellImg
		 * which expects a CellImgFactory in the constructor.
		 * (2) Make CellImgFactory non-final and extend, that may come with
		 * some small compatibility breaking changes on the CellImg + Factory
		 * side.
		 */
		super( new CellImgFactory< T >(), new CachedImgCells< A >( cache, dimensions, cellDimensions, entitiesPerPixel ) );

		this.cache = cache;
	}

	public CechedCellImg(
			final Fraction entitiesPerPixel,
			final long[] dimensions,
			final int[] cellDimensions,
			final Loader< Long, A > loader,
			final ReferenceQueue< ? super CacheEntry< A > referenceQueue )
	{

	}

	@Override
	public CachedCellImg< T, A > copy()
	{
		throw new UnsupportedOperationException();
	}
}
