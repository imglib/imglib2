package net.imglib2.ops.operations;

import static org.junit.Assert.assertTrue;
import net.imglib2.ops.img.UnaryObjectFactory;
import net.imglib2.ops.operation.Operations;
import net.imglib2.ops.operation.PipedUnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.real.unary.RealAddConstant;
import net.imglib2.ops.operation.real.unary.RealMultiplyConstant;
import net.imglib2.ops.operation.real.unary.RealSubtractConstant;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Before;
import org.junit.Test;

public class OperationTests {

	private DoubleType in;

	@Before
	public void init() {
		in = new DoubleType(5);

	}

	@Test
	public void testPipe() {

		CountingBuf createBuf = createBuf();

		UnaryOutputOperation<DoubleType, DoubleType> add = Operations.wrap(
				new RealAddConstant<DoubleType, DoubleType>(10), createBuf);

		UnaryOutputOperation<DoubleType, DoubleType> substract = Operations
				.wrap(new RealSubtractConstant<DoubleType, DoubleType>(8),
						createBuf);

		UnaryOutputOperation<DoubleType, DoubleType> multiply = Operations
				.wrap(new RealMultiplyConstant<DoubleType, DoubleType>(3),
						createBuf);

		assertTrue(Operations
				.concat(multiply, Operations.concat(add, substract))
				.compute(in, new DoubleType()).get() == ((in.get() * 3) + 10 - 8));

		assertTrue(createBuf.numBufs == 1);
	}

	@Test
	public void testIteration() {
		CountingBuf createBuf = createBuf();

		PipedUnaryOperation<DoubleType> iterate = Operations.iterate(
				Operations.wrap(new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10);

		assertTrue(iterate.compute(in, new DoubleType()).get() == 55);
		assertTrue(createBuf.numBufs == 1);
	}

	@Test
	public void testConcatIteration() {
		CountingBuf createBuf = createBuf();

		UnaryOutputOperation<DoubleType, DoubleType> combined = Operations
				.concat(Operations.wrap(
						new RealSubtractConstant<DoubleType, DoubleType>(5),
						createBuf), Operations.iterate(Operations.wrap(
						new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10));

		assertTrue(combined.compute(in, new DoubleType()).get() == 50);
		assertTrue(createBuf.numBufs == 1);

	}

	@Test
	public void testConcatIterationLeftJoin() {
		CountingBuf createBuf = createBuf();

		UnaryOutputOperation<DoubleType, DoubleType> combined = Operations
				.concat(Operations.wrap(
						new RealSubtractConstant<DoubleType, DoubleType>(5),
						createBuf), Operations.iterate(Operations.wrap(
						new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10));

		RealAddConstant<FloatType, DoubleType> adder = new RealAddConstant<FloatType, DoubleType>(
				5);

		// No buffer needs to be set
		UnaryOutputOperation<FloatType, DoubleType> leftJoin = Operations
				.joinLeft(Operations.wrap(adder,
						new UnaryObjectFactory<FloatType, DoubleType>() {

							@Override
							public DoubleType instantiate(FloatType a) {
								return new DoubleType();
							}
						}), combined);

		assertTrue(leftJoin.compute(new FloatType(in.getRealFloat()),
				new DoubleType()).get() == 55);

		assertTrue(createBuf.numBufs == 0);
	}

	@Test
	public void testConcatRightJoin() {
		CountingBuf createBuf = createBuf();

		UnaryOutputOperation<DoubleType, DoubleType> combined = Operations
				.concat(Operations.wrap(
						new RealSubtractConstant<DoubleType, DoubleType>(5),
						createBuf), Operations.iterate(Operations.wrap(
						new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10));

		RealAddConstant<DoubleType, FloatType> adder = new RealAddConstant<DoubleType, FloatType>(
				5);

		// No buffer needs to be set
		UnaryOutputOperation<DoubleType, FloatType> leftJoin = Operations
				.joinRight(combined, Operations.wrap(adder, null));

		assertTrue(leftJoin.compute(in, new FloatType(0)).get() == 55);

		// Here we actually really need two buffers...
		assertTrue(createBuf.numBufs == 2);
	}

	@Test
	public void testPipedPipesLeft() {
		CountingBuf createBuf = createBuf();

		UnaryOutputOperation<DoubleType, DoubleType> combinedA = Operations
				.concat(Operations.wrap(
						new RealSubtractConstant<DoubleType, DoubleType>(5),
						createBuf), Operations.iterate(Operations.wrap(
						new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10));

		UnaryOutputOperation<DoubleType, DoubleType> combinedB = Operations
				.concat(Operations.wrap(
						new RealSubtractConstant<DoubleType, DoubleType>(5),
						createBuf), Operations.iterate(Operations.wrap(
						new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10));

		assertTrue(Operations.joinLeft(combinedA, combinedB)
				.compute(in, new DoubleType()).get() == 95);

		assertTrue(createBuf.numBufs == 1);
	}
	
	@Test
	public void testPipedPipesRight() {
		CountingBuf createBuf = createBuf();

		UnaryOutputOperation<DoubleType, DoubleType> combinedA = Operations
				.concat(Operations.wrap(
						new RealSubtractConstant<DoubleType, DoubleType>(5),
						createBuf), Operations.iterate(Operations.wrap(
						new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10));

		UnaryOutputOperation<DoubleType, DoubleType> combinedB = Operations
				.concat(Operations.wrap(
						new RealSubtractConstant<DoubleType, DoubleType>(5),
						createBuf), Operations.iterate(Operations.wrap(
						new RealAddConstant<DoubleType, DoubleType>(5),
						createBuf), 10));

		assertTrue(Operations.joinRight(combinedA, combinedB)
				.compute(in, new DoubleType()).get() == 95);

		assertTrue(createBuf.numBufs == 1);
	}

	private CountingBuf createBuf() {
		return new CountingBuf();
	}

	class CountingBuf implements UnaryObjectFactory<DoubleType, DoubleType> {

		public int numBufs = 0;

		@Override
		public DoubleType instantiate(DoubleType a) {
			numBufs++;
			return a.createVariable();
		}
	}
}
