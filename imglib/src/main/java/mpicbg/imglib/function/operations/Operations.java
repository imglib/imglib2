package mpicbg.imglib.function.operations;

import java.util.HashSet;
import java.util.Set;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class Operations {

	static public final <R extends RealType<R>> Image<R> apply(final Operation<R> op, final R output)
	{
		// 1 - Collect all images involved in the operation
		final Set<Image<? extends RealType<?>>> images = new HashSet<Image<? extends RealType<?>>>();
		op.getImages(images);
		// debug:
		for (Image<? extends RealType<?>> im : images) {
			System.out.println("image type: " + im.createType().getClass().getSimpleName());
		}

		// 2 - Check that they are all compatible: same dimensions, same storage strategy
		// TODO

		op.init(output);

		// 3 - Operate on an empty result image
		final Image<?> first = images.iterator().next();
		final ImageFactory<R> factory = new ImageFactory<R>(output, first.getContainerFactory());
		final Image<R> result = factory.createImage(first.getDimensions(), "result");
		final Cursor<R> c = result.createCursor();

		while (c.hasNext()) {
			// Advance all cursors
			op.fwd();
			c.fwd();
			op.compute(c.getType());
		}

		// 4 - Cleanup cursors
		for (final Image<?> img : images) {
			img.removeAllCursors();
		}
		result.removeAllCursors();

		return result;
	}

	static public final Image<FloatType> inFloats(final Operation<FloatType> op)
	{
		return apply(op, new FloatType());
	}
}