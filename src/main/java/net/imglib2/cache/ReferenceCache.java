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
 * A cache associating keys {@code K} to values {@code V}.  How values are
 * obtained and when and which entries are evicted is implementation-specific.
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
	/** holds references to values */
	protected final ConcurrentHashMap< K, Reference< V > > map = new ConcurrentHashMap<>();

	protected final Loader< K, V > loader;

	protected final ReferenceQueue< ? super V > referenceQueue;

	public ReferenceCache(
			final Loader< K, V > loader,
			final ReferenceQueue< ? super V > referenceQueue )
	{
		this.loader = loader;
		this.referenceQueue = referenceQueue;
	}

	abstract protected Reference< V > createReference( final K key, final V value );

	@Override
	public V get( final K key )
	{
		Reference< V > reference = map.get( key );
		if ( reference != null ) {
			final V cachedValue = reference.get();
			if ( cachedValue != null )
				return cachedValue;
		}
		final V value = loader.get( key );
		map.put( key, createReference( key, value ) );
		return value;
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
