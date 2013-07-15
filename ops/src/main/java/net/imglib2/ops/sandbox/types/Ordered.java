package net.imglib2.ops.sandbox.types;

public interface Ordered<T> extends Equable<T> {

	int compare(T b);

	boolean isLess(T b);

	boolean isLessEqual(T b);

	boolean isGreater(T b);

	boolean isGreaterEqual(T b);

	void max(T b, T result);

	void min(T b, T result);
}

