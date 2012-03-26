package net.imglib2.script.img;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.real.DoubleType;

/** Create a new n-dimensional image based on an array of float. */
public class DoubleImage extends ArrayImg<DoubleType, DoubleArray>
{
	public DoubleImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}

	public DoubleImage(final long[] dim) {
		this(dim, new double[AlgorithmUtil.size(dim)]);
	}

	public DoubleImage(final long[] dim, final double[] pixels) {
		super(new DoubleArray(pixels), dim, 1);
		setLinkedType(new DoubleType(this));
	}
}