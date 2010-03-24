package mpicbg.imglib.type.numeric.integer;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.type.numeric.IntegerType;
import mpicbg.imglib.type.numeric.real.RealTypeImpl;

public abstract class IntegerTypeImpl<T extends IntegerTypeImpl<T>> extends RealTypeImpl<T> implements IntegerType<T>
{
	@Override
	public float getRealFloat() { return getInteger(); }
	@Override
	public double getRealDouble() { return getIntegerLong(); }
	
	@Override
	public void setReal( final float real ){ setInteger( MathLib.round( real ) ); }
	@Override
	public void setReal( final double real ){ setInteger( MathLib.round( real ) ); }	
}
