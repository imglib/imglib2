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

import net.imglib2.type.numeric.RealType;

/**
 * Something volatile that has a value and is either VALID or INVALID.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class VolatileRealType< T extends RealType< T > > extends Volatile< T > implements RealType< VolatileRealType< T > >
{
	public VolatileRealType( final T t, final boolean valid )
	{
		super( t, valid );
	}
	
	public VolatileRealType( final T t )
	{
		this( t, false );
	}

	@Override
	public double getRealDouble()
	{
		return t.getRealDouble();
	}

	@Override
	public float getRealFloat()
	{
		return t.getRealFloat();
	}

	@Override
	public double getImaginaryDouble()
	{
		return t.getImaginaryDouble();
	}

	@Override
	public float getImaginaryFloat()
	{
		return t.getImaginaryFloat();
	}

	@Override
	public void setReal( final float f )
	{
		t.setReal( f );
	}

	@Override
	public void setReal( final double f )
	{
		t.setReal( f );
	}

	@Override
	public void setImaginary( final float f )
	{
		t.setImaginary( f );
	}

	@Override
	public void setImaginary( final double f )
	{
		t.setImaginary( f );
	}

	@Override
	public void setComplexNumber( final float r, final float i )
	{
		t.setComplexNumber( r, i );
	}

	@Override
	public void setComplexNumber( final double r, final double i )
	{
		t.setComplexNumber( r, i );
	}

	@Override
	public float getPowerFloat()
	{
		return t.getPowerFloat();
	}

	@Override
	public double getPowerDouble()
	{
		return t.getPowerDouble();
	}

	@Override
	public float getPhaseFloat()
	{
		return t.getPhaseFloat();
	}

	@Override
	public double getPhaseDouble()
	{
		return t.getPhaseDouble();
	}

	@Override
	public void complexConjugate()
	{
		t.complexConjugate();
	}

	@Override
	public int compareTo( final VolatileRealType< T > o )
	{
		return t.compareTo( o.t );
	}

	@Override
	public void inc()
	{
		t.inc();
	}

	@Override
	public void dec()
	{
		t.dec();
	}

	@Override
	public double getMaxValue()
	{
		return t.getMaxValue();
	}

	@Override
	public double getMinValue()
	{
		return t.getMinValue();
	}

	@Override
	public double getMinIncrement()
	{
		return t.getMinIncrement();
	}

	@Override
	public int getBitsPerPixel()
	{
		return t.getBitsPerPixel();
	}

	@Override
	public VolatileRealType< T > createVariable()
	{
		return new VolatileRealType< T >( t.createVariable(), false );
	}

	@Override
	public VolatileRealType< T > copy()
	{
		return new VolatileRealType< T >( t.copy(), false );
	}

	@Override
	public void set( final VolatileRealType< T > c )
	{
		t.set( c.t );
		valid = c.valid;
	}

	@Override
	public void add( final VolatileRealType< T > c )
	{
		t.add( c.t );
		valid &= c.valid;
	}

	@Override
	public void sub( final VolatileRealType< T > c )
	{
		t.sub( c.t );
		valid &= c.valid;
	}

	@Override
	public void mul( final VolatileRealType< T > c )
	{
		t.mul( c.t );
		valid &= c.valid;
	}

	@Override
	public void div( final VolatileRealType< T > c )
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
