package net.imglib2.ops.measure;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.ops.pointset.PointSetIterator;
import net.imglib2.type.numeric.RealType;


// TODO

// - later: support ctors with params that are not just empty or Measurements.
//   Think how contrahamonicMean wants to have an "order" param. And a weighted
//   average wants to have weights. Maybe when you set up a NewMeasureSet you
//   associate a class (like WeightedAverage.class) with a value (like a double[]).
//   Then constructor finding code takes the one ctor and tries to fill in any
//   unknown params from the values registered with NewMeasureSet.

// - later: instead of getValue() returning doubles it could set the value of a
//   passed in T derived from ComplexType

// - note: something you can't do with this design: define a measurement that is
//   half some other generic measurement. In ctor work obtain() would have a
//   Measurement (rather than something specific) and would not know what to
//   obtain. Solution: same as weights idea - one says something like:
//   set.use(Half.class, Mean.class) which would see that Half constructor
//   would construct on a Mean and that is obtain()'ed as usual.

public class NewMeasurementSet {
	
	// -- instance variables --
	
	private List<Measurement> measurements;
	private Map<String,Measurement> namedMeasurements;
	private List<List<SamplingMeasurement>> samplingLevels;
	
	// -- constructor --
	
	public NewMeasurementSet() {
		this.measurements = new ArrayList<Measurement>();
		this.namedMeasurements = new HashMap<String,Measurement>();
		this.samplingLevels = new ArrayList<List<SamplingMeasurement>>();
	}

	// -- public api --
	
	// named measures are the retrievable measures (added here)
	//   there are numerous other measures that get hatched and computed but
	//   not retrievable
	
	public void addMeasure(String name,
		Class<? extends Measurement> measuringClass) // TODO - add Object... params?
	{
		// allocate Measurement passing correct values to ctor
		// update dependency graph
		// if measureClass already present use it else allocate a new one
		Measurement measurement = obtain(measuringClass);
		namedMeasurements.put(name, measurement);
	}

	// Getting a correct value for a measure depends upon its parents being
	// calced. At some level it all falls back to sampling the data at approp
	// times. So we make sure that parents are all sampled in the right order.
	
	public <T extends RealType<T>>
	void doMeasurements(Function<long[],T> func, PointSet region)
	{
		T tmp = func.createOutput();
		PointSetIterator iter = region.createIterator();
		for (List<SamplingMeasurement> level : samplingLevels) {
			for (SamplingMeasurement measurement : level) {
				measurement.preprocess(region.getOrigin());
			}
			iter.reset();
			while (iter.hasNext()) {
				long[] pos = iter.next();
				func.compute(pos, tmp);
				for (SamplingMeasurement measurement : level) {
					measurement.dataValue(pos, tmp.getRealDouble());
				}
			}
			for (SamplingMeasurement measurement : level) {
				measurement.postprocess();
			}
		}
	}
	
	public double getValue(String name) {
		Measurement m = namedMeasurements.get(name);
		if (m == null) return Double.NaN;
		return m.getValue();
	}

	// -- private helpers --
	
	private Measurement lookupMeasurement(Class<? extends Measurement> clazz) {
		for (Measurement m : measurements) {
			if (m.getClass() == clazz) return m;
		}
		return null;
	}
	
	private Measurement obtain(Class<? extends Measurement> clazz)
	{
		Measurement m = lookupMeasurement(clazz);
		if (m != null) return m;
		
		try {
			Constructor<? extends Measurement> constructor = getConstructor(clazz); 
			if (constructor == null) return clazz.newInstance();
		
			@SuppressWarnings("unchecked")
			Class<? extends Measurement>[] paramTypes =
				(Class<? extends Measurement>[]) constructor.getParameterTypes();
		
			Object[] measures = new Object[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) {
				measures[i] = obtain(paramTypes[i]);
			}
		
			Measurement measure = constructor.newInstance(measures);
			measurements.add(measure);
			if (measure instanceof SamplingMeasurement)
				prioritize((SamplingMeasurement) measure);
			return measure;
		}
		catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	private Constructor<? extends Measurement>
		getConstructor(Class<? extends Measurement> clazz)
	{
		@SuppressWarnings("unchecked")
		Constructor<? extends Measurement>[] constructors =
				(Constructor<? extends Measurement>[]) clazz.getConstructors();
		
		if (constructors.length == 0) return null;
		return constructors[0]; 
	}
	
	private void prioritize(SamplingMeasurement measure) {
		int level = determineLevel(measure);
		while (level >= samplingLevels.size()) {
			samplingLevels.add(new ArrayList<SamplingMeasurement>());
		}
		System.out.println("Adding a sampling measure to level "+level+" : "+measure.getClass());
		samplingLevels.get(level).add(measure);
	}
	
	private int determineLevel(Measurement measure) {
		try {
			int max = 0;
			Field[] fields = measure.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(measure);
				if (value instanceof Measurement) {
					int level = determineLevel((Measurement)value);
					max = Math.max(max, level+1);
				}
			}
			return max;
		} catch (Exception e) {
			return 0;
		}
	}
}
