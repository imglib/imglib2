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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * A {@link WeakReference} based implementation of {@link ReferenceCache}
 *
 * @param <K>
 *            key type.
 * @param <V>
 *            value type.
 *
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld
 */
public class WeakReferenceCache< K, V > extends ReferenceCache< K, V >
{
	static protected class Reference< K, V > extends WeakReference< CacheEntry< K, V > > implements CacheReference< K >
	{
		final protected K key;
		final protected Map< K, ? > map;

		public Reference(
				final K key,
				final CacheEntry< K, V > referent,
				final Map< K, ? > map,
				final ReferenceQueue< ? super CacheEntry< K, V > > q )
		{
			super( referent, q );
			this.key = key;
			this.map = map;
		}

		@Override
		public K getKey()
		{
			return key;
		}

		@Override
		public Map< K, ? > getMap()
		{
			return map;
		}
	}

	public WeakReferenceCache(
			final Loader< K, V > loader,
			final ReferenceQueue< ? super CacheEntry< K, V > > referenceQueue )
	{
		super( loader, referenceQueue );
	}

	@Override
	protected Reference< K, V > createReference( final K key, final CacheEntry< K, V > entry )
	{
		return new Reference<>( key, entry, map, referenceQueue );
	}
}
