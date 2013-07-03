package net.imglib2.ops.measure.orig;

/*

want to be able to build without passing func everywhere

so in general it's
  Mean mean = new Mean(new Sum(), new ElemCount());
  and then
  mean.calculate(function, region, output)

does this make construction get messy passing all these temps around?
    in general the construction is very difficult
	
	could have a MeasureSet that starts out empty
	set.add(Mean.class);
	  the set figures out what things the Mean needs (like Sum and ElemCount),
	  creates them, and passes them to Mean's constructor
	  (like curtis said we've done this kind of work for our plugins already)
  then calc all measures in the set (order does not matter)
  
  btw how would you construct a WeightedSum? How does the ctor know about
  the weights? Do you first register them with the set? What if you have
  two different set of weights in the set?
  
  could values get cached? this would solve Aivar's chained function idea.
  but the cost of generating a key and caching and retrieving might be
  too prohibitive as it is.
  
  anyhow chaining idea might be supportable by having all measures support
  the preproc(), visit(), postproc(). the dependencies of functions could
  be figured out and all of same level of dep can share the one visit. The
  MeasurementSet::measure() method would start iters for each level one at
  a time starting with the first. This is simple and would work.

  note: I can measure a weighted average by providing weights externally.
  But I can't think of any method that allows a new Measure to base itself
  on a weighted average that can somehow provide weights with no arg ctor

  what if you set up measure set empty. provide it with a Weights class that
  has to be a Measurement. Then when other Measurements are added if they
  need a Weights in their constructor they just use it. This would allow a
  single set of Weights but not more than one. We need a general solution.

  (Note: to last issue see TODOs in NewMeasurementService)
  
  
  12-13-12 Much later notes

    Gabriel Landini and Lee Kamentsky had comments about this toy implementation
    online (fiji-devel, 10-23-12). From their discussion it is apparent:
    
     1) measurements may be more than complex numbers. For instance one could
        produce a grayscale image as a measure while one is calcing convex hull.
        Or a vector containing perimeters (did Gabriel mean the perim of a shape
        in each dim?)
     2) measurements might utilize more than one channel of input data
        (this could be modeled with a function that combines N others. N way
        function combination could be used for adding two images also.
        Investigate.)
     3) Function<long[],T> is not strong enough. At least double[] needed.
        (this leads into an ever bigger investigation of spaces, calibration
        [by bijective functions], sampling within regions, interpolations, etc.)
     4) There are other related measures: convex hull, convex area, feret diam,
        concavity, convexity, solidity, R factor, etc. It would be great if they
        could be defined in terms of each other.
     5) A measure might correlate two values that have different spatial
        locations. I support by a coord translating function. Is this okay?
     6) Measurements are typically in a region of interest
     
     
     One might construct a WeightedMeanFactory with a set of weights. Then
     engine tries to create a WeightedMean from factory when someone asks for
     a WieghtedMean. So you register factories with engine along with all params.
     And then WeightedMean instance obtains existing measures when it can and
     submits factories for those it can't.
     I think what I'm after is the Mean maintains info on how to construct self
     and does so lazily if possible. There is maybe a two step process within
     the Mean's implementation.

     This calculation engine really is how functional code works with lazy
     evaluation. Things are calced once as needed and reused.
     
     Notes:
      - a convex hull is a PointSet (not just the corners but all points)
        (so we need a measure that is a PointSet)
      - a Feret Diameter would measure the span of a PointSet. Usually along a
        x or y axis. But could be arbitrarily placed at any xy angle (ignoring
        multidim cases right now).
      - Convex area would be calculated as the area of a PointSet : a DoubleType
      - A redirected image could be a measure itself: IntensityMeasure which
        takes a color image function and makes a greyscale image from it. In
        Gabriel's example it could be at same calculation level as a center of
        mass computation and thus the pixels are gathered the least number of
        times.
*/

public class MeasureIdeas {

	private interface PointSet {

		int numDimensions();

		boolean includes(double[] pt);

		void min(double[] pt);

		void max(double[] pt);

		double min(int d);

		double max(int d);
	}

	private interface Function<T> {

		T compute(PointSet ps);
	}

	private static interface Measure<T> {

		T getType();

		T getResult();
	}

	private static class Engine {

		public Engine() { // Not good to limit it to 1 func
		}

		<A, B> void defineMeasure(Function<A> func, String measureName,
			Measure<B> measure)
		{}

		void measure(PointSet ps) {}

		<T> T getValue(String measureName, T outputType) {
			return null;
		}

		void forget(String measureName) {} // purge a measure from memory
		// does this purge the one measure or all related unnamed ones?

		void forget() {} // purge all measures from memory
	}

	private static class ConvexHull implements Measure<PointSet> {

		private PointSet inputPoints;
		private boolean calced;

		public ConvexHull() {}

		@Override
		public PointSet getType() {
			return null;
		}

		@Override
		public PointSet getResult() {
			// TODO
			// inputPoints = engine.obtain(PointSet.class);
			if (!calced) throw new IllegalStateException(
				"ConvexHull is not initialized");
			calced = true;
			// TODO calc from input points
			return null;
		}
	}

	private static class FeretDiamX implements Measure<Double> {

		private ConvexHull hull;

		FeretDiamX() {
			this.hull = null;
		}

		@Override
		public Double getType() {
			return 0d;
		}

		@Override
		public Double getResult() {
			PointSet result = hull.getResult();
			return result.max(0) - result.min(0);
		}
	}

	private static class CenterOfMass {

	}

	private static class IntensityGreyscale {

	}

	private static class WeightedAverage {

	}

	public static void main(String[] args) {
		PointSet region = null;
		Function<Double> func = null;
		Engine engine = new Engine();
		ConvexHull convHull = new ConvexHull();
		FeretDiamX feretX = new FeretDiamX();
		engine.defineMeasure(func, "convex hull", convHull);
		engine.defineMeasure(func, "feret x", feretX);
		engine.measure(region);
		PointSet ps = engine.getValue("convex hull", convHull.getType());
		Double d = engine.getValue("feret x", feretX.getType());
	}
}
