package net.imglib2.ops.operation;

import net.imglib2.ops.img.UnaryObjectFactory;

public class LeftJoinedUnaryOperation<A, B> implements
		UnaryOutputOperation<A, B> {
	private PipedUnaryOperation<B> follower;

	private UnaryOutputOperation<A, B> first;

	protected LeftJoinedUnaryOperation(UnaryOutputOperation<A, B> first,
			PipedUnaryOperation<B> follower) {
		this.first = first;
		this.follower = follower;
	}

	@SuppressWarnings("unchecked")
	@Override
	public B compute(A input, B output) {
		if (first instanceof PipedUnaryOperation) {
			return Operations.compute((B) input, output,
					(PipedUnaryOperation<B>) first, follower);
		} else {
			return Operations.compute(input, output, first, follower);
		}

	}

	@Override
	public UnaryObjectFactory<A, B> bufferFactory() {
		return first.bufferFactory();
	}

	@Override
	public UnaryOutputOperation<A, B> copy() {
		return new LeftJoinedUnaryOperation<A, B>(first, follower);
	}

	public UnaryOutputOperation<A, B> first() {
		return first;
	}

	public UnaryOutputOperation<B, B> follower() {
		return follower;
	}
}
