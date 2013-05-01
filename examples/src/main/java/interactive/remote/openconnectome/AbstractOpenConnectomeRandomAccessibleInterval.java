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

import interactive.remote.AbstractRemoteRandomAccessibleInterval;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;

/**
 * <p>Read pixels served by the
 * <a href="http://hssl.cs.jhu.edu/wiki/doku.php?id=randal:hssl:research:brain:data_set_description">Open
 * Connectome Volume Cutout Service</a>.</p>
 * 
 * <p>The {@link AbstractOpenConnectomeRandomAccessibleInterval} is created with a base
 * URL, e.g.
 * <a href="http://openconnecto.me/emca/kasthuri11">http://openconnecto.me/emca/kasthuri11</a>
 * the interval dimensions, the dimensions of image cubes to be fetched and
 * cached, and an offset in <em>z</em>.  This offset constitutes the
 * 0-coordinate in <em>z</em> and should point to the first slice of the
 * dataset.</p> 
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
abstract public class AbstractOpenConnectomeRandomAccessibleInterval< T extends RealType< T > > extends AbstractRemoteRandomAccessibleInterval< T, AbstractOpenConnectomeRandomAccessibleInterval< T >.Key, AbstractOpenConnectomeRandomAccessibleInterval< T >.Entry >
{
	public class Key
	{
		final public long x, y, z;
		
		public Key( final long x, final long y, final long z )
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public boolean equals( final Object other )
		{
			if ( this == other )
				return true;
			if ( !( other instanceof AbstractOpenConnectomeRandomAccessibleInterval.Key ) )
				return false;
			
			@SuppressWarnings( "unchecked" )
			final Key that = ( Key )other;
			
			return
					( this.x == that.x ) &&
					( this.y == that.y ) &&
					( this.z == that.z );
		}
		
		/**
		 * Return a hash code for the long tile index according to
		 * {@link Long#hashCode()}.  The hash has no collisions if the tile
		 * index is smaller than 2<sup>32</sup>.
		 *   
		 * @return
		 */
		@Override
		public int hashCode() {
			final long value = ( z * height + y ) * width + x;
			return ( int )( value ^ ( value >>> 32 ) );
		}
	}
	
	class Entry extends AbstractRemoteRandomAccessibleInterval< T, Key, Entry >.Entry
	{
		final public byte[] data;
		
		public Entry( final Key key, final byte[] data )
		{
			super( key );
			this.data = data;
		}
	}
	
	abstract public class AbstractOpenConnectomeRandomAccess extends AbstractLocalizable implements RandomAccess< T >
	{
		protected long xDiv, yDiv, zDiv;
		protected int xMod, yMod, zMod;
		protected byte[] pixels;
		final T t;

		public AbstractOpenConnectomeRandomAccess( final T t )
		{
			super( 3 );
			this.t = t;
			fetchPixels();
		}
		
		public AbstractOpenConnectomeRandomAccess( final AbstractOpenConnectomeRandomAccess template )
		{
			super( 3 );
			
			t = template.t.copy();
			
			position[ 0 ] = template.position[ 0 ];
			position[ 1 ] = template.position[ 1 ];
			position[ 2 ] = template.position[ 2 ];
			
			xDiv = template.xDiv;
			yDiv = template.yDiv;
			zDiv = template.zDiv;
			
			xMod = template.xMod;
			yMod = template.yMod;
			zMod = template.zMod;
			
			pixels = template.pixels;
		}
		
		protected void fetchPixels()
		{
			pixels = AbstractOpenConnectomeRandomAccessibleInterval.this.fetchPixels( xDiv, yDiv, zDiv );
		}
		
		@Override
		public void fwd( final int d )
		{
			++position[ d ];
			switch ( d )
			{
			case 0:
				++xMod;
				if ( xMod == cellWidth )
				{
					++xDiv;
					xMod = 0;
					fetchPixels();
				}
				break;
			case 1:
				++yMod;
				if ( yMod == cellHeight )
				{
					++yDiv;
					yMod = 0;
					fetchPixels();
				}
				break;
			default:
				++zMod;
				if ( zMod == cellDepth )
				{
					++zDiv;
					zMod = 0;
					fetchPixels();
				}
			}
		}

		@Override
		public void bck( final int d )
		{
			--position[ d ];
			switch ( d )
			{
			case 0:
				--xMod;
				if ( xMod == -1 )
				{
					--xDiv;
					xMod = cellWidth - 1;
					fetchPixels();
				}
				break;
			case 1:
				--yMod;
				if ( yMod == -1 )
				{
					--yDiv;
					yMod = cellHeight - 1;
					fetchPixels();
				}
				break;
			default:
				--zMod;
				if ( zMod == -1 )
				{
					--zDiv;
					zMod = cellDepth - 1;
					fetchPixels();
				}
				break;
			}
		}

		@Override
		public void move( final int distance, final int d )
		{
			move( ( long )distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			position[ d ] += distance;
			switch ( d )
			{
			case 0:
				final long xDiv1 = position[ 0 ] / cellWidth;
				if ( xDiv1 == xDiv )
					xMod -= distance;
				else
				{
					xDiv = xDiv1;
					xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
					fetchPixels();
				}
				break;
			case 1:
				final long yDiv1 = position[ 1 ] / cellHeight;
				if ( yDiv1 == yDiv )
					yMod -= distance;
				else
				{
					yDiv = yDiv1;
					yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
					fetchPixels();
				}
				break;
			default:
				final long zDiv1 = position[ 2 ] / cellDepth;
				if ( zDiv1 == zDiv )
					zMod -= distance;
				else
				{
					zDiv = zDiv1;
					zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
					fetchPixels();
				}
				break;
			}
		}

		@Override
		public void move( final Localizable localizable )
		{
			boolean updatePixels = false;
			
			final long dx = localizable.getLongPosition( 0 );
			final long dy = localizable.getLongPosition( 1 );
			final long dz = localizable.getLongPosition( 2 );
			
			position[ 0 ] += dx;
			position[ 1 ] += dy;
			position[ 2 ] += dz;
			
			final long xDiv1 = position[ 0 ] / cellWidth;
			if ( xDiv1 == xDiv )
				xMod += dx;
			else
			{
				xDiv = xDiv1;
				xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
				updatePixels = true;
			}
			
			final long yDiv1 = position[ 1 ] / cellHeight;
			if ( yDiv1 == yDiv )
				yMod += dy;
			else
			{
				yDiv = yDiv1;
				yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
				updatePixels = true;
			}
			
			final long zDiv1 = position[ 2 ] / cellDepth;
			if ( zDiv1 == zDiv )
				zMod += dz;
			else
			{
				zDiv = zDiv1;
				zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
				updatePixels = true;
			}
			
			if ( updatePixels )
				fetchPixels();
		}

		@Override
		public void move( final int[] distance )
		{
			boolean updatePixels = false;
			
			position[ 0 ] += distance[ 0 ];
			position[ 1 ] += distance[ 1 ];
			position[ 2 ] += distance[ 2 ];
			
			final long xDiv1 = position[ 0 ] / cellWidth;
			if ( xDiv1 == xDiv )
				xMod += distance[ 0 ];
			else
			{
				xDiv = xDiv1;
				xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
				updatePixels = true;
			}
			
			final long yDiv1 = position[ 1 ] / cellHeight;
			if ( yDiv1 == yDiv )
				yMod += distance[ 1 ];
			else
			{
				yDiv = yDiv1;
				yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
				updatePixels = true;
			}
			
			final long zDiv1 = position[ 2 ] / cellDepth;
			if ( zDiv1 == zDiv )
				zMod += distance[ 2 ];
			else
			{
				zDiv = zDiv1;
				zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
				updatePixels = true;
			}
			
			if ( updatePixels )
				fetchPixels();
		}

		@Override
		public void move( final long[] distance )
		{
			boolean updatePixels = false;
			
			position[ 0 ] += distance[ 0 ];
			position[ 1 ] += distance[ 1 ];
			position[ 2 ] += distance[ 2 ];
			
			final long xDiv1 = position[ 0 ] / cellWidth;
			if ( xDiv1 == xDiv )
				xMod += distance[ 0 ];
			else
			{
				xDiv = xDiv1;
				xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
				updatePixels = true;
			}
			
			final long yDiv1 = position[ 1 ] / cellHeight;
			if ( yDiv1 == yDiv )
				yMod += distance[ 1 ];
			else
			{
				yDiv = yDiv1;
				yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
				updatePixels = true;
			}
			
			final long zDiv1 = position[ 2 ] / cellDepth;
			if ( zDiv1 == zDiv )
				zMod += distance[ 2 ];
			else
			{
				zDiv = zDiv1;
				zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
				updatePixels = true;
			}
			
			if ( updatePixels )
				fetchPixels();
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			boolean updatePixels = false;
			
			position[ 0 ] = localizable.getLongPosition( 0 );
			position[ 1 ] = localizable.getLongPosition( 1 );
			position[ 2 ] = localizable.getLongPosition( 2 );
			
			final long xDiv1 = position[ 0 ] / cellWidth;
			xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
			if ( xDiv1 != xDiv )
			{
				xDiv = xDiv1;
				updatePixels = true;
			}
			
			final long yDiv1 = position[ 1 ] / cellHeight;
			yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
			if ( yDiv1 != yDiv )
			{
				yDiv = yDiv1;
				updatePixels = true;
			}
			
			final long zDiv1 = position[ 2 ] / cellDepth;
			zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
			if ( zDiv1 != zDiv )
			{
				zDiv = zDiv1;
				updatePixels = true;
			}
			
			if ( updatePixels )
				fetchPixels();
		}

		@Override
		public void setPosition( final int[] pos )
		{
			boolean updatePixels = false;
			
			position[ 0 ] = pos[ 0 ];
			position[ 1 ] = pos[ 1 ];
			position[ 2 ] = pos[ 2 ];
			
			
			final long xDiv1 = position[ 0 ] / cellWidth;
			xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
			if ( xDiv1 != xDiv )
			{
				xDiv = xDiv1;
				updatePixels = true;
			}
			
			final long yDiv1 = position[ 1 ] / cellHeight;
			yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
			if ( yDiv1 != yDiv )
			{
				yDiv = yDiv1;
				updatePixels = true;
			}
			
			final long zDiv1 = position[ 2 ] / cellDepth;
			zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
			if ( zDiv1 != zDiv )
			{
				zDiv = zDiv1;
				updatePixels = true;
			}
			
			if ( updatePixels )
				fetchPixels();
		}

		@Override
		public void setPosition( final long[] pos )
		{
			boolean updatePixels = false;
			
			position[ 0 ] = pos[ 0 ];
			position[ 1 ] = pos[ 1 ];
			position[ 2 ] = pos[ 2 ];
			
			final long xDiv1 = position[ 0 ] / cellWidth;
			xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
			if ( xDiv1 != xDiv )
			{
				xDiv = xDiv1;
				updatePixels = true;
			}
			
			final long yDiv1 = position[ 1 ] / cellHeight;
			yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
			if ( yDiv1 != yDiv )
			{
				yDiv = yDiv1;
				updatePixels = true;
			}
			
			final long zDiv1 = position[ 2 ] / cellDepth;
			zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
			if ( zDiv1 != zDiv )
			{
				zDiv = zDiv1;
				updatePixels = true;
			}
			
			if ( updatePixels )
				fetchPixels();
		}

		@Override
		public void setPosition( final int pos, final int d )
		{
			position[ d ] = pos;
			
			switch ( d )
			{
			case 0:
				final long xDiv1 = position[ 0 ] / cellWidth;
				xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
				if ( xDiv1 != xDiv )
				{
					xDiv = xDiv1;
					fetchPixels();
				}
				break;
			case 1:
				final long yDiv1 = position[ 1 ] / cellHeight;
				yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
				if ( yDiv1 != yDiv )
				{
					yDiv = yDiv1;
					fetchPixels();
				}
				break;
			default:
				final long zDiv1 = position[ 2 ] / cellDepth;
				zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
				if ( zDiv1 != zDiv )
				{
					zDiv = zDiv1;
					fetchPixels();
				}	
			}
		}

		@Override
		public void setPosition( final long pos, final int d )
		{
			//System.out.println( "setting position " + d + " to " + pos );
			
			position[ d ] = pos;
			
			switch ( d )
			{
			case 0:
				final long xDiv1 = position[ 0 ] / cellWidth;
				xMod = ( int )( position[ 0 ] - xDiv1 * cellWidth );
				if ( xDiv1 != xDiv )
				{
					xDiv = xDiv1;
					fetchPixels();
				}
				break;
			case 1:
				final long yDiv1 = position[ 1 ] / cellHeight;
				yMod = ( int )( position[ 1 ] - yDiv1 * cellHeight );
				if ( yDiv1 != yDiv )
				{
					yDiv = yDiv1;
					fetchPixels();
				}
				break;
			default:
				final long zDiv1 = position[ 2 ] / cellDepth;
				zMod = ( int )( position[ 2 ] - zDiv1 * cellDepth );
				if ( zDiv1 != zDiv )
				{
					zDiv = zDiv1;
					fetchPixels();
				}	
			}
		}
	}
	
	final protected String baseUrl;
	final protected long height, width, depth, minZ;
	final protected int cellWidth, cellHeight, cellDepth;
	protected long i;
	
	public AbstractOpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final int cellWidth, final int cellHeight, final int cellDepth, final long minZ )
	{
		super( new long[]{ width, height, depth } );
		this.baseUrl = url + "/zip/";
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.cellDepth = cellDepth;
		this.width = ( long )Math.ceil( ( double )width / cellWidth );
		this.height = ( long )Math.ceil( ( double )height / cellHeight );
		this.depth = ( long )Math.ceil( ( double )depth / cellDepth );
		this.minZ = minZ;
	}
	
	public AbstractOpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final long minZ )
	{
		this( url, width, height, depth, 64, 64, 64, minZ );
	}
	
	public AbstractOpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth )
	{
		this( url, width, height, depth, 0 );
	}
	
	@Override
	public int numDimensions()
	{
		return 3;
	}
	
	protected void fetchPixels3( final byte[] bytes, final long x, final long y, final long z )
	{
		final long x0 = cellWidth * x;
		final long y0 = cellHeight * y;
		final long z0 = cellDepth * z + minZ;
			
		final StringBuffer url = new StringBuffer( baseUrl );
		url.append( "0/" );
		url.append( x0 );
		url.append( "," );
		url.append( x0 + cellWidth );
		url.append( "/" );
		url.append( y0 );
		url.append( "," );
		url.append( y0 + cellHeight );
		url.append( "/" );
		url.append( z0 );
		url.append( "," );
		url.append( z0 + cellDepth );
		url.append( "/" );
		
		try
		{
			final URL file = new URL( url.toString() );
			final InputStream in = file.openStream();
			final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			final byte[] chunk = new byte[ 4096 ];
			int l;
			for ( l = in.read( chunk ); l > 0; l = in.read( chunk ) )
			    byteStream.write( chunk, 0, l );

			final byte[] zippedBytes = byteStream.toByteArray();
			final Inflater inflater = new Inflater();
			inflater.setInput( zippedBytes );
			inflater.inflate( bytes );
			inflater.end();
			byteStream.close();
		}
		catch (final IOException e)
		{
			System.out.println( "failed loading x=" + x + " y=" + y + " z=" + z + " url(" + url.toString() + ")" );
		}
		catch( final DataFormatException e )
		{
			System.out.println( "failed unpacking x=" + x + " y=" + y + " z=" + z + " url(" + url.toString() + ")" );
		}
	}
		
	protected byte[] fetchPixels2( final long x, final long y, final long z )
	{
		final SoftReference< Entry > ref;
		final byte[] bytes;
		synchronized ( cache )
		{
			final Key key = new Key( x, y, z );
			final Reference< Entry > cachedReference = cache.get( key );
			if ( cachedReference != null )
			{
				final Entry cachedEntry = cachedReference.get();
				if ( cachedEntry != null )
					return cachedEntry.data;
			}
			
			bytes = new byte[ cellWidth * cellHeight * cellDepth ];
			ref = new SoftReference< Entry >( new Entry( key, bytes ) );
			cache.put( key, ref );
		}
		fetchPixels3( bytes, x, y, z );
		return bytes;
	}
	
	protected byte[] fetchPixels( final long x, final long y, final long z )
	{
		try
		{
			return fetchPixels2( x, y, z );
		}
		catch ( final OutOfMemoryError e )
		{
			System.gc();
			return fetchPixels2( x, y, z );
		}
	}
}
