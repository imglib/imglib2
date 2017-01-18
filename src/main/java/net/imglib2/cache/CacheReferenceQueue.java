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

import net.imglib2.cache.ReferenceCache.CacheReference;

/**
 * A {@link ReferenceQueue} for {@link ReferenceCache}s.  Runs a thread to
 * clean pending references when ...
 *
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld
 */
public class CacheReferenceQueue< K, V > extends ReferenceQueue< V >
{

	/**
	 * Remove references from the cache that have been garbage-collected.
	 * To avoid long run-times, per call to {@code cleanUp()}, at most
	 * {@link #MAX_PER_FRAME_FINALIZE_ENTRIES} are processed.
	 */
	public void cleanUp( final int maxNumEntries )
	{
		System.out.println( "Cleaning up..." );
		for ( int i = 0; i < maxNumEntries; ++i )
		{
			final Reference< ? > poll = poll();
			if ( poll == null )
			{
				System.out.println( "  Cleaned up " + i + " entries" );
				break;
			}
			final CacheReference< K > x = ( CacheReference< K > ) poll;
			final Object key = x.getKey();
			final Map< K, ? > map = x.getMap();
			final Object ref = map.get( key );
			if ( ref == poll )
			{
				map.remove( key );
				System.out.println( "  Removed key " + key );
			}
		}
	}
}
