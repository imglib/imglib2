package net.imglib2.ops.measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// an attempt to follow through on the measurement ideas but not based on
// functional programming as much? and more latitude in types of things it can
// calc.

public class Test2 {

	private interface DiscreteIterator {

		boolean hasNext();

		long[] next();
	}

	private interface DiscretePointSet {

		DiscreteIterator iterator();
	}

	private interface ContinuousPointSet {}

	private interface DiscreteFunction<T> {

		T get(long[] pos);
	}

	private interface ContinuousFunction<T> {

		T get(double[] pos);
	}

	private interface PrimitiveLong {

		long getLong();
	}

	private interface PrimitiveDouble {

		double getDouble();
	}


	// ---------------------------------------------------------------------------

	/* Another attempt
	 * 
	 * GOALS
	 * Measures accessible by name
	 * Deleting a measure will delete all unique building blocks
	 * Measures can be partially invalidated
	 * Retrieving measures will calc as needed
	 * Measures have a list of inputs it cares about. Changes to these inputs
	 *   invalidates their current calculation
	 * The measure list should contain named measures and anonymously hatched ones
	 * Eventually minimize number of passes over data
	 * During construction time we should reuse existing measure when possible
	 */

	public static void main(String[] args) {
		Measures measures = new Measures();
		DiscretePointSet region = new FourPoints();
		DiscreteFunction<PrimitiveDouble> func = new ConstFunc();
		Avg2 measure = new Avg2(measures, region, func);
		measures.name("avg2", measure);
		// / ... some time later elsewhere in code ...
		Avg2 retrieved = measures.retrieve("avg2", Avg2.class);
		// notice that we can retrieve an autohatched measure here
		Count2 count = measures.retrieve(Count2.class, region);
		double avg = retrieved.getDouble();
		double cnt = count.getLong();
		System.out.println("Average = " + avg);
		System.out.println("Count   = " + cnt);
	}

	private interface Measure2Base {

		void invalidate();

		boolean isInvalid();

		Set<Object> getInputs();

		void isInput(Object input);

		Set<Measure2<?>> getBasedOnMeasures();

		void isBasedOn(Measure2<?> measure);

		void objectChanged(Object o);

	}

	private interface Measure2<T> extends Measure2Base {

		T get();
	}

	private interface MeasureList {

		// add anonymously
		void add(Measure2<?> measure);

		void name(String name, Measure2<?> measure);

		void unname(String name);

		// get without regard to name
		<T> T retrieve(Class<T> measureClass, Object... inputs);

		<T> T retrieve(String name, Class<T> measureClass);

		void delete(String name);

		void delete(Measure2<?> measure);

		void objectChanged(Object o);

		void objectDeleted(Object o);
	}

	private static abstract class AbstractMeasure2 implements Measure2Base {

		private boolean isInvalid = true;
		private Set<Object> inputs = new HashSet<Object>();
		private Set<Measure2<?>> baseMeasures = new HashSet<Measure2<?>>();

		@Override
		public void invalidate() {
			isInvalid = true;
		}

		@Override
		public boolean isInvalid() {
			return isInvalid;
		}

		protected void setValid(boolean b) {
			isInvalid = b;
		}

		@Override
		public void isInput(Object input) {
			inputs.add(input);
		}

		@Override
		public Set<Object> getInputs() {
			return inputs;
		}

		@Override
		public void isBasedOn(Measure2<?> measure) {
			baseMeasures.add(measure);
		}

		@Override
		public Set<Measure2<?>> getBasedOnMeasures() {
			return baseMeasures;
		}

		@Override
		public void objectChanged(Object o) {
			// TODO - this ignores basedOnMeasures. Is that a prob?
			if (inputs.contains(o)) invalidate();
		}
	}

	// TODO - do not like that the Measure2 implementations require a MeasureList
	// in their constructors. This makes the measures more tightly coupled than
	// desired maybe but it simplifies the construction/addition of measures.

	private static class Count2 extends AbstractMeasure2 implements
		Measure2<PrimitiveLong>, PrimitiveLong
	{

		private long value;
		private DiscretePointSet region;

		public Count2(MeasureList measures, DiscretePointSet region) {
			this.region = region;
			isInput(region);
			measures.add(this);
		}

		@Override
		public PrimitiveLong get() {
			if (isInvalid()) {
				DiscreteIterator iter = region.iterator();
				value = 0;
				while (iter.hasNext()) {
					iter.next();
					value++;
				}
				setValid(true);
			}
			return this;
		}

		@Override
		public long getLong() {
			// note: calling count2.get().getDouble() invokes get() twice
			get();
			return value;
		}
	}

