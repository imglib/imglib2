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
		return Views.expandValue(rai, value, border).view();
	}

	@Override
	public StreamifiedView<T> permute(final int from, final int to) {
		return Views.permute(rai, from, to).view();
	}

	@Override
	public StreamifiedView<T> translate(long... translation) {
		return Views.translate(rai, translation).view();
	}
}
