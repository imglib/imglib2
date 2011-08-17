package net.imglib2.ops;

public interface UnaryOperation<T> {
	void compute(T input, T output);
}
