package net.imglib2.script.filter;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.filter.fn.AbstractFilterFn;
import net.imglib2.script.math.Divide;
import net.imglib2.script.math.Exp;
import net.imglib2.script.math.Log;
import net.imglib2.script.math.Max;
import net.imglib2.script.math.Min;
import net.imglib2.script.math.Multiply;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.ImageFunction;
import net.imglib2.type.numeric.RealType;

/**
 * Return a function that, when evaluated, computes the gamma of the given image.
 * If 'i' was the pixel value, and the image was 8-bit, then this function would do: 
      double v = Math.exp(Math.log(i/255.0) * gamma) * 255.0); 
      if (v < 0) v = 0; 
      if (v >255) v = 255;
 * 
 * @author Albert Cardona
 *
 */
public class Gamma extends AbstractFilterFn
{
	protected final IFunction fn;
	protected final Number gamma, min, max;

	public Gamma(final IFunction fn, final Number gamma, final Number min, final Number max) {
		super(new Min(max,
				      new Max(min,
				              new Multiply(new Exp(new Multiply(gamma,
				                                                new Log(new Divide(fn, max)))),
				                           max))));
		this.fn = fn;
		this.gamma = gamma;
		this.min = min;
		this.max = max;
	}

	@SuppressWarnings("boxing")
	public <R extends RealType<R>> Gamma(final IterableRealInterval<R> img, final Number val) {
		this(new ImageFunction<R>(img), val, 0, img.firstElement().getMaxValue());
	}

	@Override
	public IFunction duplicate() throws Exception {
		return new Gamma(fn.duplicate(), gamma, min, max);
	}
}
