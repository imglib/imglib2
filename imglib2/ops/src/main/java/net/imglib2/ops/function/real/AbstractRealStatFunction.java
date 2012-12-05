package net.imglib2.ops.function.real;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractRealStatFunction<T extends RealType<T>> implements
	Function<PointSet, T>
{
	// -- instance variables --
	
	protected final Function<long[], T> otherFunc;
	private StatCalculator<T> calculator;
	
	// -- constructor --
	
	public AbstractRealStatFunction(Function<long[],T> otherFunc) {
		this.otherFunc = otherFunc;
		this.calculator = null;
	}
	
	// -- public api --

	@Override
	public void compute(PointSet input, T output) {
		if (calculator == null) {
			calculator = new StatCalculator<T>(otherFunc, input);
		}
		else calculator.reset(otherFunc, input);
		double value = value(calculator);
		output.setReal(value);
	}

	@Override
	public T createOutput() {
		return otherFunc.createOutput();
	}

	// -- protected api --

	abstract protected double value(StatCalculator<T> calc);

}
