package net.imglib2.type.numeric;

public interface ExponentialMathType< T extends ExponentialMathType<T> > extends RealType<T>
{
	public void exp();
	public void round();
}
