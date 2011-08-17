package net.imglib2.ops.function.bool;

import net.imglib2.ops.Bool;
import net.imglib2.ops.Condition;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;


public class ConditionalBooleanFunction<N extends Neighborhood<?>>
	implements Function<N,Bool>
{
	private Condition<N> condition;

	public ConditionalBooleanFunction(Condition<N> cond) {
		this.condition = cond;
	}

	@Override
	public void evaluate(N neigh, Bool b) {
		b.setBool(condition.isTrue(neigh));
	}
	
	@Override
	public Bool createVariable() {
		return new Bool();
	}
}

