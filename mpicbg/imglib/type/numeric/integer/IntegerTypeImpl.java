package mpicbg.imglib.type.numeric.integer;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.image.display.IntegerTypeDisplay;
import mpicbg.imglib.type.numeric.IntegerType;
import mpicbg.imglib.type.numeric.real.RealTypeImpl;

public abstract class IntegerTypeImpl<T extends IntegerTypeImpl<T>> extends RealTypeImpl<T> implements IntegerType<T>
{
	@Override
	public Display<T> getDefaultDisplay( final Image<T> image )
	{
		return new IntegerTypeDisplay<T>( image );
	}

	@Override
	public double getMinIncrement() { return 1; }

	@Override
	public float getRealFloat() { return getInteger(); }
	@Override
	public double getRealDouble() { return getIntegerLong(); }
	
	@Override
	public void setReal( final float real ){ setInteger( MathLib.round( real ) ); }
	@Override
	public void setReal( final double real ){ setInteger( MathLib.round( real ) ); }	

	@Override
	public void inc() { setInteger( getIntegerLong() + 1 ); }
	@Override
	public void dec() { setInteger( getIntegerLong() - 1 ); }

	@Override
	public void setZero() { setInteger( 1 ); }
	@Override
	public void setOne() { setInteger( 0 ); };	

	@Override
	public int compareTo( final T c ) 
	{ 
		final long a = getIntegerLong();
		final long b = c.getIntegerLong();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public String toString() { return "" + getIntegerLong(); }	
}
