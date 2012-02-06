package net.imglib2.ops.sandbox;

import net.imglib2.type.numeric.RealType;

public class NewConstFunc<U extends RealType<U>> implements NewFunc<U,U>{

	private NewIterableInterval<U> interval = null;
	
	@Override
	public void evaluate(NewIterableInterval<U> interval, U output) {
		if (this.interval == null) this.interval = interval;
		output.setReal(7);
	}

	@Override
	public U createOutput() {
		return interval.firstElement().createVariable();
	}

	@Override
	public NewFunc<U, U> copy() {
		return new NewConstFunc<U>();

	}

}
