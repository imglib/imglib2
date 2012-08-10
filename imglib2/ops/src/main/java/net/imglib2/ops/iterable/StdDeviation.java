package net.imglib2.ops.iterable;

import net.imglib2.type.numeric.RealType;

public class StdDeviation< T extends RealType< T >, V extends RealType< V >> extends Variance< T, V >
{
	public V compute( java.util.Iterator< T > input, V output )
	{
		super.compute( input, output );
		output.setReal( Math.sqrt( output.getRealDouble() ) );
		return output;
	}
}
