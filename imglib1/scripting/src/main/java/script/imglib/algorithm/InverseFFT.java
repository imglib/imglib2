/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package script.imglib.algorithm;

import mpicbg.imglib.algorithm.fft.FourierTransform;
import mpicbg.imglib.algorithm.fft.InverseFourierTransform;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexDoubleType;

/**
 * TODO
 *
 */
public class InverseFFT<T extends RealType<T>> extends Image<T>
{
	public InverseFFT(final FFT<T> fftImage) throws Exception {
		super(process(fftImage, fftImage.fft).getContainer(), fftImage.value.copy(), "Inverse FFT");
	}

	public InverseFFT(final Image<ComplexDoubleType> img, final FFT<T> fftImage) throws Exception {
		super(process(img, fftImage.fft).getContainer(), fftImage.value.copy(), "Inverse FFT");
	}

	static private final <T extends RealType<T>> Image<T> process(final Image<ComplexDoubleType> fftImage, final FourierTransform<T, ComplexDoubleType> fft) throws Exception {
		final InverseFourierTransform<T, ComplexDoubleType> ifft = new InverseFourierTransform<T, ComplexDoubleType>(fftImage, fft);
		if (!ifft.checkInput() || !ifft.process()) {
			throw new Exception("FFT: failed to process for image " + fftImage.getClass() + " -- " + ifft.getErrorMessage());
		}
		return ifft.getResult();
	}
}
