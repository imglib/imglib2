package net.imglib2.ops.operation.unary.complex;

import static org.junit.Assert.*;

import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexLogTest {

	private ComplexLog op = new ComplexLog();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		double rt2 = Math.sqrt(2);
		double rt32 = Math.sqrt(3.0)/2;
		
		// note: values taken from Wolfram Alpha online calculator
		
		// try 0 case
		
		doCase(0,0,Math.log(0),Double.NaN);

		// try all the cardinal directions
		
		doCase(1,0,Math.log(1),0);  // 0
		doCase(0,1,Math.log(1),Math.PI/2); // 90
		doCase(-1,0,Math.log(1),Math.PI);  // 180/-180
		doCase(0,-1,Math.log(1),-Math.PI/2);  // -90

		// try all 45 angles
		
		doCase(1,1,Math.log(rt2),Math.PI/4); // 45
		doCase(-1,1,Math.log(rt2),3*Math.PI/4);  // 135
		doCase(-1,-1,Math.log(rt2),-3*Math.PI/4); // -135
		doCase(1,-1,Math.log(rt2),-Math.PI/4);  // -45

		// try all 30/60 angles
		
		doCase(rt32,0.5,Math.log(1),Math.PI/6);  // 30
		doCase(0.5,rt32,Math.log(1),2*Math.PI/6);  // 60
		doCase(-0.5,rt32,Math.log(1),4*Math.PI/6);  // 120
		doCase(-rt32,0.5,Math.log(1),5*Math.PI/6);  // 150
		doCase(rt32,-0.5,Math.log(1),-Math.PI/6);  // -30
		doCase(0.5,-rt32,Math.log(1),-2*Math.PI/6);  // -60
		doCase(-0.5,-rt32,Math.log(1),-4*Math.PI/6);  // -120
		doCase(-rt32,-0.5,Math.log(1),-5*Math.PI/6);  // -150
		
		// try a few random values
		
		doCase(Math.E,Math.E,1.346573,0.785398);
		doCase(1,2,0.804718,1.107148);
		doCase(-3,-2,1.282474,-2.553590);
		doCase(4,-3,1.609437,-0.643501);
		doCase(-7,3,2.030221,2.736700);
	}

	private void doCase(double r1, double i1, double expR, double expI) {
		input1.setComplexNumber(r1, i1);
		op.compute(input1, output);
		assertEquals(expR, output.getRealDouble(), 0.000001);
		assertEquals(expI, output.getImaginaryDouble(), 0.000001);
	}
}
