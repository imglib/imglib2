package mpicbg.imglib.type.numeric;

public interface ComplexType<T extends ComplexType<T>> extends NumericType<T>
{
	public double getRealDouble();
	public float getRealFloat();

	public double getComplexDouble();
	public float getComplexFloat();
	
	public void setReal( float f );
	public void setReal( double f );

	public void setComplex( float f );
	public void setComplex( double f );
}
