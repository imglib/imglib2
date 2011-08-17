package net.imglib2.ops.function.general;

import net.imglib2.ops.ContinuousNeigh;
import net.imglib2.ops.Function;


public class ContinuousTranslationFunction<T> implements Function<ContinuousNeigh,T> {

	private Function<ContinuousNeigh,T> otherFunc;
	private double[] deltas;
	private double[] localCtr;
	private ContinuousNeigh localRegion;
	
	public ContinuousTranslationFunction(Function<ContinuousNeigh,T> otherFunc,
		ContinuousNeigh region, double[] deltas)
	{
		this.otherFunc = otherFunc;
		this.deltas = deltas;
		this.localRegion = region.duplicate();
		this.localCtr = new double[deltas.length];
	}
	
	@Override
	public void evaluate(ContinuousNeigh input, T output) {
		double[] keyPt = input.getKeyPoint();
		for (int i = 0; i < localCtr.length; i++)
			localCtr[i] = keyPt[i] + deltas[i];
		localRegion.moveTo(localCtr);
		otherFunc.evaluate(localRegion, output);
	}

	@Override
	public T createVariable() {
		return otherFunc.createVariable();
	}

}
