package mpicbg.imglib.algorithm.math.function;

import mpicbg.imglib.type.numeric.RealType;

public class RealTypeConverter< A extends RealType<A>, B extends RealType<B> > implements Converter<A, B>
{
	@Override
	public void convert( final A input, final B output )
	{
		output.setReal( input.getRealDouble() );
	}
}
