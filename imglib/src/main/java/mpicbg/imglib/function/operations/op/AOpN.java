package mpicbg.imglib.function.operations.op;

//import java.util.HashMap;
import java.util.Set;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public abstract class AOpN< A extends NumericType<A> > extends AN<A> implements Op<A> {

	protected final Operation<A> other, op;
	protected final A num;
	protected A tmp;

	public AOpN(final Operation<A> other, final Number val, final Operation<A> op) {
		this.other = other;
		this.op = op;
		this.num = (A) asType(val);
	}

	@Override
	public final void fwd() {
		other.fwd();
	}

	@Override
	public void getImages(final Set<Image<A>> images) {
		other.getImages(images);
	}

	@Override
	public void init(final A ref) {
		tmp = ref.createVariable();
		other.init(ref);
	}
}
