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

import net.imglib2.type.numeric.NumericType;

/**
 * Something volatile that has a value and is either VALID or INVALID.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class VolatileNumericType< T extends NumericType< T > > extends Volatile< T > implements NumericType< VolatileNumericType< T > >
{
	public VolatileNumericType( final T t, final boolean valid )
	{
		super( t, valid );
	}
	
	public VolatileNumericType( final T t )
	{
		this( t, false );
	}

	@Override
	public VolatileNumericType< T > createVariable()
	{
		return new VolatileNumericType< T >( t.createVariable(), false );
	}

	@Override
	public VolatileNumericType< T > copy()
	{
		return new VolatileNumericType< T >( t.copy(), false );
	}

	@Override
	public void set( final VolatileNumericType< T > c )
	{
		t.set( c.t );
		valid = c.valid;
	}

	@Override
	public void add( final VolatileNumericType< T > c )
	{
		t.add( c.t );
		valid &= c.valid;
	}

	@Override
	public void sub( final VolatileNumericType< T > c )
	{
		t.sub( c.t );
		valid &= c.valid;
	}

	@Override
	public void mul( final VolatileNumericType< T > c )
	{
		t.mul( c.t );
		valid &= c.valid;
	}

	@Override
	public void div( final VolatileNumericType< T > c )
	{
		t.div( c.t );
		valid &= c.valid;
	}

	@Override
	public void setZero()
	{
		t.setZero();
	}

	@Override
	public void setOne()
	{
		t.setOne();
	}

	@Override
	public void mul( final float c )
	{
		t.mul( c );
	}

	@Override
	public void mul( final double c )
	{
		t.mul( c );
	}
}
