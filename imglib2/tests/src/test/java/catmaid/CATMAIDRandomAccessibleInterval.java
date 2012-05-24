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
package catmaid;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.imglib2.AbstractInterval;
import net.imglib2.AbstractLocalizable;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYProjector;
import net.imglib2.type.numeric.ARGBType;

/**
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class CATMAIDRandomAccessibleInterval extends AbstractInterval implements RandomAccessibleInterval< ARGBType >
{
	public class Key
	{
		final protected long r, c, z;
		
		public Key( final long r, final long c, final long z )
		{
			this.r = r;
			this.c = c;
			this.z = z;
		}
		
		@Override
		public boolean equals( final Object other )
		{
			if ( this == other )
				return true;
			if ( !( other instanceof Key ) )
				return false;
			final Key that = ( Key )other;
			return
					( this.r == that.r ) &&
					( this.c == that.c ) &&
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
			final long value = ( z * rows + r ) * cols + c;
			return ( int )( value ^ ( value >>> 32 ) );
		}
	}
	
	class Entry
	{
		final protected Key key;
		final protected int[] data;
		
		public Entry( final Key key, final int[] data )
		{
			this.key = key;
			this.data = data;
		}
		
		@Override
		public void finalize()
		{
			synchronized ( cache )
			{
				System.out.println( "finalizing..." );
				cache.remove( key );
				System.out.println( cache.size() + " tiles chached." );
			}
		}
	}
	
	public class CATMAIDRandomAccess extends AbstractLocalizable implements RandomAccess< ARGBType >
	{
		protected long r, c;
		protected int xMod, yMod;
		protected int[] pixels;
		final ARGBType t = new ARGBType();

		public CATMAIDRandomAccess()
		{
			super( 3 );
			fetchPixels();
		}
		
		public CATMAIDRandomAccess( final CATMAIDRandomAccess template )
		{
			super( 3 );
			
			position[ 0 ] = template.position[ 0 ];
			position[ 1 ] = template.position[ 1 ];
			position[ 2 ] = template.position[ 2 ];
			
			r = template.r;
			c = template.c;
			
			xMod = template.xMod;
			yMod = template.yMod;
			
			pixels = template.pixels;
		}
		
		protected void fetchPixels()
		{
			pixels = CATMAIDRandomAccessibleInterval.this.fetchPixels( r, c, position[ 2 ] );
		}
		
		@Override
		public void fwd( final int d )
		{
			++position[ d ];
			switch ( d )
			{
			case 0:
				++xMod;
				if ( xMod == 256 )
				{
					++c;
					xMod = 0;
					fetchPixels();
				}
				break;
			case 1:
				++yMod;
				if ( yMod == 256 )
				{
					++r;
					yMod = 0;
					fetchPixels();
				}
				break;
			default:
				fetchPixels();
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
					--c;
					xMod = 255;
					fetchPixels();
				}
				break;
			case 1:
				--yMod;
				if ( yMod == -1 )
				{
					--r;
					yMod = 255;
					fetchPixels();
				}
				break;
			default:
				fetchPixels();
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
				final long c1 = position[ 0 ] / 256;
				if ( c1 == c )
					xMod -= distance;
				else
				{
					c = c1;
					xMod = ( int )( position[ 0 ] - c1 * 256 );
					fetchPixels();
				}
				break;
			case 1:
				final long r1 = position[ 1 ] / 256;
				if ( r1 == r )
					yMod -= distance;
				else
				{
					r = r1;
					yMod = ( int )( position[ 1 ] - r1 * 256 );
					fetchPixels();
				}
				break;
			default:
				fetchPixels();
			}
		}

		@Override
		public void move( final Localizable localizable )
		{
			boolean updatePixels = false;
			
			final long dx = localizable.getLongPosition( 0 );
			final long dy = localizable.getLongPosition( 1 );
			
			position[ 0 ] += dx;
			position[ 1 ] += dy;
			
			final long c1 = position[ 0 ] / 256;
			if ( c1 == c )
				xMod += dx;
			else
			{
				c = c1;
				xMod = ( int )( position[ 0 ] - c1 * 256 );
				updatePixels = true;
			}
			
			final long r1 = position[ 1 ] / 256;
			if ( r1 == r )
				yMod += dy;
			else
			{
				r = r1;
				yMod = ( int )( position[ 1 ] - r1 * 256 );
				updatePixels = true;
			}
			
			for ( int d = 2; d < numDimensions(); ++d )
			{
				final long distance = localizable.getLongPosition( d );
				updatePixels |= distance != 0;
				position[ d ] += distance;
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
			
			final long c1 = position[ 0 ] / 256;
			if ( c1 == c )
				xMod += distance[ 0 ];
			else
			{
				c = c1;
				xMod = ( int )( position[ 0 ] - c1 * 256 );
				updatePixels = true;
			}
			
			final long r1 = position[ 1 ] / 256;
			if ( r1 == r )
				yMod += distance[ 1 ];
			else
			{
				r = r1;
				yMod = ( int )( position[ 1 ] - r1 * 256 );
				updatePixels = true;
			}
			
			for ( int d = 2; d < numDimensions(); ++d )
			{
				updatePixels |= distance[ d ] != 0;
				position[ d ] += distance[ d ];
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
			
			final long c1 = position[ 0 ] / 256;
			if ( c1 == c )
				xMod += distance[ 0 ];
			else
			{
				c = c1;
				xMod = ( int )( position[ 0 ] - c1 * 256 );
				updatePixels = true;
			}
			
			final long r1 = position[ 1 ] / 256;
			if ( r1 == r )
				yMod += distance[ 1 ];
			else
			{
				r = r1;
				yMod = ( int )( position[ 1 ] - r1 * 256 );
				updatePixels = true;
			}
			
			for ( int d = 2; d < numDimensions(); ++d )
			{
				updatePixels |= distance[ d ] != 0;
				position[ d ] += distance[ d ];
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
			
			final long c1 = position[ 0 ] / 256;
			xMod = ( int )( position[ 0 ] - c1 * 256 );
			if ( c1 != c )
			{
				c = c1;
				updatePixels = true;
			}
			
			final long r1 = position[ 1 ] / 256;
			yMod = ( int )( position[ 1 ] - r1 * 256 );
			if ( r1 != r )
			{
				r = r1;
				updatePixels = true;
			}
			
			for ( int d = 2; d < numDimensions(); ++d )
			{
				final long p = localizable.getLongPosition( d );
				updatePixels |= position[ d ] != p;
				position[ d ] = p;
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
			
			final long c1 = position[ 0 ] / 256;
			xMod = ( int )( position[ 0 ] - c1 * 256 );
			if ( c1 != c )
			{
				c = c1;
				updatePixels = true;
			}
			
			final long r1 = position[ 1 ] / 256;
			yMod = ( int )( position[ 1 ] - r1 * 256 );
			if ( r1 != r )
			{
				r = r1;
				updatePixels = true;
			}
			
			for ( int d = 2; d < numDimensions(); ++d )
			{
				updatePixels |= position[ d ] != pos[ d ];
				position[ d ] = pos[ d ];
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
			
			final long c1 = position[ 0 ] / 256;
			xMod = ( int )( position[ 0 ] - c1 * 256 );
			if ( c1 != c )
			{
				c = c1;
				updatePixels = true;
			}
			
			final long r1 = position[ 1 ] / 256;
			yMod = ( int )( position[ 1 ] - r1 * 256 );
			if ( r1 != r )
			{
				r = r1;
				updatePixels = true;
			}
			
			for ( int d = 2; d < numDimensions(); ++d )
			{
				updatePixels |= position[ d ] != pos[ d ];
				position[ d ] = pos[ d ];
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
				final long c1 = position[ 0 ] / 256;
				xMod = ( int )( position[ 0 ] - c1 * 256 );
				if ( c1 != c )
				{
					c = c1;
					fetchPixels();
				}
				break;
			case 1:
				final long r1 = position[ 1 ] / 256;
				yMod = ( int )( position[ 1 ] - r1 * 256 );
				if ( r1 != r )
				{
					r = r1;
					fetchPixels();
				}
				break;
			default:
				if ( position[ d ] != pos )
					fetchPixels();	
			}
		}

		@Override
		public void setPosition( final long pos, final int d )
		{
			position[ d ] = pos;
			
			switch ( d )
			{
			case 0:
				final long c1 = position[ 0 ] / 256;
				xMod = ( int )( position[ 0 ] - c1 * 256 );
				if ( c1 != c )
				{
					c = c1;
					fetchPixels();
				}
				break;
			case 1:
				final long r1 = position[ 1 ] / 256;
				yMod = ( int )( position[ 1 ] - r1 * 256 );
				if ( r1 != r )
				{
					r = r1;
					fetchPixels();
				}
				break;
			default:
				if ( position[ d ] != pos )
					fetchPixels();	
			}
		}

		@Override
		public ARGBType get()
		{
			t.set( pixels[ 256 * yMod + xMod ] );
			return t;
		}

		@Override
		public CATMAIDRandomAccess copy()
		{
			return new CATMAIDRandomAccess( this );
		}

		@Override
		public CATMAIDRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}
	
	final protected HashMap< Key, SoftReference< Entry > > cache = new HashMap< CATMAIDRandomAccessibleInterval.Key, SoftReference< Entry > >();
	final protected String baseUrl;
	final protected long rows, cols;
	protected long i;
	
	public CATMAIDRandomAccessibleInterval( final long width, final long height, final long depth, final String url )
	{
		super( new long[]{ width, height, depth } );
		this.baseUrl = url;
		cols = ( long )Math.ceil( width / 256.0 );
		rows = ( long )Math.ceil( height / 256.0 );
	}
	
	@Override
	public int numDimensions()
	{
		return 3;
	}

	@Override
	public RandomAccess< ARGBType > randomAccess()
	{
		return new CATMAIDRandomAccess();
	}

	@Override
	public RandomAccess< ARGBType > randomAccess( final Interval interval )
	{
		return randomAccess();
	}
	
	protected int[] fetchPixels( final long r, final long c, final long z )
	{
		try
		{
			return fetchPixels2( r, c, z );
		}
		catch ( final OutOfMemoryError e )
		{
			System.gc();
			return fetchPixels2( r, c, z );
		}
	}
		
	protected int[] fetchPixels2( final long r, final long c, final long z )
	{
		final Key key = new Key( r, c, z );
		synchronized ( cache )
		{
			final SoftReference< Entry > cachedReference = cache.get( key );
			if ( cachedReference != null )
			{
				final Entry cachedEntry = cachedReference.get();
				if ( cachedEntry != null )
					return cachedEntry.data;
			}

			final String urlString = new StringBuffer( baseUrl ).append( z ).append( "/" ).append( r ).append( "_" ).append( c ).append( "_0.jpg" ).toString();
			final int[] pixels = new int[ 256 * 256 ];
			try
			{
				final URL url = new URL( urlString );
			    final BufferedImage image = ImageIO.read( url );
			    final PixelGrabber pg = new PixelGrabber( image, 0, 0, 256, 256, pixels, 0, 256 );
				pg.grabPixels();
				cache.put( key, new SoftReference< Entry >( new Entry( key, pixels ) ) );
			}
			catch (final IOException e)
			{
				System.out.println( "failed loading r=" + r + " c=" + c + " url(" + urlString + ")" );
				cache.put( key, new SoftReference< Entry >( new Entry( key, pixels ) ) );
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
			return pixels;
		}
	}
	
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		final CATMAIDRandomAccessibleInterval map = new CATMAIDRandomAccessibleInterval( 6016, 4464, 803, "http://catmaid.mpi-cbg.de/map/c-elegans/" );
		final ARGBScreenImage screenImage = new ARGBScreenImage( 1024, 1024 );
		final XYProjector< ARGBType, ARGBType > projector = new XYProjector< ARGBType, ARGBType >( map, screenImage, new TypeIdentity< ARGBType >() );
		projector.map();
		new ImagePlus( "map", new ColorProcessor( screenImage.image() ) ).show();
	}
}
