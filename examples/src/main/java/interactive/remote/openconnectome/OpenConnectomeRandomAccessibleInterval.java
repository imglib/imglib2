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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

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
public class OpenConnectomeRandomAccessibleInterval extends
	AbstractOpenConnectomeRandomAccessibleInterval< UnsignedByteType, OpenConnectomeRandomAccessibleInterval.Entry >
{
	public class Entry extends AbstractOpenConnectomeRandomAccessibleInterval< UnsignedByteType, Entry >.Entry
	{
		final public byte[] data;
		
		public Entry( final Key key, final byte[] data )
		{
			super( key );
			this.data = data;
		}
	}
	
	public class OpenConnectomeRandomAccess extends AbstractOpenConnectomeRandomAccess
	{
		protected byte[] pixels;
		
		public OpenConnectomeRandomAccess()
		{
			super( new UnsignedByteType() );
		}
		
		public OpenConnectomeRandomAccess( final OpenConnectomeRandomAccess template )
		{
			super( template );
			pixels = template.pixels;
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
		
		@Override
		protected void fetchPixels()
		{
			final Entry entry = OpenConnectomeRandomAccessibleInterval.this.fetchPixels( xDiv, yDiv, zDiv );
			pixels = entry.data;
		}
	}
	
	public OpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final int cellWidth, final int cellHeight, final int cellDepth, final long minZ, final int level )
	{
		super( url, width, height, depth, cellWidth, cellHeight, cellDepth, minZ, level );
	}
	
	public OpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final long minZ, final int level )
	{
		this( url, width, height, depth, 64, 64, 64, minZ, level );
	}
	
	public OpenConnectomeRandomAccessibleInterval( final String url, final long width, final long height, final long depth, final int level )
	{
		this( url, width, height, depth, 0, level );
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
	
	@Override
	protected Entry fetchPixels2( final long x, final long y, final long z )
	{
		final SoftReference< Entry > ref;
		final Entry entry;
		final byte[] bytes;
		synchronized ( cache )
		{
			final Key key = new Key( x, y, z );
			final Reference< Entry > cachedReference = cache.get( key );
			if ( cachedReference != null )
			{
				final Entry cachedEntry = cachedReference.get();
				if ( cachedEntry != null )
					return cachedEntry;
			}
			
			bytes = new byte[ cellWidth * cellHeight * cellDepth ];
			entry = new Entry( key, bytes );
			ref = new SoftReference< Entry >( entry );
			cache.put( key, ref );
		}
		fetchPixels3( bytes, x, y, z );
		return entry;
	}
}
