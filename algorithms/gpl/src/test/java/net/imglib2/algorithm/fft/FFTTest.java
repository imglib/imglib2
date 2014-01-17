/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.algorithm.fft;

import static org.junit.Assert.assertEquals;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Make sure that FFT works alright
 *
 * @author Johannes Schindelin
 */
public class FFTTest {
	/**
	 * Test the Fourier transformation with a known frequency
	 */
	@Test
	public void oneDimensional() throws IncompatibleTypeException {
		double[] values = { 0, 1, 0, -1, 0 };
		final Img< DoubleType > img = ArrayImgs.doubles(values, 5);
		final FourierTransform< DoubleType, ComplexDoubleType > fft = new FourierTransform< DoubleType, ComplexDoubleType >( img, new ComplexDoubleType() );
		fft.process();
		Img<ComplexDoubleType> convolved = fft.getResult();
		assertEquals( convolved.numDimensions(), 1 );
	}
}
