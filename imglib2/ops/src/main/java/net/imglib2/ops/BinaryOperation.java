package net.imglib2.ops;

public interface BinaryOperation<T> {
	void compute(T input1, T input2, T output);
}
