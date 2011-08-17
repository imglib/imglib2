package net.imglib2.ops.relation;

import net.imglib2.ops.BinaryRelation;
import net.imglib2.ops.Real;


public class RealGreaterThan implements BinaryRelation<Real> {

	@Override
	public boolean holds(Real val1, Real val2) {
		return val1.getReal() > val2.getReal();
	}

}
