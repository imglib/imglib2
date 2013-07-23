package net.imglib2.ops.sandbox.types;

/*
 * This example shows a way to support three types of operators with minimal
 * code.
 * 1) a.mul(b);
 * 2) a.mul(b,c);
 * 3) op.compute(b,c,a);
 * 
 * We need a definition of BinaryOps and UnaryOps that apply to various subtype.
 * For instance we might not support a DivOp in MyType because it is inexact.
 * But it could be in a derived type.
 */

public class Types3 {

	private interface UnaryOp<T> {

		void compute(T a, T result);
	}

	private interface BinaryOp<T> {

		void compute(T a, T b, T result);
	}

	private interface Number<T> {

		void set(T val);

		void add(T val);

		void sub(T val);

		void mul(T val);

		void div(T val);

		void negate();

		void add(T val1, T val2);

		void sub(T val1, T val2);

		void mul(T val1, T val2);

		void div(T val1, T val2);

		void negate(T val);

		BinaryOp<T> getAddOp();

		BinaryOp<T> getSubOp();

		BinaryOp<T> getMulOp();

		BinaryOp<T> getDivOp();

		UnaryOp<T> getNegateOp();
	}

	private static class MyType implements Number<MyType> {

		private int val;

		// -- static ops --

		// NOTE they should be be designed to be accessible from multiple threads

		private static final BinaryOp<MyType> addOp = new BinaryOp<MyType>() {

			@Override
			public void compute(MyType a, MyType b, MyType result) {
				result.val = a.val + b.val;
			}
		};

		private static final BinaryOp<MyType> subOp = new BinaryOp<MyType>() {

			@Override
			public void compute(MyType a, MyType b, MyType result) {
				result.val = a.val - b.val;
			}
		};

		private static final BinaryOp<MyType> mulOp = new BinaryOp<MyType>() {

			@Override
			public void compute(MyType a, MyType b, MyType result) {
				result.val = a.val * b.val;
			}
		};

		private static final BinaryOp<MyType> divOp = new BinaryOp<MyType>() {

			@Override
			public void compute(MyType a, MyType b, MyType result) {
				result.val = a.val / b.val;
			}
		};

		private static final UnaryOp<MyType> negateOp = new UnaryOp<MyType>() {

			@Override
			public void compute(MyType input, MyType result) {
				result.val = -input.val;
			}
		};

		public MyType() {
			val = 0;
		}

		@Override
		public void set(MyType val) {
			this.val = val.val;
		}

		// -- new style math signatures --

		@Override
		public void add(MyType input1, MyType input2) {
			addOp.compute(input1, input2, this);
		}

		@Override
		public void sub(MyType input1, MyType input2) {
			subOp.compute(input1, input2, this);
		}

		@Override
		public void mul(MyType input1, MyType input2) {
			mulOp.compute(input1, input2, this);
		}

		@Override
		public void div(MyType input1, MyType input2) {
			divOp.compute(input1, input2, this);
		}

		@Override
		public void negate(MyType input) {
			negateOp.compute(input, this);
		}

		// -- old style math signatures: use self as an input --

		@Override
		public void add(MyType other) {
			addOp.compute(this, other, this);
		}

		@Override
		public void sub(MyType other) {
			subOp.compute(this, other, this);
		}

		@Override
		public void mul(MyType other) {
			mulOp.compute(this, other, this);
		}

		@Override
		public void div(MyType other) {
			divOp.compute(this, other, this);
		}

		@Override
		public void negate() {
			negateOp.compute(this, this);
		}

		// signatures that allow one to obtain the correct op from a type
		// instance. This allows one to add() for instance with a result value not
		// equal to self.

		@Override
		public BinaryOp<MyType> getAddOp() {
			return addOp;
		}

		@Override
		public BinaryOp<MyType> getSubOp() {
			return subOp;
		}

		@Override
		public BinaryOp<MyType> getMulOp() {
			return mulOp;
		}

		@Override
		public BinaryOp<MyType> getDivOp() {
			return divOp;
		}

		@Override
		public UnaryOp<MyType> getNegateOp() {
			return negateOp;
		}
	}
}
