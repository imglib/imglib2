package net.imglib2.streamifiedview;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.StreamifiedView;
import net.imglib2.view.Views;

public class RaiWrapper<T> implements StreamifiedView<T> {

	private final RandomAccessibleInterval<T> rai;

	public RaiWrapper(final RandomAccessibleInterval<T> rai) {
		this.rai = rai;
	}

	@Override
	public StreamifiedView<T> expandValue(final T value, long... border) {
		return new RaiWrapper<>(Views.expandValue(rai, value, border));
	}

	@Override
	public StreamifiedView<T> permute(final int from, final int to) {
		return new RaiWrapper<>(Views.permute(rai, from, to));
	}

	@Override
	public StreamifiedView<T> translate(long... translation) {
		return new RaiWrapper<>(Views.translate(rai, translation));
	}
}
