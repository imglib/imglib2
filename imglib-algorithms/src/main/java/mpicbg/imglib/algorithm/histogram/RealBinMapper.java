package mpicbg.imglib.algorithm.histogram;

import mpicbg.imglib.type.numeric.RealType;

public class RealBinMapper <T extends RealType<T>> implements HistogramBinMapper<T>{

	private final int numBins;
	private final T minBin;
	private final T maxBin;
	private final double binWidth;
	private final double halfBinWidth;
	private final double minVal;
	
	
	public RealBinMapper(final T minBin, final T maxBin, final int numBins)	
	{
		this.numBins = numBins;
		this.minBin = minBin;
		this.maxBin = maxBin;
		
		binWidth = (maxBin.getRealDouble() - minBin.getRealDouble()) /
			((double) numBins);
		halfBinWidth = binWidth / 2;
		
		minVal = minBin.getRealDouble();
	}
	
	@Override
	public T getMaxBin() {
		return maxBin;
	}

	@Override
	public T getMinBin() {
		return minBin;
	}

	@Override
	public int getNumBins() {
		return numBins;
	}

	@Override
	public T invMap(int i) {				
		T out = minBin.createVariable();
		double t = i;
	
		t *= binWidth;
		t -= halfBinWidth;
		t += minVal;
		out.setReal(t);
		return out;
	}

	@Override
	public int map(T type) {
		double tVal = type.getRealDouble();
		int iVal;
		tVal -= minVal;
		tVal += halfBinWidth;
		tVal /= binWidth;
		
		iVal = (int)tVal;
		
		if (iVal < 0)
		{
			iVal = 0;
		}
		else if (iVal >= numBins)
		{
			iVal = numBins - 1 ;
		}
		
		return iVal;
	}

}
