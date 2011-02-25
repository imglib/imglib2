package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Gamma implements UnaryOperator
{
	private double constant;
	
	public Gamma(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		if (input <= 0)
			return 0;
		
		return Math.exp(this.constant * Math.log(input));
	}

}
