package net.imglib2.ops.measure.sandbox;

import java.util.List;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.ops.pointset.PointSetIterator;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;

public class MeasureService {

	public void add(Computation<?> c) {

	}

	public void delete(Computation<?> c) {

	}

	public void name(Computation<?> c, String name) {

	}

	public Computation<?> find(String name) {
		return null;
	}

	private interface Computer<T> {

		T compute(List<Computation<?>> inputs);
	}

	private class Computation<T> {

		protected T value;

		public boolean isResolved() {
			return value != null;
		}

		public void resolve() {
			if (value == null) {
				// for (Computation<?> c : inputComps) c.resolve();
				// value = computer.compute(inputComps);
			}
		}

		public void unresolve() {
			value = null;
		}

		public T getValue() {
			return value;
		}
	}

	private class Count extends Computation<LongType> {

		private PointSet region;

		public Count(PointSet region) {
			this.region = region;
		}

		public void compute() {
			value = new LongType(region.size()); // TODO : make it a SamplingMeasure
																						// instead
		}

		public LongType getValue() {
			return value;
		}
	}

	private class Sum extends Computation<DoubleType> {

		private Function<long[], RealType<?>> func;
		private PointSet region;

		public Sum(Function<long[], RealType<?>> func, PointSet region) {
			this.func = func;
			this.region = region;
		}

		public void compute() {
			double sum = 0;
			RealType<?> tmp = func.createOutput();
			PointSetIterator iter = region.iterator();
			while (iter.hasNext()) {
				long[] pos = iter.next();
				func.compute(pos, tmp);
				sum += tmp.getRealDouble();
			}
			value = new DoubleType(sum);
		}

		public DoubleType getValue() {
			return value;
		}
	}

	private class Mean extends Computation<DoubleType> {

		private Sum sum;
		private Count count;

		public Mean(Function<long[], RealType<?>> func, PointSet region) {
			sum = new Sum(func, region);
			count = new Count(region);
		}

		public void compute() {
			long vals = count.getValue().get();
			double mean = vals == 0 ? 0 : sum.getValue().get() / vals;
			value = new DoubleType(mean);
		}

		public DoubleType getValue() {
			return value;
		}
	}

	private class Variance extends Computation<DoubleType> {

		private Function<long[], RealType<?>> func;
		private PointSet region;
		private Mean mean;

		public Variance(Function<long[], RealType<?>> func, PointSet region) {
			this.func = func;
			this.region = region;
			this.mean = new Mean(func, region);
		}

		public void compute() {

		}
	}

	private class ConvexHull extends Computation<PointSet> {

		private PointSet input;

		public ConvexHull(PointSet input) {
			this.input = input;
		}

		public void compute() {
			// TODO
			value = null;
		}

		public PointSet getValue() {
			return value;
		}
	}

	/*
	 * Having trouble with this
	 * I want a service that
	 * - can register type's of measurements on input variables
	 * - tries to pass over data the minimum number of times
	 * - can add and delete measures from engine and related families are added and deleted 
	 * - can unresolve a (family of) measures and recalc. This would allow reactions to update events
	 * - register with names and pull out by name and also register anonymously
	 * - someone trying to register one that exists gets reused
	 * - does some dead measurement reclamation? maybe not.
	 */
	// related fiji mail list post:
	// https://groups.google.com/forum/?fromgroups=#!topic/fiji-devel/AnYq_caJA1M

	/*
	 * How would lookup work?
	 *   Variance actual = service.lookup(new Variance(dataset,region)); ???
	 *   OR
	 *   Variance actual = service.lookup(Variance.class, dataset, region);
	 *   
	 *   Both ways are a bit clunky
	 *   1st one ends up using same constructor twice: once for registration and
	 *     once for lookup. This is not the friendliest way to work.
	 *   2nd one seems more reasonable after further thought
	 *     The matcher can just call Object::equals() on each param and each
	 *     param type can implement equals if they want. Thus passing numbers
	 *     would automatically work if the values were the same (I think). Passing
	 *     numbers is evil in a way. What if you pass two of them as 5,4 and later
	 *     check for two as 4,5. As ctor params their order matters. As lookup
	 *     params I was hoping they wouldn't have to be.
	 */
}
