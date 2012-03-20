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
 * @modifier Christian Dietz, Martin Horn
 *
 */

package net.imglib2.labeling;

import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsFactory;

public class LabelingOutOfBoundsRandomAccessFactory< T extends Comparable< T >, F extends Labeling< T >> implements OutOfBoundsFactory< LabelingType< T >, F >
{

	@Override
	public OutOfBounds< LabelingType< T >> create( final F f )
	{
		return new LabelingOutOfBoundsRandomAccess< T >( f );
	}

}
