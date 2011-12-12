package net.imglib2.script.img;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/** Create a new n-dimensional image based on an array of byte. */
public class UnsignedByteImage extends ArrayImg<UnsignedByteType, ByteArray>
{
	public UnsignedByteImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}

	public UnsignedByteImage(final long[] dim) {
		this(dim, new byte[AlgorithmUtil.size(dim)]);
	}

	public UnsignedByteImage(final long[] dim, final byte[] pixels) {
		super(new ByteArray(pixels), dim, 1);
		setLinkedType(new UnsignedByteType(this));
	}
}

