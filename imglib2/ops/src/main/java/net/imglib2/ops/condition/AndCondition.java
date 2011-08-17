package net.imglib2.ops.condition;

import net.imglib2.ops.Condition;
import net.imglib2.ops.Neighborhood;


public class AndCondition<N extends Neighborhood<?>> implements Condition<N> {

	Condition<N> cond1;
	Condition<N> cond2;

	AndCondition(Condition<N> cond1, Condition<N> cond2) {
		this.cond1 = cond1;
		this.cond2 = cond2;
	}
	
	@Override
	public boolean isTrue(N neigh) {
		return cond1.isTrue(neigh) && cond2.isTrue(neigh);
	}

}
