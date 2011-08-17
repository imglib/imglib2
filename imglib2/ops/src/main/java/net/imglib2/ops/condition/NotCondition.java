package net.imglib2.ops.condition;

import net.imglib2.ops.Condition;
import net.imglib2.ops.Neighborhood;


public class NotCondition<N extends Neighborhood<?>> implements Condition<N> {

	Condition<N> cond1;

	NotCondition(Condition<N> cond1) {
		this.cond1 = cond1;
	}
	
	@Override
	public boolean isTrue(N neigh) {
		return ! cond1.isTrue(neigh);
	}

}
