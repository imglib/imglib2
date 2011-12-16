package net.imglib2.script.analysis.fn;

public class NumericResult<N extends Number> extends Number
{
	private static final long serialVersionUID = 1L;
	protected N d;

	public NumericResult(final N d) {
		this.d = d;
	}
	
	public NumericResult() {}
	
	@Override
	public int intValue() {
		return d.intValue();
	}

	@Override
	public long longValue() {
		return d.longValue();
	}

	@Override
	public float floatValue() {
		return d.floatValue();
	}

	@Override
	public double doubleValue() {
		return d.doubleValue();
	}
}
