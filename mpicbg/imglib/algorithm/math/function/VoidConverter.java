package mpicbg.imglib.algorithm.math.function;

import mpicbg.imglib.type.Type;

public class VoidConverter< A extends Type<A> > implements Converter<A, A>
{
	@Override
	public void convert( final A input, final A output )
	{
		output.set( input );
	}
}
