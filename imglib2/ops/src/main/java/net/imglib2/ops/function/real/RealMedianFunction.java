package net.imglib2.ops.function.real;

import java.util.ArrayList;
import java.util.Collections;

import net.imglib2.ops.DiscreteIterator;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;

public class RealMedianFunction implements Function<DiscreteNeigh,Real> {

	private DiscreteNeigh region;
	private Function<DiscreteNeigh,Real> otherFunc;
	private Real variable;
	private ArrayList<Double> values;
	
	public RealMedianFunction(DiscreteNeigh region,
		Function<DiscreteNeigh,Real> otherFunc)
	{
		this.region = region.duplicate();
		this.otherFunc = otherFunc;
		this.variable = createVariable();
		this.values = new ArrayList<Double>();
		int numNeighs = getNeighborhoodSize(region);
		for (int i = 0; i < numNeighs; i++)
			this.values.add(0.0);
	}
	
	@Override
	public Real createVariable() {
		return new Real();
	}

	@Override
	public void evaluate(DiscreteNeigh input, Real output) {
		DiscreteIterator iter = input.getIterator();
		iter.reset();
		int numElements = 0;
		while (iter.hasNext()) {
			iter.fwd();
			region.moveTo(iter.getPosition());
			otherFunc.evaluate(region, variable);
			values.set(numElements++, variable.getReal());
		}
		Collections.sort(values);
		if (numElements == 0)
			output.setReal(0);
		else if ((numElements % 2) == 1)  // odd number of elements
			output.setReal( values.get(numElements/2) );
		else { // even number of elements
			double value1 = values.get((numElements/2) - 1); 
			double value2 = values.get((numElements/2));
			output.setReal((value1 + value2) / 2);
		}
	}

	private int getNeighborhoodSize(DiscreteNeigh neigh) {
		long[] negOffs = neigh.getNegativeOffsets();
		long[] posOffs = neigh.getPositiveOffsets();
		int size = 1;
		for (int i = 0; i < neigh.getNumDims(); i++) {
			size *= 1 + negOffs[i] + posOffs[i];
		}
		return size;
	}
}
