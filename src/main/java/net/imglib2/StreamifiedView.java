package net.imglib2;

import net.imglib2.view.Views;

/**
 * First attempt at an interface which basically copies java's stream syntax to imglib2-views.
 *
 * @author Michael Innerberger
 * @see net.imglib2.view.Views
 */
public interface StreamifiedView<T> {
	RandomAccessibleInterval<T> getGenericRai();

	default StreamifiedView<T> expandValue(final T value, long... border) {
		return Views.expandValue(getGenericRai(), value, border);
	}

	default StreamifiedView<T> permute(final int from, final int to) {
		return Views.permute(getGenericRai(), from, to);
	}

	default StreamifiedView<T> translate(long... translation) {
		return Views.translate(getGenericRai(), translation);
	}
}
