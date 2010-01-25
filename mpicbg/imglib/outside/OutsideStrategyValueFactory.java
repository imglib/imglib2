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

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.Type;

public class OutsideStrategyValueFactory<T extends Type<T>> extends OutsideStrategyFactory<T>
{
	T value;
	
	public OutsideStrategyValueFactory( )
	{
		this.value = null;
	}
	
	public OutsideStrategyValueFactory( final T value )
	{
		this.value = value;
	}
		
	public void setValue( T value ) { this.value = value; }
	public T getValue() { return value; }
	
	@Override
	public OutsideStrategyValue<T> createStrategy( final LocalizableCursor<T> cursor )
	{
		if ( value == null )
			return new OutsideStrategyValue<T>( cursor, cursor.getType().createVariable() );
		else
			return new OutsideStrategyValue<T>( cursor, value );
	}

}
