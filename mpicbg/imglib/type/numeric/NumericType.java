package mpicbg.imglib.type.numeric;

import mpicbg.imglib.type.ComparableType;

public interface NumericType<T extends NumericType<T>> extends ComparableType<T>
{
	public void add( T c );
	public void sub( T c );
	public void mul( T c );
	public void div( T c );

	public void setZero();
	public void setOne();	
}
