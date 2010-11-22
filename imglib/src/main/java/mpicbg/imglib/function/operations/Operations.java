package mpicbg.imglib.function.operations;

import java.util.HashSet;
import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class Operations {

	static public final <A extends RealType<A>> Image<A> apply(final Operation<A> op)
	{
		// 1 - Collect all images involved in the operation
		final Set<Image<A>> images = new HashSet<Image<A>>();
		op.getImages(images);

		// 2 - Check that they are all compatible: same type, same dimensions, same storage strategy
		// TODO

		// 3 - Operate on an empty result image
		final Image<A> first = images.iterator().next();
		op.init(first.createType());
		final Image<A> result = first.createNewImage();
		final Cursor<A> c = result.createCursor();
		while (c.hasNext()) {
			// Advance all cursors
			op.fwd();
			c.fwd();
			op.compute(c.getType()); // TODO: ideally, the computations are done in float,
									 // and then set back into whatever the type is.
		}

		// 4 - Cleanup cursors
		for (final Image<A> img : images) {
			img.removeAllCursors();
		}
		result.removeAllCursors();

		return result;
	}
}
