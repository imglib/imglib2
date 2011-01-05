package mpicbg.imglib.type.numeric.complex;

import mpicbg.imglib.algorithm.Precision.PrecisionReal;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.ComplexTypePowerSpectrumDisplay;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.TypeImpl;
import mpicbg.imglib.type.numeric.ComplexType;
import mpicbg.imglib.util.Util;

public abstract class ComplexTypeImpl<T extends ComplexTypeImpl<T>> extends TypeImpl<T> implements ComplexType<T>
{
	@Override
	public int getEntitiesPerPixel() { return 2; } 

	// the indices for real and complex number
	int realI = 0, complexI = 1;

	@Override
	public Display<T> getDefaultDisplay( final Image<T> image )
	{
		return new ComplexTypePowerSpectrumDisplay<T>( image );
	}
	
	@Override
	public void set( final T c )
	{ 
		setReal( c.getRealDouble() ); 
		setComplex( c.getComplexDouble() ); 
	}
	
	@Override
	public void mul( final float c )
	{
		setReal( getRealFloat() * c );
		setComplex( getComplexFloat() * c );
	}
	
	@Override
	public void mul( final double c )
	{
		setReal( getRealDouble() * c );
		setComplex( getComplexDouble() * c );
	}		

	@Override
	public void add( final T c ) 
	{
		setReal( getRealDouble() + c.getRealDouble() );
		setComplex( getComplexDouble() + c.getComplexDouble() );
	}

	@Override
	public void div( final T c ) 
	{ 
		final double a1 = getRealDouble(); 
		final double b1 = getComplexDouble();
		final double c1 = c.getRealDouble();
		final double d1 = c.getComplexDouble();
		
		setReal( ( a1*c1 + b1*d1 ) / ( c1*c1 + d1*d1 ) );
		setComplex( ( b1*c1 - a1*d1 ) / ( c1*c1 + d1*d1 ) );
	}

	@Override
	public void mul( final T t ) 
	{
		// a + bi
		final double a = getRealDouble(); 
		final double b = getComplexDouble();
		
		// c + di
		final double c = t.getRealDouble();
		final double d = t.getComplexDouble();
		
		setReal( a*c - b*d ); 
		setComplex( a*d + b*c ); 
	}

	@Override
	public void sub( final T c ) 
	{ 
		setReal( getRealDouble() - c.getRealDouble() ); 
		setComplex( getComplexDouble() - c.getComplexDouble() ); 
	}
	
	@Override
	public void complexConjugate(){ setComplex( -getComplexDouble() ); }

	@Override
	public float getPowerFloat()
	{
		final float real = getRealFloat();
		final float complex = getComplexFloat();

		return (float)Util.gLog( Math.sqrt( real * real + complex * complex ), 2 );				
	}

	@Override
	public double getPowerDouble()
	{
		final double real = getRealDouble();
		final double complex = getComplexDouble();

		return Util.gLog( Math.sqrt( real * real + complex * complex ), 2 );				
	}
	
	@Override
	public float getPhaseFloat()
	{
		final float real = getRealFloat();
		final float complex = getComplexFloat();

		if ( real != 0.0 || complex != 0)
			return (float)Math.atan2( complex, real );
		else
			return 0;				
	}
	
	@Override
	public double getPhaseDouble()
	{
		final double real = getRealDouble();
		final double complex = getComplexDouble();

		if ( real != 0.0 || complex != 0)
			return (float)Math.atan2( complex, real );
		else
			return 0;				
	}

	@Override
	public void setOne() 
	{ 
		setReal( 1 );
		setComplex( 0 );
	}

	@Override
	public void setZero() 
	{ 
		setReal( 0 );
		setComplex( 0 );
	}
	
	@Override
	public void setComplexNumber( final float real, final float complex )
	{
		setReal( real );
		setComplex( complex );
	}
	
	@Override
	public void setComplexNumber( final double real, final double complex )
	{
		setReal( real );
		setComplex( complex );
	}
	
	@Override
	public PrecisionReal getPreferredRealPrecision() { return PrecisionReal.Double; }
	

	@Override
	public void updateIndex( final int i ) 
	{ 
		this.i = i;
		realI = i * 2;
		complexI = i * 2 + 1;
	}
	
	@Override
	public void incIndex() 
	{ 
		++i;
		realI += 2;
		complexI += 2;
	}
	@Override
	public void incIndex( final int increment ) 
	{ 
		i += increment; 
		
		final int inc2 = 2 * increment;		
		realI += inc2;
		complexI += inc2;
	}
	@Override
	public void decIndex() 
	{ 
		--i; 
		realI -= 2;
		complexI -= 2;
	}
	@Override
	public void decIndex( final int decrement ) 
	{ 
		i -= decrement; 
		final int dec2 = 2 * decrement;		
		realI -= dec2;
		complexI -= dec2;
	}	
	
	@Override
	public String toString(){ return "(" + getRealDouble() + ") + (" + getComplexDouble() + ")i"; }	
}
