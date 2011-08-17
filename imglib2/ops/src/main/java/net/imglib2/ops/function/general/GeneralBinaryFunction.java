package net.imglib2.ops.function.general;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;

public class GeneralBinaryFunction<N extends Neighborhood<?>, T> implements Function<N,T> {
	private Function<N,T> f1;
	private Function<N,T> f2;
	private T input1;
	private T input2;
	private BinaryOperation<T> operation;
	
	public GeneralBinaryFunction(Function<N,T> f1, Function<N,T> f2, BinaryOperation<T> operation)
	{
		this.f1 = f1;
		this.f2 = f2;
		this.input1 = createVariable();
		this.input2 = createVariable();
		this.operation = operation;
	}
	
	@Override
	public void evaluate(N input, T output) {
		f1.evaluate(input, input1);
		f2.evaluate(input, input2);
		operation.compute(input1, input2, output);
	}
	
	@Override
	public T createVariable() {
		return f1.createVariable();
	}
}
