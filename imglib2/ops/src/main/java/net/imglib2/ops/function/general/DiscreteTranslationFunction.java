package net.imglib2.ops.function.general;

import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;


public class DiscreteTranslationFunction<T> implements Function<DiscreteNeigh,T> {

	private Function<DiscreteNeigh,T> otherFunc;
	private long[] deltas;
	private long[] localCtr;
	private DiscreteNeigh localRegion;
	
	public DiscreteTranslationFunction(Function<DiscreteNeigh,T> otherFunc,
		DiscreteNeigh region, long[] deltas)
	{
		this.otherFunc = otherFunc;
		this.deltas = deltas;
		this.localRegion = region.duplicate();
		this.localCtr = new long[deltas.length];
	}
	
	@Override
	public void evaluate(DiscreteNeigh input, T output) {
		long[] keyPt = input.getKeyPoint();
		for (int i = 0; i < localCtr.length; i++)
			localCtr[i] = keyPt[i] + deltas[i];
		localRegion.moveTo(localCtr);
		otherFunc.evaluate(localRegion, output);
	}

	@Override
	public T createVariable() {
		// TODO Auto-generated method stub
		return null;
	}

}
