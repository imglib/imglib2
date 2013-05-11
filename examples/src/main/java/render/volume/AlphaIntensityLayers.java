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
package render.volume;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class AlphaIntensityLayers< T extends RealType< T > > implements RowAccumulator< T >
{
	final protected double scale = 1.0 / 4095.0;
	
	final double alpha( final double intensity )
	{
		return intensity * scale;
	}
	
	@Override
	public void accumulateRow( final T accumulator, final RandomAccess< T > access, final long min, final long max, final long step, final int d )
	{
		access.setPosition( max, d );
		double a = accumulator.getRealDouble();
		while ( access.getLongPosition( d ) >= min )
		{
			final double b = access.get().getRealDouble();
			final double alpha = alpha( b );
			a *= 1.0 - alpha;
			a += b * alpha;
			access.move( -step, d );
		}
		accumulator.setReal( a );
	}
}
