package net.imglib2.meta;



// TODO - what other interface should this mix in besides TypedRealInterval

public interface CalibratedRealInterval<A extends CalibratedAxis> extends
	TypedRealInterval<A>
{

	// These babies are necessary

	String unit(int d);

	void setUnit(String unit, int d);

	double calibration(int d);

	void setCalibration(double v, int d);

	// The following are TEMP for compile fixing

	AxisType[] getAxes();

	long[] getDims();

	boolean isDiscrete();

}
