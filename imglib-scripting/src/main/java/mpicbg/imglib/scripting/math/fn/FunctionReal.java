package mpicbg.imglib.scripting.math.fn;

import mpicbg.imglib.type.numeric.RealType;

public interface FunctionReal< R extends RealType<R> >
{
	public void compute( RealType<?> input1, RealType<?> input2, R output );
}