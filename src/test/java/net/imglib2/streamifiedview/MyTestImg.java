package net.imglib2.streamifiedview;

import net.imglib2.StreamifiedView;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.DataAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Fraction;

public class MyTestImg extends ArrayImg<DoubleType, DoubleArray> {
	private MyTestImg(DataAccess data, long[] dim, Fraction entitiesPerPixel) {
		super(data, dim, entitiesPerPixel);
	}

	public static MyTestImg create(final double[] array, final long... dim) {
		return new MyTestImg(new DoubleArray(array), dim, new Fraction());
	}

	public StreamifiedView<DoubleType> view() {
		return new MyCustomStreamifiedView(this);
	}
}
