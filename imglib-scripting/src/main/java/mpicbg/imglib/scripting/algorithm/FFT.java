package mpicbg.imglib.scripting.algorithm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mpicbg.imglib.algorithm.fft.FourierTransform;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexDoubleType;

public class FFT<T extends RealType<T>> extends Image<ComplexDoubleType>
{
	static private Map<Thread,FourierTransform<?, ComplexDoubleType>> m =
		Collections.synchronizedMap(new HashMap<Thread,FourierTransform<?, ComplexDoubleType>>());

	final FourierTransform<T, ComplexDoubleType> fft;
	final T value;

	@SuppressWarnings("unchecked")
	public FFT(final Image<T> img) throws Exception {
		super(process(img).getContainer(), new ComplexDoubleType(), "FFT");
		fft = (FourierTransform<T, ComplexDoubleType>) m.remove(Thread.currentThread());
		value = img.createType();
	}

	@SuppressWarnings("unchecked")
	public FFT(final IFunction fn) throws Exception {
		this((Image)Compute.inDoubles(fn));
	}

	static synchronized private final <T extends RealType<T>> Image<ComplexDoubleType> process(final Image<T> img) throws Exception {
		final FourierTransform<T, ComplexDoubleType> fft = new FourierTransform<T, ComplexDoubleType>(img, new ComplexDoubleType());
		if (!fft.checkInput() || !fft.process()) {
			throw new Exception("FFT: failed to process for image " + img.getClass() + " -- " + fft.getErrorMessage());
		}
		m.put(Thread.currentThread(), fft);
		return fft.getResult();
	}
}