package net.imglib2.script.algorithm;

import net.imglib2.algorithm.fft.FourierTransform;
import net.imglib2.algorithm.fft.InverseFourierTransform;
import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

public class InverseFFT<T extends RealType<T>> extends ImgProxy<T>
{
	public InverseFFT(final FFT<T> fftImage) throws Exception {
		super(process(fftImage, fftImage.fft));
	}

	public InverseFFT(final Img<ComplexDoubleType> img, final FFT<T> fftImage) throws Exception {
		super(process(img, fftImage.fft));
	}

	static private final <T extends RealType<T>> Img<T> process(final Img<ComplexDoubleType> fftImage, final FourierTransform<T, ComplexDoubleType> fft) throws Exception {
		final InverseFourierTransform<T, ComplexDoubleType> ifft = new InverseFourierTransform<T, ComplexDoubleType>(fftImage, fft);
		if (!ifft.checkInput() || !ifft.process()) {
			throw new Exception("FFT: failed to process for image " + fftImage.getClass() + " -- " + ifft.getErrorMessage());
		}
		return ifft.getResult();
	}
}
