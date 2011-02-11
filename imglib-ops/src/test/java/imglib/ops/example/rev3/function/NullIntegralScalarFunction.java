package imglib.ops.example.rev3.function;

import mpicbg.imglib.type.numeric.RealType;

/** a null function can be useful. for example to do a query you could setup you Operation with image, subregion,
 *  function, and all conditions. Then attach an Observer and pass a null function and collect all the info
 *  you need.
 */
public class NullIntegralScalarFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	@Override
	public void evaluate(int[] position, T output)
	{
		// DO NOTHING
	}

	@Override
	public T createVariable()
	{
		return null;  // CREATE NOTHING
	}
}
