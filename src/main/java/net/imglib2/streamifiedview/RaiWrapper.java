package net.imglib2.streamifiedview;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.StreamifiedView;

public class RaiWrapper<T> implements StreamifiedView<T> {

	private final RandomAccessibleInterval<T> rai;

	public RaiWrapper(final RandomAccessibleInterval<T> rai) {
		this.rai = rai;
	}

	@Override
	public RandomAccessibleInterval<T> getGenericRai() {
		return rai;
	}
}
