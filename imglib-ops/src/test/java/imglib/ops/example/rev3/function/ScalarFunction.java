package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.VariableFactory;
import mpicbg.imglib.type.numeric.RealType;

/** unused but a valid direction to go. a resample function would be of this type */
public interface ScalarFunction<T extends RealType<T>> extends VariableFactory<T>
{
	void evaluate(double[] position, T output);

	// evaluate() could return a double and not modify a T output. But then it breaks how VectorFunction's evaluate would work.
	// but by using a T we need to be able to create/get a variable from any function in order for them to be composable. neither
	// way is great. the T output strategy does keep the idea of a NullFunction alive which I see as useful. but as I look more
	// it looks like getting rid of createVariable() and having evaluate() return a double simplifies things. Null function becomes
	// impossible. Thus a query would do meaningless assignments to an output image (that would be a throwaway really). One good
	// thing though is that the need for generic typing would be minimized.
	
}
