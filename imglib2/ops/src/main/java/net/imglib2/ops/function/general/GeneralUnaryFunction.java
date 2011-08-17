package net.imglib2.ops.function.general;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.UnaryOperation;

public class GeneralUnaryFunction<N extends Neighborhood<?>, T> implements Function<N,T> {
	private Function<N,T> f1;
	private T temp;
	private UnaryOperation<T> operation;
	
	public GeneralUnaryFunction(Function<N,T> f1, UnaryOperation<T> operation)
	{
		this.f1 = f1;
		this.temp = createVariable();
		this.operation = operation;
	}
	
	@Override
	public void evaluate(N input, T output) {
		f1.evaluate(input, temp);
		operation.compute(temp, output);
	}

	@Override
	public T createVariable() {
		return f1.createVariable();
	}
	
}
