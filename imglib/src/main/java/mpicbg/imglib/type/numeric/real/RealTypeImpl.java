package mpicbg.imglib.type.numeric.real;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.image.display.RealTypeDisplay;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexTypeImpl;

public abstract class RealTypeImpl<T extends RealTypeImpl<T>> extends ComplexTypeImpl<T> implements RealType<T>
{
	@Override
	public int getEntitiesPerPixel() { return 1; } 

	@Override
	public Display<T> getDefaultDisplay( final Image<T> image )
	{
		return new RealTypeDisplay<T>( image );
	}

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
	public void set( final T c ){ setReal( c.getRealDouble() ); }
	
	@Override
	public void mul( final float c ) { setReal( getRealDouble() * c ); }

	@Override
	public void mul( final double c ) { setReal( getRealDouble() * c ); }
	
	@Override
	public void add( final T c ) { setReal( getRealDouble() + c.getRealDouble() ); }

	@Override
	public void div( final T c ) { setReal( getRealDouble() / c.getRealDouble() ); }

	@Override
	public void mul( final T c ) { setReal( getRealDouble() * c.getRealDouble() ); }

	@Override
	public void sub( final T c ) { setReal( getRealDouble() - c.getRealDouble() ); }
	
	@Override
	public void setZero() { setReal( 0 ); }
	@Override
	public void setOne() { setReal( 1 ); };	
	
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
	@Override
	public float getPowerFloat() { return getRealFloat(); }

	@Override
	public double getPowerDouble() { return getRealDouble(); }
	
	@Override
	public float getPhaseFloat() { return 0; }
	
	@Override
	public double getPhaseDouble() { return 0; }
	
	@Override
	public void updateIndex( final int index ) { this.i = index; }
	
	@Override
	public void incIndex() { ++i; }
	@Override
	public void incIndex( final int increment ) { i += increment; }
	@Override
	public void decIndex() { --i; }
	@Override
	public void decIndex( final int decrement ) { i -= decrement; }	
	
	@Override
	public String toString() { return "" + getRealDouble(); }	
}
