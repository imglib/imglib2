package net.imglib2.ops.descriptors.haralick.helpers;

import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;

/**
 * 
 */
public class CoocParameter
{
	public int nrGrayLevels;

	public int distance;

	public MatrixOrientation orientation;

	public int getNrGrayLevels()
	{
		return nrGrayLevels;
	}

	public int getDistance()
	{
		return distance;
	}

	public MatrixOrientation getOrientation()
	{
		return orientation;
	}
}
