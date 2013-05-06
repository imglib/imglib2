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
package interactive.remote;

/**
 * Something volatile that has a value and a {@link State} that is either
 * INVALID, LOADING or VALID.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class Volatile< T >
{
	final protected T t;
	protected boolean valid;
	
	public Volatile( final T t, final boolean valid )
	{
		this.t = t;
		this.valid = valid;
	}
	
	public Volatile( final T t )
	{
		this( t, false );
	}
	
	public T get()
	{
		return t;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public void setValid( final boolean valid )
	{
		this.valid = valid;
	}
}
