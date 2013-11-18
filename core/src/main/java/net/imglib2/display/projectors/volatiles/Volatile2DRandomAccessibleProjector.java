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
package net.imglib2.display.projectors.volatiles;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.display.projectors.Projector2D;
import net.imglib2.view.Views;

/**
 * {@link XYRandomAccessibleProjector} for {@link Volatile} input. After each
 * {@link #map()} call, the projector has a {@link #isValid() state} that
 * signalizes whether all projected pixels were valid.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class Volatile2DRandomAccessibleProjector< T, A extends Volatile< T >, B > extends Projector2D< A, B >
{
	protected boolean valid = false;

	public Volatile2DRandomAccessibleProjector( int dimX, int dimY, final RandomAccessible< A > source, final RandomAccessibleInterval< B > target, final Converter< ? super A, B > converter )
	{
		super( dimX, dimY, source, Views.iterable( target ), converter );
	}

	/**
	 * @return true if all mapped pixels were {@link Volatile#isValid() valid}.
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 * projects data from the source to the target and applies the former
	 * specified {@link Converter} e.g. for normalization.
	 */
	@Override
	public void map()
	{
		// fix interval for all dimensions
		for ( int d = 0; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ X ] = target.min( X );
		min[ Y ] = target.min( Y );
		max[ X ] = target.max( X );
		max[ Y ] = target.max( Y );

		IterableInterval< A > srcIterable = Views.iterable( Views.interval( source, new FinalInterval( min, max ) ) );
		final Cursor< B > targetCursor = target.localizingCursor();

		if ( target.iterationOrder().equals( srcIterable.iterationOrder() ) )
		{
			// use cursors
			final Cursor< A > sourceCursor = srcIterable.cursor();
			while ( targetCursor.hasNext() )
			{
				converter.convert( sourceCursor.next(), targetCursor.next() );
			}
		}
		else
		{
			// use localizing cursor
			RandomAccess< A > sourceRandomAccess = source.randomAccess();
			while ( targetCursor.hasNext() )
			{
				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( X ), X );
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( Y ), Y );

				converter.convert( sourceRandomAccess.get(), b );
			}
		}
	}
}
