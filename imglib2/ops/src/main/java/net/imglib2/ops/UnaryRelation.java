package net.imglib2.ops;


public interface UnaryRelation<T> {
	boolean holds(T val);
}

