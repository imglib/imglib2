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

public abstract class OutsideStrategy<T extends Type<T>>
{
	final Cursor<T> parentCursor;
	
	public OutsideStrategy( final Cursor<T> parentCursor )
	{
		this.parentCursor = parentCursor;
	}
	
	/*
	 * Returns a link to the parent Cursor of this Strategy
	 */
	public Cursor<T> getParentCursor() { return parentCursor; }

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutside();

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutside( int steps, int dim );

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutsideFwd( int dim );

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutsideBck( int dim );
	
	/*
	 * This method is fired by the parent cursor in the case that it leaves the image
	 */
	public abstract void initOutside();
	
	/*
	 * Returns the Type that stores the current value of the Outside Strategy
	 */
	public abstract T getType();
	
	/*
	 * Closed possibly created cursors or images
	 */
	public abstract void close();
}
