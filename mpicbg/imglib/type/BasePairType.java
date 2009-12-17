package mpicbg.imglib.type;

import mpicbg.imglib.type.label.BasePairBitType.Base;

public interface BasePairType<T extends BasePairType<T>> extends ComparableType<T>
{
	public void set( final Base base ); 
	public Base get();
	
	public void complement();
	byte baseToValue();
}
