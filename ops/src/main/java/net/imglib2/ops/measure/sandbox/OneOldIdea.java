package net.imglib2.ops.measure.sandbox;

public class OneOldIdea {

	// measure engine

	private interface IterableInterval<T> {

	}

	private interface Value<T> {

		T get();
	}

	private interface Source<T> {

		boolean isChanged();

		boolean hasNext();

		T next();

		// question: when do sources get marked as unchanged?
		// maybe Values subscribe and unsubscribe from sources. a source is not
		// marked unchanged until all its subscribers say okay.
		boolean setUnchanged();
	}

	// note we want an AbstractValue class that does some of the work
	// Also we want engine such that minimum numbers of passes are computed
	// Maybe this can be avoided if metrics are well related. The min passes
	// might come for free? Maybe not.

	private class Count implements Value<Long> {

		private Source<?> source;
		private Long value;

		public Count(Source<?> source) {
			this.source = source;
			value = null;
		}

		public Long get() {
			if (value == null || source.isChanged()) {
				long total = 0;
				while (source.hasNext()) {
					source.next();
					total++;
				}
				value = total;
			}
			return value;
		}
	}

	private class Sum implements Value<Double> {

		private Source<Double> source;
		private Double value;

		public Sum(Source<Double> source) {
			this.source = source;
			value = null;
		}

		public Double get() {
			if (value == null || source.isChanged()) {
				double sum = 0;
				while (source.hasNext()) {
					sum += source.next();
				}
				value = sum;
			}
			return value;
		}
	}

	private class Mean implements Value<Double> {

		private Count count;
		private Sum sum;

		public Mean(Source<Double> source) {
			count = new Count(source);
			sum = new Sum(source);
		}

		public Double get() {
			Double numer = sum.get();
			Long denom = count.get();
			return numer / denom;
		}
	}

	// IterableInterval approach recalcs the data every time
}
