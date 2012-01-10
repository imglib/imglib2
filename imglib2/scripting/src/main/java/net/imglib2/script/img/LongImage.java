package net.imglib2.script.img;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.integer.LongType;

/** Create a new n-dimensional image based on an array of float. */
public class LongImage extends ArrayImg<LongType, LongArray>
{
	public LongImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}

	public LongImage(final long[] dim) {
		this(dim, new long[AlgorithmUtil.size(dim)]);
	}

	public LongImage(final long[] dim, final long[] pixels) {
		super(new LongArray(pixels), dim, 1);
		setLinkedType(new LongType(this));
	}
}