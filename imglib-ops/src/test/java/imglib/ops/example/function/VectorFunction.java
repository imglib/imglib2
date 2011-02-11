package imglib.ops.example.function;

import imglib.ops.example.VariableFactory;
import mpicbg.imglib.type.numeric.RealType;

/** unused but a valid direction to go */
public interface VectorFunction<T extends RealType<T>> extends VariableFactory<T>
{
	void evaluate(double[] position, T... output);
}
