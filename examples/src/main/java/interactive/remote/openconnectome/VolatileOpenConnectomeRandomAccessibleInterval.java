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

import interactive.remote.VolatileRealType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.imglib2.Interval;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * <p>Read pixels served by the
 * <a href="http://hssl.cs.jhu.edu/wiki/doku.php?id=randal:hssl:research:brain:data_set_description">Open
 * Connectome Volume Cutout Service</a>.</p>
 * 
 * <p>The {@link VolatileOpenConnectomeRandomAccessibleInterval} is created with a base
 * URL, e.g.
 * <a href="http://openconnecto.me/emca/kasthuri11">http://openconnecto.me/emca/kasthuri11</a>
 * the interval dimensions, the dimensions of image cubes to be fetched and
 * cached, and an offset in <em>z</em>.  This offset constitutes the
 * 0-coordinate in <em>z</em> and should point to the first slice of the
 * dataset.</p> 
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class VolatileOpenConnectomeRandomAccessibleInterval extends AbstractOpenConnectomeRandomAccessibleInterval< VolatileRealType< UnsignedByteType > >
{
	protected class Fetcher extends Thread
	{
		@Override
		final public void run()
		{
			while ( !isInterrupted() )
			{
				Reference< Entry > ref;
				synchronized ( queue )
				{
					try { ref = queue.pop(); }
					catch ( final NoSuchElementException e ) { ref = null; }
				}
				if ( ref == null )
				{
					synchronized ( this )
					{
						try { wait(); }
						catch ( final InterruptedException e ) {}
					}
				}
				else
				{
					final Entry entry;
					synchronized ( cache )
					{
						entry = ref.get();
						if ( entry != null )
						{
							/* replace WeakReferences by SoftReferences which promotes cache entries from third to second class citizens */
							synchronized ( cache )
							{
								cache.remove( entry.key );
								cache.put( entry.key, new SoftReference< Entry >( entry ) );
							}
						}
					}
					
					if ( entry != null )
					{	
						final long x0 = cellWidth * entry.key.x;
						final long y0 = cellHeight * entry.key.y;
						final long z0 = cellDepth * entry.key.z + minZ;
						
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
							inflater.inflate( entry.data );
								
							inflater.end();
							byteStream.close();
							
							//System.out.println( "cached x=" + x + " y=" + y + " z=" + z + " url(" + url.toString() + ")" );
						}
						catch (final IOException e)
						{
							System.out.println( "failed loading x=" + entry.key.x + " y=" + entry.key.y + " z=" + entry.key.z + " url(" + url.toString() + ")" );
						}
						catch( final DataFormatException e )
						{
							System.out.println( "failed unpacking x=" + entry.key.x + " y=" + entry.key.y + " z=" + entry.key.z + " url(" + url.toString() + ")" );
						}
					}
				}
			}
		}
	}
	
	public class VolatileOpenConnectomeRandomAccess extends AbstractOpenConnectomeRandomAccess
	{
		public VolatileOpenConnectomeRandomAccess()
		{
			super( new VolatileRealType< UnsignedByteType >( new UnsignedByteType() ) );
		}
		
		public VolatileOpenConnectomeRandomAccess( final VolatileOpenConnectomeRandomAccess template )
		{
			super( template );
		}
		
		@Override
		public VolatileRealType< UnsignedByteType > get()
		{
			t.get().set( 0xff & pixels[ ( zMod * cellHeight + yMod ) * cellWidth + xMod ] );
			return t;
		}

		@Override
		public VolatileOpenConnectomeRandomAccess copy()
		{
			return new VolatileOpenConnectomeRandomAccess( this );
		}

		@Override
		public VolatileOpenConnectomeRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}
	
	final protected Fetcher fetcher;
	final protected LinkedList< Reference< Entry > > queue = new LinkedList< Reference< Entry > >();
	
	public VolatileOpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final int cellWidth, final int cellHeight, final int cellDepth, final long minZ )
	{
		super( url, width, height, depth, cellWidth, cellHeight, cellDepth, minZ );
		
		fetcher = new Fetcher();
		fetcher.start();
	}
	
	public VolatileOpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final long minZ )
	{
		this( url, width, height, depth, 64, 64, 64, minZ );
	}
	
	public VolatileOpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth )
	{
		this( url, width, height, depth, 0 );
	}
	
	@Override
	public int numDimensions()
	{
		return 3;
	}
	
	
	@Override
	public VolatileOpenConnectomeRandomAccess randomAccess()
	{
		return new VolatileOpenConnectomeRandomAccess();
	}
	
	@Override
	public VolatileOpenConnectomeRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}
		
	@Override
	protected byte[] fetchPixels2( final long x, final long y, final long z )
	{
		final Reference< Entry > ref;
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
			
			final byte[] bytes = new byte[ cellWidth * cellHeight * cellDepth ];
			ref = new WeakReference< Entry >( new Entry( key, bytes ) );
			cache.put( key, ref );
			queue.add( ref );
		}
		synchronized ( fetcher )
		{
			fetcher.notify();
		}
		
		final Entry entry = ref.get();
		if ( entry != null )
			return entry.data;
		else
			return new byte[ cellWidth * cellHeight * cellDepth ];
		
	}
	
	@Override
	public void finalize()
	{
		fetcher.interrupt();
	}
}
