package net.imglib2.script.img;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.ARGBType;

/** Create a new n-dimensional image based on an array of byte. */
public class ARGBImage extends ArrayImg<ARGBType, IntArray>
{
	public ARGBImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}

	public ARGBImage(final long[] dim) {
		this(dim, new int[AlgorithmUtil.size(dim)]);
	}

	public ARGBImage(final long[] dim, final int[] pixels) {
		super(new IntArray(pixels), dim, 1);
		setLinkedType(new ARGBType(this));
	}
}

