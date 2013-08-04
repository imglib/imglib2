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
	public AbstractRemoteRandomAccessibleInterval(
			final Cache< K, E > cache,
			final Interval interval )
	{
		super( interval );
		this.cache = cache;
	}
	
	public AbstractRemoteRandomAccessibleInterval(
			final Cache< K, E > cache,
			final long[] min,
			final long[] max )
	{
		super( min, max );
		this.cache = cache;
	}
	
	public AbstractRemoteRandomAccessibleInterval(
			final Cache< K, E > cache,
			final long[] dimensions )
	{
		super( dimensions );
		this.cache = cache;
	}
	
	public AbstractRemoteRandomAccessibleInterval( final Interval interval )
	{
		this( new Cache< K, E >(), interval );
	}

	public AbstractRemoteRandomAccessibleInterval( final long[] min, final long[] max )
	{
		this( new Cache< K, E >(), min, max );
	}
	
	public AbstractRemoteRandomAccessibleInterval( final long[] dimensions )
	{
		this( new Cache< K, E >(), dimensions );
	}

	final protected Cache< K, E > cache;
}
