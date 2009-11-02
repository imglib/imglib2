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
package mpi.imglib.outside;

import mpi.imglib.cursor.Cursor;
import mpi.imglib.type.Type;

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
	public T getType(){ return value; }

	/*
	 * Outside strategy value has nothing to do when the parent cursor moves
	 * while being outside the image
	 * @see mpi.imglib.outside.OutsideStrategy#notifyOutside()
	 */
	@Override
	public void notifyOutside( final T type ) {}

	/*
	 * Outside strategy value updates the array of the Cursor type to the constant value
	 * and sets the index to 0
	 * 
	 * @see mpi.imglib.outside.OutsideStrategy#initOutside()
	 */
	@Override
	public void initOutside( final T cursorType ) 
	{  
		cursorType.updateDataArray( value );
		cursorType.updateIndex( 0 ); /* equals type.i = value.i */
	}
	
	@Override
	public void close() {}
}
