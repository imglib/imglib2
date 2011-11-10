package net.imglib2.script.edit;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.real.FloatType;

/** Create a new n-dimensional image based on an array of float. */
public class FloatImage extends ArrayImg<FloatType, FloatArray>
{
	public FloatImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}

	public FloatImage(final long[] dim) {
		this(dim, new float[AlgorithmUtil.size(dim)]);
	}

	public FloatImage(final long[] dim, final float[] pixels) {
		super(new FloatArray(pixels), dim, 1);
		setLinkedType(new FloatType(this));
	}
}
