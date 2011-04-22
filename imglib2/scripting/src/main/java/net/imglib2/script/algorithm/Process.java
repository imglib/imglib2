package net.imglib2.script.algorithm;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;

import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/** A convenience wrapper for just about any ImgLib {@link Algorithm}.
 *  When combined with other {@link IFunction} instances, the {@code eval()}
 *  method returns the resulting, processed pixel values--either from the result
 *  image (from an {@link OutputAlgorithm} instance) or from the origina image
 *  (in the case of a {@link Algorithm} instance that is not an {@link OutputAlgorithm}).
 *  
 *  A few classes extend Process for simplified usage. See for example
 *  {@link Gauss}, {@link Dither}, {@link BandpassFilter}, {@Normalize}.
 *  */
public class Process implements IFunction {

	private final Cursor<? extends RealType<?>> c;
	private final Img<? extends RealType<?>> img;

	/** Execute the given {@link OutputAlgorithm} and prepare a cursor to deliver
	 *  its pixel values one by one in successive calls to {@code eval()}. */
	public Process(final OutputAlgorithm<Img<? extends RealType<?>>> algorithm) throws Exception {
		execute(algorithm);
		this.img = algorithm.getResult();
		this.c = this.img.cursor();
	}

	/** Same as {@code this(algorithmClass, Process.asImage(fn), parameters);} */
	public Process(final Class<Algorithm> algorithmClass, final IFunction fn, final Object... parameters) throws Exception {
		this(algorithmClass, Compute.inDoubles(fn), parameters);
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
	public Process(final Class<Algorithm> algorithmClass, final Img<? extends RealType<?>> img, final Object... parameters) throws Exception {
		final Class<?>[] cargs = new Class[1 + parameters.length];
		final Object[] args = new Object[cargs.length];
		cargs[0] = Img.class;
		args[0] = img;
		for (int i=1; i<cargs.length; i++) {
			cargs[i] = parameters[i-1].getClass();
			args[i] = parameters[i-1];
		}
		final Algorithm a = algorithmClass.getConstructor(cargs).newInstance(args);
		execute(a);
		
		Object result = null;
		if (a instanceof OutputAlgorithm<?>) {
			result = ((OutputAlgorithm<?>)a).getResult();
		}
		if (result instanceof Img<?>) {
			this.img = ((Img<? extends RealType<?>>)result);
			this.c = this.img.cursor();
		} else {
			// The source image
			this.img = img;
			this.c = img.cursor();
		}
	}

	private Process(final Img<? extends RealType<?>> img) {
		this.img = img;
		this.c = this.img.cursor();
	}
	
	/** Evaluate the @param fn for every pixel and return a new {@link Image} with the result.
	 *  This method enables {@link Algorithm} instances to interact with {@link IFunction},
	 *  by creating an intermediate (and temporary) {@link DoubleType} {@link Image}. */

	private final void execute(final Algorithm algorithm) throws Exception {
		if (!algorithm.checkInput() || !algorithm.process()) {
			throw new Exception("Algorithm " + algorithm.getClass().getSimpleName() + " failed: " + algorithm.getErrorMessage());
		}
	}

	public Img<? extends RealType<?>> getResult() {
		return img;
	}

	@Override
	public final IFunction duplicate() throws Exception {
		return new Process(img);
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public void findImgs(Collection<Img<?>> imgs) {
		imgs.add(img);
	}
}