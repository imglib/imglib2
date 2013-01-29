/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.script.algorithm;

import net.imglib2.algorithm.math.ComputeMinMax;
import net.imglib2.algorithm.math.ImageConverter;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.color.Alpha;
import net.imglib2.script.color.Blue;
import net.imglib2.script.color.Green;
import net.imglib2.script.color.RGBA;
import net.imglib2.script.color.Red;
import net.imglib2.script.color.fn.ColorFunction;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;

/** Becomes a normalized version of the given image, within min and max bounds,
 * where all pixels take values between 0 and 1.
 *
 * The constructor accepts any of {@link IFunction, ColorFunction, Image}.
 * 
 * Images may be of any RealType or ARGBType. In the latter case, each color
 * channel is normalized independently.
 * 
 * When the min equals the max, the result is an image with zero values.
 *
 */
public class Normalize<N extends NumericType<N>> extends ImgProxy<N>
{
	@SuppressWarnings("unchecked")
	public Normalize(final Object fn) throws Exception {
		super(process(fn));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static final private Img process(final Object fn) throws Exception {
		if (fn instanceof ColorFunction) return (Img)processRGBA(Compute.inRGBA((ColorFunction)fn));
		if (fn instanceof IFunction) return processReal((IFunction)fn);
		if (fn instanceof Img<?>) {
			if (((Img)fn).firstElement() instanceof ARGBType) {
				return (Img)processRGBA((Img<ARGBType>)fn);
			} else {
				return processReal((Img)fn);
			}
		}
		throw new Exception("NormalizeMinMax: don't know how to process " + fn.getClass());
	}

	static final private Img<ARGBType> processRGBA(final Img<ARGBType> img) throws Exception {
		return new RGBA(processReal(new Red(img)),
						processReal(new Green(img)),
						processReal(new Blue(img)),
						processReal(new Alpha(img))).asImage();
	}

	static final private Img<FloatType> processReal(final IFunction fn) throws Exception {
		return processReal(Compute.inFloats(fn));
	}

	static final private <T extends RealType<T>> Img<FloatType> processReal(final Img<T> img) throws Exception {
		// Compute min and max
		final ComputeMinMax<T> cmm = new ComputeMinMax<T>(img);
		if (!cmm.checkInput() || !cmm.process()) {
			throw new Exception("Coult not compute min and max: " + cmm.getErrorMessage());
		}
		// If min and max are the same, we just return the empty image will all zeros
		if (0 == cmm.getMin().compareTo(cmm.getMax())) {
			return img.factory().imgFactory(new FloatType()).create(Util.intervalDimensions(img), new FloatType());
		}

		// Copy img into a new target image
		final Img<FloatType> target = img.factory().imgFactory(new FloatType()).create(Util.intervalDimensions(img), new FloatType());

		// Normalize in place the target image
		final double min = cmm.getMin().getRealDouble();
		final double max = cmm.getMax().getRealDouble();
		final double range = max - min;
		final ImageConverter<T, FloatType> conv = new ImageConverter<T, FloatType>( img, target, new Converter<T,FloatType>() {
			@Override
			public void convert(final T input, final FloatType output) {
				output.setReal( (input.getRealDouble() - min) / range );
			}
		});
		if (!conv.checkInput() || !conv.process()) {
			throw new Exception("Could not normalize image: " + conv.getErrorMessage());
		}

		return target;
	}
}
