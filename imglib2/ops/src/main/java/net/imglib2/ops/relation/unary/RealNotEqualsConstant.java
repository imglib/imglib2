package net.imglib2.ops.relation.unary;

import net.imglib2.ops.UnaryRelation;
import net.imglib2.type.numeric.RealType;

public class RealNotEqualsConstant<T extends RealType<T>> implements
		UnaryRelation<T> {

	private T m_constant;

	public RealNotEqualsConstant(T constant) {
		m_constant = constant;
	}

	@Override
	public boolean holds(T val) {
		return val.getRealDouble() != m_constant.getRealDouble();
	}

	@Override
	public UnaryRelation<T> copy() {
		return new RealNotEqualsConstant<T>(m_constant);
	}

}
