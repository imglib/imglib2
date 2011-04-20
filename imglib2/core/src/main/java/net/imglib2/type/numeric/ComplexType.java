package net.imglib2.type.numeric;

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

	public void setComplexNumber( float r, float i );
	public void setComplexNumber( double r, double i );
	
	public float getPowerFloat();
	public double getPowerDouble();
	public float getPhaseFloat();
	public double getPhaseDouble();
	
	public void complexConjugate();
}
