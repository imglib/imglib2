package mpicbg.imglib.type.numeric.real;

import mpicbg.imglib.type.TypeImpl;
import mpicbg.imglib.type.numeric.RealType;

public abstract class RealTypeImpl<T extends RealTypeImpl<T>> extends TypeImpl<T> implements RealType<T>
{
	@Override
	public float getComplexFloat() { return 0; }
	@Override
	public double getComplexDouble() { return 0; }
	
	@Override
	public void setComplex( final float complex ){}
	@Override
	public void setComplex( final double complex ){}
}
