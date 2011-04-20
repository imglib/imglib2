package net.imglib2.type.numeric;

public interface IntegerType<T extends IntegerType<T>> extends RealType<T>
{
	public int getInteger();
	public long getIntegerLong();

	public void setInteger( int f );
	public void setInteger( long f );
}
