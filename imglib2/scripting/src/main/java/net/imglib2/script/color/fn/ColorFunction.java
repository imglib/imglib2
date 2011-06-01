package net.imglib2.script.color.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.ImageComputation;
import net.imglib2.script.math.fn.NumberFunction;
import net.imglib2.script.math.fn.Util;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

public abstract class ColorFunction implements IFunction, ImageComputation<ARGBType> {

	protected static final NumberFunction empty = new NumberFunction(0.0d);

	public static final class Channel implements IFunction {
		final RealCursor<? extends RealType<?>> c;
		final IterableRealInterval<? extends RealType<?>> img;
		final int shift;

		/** In RGBALegacyType, A=4, R=3, G=2, B=1, or H=3, S=2, B=1 */
		public Channel(final IterableRealInterval<? extends RealType<?>> img, final int channel) {
			this.img = img;
			this.c = img.cursor();
			this.shift = (channel-1) * 8;
		}

		@Override
		public final IFunction duplicate() throws Exception {
			return new Channel(img, (shift / 8) + 1);
		}

		@Override
		public final double eval() {
			c.fwd();
			return (((int)c.get().getRealDouble()) >> shift) & 0xff;
		}

		@Override
		public final void findCursors(final Collection<RealCursor<?>> cursors) {
			cursors.add(c);
		}
		@Override
		public final void findImgs(final Collection<IterableRealInterval<?>> iris) {
			iris.add(img);
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
