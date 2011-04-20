package imglib.ops.function;

import mpicbg.imglib.type.numeric.RealType;

public interface RealFunction<T extends RealType<T>>
{
	boolean canAccept(final int numParameters);
	void compute(final T[] inputs, final T output);
}

