package net.imglib2.ops.sandbox.types;

public interface Equable<T> {

	boolean isEqual(T b);

	boolean isNotEqual(T b);
}

