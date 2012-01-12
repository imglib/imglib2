package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;


public interface RealFunction<T> {
	void evaluate(double[] coordinate, RelocatingIterableInterval<T> interval, T output);
	T createOutput();
	
	/*
	 * Need to define a relocating interval. Then want to pass it to Average
	 * function at constructor time or before 1st call to evaluate(). This allows
	 * us to reuse cursors etc.
	 */

	public interface RelocatingIterableInterval<K> {
		Cursor<K> cursor();
		void relocate(long[] newOrigin);
	}
	
	public interface RelocatableCursor<K> extends Cursor<K> {
		void setOrigin(long[] newOrigin);
	}
	
	public class Average implements RealFunction<RealType<?>> {

		RealType<?> type;
		
		public Average(RealType<?> type) {
			this.type = type.createVariable();
		}
		
		@Override
		public void evaluate(double[] coordinate,
			RelocatingIterableInterval<RealType<?>> interval, RealType<?> output)
		{
			double sum = 0;
			long numElem = 0;
			Cursor<RealType<?>> cursor = interval.cursor();
			while (cursor.hasNext()) {
				sum += cursor.get().getRealDouble();
				numElem++;
			}
			if (numElem == 0)
				output.setReal(0);
			else
				output.setReal(sum/numElem);
		}

		@Override
		public RealType<?> createOutput() {
			return type.createVariable();
		}
		
	}
}
