package net.imglib2.streamifiedview;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.StreamifiedView;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

public class MyCustomStreamifiedView implements StreamifiedView<DoubleType> {

	MyTestImg img;

	public MyCustomStreamifiedView(final MyTestImg img) {
		this.img = img;
	}

	@Override
	public RandomAccessibleInterval<DoubleType> getGenericRai() {
		return img;
	}

	@Override
	public StreamifiedView<DoubleType> expandValue(final DoubleType value, long... border) {
		// demonstration how adding custom functionality for only a subset of views is possible
		System.out.println("Setting extension value to pi.");
		final DoubleType pi = new DoubleType(3.14);
		return Views.expandValue(getGenericRai(), pi, border);
	}
}
