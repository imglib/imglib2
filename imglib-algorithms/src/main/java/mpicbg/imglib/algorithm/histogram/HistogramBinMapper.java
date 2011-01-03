package mpicbg.imglib.algorithm.histogram;

import mpicbg.imglib.type.Type;

public interface HistogramBinMapper <T extends Type<T>>{
	
	public T getMinBin();
	
	public T getMaxBin();
	
	public int getNumBins();

	public int map(T type);
	
	public T invMap(int i);
}
