package mpicbg.imglib.function.operations.op;

//import java.util.HashMap;
import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public abstract class AIN< A extends NumericType<A> > extends AN<A> implements Op<A> {

	protected final Cursor<A> c;
	protected final A num;
	protected final Operation<A> op;

	public AIN(final Image<A> img, final Number val, final Operation<A> op) {
		this.c = img.createCursor();
		this.op = op;
		this.num = (A) asType(val);
	}

	@Override
	public final void fwd() {
		c.fwd();
	}

	@Override
	public void getImages(final Set<Image<A>> images) {
		images.add(c.getImage());
	}
}