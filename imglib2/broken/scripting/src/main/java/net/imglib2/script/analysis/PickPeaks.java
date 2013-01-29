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

package net.imglib2.script.analysis;

import java.util.ArrayList;

import net.imglib2.script.algorithm.fn.AlgorithmUtil;

import net.imglib2.algorithm.math.PickImagePeaks;
import net.imglib2.type.numeric.RealType;

/** Picks peaks in an image. It is recommended to smooth the image a bit with a Gaussian first.
 * 
 * See underlying algorithm {@link PickImagePeaks}. */
/**
 * TODO
 *
 */
public class PickPeaks<T extends RealType<T>> extends ArrayList<float[]>
{
	private static final long serialVersionUID = 5392390381529251353L;

	/** @param fn Any of {@link IFunction, Image}. */
	public PickPeaks(final Object fn) throws Exception {
		this(fn, null);
	}

	/**
	 * @param fn Any of {@link IFunction, Image}.
	 * @param suppressionRegion A float array with as many dimensions as the image has, and which describes an spheroid for supression around a peak.
	 */
	@SuppressWarnings("unchecked")
	public PickPeaks(final Object fn, final double[] suppressionRegion) throws Exception {
		PickImagePeaks<T> pick = new PickImagePeaks<T>(AlgorithmUtil.wrapS(fn));
		if (null != suppressionRegion) pick.setSuppression(suppressionRegion);
		if (!pick.checkInput() || !pick.process()) {
			throw new Exception("PickPeaks error: " + pick.getErrorMessage());
		}
		for (int[] p : pick.getPeakList()) {
			float[] f = new float[p.length];
			System.arraycopy(p, 0, f, 0, p.length);
			this.add(f);
		}
	}
}
