package net.imglib2.algorithm.function;

import net.imglib2.type.Type;

public interface Function<S extends Type<S>, T extends Type<T>, U extends Type<U>>
{
	/** Perform an operation with inputs S and T and output U. */
	public void compute(S s, T t, U u);
}
