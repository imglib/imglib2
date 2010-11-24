package mpicbg.imglib.scripting.math2.fn;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public abstract class UnaryOperation implements IFunction
{
	protected final IFunction a;

	public UnaryOperation(final Image<? extends RealType<?>> img) {
		this.a = new ImageFunction(img);
	}

	public UnaryOperation(final IFunction fn) {
		this.a = fn;
	}

	public UnaryOperation(final Number val) {
		this.a = new NumberFunction(val);
	}

	@Override
	public final void findImages(final Set<Image<?>> images) {
		a.findImages(images);
	}
}