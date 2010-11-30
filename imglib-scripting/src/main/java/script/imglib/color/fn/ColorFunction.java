package script.imglib.color.fn;

import java.util.Collection;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.NumberFunction;
import script.imglib.math.fn.Util;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;

public abstract class ColorFunction implements IFunction {

	protected static final NumberFunction empty = new NumberFunction(0.0d);

	public static final class Channel implements IFunction {
		final Cursor<? extends RealType<?>> c;
		final int shift;

		/** In RGBALegacyType, A=4, R=3, G=2, B=1, or H=3, S=2, B=1 */
		public Channel(final Image<? extends RealType<?>> img, final int channel) {
			this.c = img.createCursor();
			this.shift = (channel-1) * 8;
		}

		@Override
		public final IFunction duplicate() throws Exception {
			return new Channel(c.getImage(), (shift / 8) + 1);
		}

		@Override
		public final double eval() {
			c.fwd();
			return (((int)c.getType().getRealDouble()) >> shift) & 0xff;
		}

		@Override
		public final void findCursors(final Collection<Cursor<?>> cursors) {
			cursors.add(c);
		}
	}

	static protected IFunction wrap(final Object ob) throws Exception {
		if (null == ob) return empty;
		return Util.wrap(ob);
	}

	public Image<RGBALegacyType> asImage() throws Exception {
		return asImage(Runtime.getRuntime().availableProcessors());
	}
	
	public Image<RGBALegacyType> asImage(final int numThreads) throws Exception {
		return Compute.apply(this, new RGBALegacyType(), numThreads);
	}
}