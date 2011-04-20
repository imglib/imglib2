package script.imglib.algorithm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;

import mpicbg.imglib.algorithm.fft.FourierTransform;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexDoubleType;

public class FFT<T extends RealType<T>> extends ImgProxy<ComplexDoubleType>
{
	static private Map<Thread,FourierTransform<?, ComplexDoubleType>> m =
		Collections.synchronizedMap(new HashMap<Thread,FourierTransform<?, ComplexDoubleType>>());

	final FourierTransform<T, ComplexDoubleType> fft;
	final T value;

	@SuppressWarnings("unchecked")
	public FFT(final Img<T> img) throws Exception {
		super(process(img));
		fft = (FourierTransform<T, ComplexDoubleType>) m.remove(Thread.currentThread());
		value = img.firstElement().createVariable();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FFT(final IFunction fn) throws Exception {
		this((Img)Compute.inDoubles(fn));
	}

	static synchronized private final <T extends RealType<T>> Img<ComplexDoubleType> process(final Img<T> img) throws Exception {
		final FourierTransform<T, ComplexDoubleType> fft = new FourierTransform<T, ComplexDoubleType>(img, new ComplexDoubleType());
		if (!fft.checkInput() || !fft.process()) {
			throw new Exception("FFT: failed to process for image " + img.getClass() + " -- " + fft.getErrorMessage());
		}
		m.put(Thread.currentThread(), fft);
		return fft.getResult();
	}
}
