package net.imglib2.ops.operation.binary.bool;

import static org.junit.Assert.*;

import net.imglib2.type.logic.BitType;

import org.junit.Test;

public class BinaryXorTest {

	private BinaryXor op;
	private BitType input1;
	private BitType input2;
	private BitType output;
	
	@Test
	public void testOp() {
		op = new BinaryXor();
		input1 = new BitType();
		input2 = new BitType();
		output = new BitType();
		boolean[] choices = new boolean[]{false,true};
		for (boolean b1 : choices)
			for (boolean b2 : choices)
				doCase(b1,b2);
	}

	private void doCase(boolean b1, boolean b2) {
		input1.set(b1);
		input2.set(b2);
		op.compute(input1, input2, output);
		boolean expected = b1 ^ b2;
		assertEquals(expected, output.get());
	}
}