	private static class Sum2 extends AbstractMeasure2 implements
		Measure2<PrimitiveDouble>, PrimitiveDouble
	{

		private double value;
		private DiscretePointSet region;
		private DiscreteFunction<PrimitiveDouble> func;

		public Sum2(MeasureList measures, DiscretePointSet region,
			DiscreteFunction<PrimitiveDouble> func)
		{
			this.region = region;
			this.func = func;
			isInput(region);
			isInput(func);
			measures.add(this);
		}

		@Override
		public PrimitiveDouble get() {
			if (isInvalid()) {
				DiscreteIterator iter = region.iterator();
				value = 0;
				while (iter.hasNext()) {
					long[] pos = iter.next();
					value += func.get(pos).getDouble();
				}
				setValid(true);
			}
			return this;
		}

		@Override
		public double getDouble() {
			// note: calling sum2.get().getDouble() invokes get() twice
			get();
			return value;
		}
	}

	private static class Avg2 extends AbstractMeasure2 implements
		Measure2<PrimitiveDouble>, PrimitiveDouble
	{
		private double value;
		private Sum2 sum;
		private Count2 count;

		public Avg2(MeasureList measures, DiscretePointSet region,
			DiscreteFunction<PrimitiveDouble> func)
		{
			sum = measures.retrieve(Sum2.class, region, func);
			if (sum == null) {
				sum = new Sum2(measures, region, func);
			}
			count = measures.retrieve(Count2.class, region);
			if (count == null) {
				count = new Count2(measures, region);
			}
			isInput(region);
			isInput(func);
			isBasedOn(sum);
			isBasedOn(count);
			measures.add(this);
		}

		@Override
		public PrimitiveDouble get() {
			if (isInvalid()) {
				value = sum.getDouble() / count.getLong();
				setValid(true);
			}
			return this;
		}

		@Override
		public double getDouble() {
			// note: calling avg2.get().getDouble() invokes get() twice
			get();
			return value;
		}
	}

	private static class Bounds implements DiscretePointSet {

		private long[] min, max;

		public Bounds(long[] min, long[] max) {
			this.min = min;
			this.max = max;
		}

		@Override
		public DiscreteIterator iterator() {
			return new DiscreteIterator() {

				private int pos = -1;

				@Override
				public boolean hasNext() {
					return pos < 1;
				}

				@Override
				public long[] next() {
					pos++;
					if (pos == 0) return min;
					if (pos == 1) return max;
					throw new IllegalArgumentException("next()'ed past end");
				}
			};
		}

	}

	private static class BoundingSet extends AbstractMeasure2 implements
		Measure2<DiscretePointSet>
	{

		private long[] min = new long[2];
		private long[] max = new long[2];
		private DiscretePointSet region;
		private Bounds bounds;

		public BoundingSet(MeasureList measures, DiscretePointSet region) {
			this.region = region;
			isInput(region);
			measures.add(this);
		}

		@Override
		public DiscretePointSet get() {
			if (isInvalid()) {
				min[0] = min[1] = Long.MAX_VALUE;
				max[0] = max[1] = Long.MIN_VALUE;
				DiscreteIterator iter = region.iterator();
				while (iter.hasNext()) {
					long[] pos = iter.next();
					min[0] = Math.min(min[0], pos[0]);
					min[1] = Math.min(min[1], pos[1]);
					max[0] = Math.max(max[0], pos[0]);
					max[1] = Math.max(max[1], pos[1]);
				}
				bounds = new Bounds(min, max);
			}
			return bounds;
		}

	}

	private static class MinX extends AbstractMeasure2 implements
		Measure2<PrimitiveLong>, PrimitiveLong
	{

		private BoundingSet bounds;

		public MinX(MeasureList measures, BoundingSet bounds) {
			this.bounds = bounds;
			isBasedOn(bounds); // TODO : both? or one? investigate
			isInput(bounds); // TODO
			measures.add(this);
		}

		@Override
		public long getLong() {
			get();
			return bounds.min[0];
		}

		@Override
		public PrimitiveLong get() {
			return this;
		}

	}

	private static class Measures implements MeasureList {

