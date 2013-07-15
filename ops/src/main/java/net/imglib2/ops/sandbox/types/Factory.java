package net.imglib2.ops.sandbox.types;

//TODO - attach to Number?

public interface Factory<T> {

	T create();

	T copy();
}
