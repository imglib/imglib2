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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.outside;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class OutsideStrategyValue<T extends Type<T>> extends OutsideStrategy<T>
{
	final T value;
	
	public OutsideStrategyValue( final Cursor<T> parentCursor, final T value )
	{
		super( parentCursor );
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see mpi.imglib.outside.OutsideStrategy#getType()
	 */
	@Override
	final public T getType(){ return value; }

	/*
	 * Outside strategy value has nothing to do when the parent cursor moves
	 * while being outside the image
	 * @see mpi.imglib.outside.OutsideStrategy#notifyOutside()
	 */
	@Override
	final public void notifyOutside() {}

	/*
	 * Outside strategy value has nothing to do when the parent cursor moves
	 * while being outside the image
	 * @see mpi.imglib.outside.OutsideStrategy#notifyOutside()
	 */
	@Override
	final public void notifyOutside( final int dim, final int steps ) {}

	@Override
	final public void notifyOutsideFwd( final int dim ) {}

	@Override
	final public void notifyOutsideBck( final int dim ) {}
	
	/*
	 * Outside strategy value updates the array of the Cursor type to the constant value
	 * and sets the index to 0
	 * 
	 * @see mpi.imglib.outside.OutsideStrategy#initOutside()
	 */
	@Override
	final public void initOutside() {}
	
	@Override
	final public void close() {}
}
