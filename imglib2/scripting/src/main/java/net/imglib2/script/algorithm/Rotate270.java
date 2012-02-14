package net.imglib2.script.algorithm;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.GenericRotate;
import net.imglib2.type.numeric.RealType;

public class Rotate270<R extends RealType<R>> extends GenericRotate<R>
{
	public Rotate270(final Img<R> img) {
		super(img, GenericRotate.Mode.R270);
	}
}