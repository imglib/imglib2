package net.imglib2;

/**
 * First attempt at an interface which basically copies java's stream syntax to imglib2-views.
 *
 * @author Michael Innerberger
 * @see net.imglib2.view.Views
 */
public interface StreamifiedView<T> {
	StreamifiedView<T> expandValue(T value, long... border);
	StreamifiedView<T> permute(int from, int to);
	StreamifiedView<T> translate(long... translation);
}
