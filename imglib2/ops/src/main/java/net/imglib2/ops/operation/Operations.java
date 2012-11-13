package net.imglib2.ops.operation;

import net.imglib2.IterableInterval;
import net.imglib2.ops.img.UnaryObjectFactory;
import net.imglib2.ops.img.UnaryOperationAssignment;
import net.imglib2.ops.img.UnaryOperationBridge;
import net.imglib2.ops.img.UnaryOperationWrapper;

public class Operations {

	/*
	 * General Joiner
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> UnaryOutputOperation<A, B> joinLeft(
			UnaryOutputOperation<A, B> op1, UnaryOutputOperation<B, B> op2) {
		return new LeftJoinedUnaryOperation<A, B>(op1, concat(op2));
	}

	@SuppressWarnings("unchecked")
	public static <A, B> UnaryOutputOperation<A, B> joinRight(
			UnaryOutputOperation<A, A> op1, UnaryOutputOperation<A, B> op2) {
		return new RightJoinedUnaryOperation<A, B>(concat(op1), op2);
	}

	public static <A, B, C> UnaryOperationBridge<A, B, C> bridge(
			UnaryOutputOperation<A, B> op1, UnaryOutputOperation<B, C> op2) {
		return new UnaryOperationBridge<A, B, C>(op1, op2);
	}

	@SuppressWarnings("unchecked")
	public static <A> UnaryOutputOperation<A, A> concat(
			UnaryOutputOperation<A, A> op1, UnaryOutputOperation<A, A> op2) {
		return concat(new UnaryOutputOperation[] { op1, op2 });
	}

	public static <A> PipedUnaryOperation<A> concat(
			UnaryOutputOperation<A, A>... ops) {
		return new PipedUnaryOperation<A>(ops);
	}

	/*
	 * Helper to create output operation
	 */
	public static <A, B> UnaryOutputOperation<A, B> wrap(
			final UnaryOperation<A, B> op, final UnaryObjectFactory<A, B> fac) {
		return new UnaryOperationWrapper<A, B>(op, fac);
	}

	/*
	 * Mapper
	 */
	public static <A, B> UnaryOperation<IterableInterval<A>, IterableInterval<B>> map(
			UnaryOperation<A, B> op) {
		return new UnaryOperationAssignment<A, B>(op);
	}

	/*
	 * Compute
	 */
	public static <A, B> B compute(B input, B output,
			UnaryOutputOperation<B, B>[] ops) {
		@SuppressWarnings("unchecked")
		UnaryOutputOperation<B, B>[] follower = new UnaryOutputOperation[ops.length - 1];
		System.arraycopy(ops, 1, follower, 0, follower.length);

		return compute(input, output, ops[0], concat(follower));
	}

	public static <B> B compute(B input, B output, PipedUnaryOperation<B> op1,
			PipedUnaryOperation<B> op2) {

		PipedUnaryOperation<B> unpack = concat(op2.ops());
		unpack.append(op2.ops());

		return unpack.compute(input, output);
	}

	public static <A, B> B compute(A input, B output,
			UnaryOutputOperation<A, B> op1, UnaryOutputOperation<B, B> op2) {
		return op2.compute(
				op1.compute(input, op1.bufferFactory().instantiate(input)),
				output);
	}

	public static <A, B> B compute(A input, B output,
			UnaryOutputOperation<A, B> op1, PipedUnaryOperation<B> op2) {

		UnaryOutputOperation<B, B>[] unpack = op2.ops();

		B buffer = op1.bufferFactory().instantiate(input);

		B tmpOutput = output;
		B tmpInput = buffer;
		B tmp;

		if (unpack.length % 2 == 1) {
			tmpOutput = buffer;
			tmpInput = output;
		}

		op1.compute(input, tmpOutput);

		for (int i = 0; i < unpack.length; i++) {
			tmp = tmpInput;
			tmpInput = tmpOutput;
			tmpOutput = tmp;
			unpack[i].compute(tmpInput, tmpOutput);
		}

		return output;
	}

	public static <A, B> B compute(UnaryOutputOperation<A, B> op, A in) {
		return op.compute(in, op.bufferFactory().instantiate(in));
	}

	public static <A, B, C> C compute(BinaryOutputOperation<A, B, C> op, A in1,
			B in2) {
		return op.compute(in1, in2, op.bufferFactory().instantiate(in1, in2));
	}

	// /////////////////////// Iterators ///////////////////////////////
	/*
	 * Iterative Operation
	 */
	public static <A> PipedUnaryOperation<A> iterate(
			UnaryOutputOperation<A, A> op, int numIterations) {

		@SuppressWarnings("unchecked")
		UnaryOutputOperation<A, A>[] ops = new UnaryOutputOperation[numIterations];

		for (int i = 0; i < numIterations; i++)
			ops[i] = op;

		return concat(ops);
	}

}
