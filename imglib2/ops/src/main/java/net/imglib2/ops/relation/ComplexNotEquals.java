package net.imglib2.ops.relation;

import net.imglib2.ops.BinaryRelation;
import net.imglib2.ops.Complex;


public class ComplexNotEquals implements BinaryRelation<Complex> {

	@Override
	public boolean holds(Complex val1, Complex val2) {
		return (val1.getReal() != val2.getReal()) || (val1.getImag() != val2.getImag());
	}

}
