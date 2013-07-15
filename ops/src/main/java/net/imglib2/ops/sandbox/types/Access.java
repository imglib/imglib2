package net.imglib2.ops.sandbox.types;

// TODO - attach to Number?

public interface Access<T> {

	void setValue(T src);

	void getValue(T dest);
}
