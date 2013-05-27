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
package net.imglib2.display;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;

/**
 * {@link XYRandomAccessibleProjector} for {@link Volatile} input.  After each
 * {@link #map()} call, the projector has a {@link #isValid() state} that
 * signalizes whether all projected pixels were valid.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class VolatileXYRandomAccessibleProjector< T, A extends Volatile< T >, B > extends XYRandomAccessibleProjector< A, B >
{
	protected boolean valid = false;
	
	public VolatileXYRandomAccessibleProjector( final RandomAccessible< A > source, final RandomAccessibleInterval< B > target, final Converter< ? super A, B > converter )
	{
		super( source, target, converter );
	}
	
	/**
	 * @return true if all mapped pixels were {@link Volatile#isValid() valid}.
	 */
	public boolean isValid()
	{
		return valid;
	}
	
	@Override
	public void map()
	{
		for ( int d = 2; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ 0 ] = target.min( 0 );
		min[ 1 ] = target.min( 1 );
		max[ 0 ] = target.max( 0 );
		max[ 1 ] = target.max( 1 );
		final FinalInterval sourceInterval = new FinalInterval( min, max );

		final long cr = -target.dimension( 0 );

		final RandomAccess< B > targetRandomAccess = target.randomAccess( target );
		final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );

		final long width = target.dimension( 0 );
		final long height = target.dimension( 1 );

		sourceRandomAccess.setPosition( min );
		targetRandomAccess.setPosition( min[ 0 ], 0 );
		targetRandomAccess.setPosition( min[ 1 ], 1 );
		valid = true;
		for ( long y = 0; y < height; ++y )
		{
			for ( long x = 0; x < width; ++x )
			{
				final A a = sourceRandomAccess.get();
				converter.convert( a, targetRandomAccess.get() );
				valid &= a.isValid();
				sourceRandomAccess.fwd( 0 );
				targetRandomAccess.fwd( 0 );
			}
			sourceRandomAccess.move( cr, 0 );
			targetRandomAccess.move( cr, 0 );
			sourceRandomAccess.fwd( 1 );
			targetRandomAccess.fwd( 1 );
		}
	}
}
