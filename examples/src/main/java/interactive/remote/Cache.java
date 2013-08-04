/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package interactive.remote;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * A simple cache that depends on {@link Reference} cleanup triggered by some
 * external mechanism, e.g. garbage collection.  To that end, cache entries
 * need to implement the finalize method as in the example implementation in 
 * {@link Entry}.  Typically, you would want to extend {@link Entry}.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class Cache< K, E >
{
	final protected HashMap< K, Reference< E > > cache = new HashMap< K, Reference< E > >();
	
	static public class Entry< K, E extends Entry< K, E > >
	{
		final public K key;
		final HashMap< K, Reference< E > > cache;
		
		public Entry( final K key, final HashMap< K, Reference< E > > cache )
		{
			this.key = key;
			this.cache = cache;
		}
		
		public Entry( final K key, final Cache< K, E > cache )
		{
			this.key = key;
			this.cache = cache.getMap();
		}
		
		@Override
		public void finalize()
		{
			synchronized ( cache )
			{
//				System.out.println( "finalizing " + key.toString() );
				cache.remove( key );
//				System.out.println( cache.size() + " tiles chached." );
			}
		}
	}
	
	public E get( final K key )
	{
		final Reference< E > ref = cache.get( key );
		if ( ref != null )
			return ref.get();
		else
			return null;
	}
	
	public void putReference( final K key, final Reference< E > reference )
	{
		cache.put( key, reference );
	}
	
	public void putSoft( final K key, final E entry )
	{
		cache.put( key, new SoftReference< E >( entry ) );
	}
	
	public void putWeak( final K key, final E entry )
	{
		cache.put( key, new WeakReference< E >( entry ) );
	}
	
	public E remove( final K key )
	{
		final Reference< E > ref = cache.remove( key );
		if ( ref != null )
			return ref.get();
		else
			return null;
	}
	
	public HashMap< K, Reference< E > > getMap()
	{
		return cache;
	}
}
