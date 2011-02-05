package mpicbg.imglib.type.numeric;

import mpicbg.imglib.algorithm.Precision.PrecisionReal;

public interface ComplexType<T extends ComplexType<T>> extends NumericType<T>
{
	public double getRealDouble();
	public float getRealFloat();

	public double getImaginaryDouble();
	public float getImaginaryFloat();
	
	public void setReal( float f );
	public void setReal( double f );

	public void setImaginary( float f );
	public void setImaginary( double f );

	public void setComplexNumber( float real, float complex );
	public void setComplexNumber( double real, double complex );
	
	public PrecisionReal getPreferredRealPrecision();

	public float getPowerFloat();
	public double getPowerDouble();
	public float getPhaseFloat();
	public double getPhaseDouble();
	
	public void complexConjugate();
}