		private final List<Measure2<?>> measures;
		private final Map<String, Measure2<?>> namedMeasures;

		public Measures() {
			measures = new ArrayList<Measure2<?>>();
			namedMeasures = new HashMap<String, Measure2<?>>();
		}

		@Override
		public void add(Measure2<?> measure) {
			measures.add(measure);
		}

		@Override
		public void name(String name, Measure2<?> measure) {
			namedMeasures.put(name, measure);
		}

		@Override
		public void unname(String name) {
			namedMeasures.put(name, null);
		}

		@Override
		public <T> T retrieve(Class<T> measureClass, Object... inputs) {
			for (Measure2<?> measure : measures) {
				if (measure.getClass().isAssignableFrom(measureClass)) {
					Set<Object> measureInputs = measure.getInputs();
					if (measureInputs.size() == inputs.length) {
						boolean matches = true;
						for (Object input : inputs) {
							if (!measureInputs.contains(input)) {
								matches = false;
								break;
							}
						}
						if (matches) return (T) measure;
					}
				}
			}
			return null;
		}

		@Override
		public <T> T retrieve(String name, Class<T> measureClass) {
			Measure2<?> measure = namedMeasures.get(name);
			if (measure != null) {
				if (measure.getClass().equals(measureClass)) {
					return (T) measure;
				}
			}
			return null;
		}

		@Override
		public void delete(String name) {
			Measure2<?> measure = namedMeasures.get(name);
			if (measure != null) {
				measures.remove(measure);
				namedMeasures.remove(name);
			}
			// TODO - does this have some cascading effect on related measures?????
		}

		@Override
		public void delete(Measure2<?> measure) {
			boolean remove = false;
			for (Measure2<?> m : measures) {
				if (m.equals(measure)) {
					remove = true;
					break;
				}
			}
			if (remove) {
				measures.remove(measure);
				if (namedMeasures.containsValue(measure)) {
					for (String key : namedMeasures.keySet()) {
						if (namedMeasures.get(key).equals(measure)) {
							namedMeasures.put(key, null);
						}
					}
				}
			}
			// TODO - does this have some cascading effect on related measures?????
		}

		@Override
		public void objectChanged(Object o) {
			for (Measure2<?> measure : measures) {
				measure.objectChanged(o);
			}
		}

		@Override
		public void objectDeleted(Object o) {
			List<Measure2<?>> toDelete = new ArrayList<Measure2<?>>();
			for (Measure2<?> measure : measures) {
				if (measure.getInputs().contains(o)) {
					toDelete.add(measure);
				}
			}
			for (Measure2<?> m : toDelete) {
				delete(m);
			}
			// TODO - does this have some cascading effect on related measures?????
		}

	}

	private static class ConstFunc implements DiscreteFunction<PrimitiveDouble>,
		PrimitiveDouble
	{

		@Override
		public PrimitiveDouble get(long[] pos) {
			return this;
		}

		@Override
		public double getDouble() {
			return 7;
		}
	}

	private static class FourPoints implements DiscretePointSet {

		private long[][] points = new long[][] { { 0, 0 }, { 0, 1 }, { 1, 0 },
			{ 1, 1 } };

		@Override
		public DiscreteIterator iterator() {
			return new DiscreteIterator() {

				int pos = -1;

				@Override
				public long[] next() {
					pos++;
					return points[pos];
				}

				@Override
				public boolean hasNext() {
					return pos < 3;
				}
			};
		}

	}

	// ----------------------------------------------------------------------------
	/* OLDER UNSUCCESFUL ATTEMPT */
	// ----------------------------------------------------------------------------

	private interface Measure<T> {

		T get();
	}

	private interface Engine {

		<T> void addMeasure(String varName, Measure<T> measure);

		void removeMeasure(String varName);

		<T> T getMeasure(String varName, Class<T> measureClass);
	}

	private class Sum<T extends PrimitiveDouble> implements
		Measure<PrimitiveDouble>, PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final DiscreteFunction<T> function;
		private double sum;

		public Sum(DiscretePointSet region, DiscreteFunction<T> function) {
			this.region = region;
			this.function = function;
			sum = Double.NaN;
		}

		@Override
		public PrimitiveDouble get() {
			DiscreteIterator iter = region.iterator();
			sum = 0;
			while (iter.hasNext()) {
				long[] pos = iter.next();
				sum += function.get(pos).getDouble();
			}
			return this;
		}

