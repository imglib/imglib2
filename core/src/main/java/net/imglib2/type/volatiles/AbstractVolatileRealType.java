package net.imglib2.type.volatiles;

import net.imglib2.Volatile;
import net.imglib2.type.numeric.RealType;

/**
 * Abstract base class for {@link VolatileRealType}s that wrap {@link RealType}.
 *
 * @param <R>
 *            wrapped {@link RealType}.
 * @param <T>
 *            type of derived concrete class.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractVolatileRealType< R extends RealType< R >, T extends AbstractVolatileRealType< R, T > > extends Volatile< R > implements RealType< T >
{
	public AbstractVolatileRealType( final R t, final boolean valid )
	{
		super( t, valid );
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
	public int compareTo( final T o )
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
	public void set( final T c )
	{
		t.set( c.t );
		valid = c.valid;
	}

	@Override
	public void add( final T c )
	{
		t.add( c.t );
		valid &= c.valid;
	}

	@Override
	public void sub( final T c )
	{
		t.sub( c.t );
		valid &= c.valid;
	}

	@Override
	public void mul( final T c )
	{
		t.mul( c.t );
		valid &= c.valid;
	}

	@Override
	public void div( final T c )
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
