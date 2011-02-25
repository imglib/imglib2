package script.imglib.color.fn;

import java.util.Collection;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.ImageComputation;
import script.imglib.math.fn.NumberFunction;
import script.imglib.math.fn.Util;

import mpicbg.imglib.img.ImgCursor;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.RealType;

public abstract class ColorFunction implements IFunction, ImageComputation<ARGBType> {

	protected static final NumberFunction empty = new NumberFunction(0.0d);

	public static final class Channel implements IFunction {
		final ImgCursor<? extends RealType<?>> c;
		final int shift;

		/** In RGBALegacyType, A=4, R=3, G=2, B=1, or H=3, S=2, B=1 */
		public Channel(final Img<? extends RealType<?>> img, final int channel) {
			this.c = img.cursor();
			this.shift = (channel-1) * 8;
		}

		@Override
		public final IFunction duplicate() throws Exception {
			return new Channel(c.getImg(), (shift / 8) + 1);
		}

		@Override
		public final double eval() {
			c.fwd();
			return (((int)c.get().getRealDouble()) >> shift) & 0xff;
		}

		@Override
		public final void findCursors(final Collection<ImgCursor<?>> cursors) {
			cursors.add(c);
		}
	}

	static protected IFunction wrap(final Object ob) throws Exception {
		if (null == ob) return empty;
		return Util.wrap(ob);
	}

	@Override
	public Img<ARGBType> asImage() throws Exception {
		return asImage(Runtime.getRuntime().availableProcessors());
	}

	@Override
	public Img<ARGBType> asImage(final int numThreads) throws Exception {
		return Compute.apply(this, new ARGBType(), numThreads);
	}
}