		@Override
		public double getDouble() {
			if (Double.isNaN(sum)) get();
			return sum;
		}
	}

	private class Count implements Measure<PrimitiveLong>, PrimitiveLong {

		private final DiscretePointSet region;
		private long count;

		public Count(DiscretePointSet region) {
			this.region = region;
			count = Long.MIN_VALUE;
		}

		@Override
		public PrimitiveLong get() {
			DiscreteIterator iter = region.iterator();
			count = 0;
			while (iter.hasNext()) {
				iter.next();
				count++;
			}
			return this;
		}

		@Override
		public long getLong() {
			if (count < 0) get();
			return count;
		}
	}

	private class Avg<T extends PrimitiveDouble> implements
		Measure<PrimitiveDouble>, PrimitiveDouble
	{

		private final Sum<T> sum;
		private final Count count;
		private double avg;

		public Avg(DiscretePointSet region, DiscreteFunction<T> function) {
			sum = new Sum<T>(region, function);
			count = new Count(region);
			avg = Double.NaN;
		}

		@Override
		public PrimitiveDouble get() {
			avg = sum.getDouble() / count.getLong();
			return this;
		}

		@Override
		public double getDouble() {
			if (Double.isNaN(avg)) get();
			return avg;
		}
	}

	private class ConvexHull implements Measure<DiscretePointSet> {

		@Override
		public DiscretePointSet get() {
			return null;
		}
	}

	// TODO : only thinking in XY plane. Ideally we have a definition of plane in
	// space and angle within that plane as input parameters.

	private class FeretDiameter implements Measure<PrimitiveDouble>,
		PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final double angle;
		private double value;

		// the angle is the direction of the rays passing through the object
		// angle >= 0 and < pi
		// angle == 0 : max x width
		// angle = pi/2 : max y width
		public FeretDiameter(DiscretePointSet region, double angle) {
			this.region = region;
			this.angle = angle;
			value = Double.NaN;
		}

		@Override
		public PrimitiveDouble get() {

			// TODO : calc value

			return this;
		}

		@Override
		public double getDouble() {
			if (Double.isNaN(value)) get();
			return value;
		}

	}

	// TODO : min and max feret diams can be thoguht of as a Min() or Max()
	// measure of dozens of feret diams at different angles. This would allow
	// the calcs of these two at once to reuse constituent parts.

	private class MaxFeretDiameter implements Measure<PrimitiveDouble>,
		PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final double angleDelta;
		private double value;

		public MaxFeretDiameter(DiscretePointSet region) {
			this(region, Math.PI / 360.0);
		}

		public MaxFeretDiameter(DiscretePointSet region, double angleDelta) {
			this.region = region;
			this.angleDelta = angleDelta;
			if ((angleDelta <= 0) || (angleDelta >= Math.PI)) {
				throw new IllegalArgumentException("angle delta out of bounds");
			}
			value = Double.NaN;
		}

		@Override
		public PrimitiveDouble get() {
			value = Double.MIN_VALUE;
			for (double ang = 0; ang < Math.PI; ang += angleDelta) {
				FeretDiameter fd = new FeretDiameter(region, ang);
				double result = fd.getDouble();
				if (result > 0) value = Math.max(value, result);
			}
			return this;
		}

		@Override
		public double getDouble() {
			if (Double.isNaN(value)) get();
			return value;
		}
	}

	private class MinFeretDiameter implements Measure<PrimitiveDouble>,
		PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final double angleDelta;
		private double value;

		public MinFeretDiameter(DiscretePointSet region) {
			this(region, Math.PI / 360.0);
		}

		public MinFeretDiameter(DiscretePointSet region, double angleDelta) {
			this.region = region;
			this.angleDelta = angleDelta;
			value = Double.NaN;
			if ((angleDelta <= 0) || (angleDelta >= Math.PI)) {
				throw new IllegalArgumentException("angle delta out of bounds");
			}
		}

		@Override
		public PrimitiveDouble get() {
			value = Double.MAX_VALUE;
			for (double ang = 0; ang < Math.PI; ang += angleDelta) {
				FeretDiameter fd = new FeretDiameter(region, ang);
				double result = fd.getDouble();
				if (result > 0) value = Math.min(value, result);
			}
			return this;
		}

		@Override
		public double getDouble() {
			if (Double.isNaN(value)) get();
			return value;
		}
	}

}
