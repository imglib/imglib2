package net.imglib2.ops;


public interface BinaryRelation<T> {
	boolean holds(T val1, T val2);
}

