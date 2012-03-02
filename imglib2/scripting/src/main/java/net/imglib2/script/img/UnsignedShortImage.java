package net.imglib2.script.img;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/** Create a new n-dimensional image based on an array of short. */
public class UnsignedShortImage extends ArrayImg<UnsignedShortType, ShortArray>
{
	public UnsignedShortImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}
	
	public UnsignedShortImage(final long[] dim) {
		this(dim, new short[AlgorithmUtil.size(dim)]);
	}
	
	public UnsignedShortImage(final long[] dim, final short[] pixels) {
		super(new ShortArray(pixels), dim, 1);
		setLinkedType(new UnsignedShortType(this));
	}
}
