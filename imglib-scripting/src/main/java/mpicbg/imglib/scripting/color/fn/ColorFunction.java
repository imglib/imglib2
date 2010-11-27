package mpicbg.imglib.scripting.color.fn;

import java.util.Collection;
import java.util.HashSet;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.NumberFunction;
import mpicbg.imglib.scripting.math.fn.Util;
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
		ContainerFactory containerFactory = null;
		int[] dimensions = null;
		final HashSet<Cursor<?>> cs = new HashSet<Cursor<?>>();
		findCursors(cs);
		for (final Cursor<?> c : cs) {
			containerFactory = c.getImage().getContainer().getFactory();
			dimensions = c.getImage().getDimensions();
			break;
		}
		if (null == dimensions) {
			throw new Exception(getClass().getSimpleName() + ".asImage(): could not determine image dimensions.");
		}
		if (null == containerFactory) {
			containerFactory = new ArrayContainerFactory();
		}
		final ImageFactory<RGBALegacyType> imageFactory = new ImageFactory<RGBALegacyType>(new RGBALegacyType(), containerFactory);
		final Image<RGBALegacyType> img = imageFactory.createImage(dimensions);
		// Place all cursors at the start
		for (final Cursor<?> c : cs) {
			c.reset();
		}
		for (final RGBALegacyType value : img) {
			value.set( (int) this.eval());
		}
		return img;
	}
}