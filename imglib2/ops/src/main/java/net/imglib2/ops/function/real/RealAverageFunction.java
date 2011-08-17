package net.imglib2.ops.function.real;

import net.imglib2.ops.DiscreteIterator;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;

public class RealAverageFunction implements Function<DiscreteNeigh,Real> {

	private DiscreteNeigh region;
	private Function<DiscreteNeigh,Real> otherFunc;
	private Real variable;
	
	public RealAverageFunction(DiscreteNeigh region,
		Function<DiscreteNeigh,Real> otherFunc)
	{
		this.region = region.duplicate();
		this.otherFunc = otherFunc;
		this.variable = createVariable();
	}
	
	@Override
	public Real createVariable() {
		return new Real();
	}

	@Override
	public void evaluate(DiscreteNeigh input, Real output) {
		DiscreteIterator iter = input.getIterator();
		iter.reset();
		double sum = 0;
		double numElements = 0;
		while (iter.hasNext()) {
			iter.fwd();
			region.moveTo(iter.getPosition());
			otherFunc.evaluate(region, variable);
			sum += variable.getReal();
			numElements++;
		}
		if (numElements == 0)
			output.setReal(0);
		else
			output.setReal(sum / numElements);
	}

}
