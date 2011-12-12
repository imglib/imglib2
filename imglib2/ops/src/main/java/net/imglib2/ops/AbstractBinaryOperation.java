package net.imglib2.ops;

public abstract class AbstractBinaryOperation<INPUT1, INPUT2, OUTPUT>
		implements BinaryOperation<INPUT1, INPUT2, OUTPUT> {

	public OUTPUT compute(INPUT1 input1, INPUT2 input2) {
		OUTPUT output = createOutput(input1, input2);
		compute(input1, input2, output);
		return output;
	}

}
