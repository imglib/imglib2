package net.imglib2.ops.measure.sandbox;


import java.util.HashMap;
import java.util.List;

public class MeasureService2 /* extends AbstractService */{

	private HashMap<Object[], List<Measure<?>>> registry;

	// @Override
	public void initialize() {
		// super.initialize();
		registry = new HashMap<Object[], List<Measure<?>>>();
	}

	public <U, V> V getMeasure(U source, Class<V> measureClass) {
		List<Measure<?>> measurements = registry.get(source);
		if (measurements == null) resolveMeasurements(source);
		return null;
	}

	public <U> void clearAllMeasurements(U source) {

	}

	public <U, V> void registerMeasure(U source, Class<V> measureClass) {

	}

	public <U> void resolveMeasurements(U source) {}

	private class Measure<T> {

		/**
		 * The list of source objects. It could one Function. Or one Display. Or a
		 * PointSet region of a Dataset. Or anything else.
		 */
		private List<Object> sourceObjects;

		/**
		 * The method by which the source objects are transformed into a
		 * measurement.
		 */
		private MeasuringAlgorithm<T> algorithm;

		/**
		 * The cached measurement. Null before it is calculated.
		 */
		private T measure;

		public void forget() {
			measure = null;
		}
	}

	private class MeasuringAlgorithm<T> {

		public T measure() { /* TODO */
			return null;
		}
	}

	public void forgetMeasurements(Object[] sourceObjects) {
		List<Measure<?>> measures = registry.get(sourceObjects);
		if (measures == null) return;
		for (Measure<?> measure : measures) {
			measure.forget();
		}
	}

	public <T> T findMeasure(Object[] sourceObjects, Class<T> measureClass) {
		return null;
	}

	// TODO - respond to data change events to forget or remove measurement
	// families.

	/*
	 * We want a dependency graph of measures. We want to be able to delete a
	 * measure from graph and kill all dependent measures as well. We want to
	 * resolve portions of the graph simultaneously maybe to save data space
	 * traversals. We want to reset measures to NaNs when certain data events
	 * happen. We want data objects to associated with more than one measure.
	 * So we could update a Dataset and have the mean of the Dataset and the mean
	 * of a neighborhood of the Dataset both be reset or recalced. We want to
	 * build measures out of simpler measures. So at constructor time so user can
	 * provide options if necessary. These options can be part of the dependency
	 * graph even if desired (maybe but maybe not). We want inputs and outputs to
	 * be arbitrary objects. For instance a measure could be a Double or a
	 * PointSet (such as the convex hull) or a String or anything else. And
	 * another measure might use the convex hull as an input for its computation.
	 */
}
