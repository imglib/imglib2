package net.imglib2.ops.function.bool;

import net.imglib2.ops.Bool;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;


public class ConstantBoolFunction<N extends Neighborhood<?>> implements Function<N,Bool> {
	private boolean bool;

	public ConstantBoolFunction(boolean b) {
		bool = b;
	}
	
	@Override
	public void evaluate(N neigh, Bool b) {
		b.setBool(bool);
	}
	
	@Override
	public Bool createVariable() {
		return new Bool();
	}
}

