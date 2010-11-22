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

	static public final <A extends RealType<A>> Image<FloatType> apply(final Operation<A> op)
	{
		// 1 - Collect all images involved in the operation
		final Set<Image<A>> images = new HashSet<Image<A>>();
		op.getImages(images);
		// debug:
		for (Image<A> im : images) {
			System.out.println("image type: " + im.createType().getClass().getSimpleName());
		}

		// 2 - Check that they are all compatible: same dimensions, same storage strategy
		// TODO

		// 3 - Initialize all operations with a FloatType
		final A f = (A) (RealType<?>) new FloatType();
		op.init(f);

		// 3 - Operate on an empty result image
		final Image<A> first = images.iterator().next();
		final ImageFactory<FloatType> factory = new ImageFactory<FloatType>( new FloatType(), new ArrayContainerFactory());
		final Image<FloatType> result = factory.createImage(first.getDimensions(), "result");
		final Cursor<FloatType> c = result.createCursor();

		int count = 0;

		while (c.hasNext()) {
			// Advance all cursors
			op.fwd();
			c.fwd();
			op.compute(f);
			
			count++;
			if (0 == count % 1000) System.out.println("result at " + count + ":" + f.getRealFloat());
			
			c.getType().setReal(f.getRealFloat());
			
			if (0 == count % 1000) System.out.println("   received as " + c.getType().getRealFloat());
		}

		// 4 - Cleanup cursors
		for (final Image<A> img : images) {
			img.removeAllCursors();
		}
		result.removeAllCursors();

		return result;
	}
}