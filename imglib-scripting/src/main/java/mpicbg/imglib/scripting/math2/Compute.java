package mpicbg.imglib.scripting.math2;

import java.util.HashSet;
import java.util.Set;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class Compute {

	static public final <R extends RealType<R>> Image<R> apply(final IFunction op, final R output) throws Exception
	{
		// 1 - Collect all images involved in the operation
		final Set<Image<?>> images = new HashSet<Image<?>>();
		op.findImages(images);
		/*
		// debug:
		System.out.println("number of images: " + images.size());
		for (Image<? extends RealType<?>> im : images) {
			System.out.println("image type: " + im.createType().getClass().getSimpleName());
		}
		*/

		// 2 - Check that they are all compatible: same dimensions, same container type
		if (images.size() > 0) {
			final Image<?> first = images.iterator().next();
			final int[] dim = first.getDimensions();
			for (final Image<?> img : images) {
				int[] d = img.getDimensions();
				if (d.length != dim.length) throw new Exception("Images have different number of dimensions!");
				for (int i=0; i<dim.length; i++) {
					if (d[i] != dim[i]) {
						throw new Exception("Images have different dimensions!");
					}
				}
				if (img.getContainerFactory().getClass() != first.getContainerFactory().getClass()) {
					throw new Exception("Images are of different container types!");
				}
			}

			// 3 - Operate on an empty result image
			final ImageFactory<R> factory = new ImageFactory<R>(output, first.getContainerFactory());
			final Image<R> result = factory.createImage(first.getDimensions(), "result");
			
			final Cursor<R> c = result.createCursor();
			while (c.hasNext()) {
				c.fwd();
				c.getType().setReal(op.eval());
			}

			// 4 - Cleanup cursors
			for (final Image<?> img : images) {
				img.removeAllCursors();
			}
			result.removeAllCursors();

			return result;
		} else {
			// Operations that only involve numbers (for consistency)
			final ImageFactory<R> factory = new ImageFactory<R>(output, new ArrayContainerFactory());
			final Image<R> result = factory.createImage(new int[]{1}, "result");
			final Cursor<R> c = result.createCursor();
			c.fwd();
			c.getType().setReal(op.eval());
			return result;
		}
	}

	static public final Image<FloatType> inFloats(final IFunction op) throws Exception
	{
		return apply(op, new FloatType());
	}

	static public final Image<DoubleType> inDoubles(final IFunction op) throws Exception
	{
		return apply(op, new DoubleType());
	}
}