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
package interactive.remote.openconnectome;

import net.imglib2.Interval;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * <p>Read pixels served by the
 * <a href="http://hssl.cs.jhu.edu/wiki/doku.php?id=randal:hssl:research:brain:data_set_description">Open
 * Connectome Volume Cutout Service</a>.</p>
 * 
 * <p>The {@link OpenConnectomeRandomAccessibleInterval} is created with a base
 * URL, e.g.
 * <a href="http://openconnecto.me/emca/kasthuri11">http://openconnecto.me/emca/kasthuri11</a>
 * the interval dimensions, the dimensions of image cubes to be fetched and
 * cached, and an offset in <em>z</em>.  This offset constitutes the
 * 0-coordinate in <em>z</em> and should point to the first slice of the
 * dataset.</p> 
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class OpenConnectomeRandomAccessibleInterval extends AbstractOpenConnectomeRandomAccessibleInterval< UnsignedByteType >
{
	public class OpenConnectomeRandomAccess extends AbstractOpenConnectomeRandomAccess
	{
		public OpenConnectomeRandomAccess()
		{
			super( new UnsignedByteType() );
		}
		
		public OpenConnectomeRandomAccess( final OpenConnectomeRandomAccess template )
		{
			super( template );
		}
		
		@Override
		public UnsignedByteType get()
		{
			t.set( 0xff & pixels[ ( zMod * cellHeight + yMod ) * cellWidth + xMod ] );
			return t;
		}

		@Override
		public OpenConnectomeRandomAccess copy()
		{
			return new OpenConnectomeRandomAccess( this );
		}

		@Override
		public OpenConnectomeRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}
	
	public OpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final int cellWidth, final int cellHeight, final int cellDepth, final long minZ )
	{
		super( url, width, height, depth, cellWidth, cellHeight, cellDepth, minZ );
	}
	
	public OpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final long minZ )
	{
		this( url, width, height, depth, 64, 64, 64, minZ );
	}
	
	public OpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth )
	{
		this( url, width, height, depth, 0 );
	}
	
	@Override
	public OpenConnectomeRandomAccess randomAccess()
	{
		return new OpenConnectomeRandomAccess();
	}

	@Override
	public OpenConnectomeRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}
}
