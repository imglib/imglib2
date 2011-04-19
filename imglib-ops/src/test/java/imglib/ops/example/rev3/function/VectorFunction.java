package imglib.ops.example.rev3.function;

import mpicbg.imglib.type.numeric.RealType;

/** unused but a valid direction to go */
public interface VectorFunction<T extends RealType<T>>
{
	void evaluate(double[] position, T... output);
}
