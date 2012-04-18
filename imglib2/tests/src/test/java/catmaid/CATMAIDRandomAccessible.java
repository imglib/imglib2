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
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYProjector;
import net.imglib2.type.numeric.ARGBType;

/**
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class CATMAIDRandomAccessible implements RandomAccessible< ARGBType >
{
	public class Key
	{
		final protected long r, c;
		
		public Key( final long r, final long c )
		{
			this.r = r;
			this.c = c;
		}
		
		@Override
		public boolean equals( final Object other )
		{
			if ( this == other )
				return true;
			if ( !( other instanceof Key ) )
				return false;
			final Key that = ( Key )other;
		    return ( this.r == that.r ) &&
		           ( this.c == that.c );
		}
		
		@Override
		public int hashCode() {
		    return new Long( r ).hashCode() * 3 + new Long( c ).hashCode() * 5;
		}
	}
	
	public class CATMAIDRandomAccess implements RandomAccess< ARGBType >
	{
		protected long x, y, r, c;
		protected int xMod, yMod;
		protected int[] pixels;
		
		public CATMAIDRandomAccess()
		{
			getPixels();
		}
		
		public CATMAIDRandomAccess( final CATMAIDRandomAccess template )
		{
			x = template.x;
			y = template.y;
			r = template.r;
			c = template.c;
			xMod = template.xMod;
			yMod = template.yMod;
			pixels = template.pixels;
		}
		
		protected void getPixels()
		{
			pixels = fetchPixels( r, c );
		}
		
		final ARGBType t = new ARGBType();

		@Override
		public void localize( final int[] position )
		{
			position[ 0 ] = ( int )x;
			position[ 1 ] = ( int )y;
		}

		@Override
		public void localize( final long[] position )
		{
			position[ 0 ] = x;
			position[ 1 ] = y;
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int )getLongPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			switch ( d )
			{
			case 0:
				return x;
			case 1:
				return y;
			default:
				return 0;
			}
		}

		@Override
		public void localize( final float[] position )
		{
			position[ 0 ] = x;
			position[ 1 ] = y;
		}

		@Override
		public void localize( final double[] position )
		{
			position[ 0 ] = x;
			position[ 1 ] = y;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return getLongPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return getLongPosition( d );
		}

		@Override
		public int numDimensions()
		{
			return 2;
		}

		@Override
		public void fwd( final int d )
		{
			if ( d == 0)
			{
				++x;
				++xMod;
				if ( xMod == 256 )
				{
					++c;
					xMod = 0;
					getPixels();
				}
			}
			else
			{	++y;
				++yMod;
				if ( yMod == 256 )
				{
					++r;
					yMod = 0;
					getPixels();
				}
			}
		}

		@Override
		public void bck( final int d )
		{
			if ( d == 0)
			{
				--x;
				--xMod;
				if ( xMod == -1 )
				{
					--c;
					xMod = 255;
					getPixels();
				}
			}
			else
			{	--y;
				--yMod;
				if ( yMod == -1 )
				{
					--r;
					yMod = 255;
					getPixels();
				}
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
			if ( d == 0)
			{
				x += distance;
				final long c1 = x / 256;
				if ( c1 == c )
					xMod -= distance;
				else
				{
					c = c1;
					xMod = ( int )( x - c1 * 256 );
					getPixels();
				}
			}
			else
			{
				y += distance;
				final long r1 = y / 256;
				if ( r1 == r )
					yMod -= distance;
				else
				{
					r = r1;
					yMod = ( int )( y - r1 * 256 );
					getPixels();
				}
			}
		}

		@Override
		public void move( final Localizable localizable )
		{
			final long dx = localizable.getLongPosition( 0 );
			final long dy = localizable.getLongPosition( 1 );
			
			x += dx;
			y += dy;
			
			final long c1 = x / 256;
			if ( c1 == c )
				xMod += dx;
			else
			{
				c = c1;
				xMod = ( int )( x - c1 * 256 );
				getPixels();
			}
			
			final long r1 = y / 256;
			if ( r1 == r )
				yMod += dy;
			else
			{
				r = r1;
				yMod = ( int )( y - r1 * 256 );
				getPixels();
			}
		}

		@Override
		public void move( final int[] distance )
		{
			x += distance[ 0 ];
			y += distance[ 1 ];
			
			final long c1 = x / 256;
			if ( c1 == c )
				xMod += distance[ 0 ];
			else
			{
				c = c1;
				xMod = ( int )( x - c1 * 256 );
				getPixels();
			}
			
			final long r1 = y / 256;
			if ( r1 == r )
				yMod += distance[ 1 ];
			else
			{
				r = r1;
				yMod = ( int )( y - r1 * 256 );
				getPixels();
			}
		}

		@Override
		public void move( final long[] distance )
		{
			x += distance[ 0 ];
			y += distance[ 1 ];
			
			final long c1 = x / 256;
			if ( c1 == c )
				xMod += distance[ 0 ];
			else
			{
				c = c1;
				xMod = ( int )( x - c1 * 256 );
				getPixels();
			}
			
			final long r1 = y / 256;
			if ( r1 == r )
				yMod += distance[ 1 ];
			else
			{
				r = r1;
				yMod = ( int )( y - r1 * 256 );
				getPixels();
			}
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			x = localizable.getLongPosition( 0 );
			y = localizable.getLongPosition( 1 );
			
			final long c1 = x / 256;
			xMod = ( int )( x - c1 * 256 );
			if ( c1 != c )
			{
				c = c1;
				getPixels();
			}
			
			final long r1 = y / 256;
			yMod = ( int )( y - r1 * 256 );
			if ( r1 != r )
			{
				r = r1;
				getPixels();
			}
		}

		@Override
		public void setPosition( final int[] position )
		{
			x = position[ 0 ];
			y = position[ 1 ];
			
			final long c1 = x / 256;
			xMod = ( int )( x - c1 * 256 );
			if ( c1 != c )
			{
				c = c1;
				getPixels();
			}
			
			final long r1 = y / 256;
			yMod = ( int )( y - r1 * 256 );
			if ( r1 != r )
			{
				r = r1;
				getPixels();
			}
		}

		@Override
		public void setPosition( final long[] position )
		{
			x = position[ 0 ];
			y = position[ 1 ];
			
			final long c1 = x / 256;
			xMod = ( int )( x - c1 * 256 );
			if ( c1 != c )
			{
				c = c1;
				getPixels();
			}
			
			final long r1 = y / 256;
			yMod = ( int )( y - r1 * 256 );
			if ( r1 != r )
			{
				r = r1;
				getPixels();
			}
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			if ( d == 0 )
			{
				x = position;
				
				final long c1 = x / 256;
				xMod = ( int )( x - c1 * 256 );
				if ( c1 != c )
				{
					c = c1;
					getPixels();
				}
			}
			else
			{
				
				y = position;
				
				final long r1 = y / 256;
				yMod = ( int )( y - r1 * 256 );
				if ( r1 != r )
				{
					r = r1;
					getPixels();
				}
			}
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			if ( d == 0 )
			{
				x = position;
				
				final long c1 = x / 256;
				xMod = ( int )( x - c1 * 256 );
				if ( c1 != c )
				{
					c = c1;
					getPixels();
				}
			}
			else
			{
				
				y = position;
				
				final long r1 = y / 256;
				yMod = ( int )( y - r1 * 256 );
				if ( r1 != r )
				{
					r = r1;
					getPixels();
				}
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
	
	final protected HashMap< Key, int[] > cache = new HashMap< CATMAIDRandomAccessible.Key, int[] >();
	
	@Override
	public int numDimensions()
	{
		return 2;
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
	
	protected int[] fetchPixels( final long r, final long c )
	{
		final Key key = new Key( r, c );
		final int[] cached = cache.get( key );
		if ( cached != null )
			return cached;
		System.out.println( r + " " + c + " " + cache.size() );
		final int [] pixels = new int[ 256 * 256 ];
		try
		{
		    final URL url = new URL( "http://tiles.openconnectomeproject.org/view/bock11/0/" + r + "_" + c + "_1.jpg" );
		    System.out.println( url );
		    final BufferedImage image = ImageIO.read( url );
//		    new ImagePlus( "", new ColorProcessor( image ) ).show();
		    final PixelGrabber pg = new PixelGrabber( image, 0, 0, 256, 256, pixels, 0, 256 );
			pg.grabPixels();
			cache.put( key, pixels );
		}
		catch (final IOException e)
		{
			System.out.println( "failed loading r=" + r + " c=" + c );
			cache.put( key, pixels );
			return pixels;
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
			return pixels;
		}
		return pixels;
	}
	
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		final CATMAIDRandomAccessible map = new CATMAIDRandomAccessible();
		final ARGBScreenImage screenImage = new ARGBScreenImage( 1024, 1024 );
		final XYProjector< ARGBType, ARGBType > projector = new XYProjector< ARGBType, ARGBType >( map, screenImage, new TypeIdentity< ARGBType >() );
		projector.map();
		new ImagePlus( "map", new ColorProcessor( screenImage.image() ) ).show();
	}
}
