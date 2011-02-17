package imglib.ops.example.rev3.function;


/** a null function can be useful. for example to do a query you could setup you Operation with image, subregion,
 *  function, and all conditions. Then attach an Observer and pass a null function and collect all the info
 *  you need.
 */
public final class NullFunction implements IntegerIndexedScalarFunction
{
	@Override
	public double evaluate(int[] position)
	{
		return Double.NaN;
	}
}
