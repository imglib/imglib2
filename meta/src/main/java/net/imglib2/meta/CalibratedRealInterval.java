package net.imglib2.meta;

import net.imglib2.Positionable;


// TODO - what other interface should this mix in besides TypedRealInterval

public interface CalibratedRealInterval<A extends CalibratedAxis> extends
	TypedRealInterval<A>
{

	String unit(int d);

	void setUnit(String unit, int d);

	double calibration(int d);

	void setCalibration(double v, int d);

	// The following are TEMP for compile fixing

	long dimension(int d);

	void dimensions(long[] dest);

	AxisType[] getAxes();

	long[] getDims();

	String getName();

	void setName(String name);

	boolean isDiscrete();

	long min(int d);

	void min(long[] dest);

	void min(Positionable dest);

	long max(int d);

	void max(long[] dest);

	void max(Positionable dest);
}
