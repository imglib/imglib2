package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.algorithm.fft.FourierTransform;
import mpicbg.imglib.algorithm.fft.InverseFourierTransform;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexDoubleType;

public class InverseFFT<T extends RealType<T>> extends Image<T>
{
	public InverseFFT(final FFT<T> fftImage) throws Exception {
		super(process(fftImage, fftImage.fft).getContainer(), fftImage.value.copy());
	}

	public InverseFFT(final Image<ComplexDoubleType> img, final FFT<T> fftImage) throws Exception {
		super(process(img, fftImage.fft).getContainer(), fftImage.value.copy());
	}

	static private final <T extends RealType<T>> Image<T> process(final Image<ComplexDoubleType> fftImage, final FourierTransform<T, ComplexDoubleType> fft) throws Exception {
		final InverseFourierTransform<T, ComplexDoubleType> ifft = new InverseFourierTransform<T, ComplexDoubleType>(fftImage, fft);
		if (!ifft.checkInput() || !ifft.process()) {
			throw new Exception("FFT: failed to process for image " + fftImage.getClass() + " -- " + ifft.getErrorMessage());
		}
		return ifft.getResult();
	}
}