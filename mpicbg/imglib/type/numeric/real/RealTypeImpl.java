package mpicbg.imglib.type.numeric.real;

import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexTypeImpl;

public abstract class RealTypeImpl<T extends RealTypeImpl<T>> extends ComplexTypeImpl<T> implements RealType<T>
{
	@Override
	public float getComplexFloat() { return 0; }
	@Override
	public double getComplexDouble() { return 0; }
	
	@Override
	public void setComplex( final float complex ){}
	@Override
	public void setComplex( final double complex ){}
	
	@Override
	public void inc() { setReal( getRealDouble() + 1 ); }
	@Override
	public void dec() { setReal( getRealDouble() - 1 ); }
	
	@Override
	public void setZero() { setReal( 1 ); }
	@Override
	public void setOne() { setReal( 0 ); };	
	
	@Override
	public void mul( float c ) { setReal( getRealFloat() * c ); }
	@Override
	public void mul( double c ) { setReal( getRealDouble() * c ); }		

	@Override
	public int compareTo( final T c ) 
	{ 
		final double a = getRealDouble();
		final double b = c.getRealDouble();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}
}
