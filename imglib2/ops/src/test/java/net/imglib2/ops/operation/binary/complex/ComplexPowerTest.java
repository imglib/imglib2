package net.imglib2.ops.operation.binary.complex;

import static org.junit.Assert.*;

import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexPowerTest {

	private ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> op =
			new ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>(new ComplexDoubleType());
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		System.out.println("ComplexPowerTest is not implemented");
		assertTrue(true);
	}

}
