package net.imglib2.ops;

public abstract class AbstractUnaryOperation<I, O> implements
		UnaryOperation<I, O> {

	public O compute(I input) {
		O output = createOutput(input);
		compute(input, output);
		return output;
	}

}
