package net.imglib2.ops.operation.binary.realtype;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.numeric.RealType;

public class RealTypeAdd<T extends RealType<T>> implements BinaryOperation<T,T,T> {

	@Override
	public void compute(T input1, T input2, T output) {
		double value = input1.getRealDouble() + input2.getRealDouble();
		output.setReal(value);
	}

	@Override
	public T createOutput() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RealTypeAdd<T> copy() {
		return new RealTypeAdd<T>();
	}

	@Override
	public T createOutput(T dataHint1, T dataHint2) {
		return dataHint1.createVariable();
	}
}
