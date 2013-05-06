/**
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package interactive.remote;

import java.lang.ref.Reference;
import java.util.HashMap;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;

/**
 * Read pixels served by a remote service.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
abstract public class AbstractRemoteRandomAccessibleInterval< T, K, E > extends AbstractInterval implements RandomAccessibleInterval< T >
{
	public AbstractRemoteRandomAccessibleInterval( final Interval interval)
	{
		super( interval );
	}

	public AbstractRemoteRandomAccessibleInterval( final long[] min, final long[] max )
	{
		super( min, max );
	}
	
	public AbstractRemoteRandomAccessibleInterval( final long[] dimensions )
	{
		super( dimensions );
	}

	public class Entry
	{
		final public K key;
		
		public Entry( final K key )
		{
			this.key = key;
		}
		
		@Override
		public void finalize()
		{
			synchronized ( cache )
			{
//				System.out.println( "finalizing..." );
				cache.remove( key );
//				System.out.println( cache.size() + " tiles chached." );
			}
		}
	}
	
	final protected HashMap< K, Reference< E > > cache = new HashMap< K, Reference< E > >();
}
