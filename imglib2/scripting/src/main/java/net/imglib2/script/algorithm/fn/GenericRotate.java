package net.imglib2.script.algorithm.fn;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class GenericRotate<R extends RealType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	
	static public enum Mode {R90, R180, R270}

	public GenericRotate(final Img<R> img, final Mode mode) {
		super(process(img, mode));
	}

	static private final <R extends RealType<R>> IntervalView<R> process(final Img<R> img, final Mode mode) {
		IntervalView<R> iv;
		if (Mode.R90 == mode) {
			iv = Views.rotate(img, 0, 1);
		} else if (Mode.R270 == mode) {
			iv = Views.rotate(img, 1, 0);
		} else if (Mode.R180 == mode) {
			iv = Views.rotate(Views.rotate(img, 0, 1), 0, 1);
		} else {
			throw new IllegalArgumentException("Invalid Mode: " + mode);
		}
		return iv;
	}
}
