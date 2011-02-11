package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.VariableFactory;
import mpicbg.imglib.type.numeric.RealType;

/** unused but a valid direction to go */
public interface VectorFunction<T extends RealType<T>> extends VariableFactory<T>
{
	void evaluate(double[] position, T... output);
}
