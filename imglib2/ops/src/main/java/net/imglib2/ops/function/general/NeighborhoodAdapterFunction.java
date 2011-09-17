package net.imglib2.ops.function.general;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;

public class NeighborhoodAdapterFunction<INDEX, T> implements Function<INDEX,T> {

	private final Function<INDEX,T> function;
	private final Neighborhood<INDEX> localNeigh;
	
	public NeighborhoodAdapterFunction(
			Function<INDEX,T> function, Neighborhood<INDEX> localNeigh)
	{
		this.function = function;
		this.localNeigh = localNeigh;
	}
	
	@Override
	public T createOutput() {
		return function.createOutput();
	}

	@Override
	public void evaluate(Neighborhood<INDEX> neigh, INDEX point, T output) {
		localNeigh.moveTo(point);
		function.evaluate(localNeigh, point, output);
	}

}
