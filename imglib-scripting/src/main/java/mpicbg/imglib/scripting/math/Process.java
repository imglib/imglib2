package mpicbg.imglib.scripting.math;

import java.util.Collection;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;

/** A convenience wrapper for just about any ImgLib {@link Algorithm}.
 *  When combined with other {@link IFunction} instances, the {@code eval()}
 *  method returns the resulting, processed pixel values--either from the result
 *  image (from an {@link OutputAlgorithm} instance) or from the origina image
 *  (in the case of a {@link Algorithm} instance that is not an {@link OutputAlgorithm}). */
public final class Process implements IFunction {

	private final Cursor<? extends RealType<?>> c;

	/** Execute the given {@link OutputAlgorithm} and prepare a cursor to deliver
	 *  its pixel values one by one in successive calls to {@code eval()}. */
	public Process(final OutputAlgorithm<? extends RealType<?>> algorithm) throws Exception {
		execute(algorithm);
		this.c = algorithm.getResult().createCursor();
	}

	/** Initialize and execute the given {@link Algorithm} and prepare a cursor
	 *  to deliver its pixel values one by one in successive calls to {@code eval()}.
	 *  If the @param algorithmClass implements {@link OutputAlgorithm}, then
	 *  this IFunction will iterate over the image obtained from {@code getResult()}.
	 *  Otherwise, this IFunction iterates over the modified input image.
	 *  
	 *  The {@link Algorithm} is constructed by reflection and assumes that the
	 *  constructor accepts one {@link Image} as first argument and then the rest of
	 *  the argument in the same order as given. Otherwise, an {@link Exception} will
	 *  be thrown. */
	@SuppressWarnings("unchecked")
	public Process(final Class<Algorithm> algorithmClass, final Image<? extends RealType<?>> img, final Object... parameters) throws Exception {
		final Class<?>[] cargs = new Class[1 + parameters.length];
		final Object[] args = new Object[cargs.length];
		cargs[0] = Image.class;
		args[0] = img;
		for (int i=1; i<parameters.length; i++) {
			cargs[i] = parameters[i].getClass();
			args[i] = parameters[i];
		}
		final Algorithm a = algorithmClass.getConstructor(cargs).newInstance(args);
		execute(a);
		
		this.c = (a instanceof OutputAlgorithm<?> ?
				((OutputAlgorithm<? extends RealType<?>>)a).getResult() : img).createCursor();
	}

	private final void execute(final Algorithm algorithm) throws Exception {
		if (!algorithm.checkInput() && !algorithm.process()) {
			throw new Exception("Algorithm " + algorithm.getClass().getSimpleName() + " failed: " + algorithm.getErrorMessage());
		}
	}

	private Process(final Cursor<? extends RealType<?>> c) {
		this.c = c;
	}

	public Image<? extends RealType<?>> getResult() {
		return c.getImage();
	}

	@Override
	public final IFunction duplicate() throws Exception {
		return new Process(c.getImage().createCursor());
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.getType().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		cursors.add(c);
	}
}