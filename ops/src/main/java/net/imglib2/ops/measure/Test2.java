package net.imglib2.ops.measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// an attempt to follow through on the measurement ideas but not based on
// functional programming as much? and more latitude in types of things it can
// calc.

// TODO - make constructors and measures much more simple if possible

// TODO - look at Knime knip features code to see how it would stretch this

// TODO - take into account Gabriel's and Lee's comments in fiji-dev re:
//        measurement approach

// NOTE - side issue note: there are cases where objects are composed of other
// objects (i.e a Dataset owns an ImgPlus which owns an Img. Datasets should
// listen for ImgPlus object changed events and rethrow an object changed event
// passing self. Similarly ImgPluses should do same with Imgs. And
// ImageFunctions should do similar things. We have a need for an object
// dependency graph if we want to avoid the event approach. Below we are already
// making a defacto dependency graph so think how we want to approach.

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
		Avg measure = new Avg(measures, region, func);
		measures.name("avg2", measure);
		// / ... some time later elsewhere in code ...
		Avg retrieved = measures.retrieve("avg2", Avg.class);
		// notice that we can retrieve an autohatched measure here
		Count count = measures.retrieve(Count.class, region);
		double avg = retrieved.getDouble();
		double cnt = count.getLong();
		System.out.println("Average = " + avg);
		System.out.println("Count   = " + cnt);
		List<PrimitiveDouble> list = new ArrayList<PrimitiveDouble>();
		list.add(new Number(1));
		list.add(new Number(2));
		list.add(new Number(3));
		list.add(new Number(4));
		ListFunction<PrimitiveDouble> listFunc =
			new ListFunction<PrimitiveDouble>(list);
		WholeIterableRegion listRegion = new WholeIterableRegion(list);
		Sum sum2 = new Sum(measures, listRegion, listFunc);
		System.out.println("List sum = " + sum2.getDouble());
	}

	private interface MeasureBase {

		void invalidate();

		boolean isInvalid();

		Set<Object> getInputs();

		void isInput(Object input);

		Set<Measure<?>> getChildMeasures();

		void isChildOf(Measure<?> measure);

		Set<Measure<?>> getParentMeasures();

		void isParentOf(Measure<?> measure);

		void objectChanged(Object o);

	}

	private interface Measure<T> extends MeasureBase {

		T get();
	}

	// TODO This is still assuming that measures autoadd themselves to measure
	// lists. Make sure that my ctor changes do not invalidate this api.

	private interface MeasureList {

		// add anonymously
		void add(Measure<?> measure);

		void name(String name, Measure<?> measure);

		void unname(String name);

		// get without regard to name
		// note that currently order of inputs doesn't matter but also can't
		// distinguish between (4.2, 3.4) and (3.4, 4.2). This is problematic.
		<T> T retrieve(Class<T> measureClass, Object... inputs);

		<T> T retrieve(String name, Class<T> measureClass);

		void delete(String name);

		void delete(Measure<?> measure);

		void objectChanged(Object o);

		void objectDeleted(Object o);
	}

	private static abstract class AbstractMeasure implements MeasureBase {

		private boolean isInvalid = true;
		private Set<Object> inputs = new HashSet<Object>();
		private Set<Measure<?>> parentMeasures = new HashSet<Measure<?>>();
		private Set<Measure<?>> childMeasures = new HashSet<Measure<?>>();

		@Override
		public void invalidate() {
			isInvalid = true;
		}

		@Override
		public boolean isInvalid() {
			return isInvalid;
		}

		protected void validate() {
			isInvalid = false;
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
		public void isParentOf(Measure<?> measure) {
			parentMeasures.add(measure);
		}

		@Override
		public Set<Measure<?>> getParentMeasures() {
			return parentMeasures;
		}

		@Override
		public void isChildOf(Measure<?> measure) {
			childMeasures.add(measure);
		}

		@Override
		public Set<Measure<?>> getChildMeasures() {
			return childMeasures;
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

	private static class Count extends AbstractMeasure implements
		Measure<PrimitiveLong>, PrimitiveLong
	{

		private long value;
		private DiscretePointSet region;

		public Count(DiscretePointSet region) {
			this.region = region;
			isInput(region);
		}

		public Count(MeasureList measures, DiscretePointSet region) {
			this(region);
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
				validate();
			}
			return this;
		}

		@Override
		public long getLong() {
			// note: calling count.get().getDouble() invokes get() twice
			get();
			return value;
		}
	}

	private static class Sum extends AbstractMeasure implements
		Measure<PrimitiveDouble>, PrimitiveDouble
	{

		private double value;
		private DiscretePointSet region;
		private DiscreteFunction<PrimitiveDouble> func;

		public Sum(DiscretePointSet region, DiscreteFunction<PrimitiveDouble> func)
		{
			this.region = region;
			this.func = func;
			isInput(region);
			isInput(func);
		}

		public Sum(MeasureList measures, DiscretePointSet region,
			DiscreteFunction<PrimitiveDouble> func)
		{
			this(region, func);
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
				validate();
			}
			return this;
		}

		@Override
		public double getDouble() {
			// note: calling sum.get().getDouble() invokes get() twice
			get();
			return value;
		}
	}

	private static class Avg extends AbstractMeasure implements
		Measure<PrimitiveDouble>, PrimitiveDouble
	{
		private double value;
		private Sum sum;
		private Count count;

		public Avg(Sum sum, Count count) {
			this.sum = sum;
			this.count = count;
			isChildOf(sum);
			isChildOf(count);
			sum.isParentOf(this);
			count.isParentOf(this);
		}

		public Avg(MeasureList measures, DiscretePointSet region,
			DiscreteFunction<PrimitiveDouble> func)
		{
			sum = measures.retrieve(Sum.class, region, func);
			if (sum == null) {
				sum = new Sum(measures, region, func);
			}
			count = measures.retrieve(Count.class, region);
			if (count == null) {
				count = new Count(measures, region);
			}
			isInput(region);
			isInput(func);
			isChildOf(sum);
			isChildOf(count);
			sum.isParentOf(this);
			count.isParentOf(this);
			measures.add(this);
		}

		@Override
		public PrimitiveDouble get() {
			if (isInvalid()) {
				value = sum.getDouble() / count.getLong();
				validate();
			}
			return this;
		}

		@Override
		public double getDouble() {
			// note: calling avg.get().getDouble() invokes get() twice
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

	// NOTE: an example measure that is not a primitive value

	private static class BoundingSet extends AbstractMeasure implements
		Measure<DiscretePointSet>
	{

		private long[] min = new long[2];
		private long[] max = new long[2];
		private DiscretePointSet region;
		private Bounds bounds;

		public BoundingSet(DiscretePointSet region) {
			this.region = region;
			isInput(region);
		}

		public BoundingSet(MeasureList measures, DiscretePointSet region) {
			this(region);
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

	private static class MinX extends AbstractMeasure implements
		Measure<PrimitiveLong>, PrimitiveLong
	{

		private BoundingSet bounds;

		public MinX(BoundingSet bounds) {
			this.bounds = bounds;
			isChildOf(bounds);
			bounds.isParentOf(this);
			isInput(bounds); // TODO: input or parent/child or both?
		}

		public MinX(MeasureList measures, BoundingSet bounds) {
			this(bounds);
			measures.add(this);
		}

		@Override
		public PrimitiveLong get() {
			return this;
		}

		@Override
		public long getLong() {
			return bounds.min[0];
		}

	}

	private static class Measures implements MeasureList {

		private final List<Measure<?>> measures;
		private final Map<String, Measure<?>> namedMeasures;

		public Measures() {
			measures = new ArrayList<Measure<?>>();
			namedMeasures = new HashMap<String, Measure<?>>();
		}

		@Override
		public void add(Measure<?> measure) {
			measures.add(measure);
		}

		@Override
		public void name(String name, Measure<?> measure) {
			namedMeasures.put(name, measure);
		}

		@Override
		public void unname(String name) {
			namedMeasures.put(name, null);
		}

		@Override
		public <T> T retrieve(Class<T> measureClass, Object... inputs) {
			for (Measure<?> measure : measures) {
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
			Measure<?> measure = namedMeasures.get(name);
			if (measure != null) {
				if (measure.getClass().equals(measureClass)) {
					return (T) measure;
				}
			}
			return null;
		}

		@Override
		public void delete(String name) {
			Measure<?> measure = namedMeasures.get(name);
			if (measure != null) {
				measures.remove(measure);
				namedMeasures.remove(name);
			}
			// TODO - does this have some cascading effect on related measures?????
		}

		@Override
		public void delete(Measure<?> measure) {
			boolean remove = false;
			for (Measure<?> m : measures) {
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
			for (Measure<?> measure : measures) {
				measure.objectChanged(o);
			}
		}

		@Override
		public void objectDeleted(Object o) {
			List<Measure<?>> toDelete = new ArrayList<Measure<?>>();
			for (Measure<?> measure : measures) {
				if (measure.getInputs().contains(o)) {
					toDelete.add(measure);
				}
			}
			for (Measure<?> m : toDelete) {
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

	private static class Number implements PrimitiveDouble {

		private double value;

		public Number(double value) {
			this.value = value;
		}

		@Override
		public double getDouble() {
			return value;
		}
	}

	private static class ListFunction<T> implements DiscreteFunction<T> {

		// would be nice but not really possible:
		// private Iterable<T> iterable;

		private List<T> list;

		public ListFunction(List<T> list) {
			this.list = list;
		}

		@Override
		public T get(long[] pos) {
			return list.get((int) pos[0]); // TODO - limited to Integer range
		}

	}

	// Treat an Iterable data source as a big list. This class is an adapter that
	// allows one to pass an Iterable and get a set of indices. The indices cover
	// the whole range of the list. Thus one can calc a measure on a whole list.
	// But this approach is more general allowing one to define regions like every
	// nth index and then for example you calc a measure using every 3rd value.

	private static class WholeIterableRegion implements DiscretePointSet {

		private Iterable<?> iterable;

		public WholeIterableRegion(Iterable<?> iterable) {
			this.iterable = iterable;
		}

		@Override
		public DiscreteIterator iterator() {
			return new DiscreteIterator() {

				private long[] index = new long[1];
				private long i = -1;
				private Iterator<?> iter = iterable.iterator();

				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}

				@Override
				public long[] next() {
					iter.next();
					index[0] = ++i;
					return index;
				}

			};
		}

		// ALTERNATIVELY
		// Could filter an Iterable<T>. So algos do work on iterables. But you can
		// make an "every 3rd value" filter whose iterator only returns a 3rd of the
		// values.
		// TODO
		// I THINK THIS MAKES A LOT OF SENSE. HOW DOES IT KILL THE FUNCTION IDEA?
		// ITERABLES ARE IN NO WAY SPATIAL SO THATS A MAJOR LIMITATION. ALSO THEY
		// ARE NOT RANDOM ACCESSIBLE.

		private static class Sum2 extends AbstractMeasure implements
			Measure<PrimitiveDouble>, PrimitiveDouble
		{

			private Iterable<PrimitiveDouble> iterable;
			private double sum;

			public Sum2(Iterable<PrimitiveDouble> iterable) {
				this.iterable = iterable;
				isInput(iterable);
			}

			public Sum2(MeasureList measures, Iterable<PrimitiveDouble> iterable) {
				this(iterable);
				measures.add(this);
			}

			@Override
			public PrimitiveDouble get() {
				if (isInvalid()) {
					Iterator<PrimitiveDouble> iter = iterable.iterator();
					sum = 0;
					while (iter.hasNext()) {
						sum += iter.next().getDouble();
					}
					validate();
				}
				return this;
			}

			@Override
			public double getDouble() {
				// note: calling sum2.get().getDouble() invokes get() twice
				get();
				return sum;
			}
		}

	}

	// TODO : only thinking in XY plane. Ideally we have a definition of plane in
	// space and angle within that plane as input parameters.

	private class FeretDiameter extends AbstractMeasure implements
		Measure<PrimitiveDouble>, PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final Double angle;
		private double value;

		// the angle is the direction of the rays passing through the object
		// angle >= 0 and < pi
		// angle == 0 : max x width
		// angle = pi/2 : max y width
		public FeretDiameter(DiscretePointSet region, Double angle)
		{
			this.region = region;
			this.angle = angle;
			isInput(region);
			isInput(angle);
		}

		public FeretDiameter(MeasureList measures, DiscretePointSet region,
			Double angle)
		{
			this(region, angle);
			measures.add(this);
		}

		@Override
		public PrimitiveDouble get() {

			// TODO : calc value

			return this;
		}

		@Override
		public double getDouble() {
			// note: calling fd.get().getDouble() invokes get() twice
			get();
			return value;
		}

	}

	// TODO : min and max feret diams can be thought of as a Min() or Max()
	// measure of dozens of feret diams at different angles. This would allow
	// the calcs of these two at once to reuse constituent parts.

	private class MaxFeretDiameter extends AbstractMeasure implements
		Measure<PrimitiveDouble>, PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final Double angleDelta;
		private double value;

		public MaxFeretDiameter(MeasureList measures, DiscretePointSet region) {
			this(measures, region, Math.PI / 360.0);
		}

		public MaxFeretDiameter(MeasureList measures, DiscretePointSet region,
			Double angleDelta)
		{
			this.region = region;
			this.angleDelta = angleDelta;
			if ((angleDelta <= 0) || (angleDelta >= Math.PI)) {
				throw new IllegalArgumentException("angle delta out of bounds");
			}
			isInput(region);
			isInput(angleDelta);
			measures.add(this);
		}

		@Override
		public PrimitiveDouble get() {
			if (isInvalid()) {
				value = Double.MIN_VALUE;
				double delta = angleDelta;
				for (double ang = 0; ang < Math.PI; ang += delta) {
					FeretDiameter fd = new FeretDiameter(region, ang); // note: not
																															// recording
					double result = fd.getDouble();
					if (result > 0) value = Math.max(value, result);
				}
				validate();
			}
			return this;
		}

		@Override
		public double getDouble() {
			get();
			return value;
		}
	}

	private class MinFeretDiameter extends AbstractMeasure implements
		Measure<PrimitiveDouble>, PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final Double angleDelta;
		private double value;

		public MinFeretDiameter(MeasureList measures, DiscretePointSet region) {
			this(measures, region, Math.PI / 360.0);
		}

		public MinFeretDiameter(MeasureList measures, DiscretePointSet region,
			Double angleDelta)
		{
			this.region = region;
			this.angleDelta = angleDelta;
			if ((angleDelta <= 0) || (angleDelta >= Math.PI)) {
				throw new IllegalArgumentException("angle delta out of bounds");
			}
			isInput(region);
			isInput(angleDelta);
			measures.add(this);
		}

		@Override
		public PrimitiveDouble get() {
			if (isInvalid()) {
				value = Double.MIN_VALUE;
				double delta = angleDelta;
				for (double ang = 0; ang < Math.PI; ang += delta) {
					FeretDiameter fd = new FeretDiameter(region, ang); // note: not
																															// recording
					double result = fd.getDouble();
					if (result > 0) value = Math.min(value, result);
				}
				validate();
			}
			return this;
		}

		@Override
		public double getDouble() {
			get();
			return value;
		}
	}

	// ---------------------------------------------------------------------------
	/* OLDER UNSUCCESSFUL ATTEMPT */
	// ---------------------------------------------------------------------------

	private interface MeasureOld<T> {

		T get();
	}

	private interface Engine {

		<T> void addMeasure(String varName, MeasureOld<T> measure);

		void removeMeasure(String varName);

		<T> T getMeasure(String varName, Class<T> measureClass);
	}

	private class SumOld<T extends PrimitiveDouble> implements
		MeasureOld<PrimitiveDouble>, PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final DiscreteFunction<T> function;
		private double sum;

		public SumOld(DiscretePointSet region, DiscreteFunction<T> function) {
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

	private class CountOld implements MeasureOld<PrimitiveLong>, PrimitiveLong {

		private final DiscretePointSet region;
		private long count;

		public CountOld(DiscretePointSet region) {
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

	private class AvgOld<T extends PrimitiveDouble> implements
		MeasureOld<PrimitiveDouble>, PrimitiveDouble
	{

		private final SumOld<T> sum;
		private final CountOld count;
		private double avg;

		public AvgOld(DiscretePointSet region, DiscreteFunction<T> function) {
			sum = new SumOld<T>(region, function);
			count = new CountOld(region);
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

	private class ConvexHullOld implements MeasureOld<DiscretePointSet> {

		@Override
		public DiscretePointSet get() {
			return null;
		}
	}

	// TODO : only thinking in XY plane. Ideally we have a definition of plane in
	// space and angle within that plane as input parameters.

	private class FeretDiameterOld implements MeasureOld<PrimitiveDouble>,
		PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final double angle;
		private double value;

		// the angle is the direction of the rays passing through the object
		// angle >= 0 and < pi
		// angle == 0 : max x width
		// angle = pi/2 : max y width
		public FeretDiameterOld(DiscretePointSet region, double angle) {
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

	// TODO : min and max feret diams can be thought of as a Min() or Max()
	// measure of dozens of feret diams at different angles. This would allow
	// the calcs of these two at once to reuse constituent parts.

	private class MaxFeretDiameterOld implements MeasureOld<PrimitiveDouble>,
		PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final double angleDelta;
		private double value;

		public MaxFeretDiameterOld(DiscretePointSet region) {
			this(region, Math.PI / 360.0);
		}

		public MaxFeretDiameterOld(DiscretePointSet region, double angleDelta) {
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
				FeretDiameterOld fd = new FeretDiameterOld(region, ang);
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

	private class MinFeretDiameterOld implements MeasureOld<PrimitiveDouble>,
		PrimitiveDouble
	{

		private final DiscretePointSet region;
		private final double angleDelta;
		private double value;

		public MinFeretDiameterOld(DiscretePointSet region) {
			this(region, Math.PI / 360.0);
		}

		public MinFeretDiameterOld(DiscretePointSet region, double angleDelta) {
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
				FeretDiameterOld fd = new FeretDiameterOld(region, ang);
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
