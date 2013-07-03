package net.imglib2.ops.measure.orig;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.real.RealEquationFunction;
import net.imglib2.ops.measure.orig.measurements.ElementCount;
import net.imglib2.ops.measure.orig.measurements.SampleKurtosisExcess;
import net.imglib2.ops.measure.orig.measurements.SampleMean;
import net.imglib2.ops.measure.orig.measurements.SampleStdDev;
import net.imglib2.ops.measure.orig.measurements.SampleVariance;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.real.DoubleType;


public class TestIdeas {

	public static void main(String[] args) {
		/* See here that any Img can be sampled as a Function
		Img<UnsignedByteType> img = null;
		Function<long[],DoubleType> func =
			new RealImageFunction(img, new DoubleType());
		 */
		Function<long[],DoubleType> func =
				new RealEquationFunction<DoubleType>(
						"[x,y], 2*x + 3*y + 7", new DoubleType(), null);
		PointSet region = new HyperVolumePointSet(new long[]{25,25});
		NewMeasurementSet measures = new NewMeasurementSet();
		// add measures - order does not matter - dependencies resolved as needed
		measures.addMeasure("mean", SampleMean.class);
		measures.addMeasure("stdev", SampleStdDev.class);
		measures.addMeasure("count", ElementCount.class);
		measures.addMeasure("kurt ex", SampleKurtosisExcess.class);
		measures.addMeasure("var", SampleVariance.class);
		measures.doMeasurements(func, region);
		System.out.println("count    = " + measures.getValue("count"));
		System.out.println("mean     = " + measures.getValue("mean"));
		System.out.println("stdev    = " + measures.getValue("stdev"));
		System.out.println("variance = " + measures.getValue("var"));
		System.out.println("kurt ex  = " + measures.getValue("kurt ex"));
	}
}
