package mpicbg.imglib.algorithm.histogram;

import mpicbg.imglib.type.numeric.IntegerType;

public class IntBinMapper <T extends IntegerType<T>> implements HistogramBinMapper<T>{

	private static <R extends IntegerType<R>> R minType(R r)
	{
		R type = r.createVariable();
		type.setReal(r.getMinValue());
		return type;
	}
	
	private static <R extends IntegerType<R>> R maxType(R r)
	{
		R type = r.createVariable();
		type.setReal(r.getMaxValue());
		return type;
	}
	
	private final T minType, maxType;
	private final int numBins;
	private final int minVal;
	
	
	public IntBinMapper(T min, T max)
	{
		minType = min;
		maxType = max;
		numBins = max.getInteger() - min.getInteger();
		minVal = min.getInteger();
		System.out.println("Min " + minType);
		System.out.println("Max " + maxType);
	}
	
	public IntBinMapper(T type)
	{
		this(minType(type), maxType(type));
	}
	
	@Override
	public T getMaxBin() {		
		return maxType;
	}

	@Override
	public T getMinBin() {
		return minType;
	}

	@Override
	public int getNumBins() {		
		return numBins;
	}

	@Override
	public T invMap(int i) {
		T out = minType.createVariable();
		out.setInteger(i + minVal);
		return out;
	}

	@Override
	public int map(T type) {
		return type.getInteger() - minVal;
	}
}
