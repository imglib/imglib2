package net.imglib2.ops.sandbox.types;


public interface Bounded<T> {

	void minBound(T result);

	void maxBound(T result);
}

