package net.imglib2.script.filter;

import net.imglib2.script.filter.fn.AbstractFilterFn;
import net.imglib2.script.math.Divide;
import net.imglib2.script.math.Multiply;
import net.imglib2.script.math.Subtract;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.Util;

/**
 * 
 * @author Albert Cardona
 *
 */
public class CorrectFlatField extends AbstractFilterFn
{
	protected final Object img, brightfield, darkfield;
	
	/**
	 * 
	 * @param img An IFunction, IterableRealInterval or Number.
	 * @param brightfield An IFunction, IterableRealInterval or Number.
	 * @param darkfield An IFunction, IterableRealInterval or Number.
	 * @throws Exception
	 */
	public CorrectFlatField(final Object img, final Object brightfield, final Object darkfield) throws Exception {
		super(new Multiply(new Divide(new Subtract(Util.wrap(img), Util.wrap(brightfield)),
		                              new Subtract(Util.wrap(brightfield), Util.wrap(darkfield)))));
		this.img = img;
		this.brightfield = brightfield;
		this.darkfield = darkfield;
	}
	
	@SuppressWarnings("boxing")
	public CorrectFlatField(final Object img, final Object brightfield) throws Exception {
		this(img, brightfield, 0);
	}

	@Override
	public IFunction duplicate() throws Exception {
		return new CorrectFlatField(Util.wrap(this.img).duplicate(),
		                            Util.wrap(this.brightfield).duplicate(),
		                            Util.wrap(this.darkfield).duplicate());
	}
}
