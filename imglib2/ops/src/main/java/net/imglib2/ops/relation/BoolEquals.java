package net.imglib2.ops.relation;

import net.imglib2.ops.BinaryRelation;
import net.imglib2.ops.Bool;


public class BoolEquals implements BinaryRelation<Bool> {

	@Override
	public boolean holds(Bool val1, Bool val2) {
		return val1.getBool() == val2.getBool();
	}

}
