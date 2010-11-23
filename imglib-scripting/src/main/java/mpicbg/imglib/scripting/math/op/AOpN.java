package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public abstract class AOpN< R extends RealType<R> > extends AN<R> implements Op<R> {

	protected final Operation<R> other, op;
	protected final RealType<?> num;
	protected R tmp;

	public AOpN(final Operation<R> other, final Number val, final Operation<R> op) {
		this.other = other;
		this.op = op;
		this.num = asType(val);
	}

	@Override
	public final void fwd() {
		other.fwd();
	}

	@Override
	public void getImages(final Set<Image<?>> images) {
		other.getImages(images);
	}

	@Override
	public void init(final R ref) {
		tmp = ref.createVariable();
		other.init(ref);
	}
}