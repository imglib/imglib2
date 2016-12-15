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

package net.imglib2.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache associating keys {@code K} to values {@code V} by {@link Reference}.
 * The {@link Reference} objects are stored in a {@link Map} and refer to
 * {@link CacheEntry} objects that hold the actual value.  The
 * {@link CacheEntry} layer is necessary for synchronization to prevent double
 * loading of values and not exposed to the user.  Implementations of this
 * abstract class, however, have to implement how to
 * {@link #createReference(Object, CacheEntry) create} a {@link Reference} to a
 * {@link CacheEntry}.
 * <p>
 * {@link Reference} objects that are subject for removal are expected to be
 * enqueued to a {@link CacheReferenceQueue} and implement logic to remove
 * themselves from their respective maps.
 *
 * @param <K>
 *            key type.
 * @param <V>
 *            value type.
 *
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld
 */
abstract public class ReferenceCache< K, V > implements Cache< K, V >
{
	/** holds references to {@link CacheEntry CacheEntries} */
	protected final ConcurrentHashMap< K, Reference< CacheEntry< K, V > > > map = new ConcurrentHashMap<>();

	/** loads values */
	protected final Loader< K, V > loader;

	protected final ReferenceQueue< ? super CacheEntry< K, V > > referenceQueue;

	public ReferenceCache(
			final Loader< K, V > loader,
			final ReferenceQueue< ? super CacheEntry< K, V > > referenceQueue )
	{
		this.loader = loader;
		this.referenceQueue = referenceQueue;
	}

	abstract protected Reference< CacheEntry< K, V > > createReference( final K key, final CacheEntry< K, V > entry );

	@Override
	public V get( final K key )
	{
		CacheEntry< K, V > entry;
		synchronized ( this )
		{
			Reference< CacheEntry< K, V > > ref = map.get( key );
			if ( ref == null || ref.get() == null )
			{
				entry = new CacheEntry<>();
				ref = createReference( key, entry );
				map.put( key, ref );
			}
			else
				entry = ref.get();
		}
		entry.load( loader, key );
		return entry.get();
	}

	@Override
	public void invalidateAll()
	{
		for ( final Reference< ? > ref : map.values() )
			ref.clear();
		map.clear();
	}

	public static interface CacheReference< K >
	{
		public K getKey();
		public Map< K, ? > getMap();
	}
}
