/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.algorithm.restoration;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.region.localneighborhood.BoundaryCheckedLocalNeighborhoodCursor2;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * This class implements the restoration algorithm based on total variation,
 * as described in
 *
 * @ARTICLE{chan2001,
 *     author={Chan, T.F. and Osher, S. and Shen, J.},
 *     journal={Image Processing, IEEE Transactions on},
 *     title={The digital TV filter and nonlinear denoising},
 *     year={2001},
 *     month={feb},
 *     volume={10},
 *     number={2},
 *     pages={231 -241},
 *     doi={10.1109/83.902288},
 * }
 *
 * @author Benjamin Schmid
 * @author Tobias Pietzsch
 */
public class Total_Variation<T extends RealType<T>> implements OutputAlgorithm<Img<FloatType>>, Benchmark
{
	private static final float a = 1e-4f;

	private Img<FloatType> result;
	private final Img<T> img;
	private final double lambda;
	private final double tolerance;
	private long processingTime;
	private String errorMessage = "";

	public Total_Variation(
				final Img<T> img,
				final double lambda,
				final double tolerance)
	{
		this.img = img;
		this.lambda = lambda;
		this.tolerance = tolerance;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		result = new ArrayImgFactory<FloatType>().create(img, new FloatType());
		Img<FloatType>  current = new ArrayImgFactory<FloatType>().create(img, new FloatType());

		double diff = step(img, result);
		int iteration = 1;
		while(diff > tolerance) {
			try {
				// swap current and result
				Img<FloatType> tmp = current;
				current = result;
				result = tmp;

				// another restoration step
				diff = step(current, result);
				iteration++;
			} catch(Exception e) {
				errorMessage = e.getMessage();
				break;
			}
		}
		System.out.println("Stopping after " + iteration + " iterations");

		processingTime = System.currentTimeMillis() - startTime;

		return true;
	}

	private <U extends RealType<U>, V extends RealType<V>> double step(
				final RandomAccessibleInterval<U> current,
				final RandomAccessibleInterval<V> next) {

		final Img<FloatType> lv = new ArrayImgFactory<FloatType>().create(img, new FloatType());
		calculateLocalVariation(current, lv);
		double diff = 0;
		final Cursor<FloatType> cursor = lv.localizingCursor();
		final BoundaryCheckedLocalNeighborhoodCursor2<FloatType> lnc
			= new BoundaryCheckedLocalNeighborhoodCursor2<FloatType>(lv);

		final RandomAccess<T> org = img.randomAccess();
		final RandomAccess<U> cur = current.randomAccess();
		final RandomAccess<V> nxt = next.randomAccess();

		while(cursor.hasNext()) {
			cursor.next();

			double w_ag = 0;
			final double lvv = 1.0 / cursor.get().getRealDouble();

			lnc.updateCenter(cursor);
			while(lnc.hasNext()) {
				lnc.next();
				w_ag += lvv + 1.0 / lnc.get().getRealDouble();
			}

			final double h_aa = lambda / (lambda + w_ag);
			org.setPosition(cursor);
			double F = h_aa * org.get().getRealDouble();

			lnc.updateCenter(cursor);
			while(lnc.hasNext()) {
				lnc.next();
				final double w_ab = lvv + 1.0 / lnc.get().getRealDouble();
				final double h_ab = w_ab / (lambda + w_ag);

				cur.setPosition(lnc);
				F += h_ab * cur.get().getRealDouble();
			}

			nxt.setPosition(cursor);
			nxt.get().setReal(F);

			cur.setPosition(cursor);
			diff = Math.max(diff, cur.get().getRealDouble() - F);
		}
		return diff;
	}

	public static <U extends RealType<U>> void calculateLocalVariation(
				final RandomAccessibleInterval< U > ip,
				final RandomAccessibleInterval< FloatType > op)
	{
		final BoundaryCheckedLocalNeighborhoodCursor2<U> lnc
				= new BoundaryCheckedLocalNeighborhoodCursor2<U>(ip);
		final Cursor<U> input = Views.iterable(ip).localizingCursor();
		final RandomAccess<FloatType> output = op.randomAccess();

		while(input.hasNext()) {
			input.next();
			lnc.updateCenter(input);
			double sum = a * a;
			final double ua = input.get().getRealDouble();
			while(lnc.hasNext()) {
				lnc.next();
				double d = ua - lnc.get().getRealDouble();
				sum += d * d;
			}
			output.setPosition(input);
			output.get().setReal(Math.sqrt(sum));
		}
	}

	@Override
	public long getProcessingTime() { return processingTime; }

	@Override
	public Img<FloatType> getResult() { return result; }

	@Override
	public boolean checkInput() { return true; }

	@Override
	public String getErrorMessage() { return errorMessage; }
}
