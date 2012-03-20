/**
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
 *
 * @author Lee Kamentsky
 * @modified Christian Dietz, Martin Horn
 *
 */

package net.imglib2.labeling;

import net.imglib2.Interval;
import net.imglib2.util.Util;

public abstract class LabelingFactory< T extends Comparable< T >>
{

	public abstract Labeling< T > create( final long[] dim );

	public Labeling< T > create( final int[] dim )
	{
		return create( Util.int2long( dim ) );
	}

	public Labeling< T > create( final Interval interval )
	{
		final long[] dim = new long[ interval.numDimensions() ];
		interval.dimensions( dim );

		return create( dim );
	}
}
