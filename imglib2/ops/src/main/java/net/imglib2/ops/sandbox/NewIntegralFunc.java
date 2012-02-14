package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;

public class NewIntegralFunc<U extends RealType<U>> implements NewFunc<U,U> {

	private NewFunc<U,U> otherFunc;
	private double[] deltas;
	private double cellSize;
	U tmp;
	
	public NewIntegralFunc(double[] deltas, NewFunc<U,U> otherFunc) {
		this.otherFunc = otherFunc;
		this.deltas = deltas;
		this.cellSize = 1;  // TODO - calc from deltas
		this.tmp = createOutput();
	}
	
	@Override
	public void evaluate(NewIterableInterval<U> interval, U output) {
		Cursor<U> cursor = interval.cursor();
		double sum = 0;
		while (cursor.hasNext()) {
			otherFunc.evaluate(interval, tmp);
			sum += (cellSize) * tmp.getRealDouble(); 
		}
		output.setReal(sum);
	}

	@Override
	public U createOutput() {
		return otherFunc.createOutput();
	}

	@Override
	public NewFunc<U, U> copy() {
		return null;  // TODO
	}

}
