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

package net.imglib2.algorithm.labeling;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.container.Img;
import net.imglib2.container.ImgCursor;
import net.imglib2.container.ImgFactory;
import net.imglib2.container.ImgRandomAccess;
import net.imglib2.container.array.ArrayContainerFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

/** Given two binary images of the same dimensions,
 * generate an interpolated image that sits somewhere
 * in between, as specified by the weight.
 * 
 * For each binary image, the edges are found
 * and then each pixel is assigned a distance to the nearest edge.
 * Inside, distance values are positive; outside, negative.
 * Then both processed images are compared, and wherever
 * the weighted sum is larger than zero, the result image
 * gets a pixel set to true (or white, meaning inside).
 * 
 * A weight of zero means that the first image is not present at all
 * in the interpolated image;
 * a weight of one means that the first image is present exclusively.
 * 
 * The code was originally created by Johannes Schindelin
 * in the VIB's vib.BinaryInterpolator class, for ij.ImagePlus.
 *
 * @author Albert Cardona, Johannes Schindelin
 */
public class BinaryInterpolation2D implements OutputAlgorithm<Img<BitType>>
{

	final private Img<BitType> img1, img2;
	private float weight;
	private Img<BitType> interpolated;
	private String errorMessage;
	private IDT2D idt1, idt2;

	public BinaryInterpolation2D(final Img<BitType> img1, final Img<BitType> img2, final float weight) {
		this.img1 = img1;
		this.img2 = img2;
		this.weight = weight;
	}

	/** NOT thread safe, stateful. */
	private final class IDT2D {
		final Img<IntType> result;
		final int w, h;
		final int[] position = new int[2];
		final ImgRandomAccess<BitType> csrc;
		final ImgRandomAccess<IntType> cout;

		IDT2D(final Img<BitType> img) {
			this.w = (int) img.dimension(0);
			this.h = (int) img.dimension(1);
			ImgFactory<IntType> f = new ArrayContainerFactory<IntType>();
			this.result = f.create(new long[]{w, h}, new IntType());

			// Set all result pixels to infinity
			final int infinity = (w + h) * 9;
			for (final IntType v : this.result) {
				v.set(infinity);
			}

			// init result pixels with those of the image:
			this.csrc = img.randomAccess();
			this.cout = result.randomAccess();

			int count = 0;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (isBoundary(x, y)) {
						setOutValueAt(x, y, 0);
						count++;
					} else if (isJustOutside(x, y)) {
						setOutValueAt(x, y, -1);
					}
				}
			}

			if (count > 0) {
				propagate();
			}
		}

		private final void setPosition(final ImgRandomAccess<?> c, final int x, final int y) {
			position[0] = x;
			position[1] = y;
			c.setPosition(position);
		}

		private final void setOutValueAt(final int x, final int y, final int value) {
			setPosition(cout, x, y);
			cout.get().set(value);
		}

		private final int getSrcValueAt(final int x, final int y) {
			setPosition(csrc, x, y);
			return csrc.get().get() ? 1 : 0;
		}

		private final int getOutValueAt(final int x, final int y) {
			setPosition(cout, x, y);
			return cout.get().get();
		}

		// reads from result, writes to result
		private final void idt(final int x, final int y, final int dx, final int dy) {
			if (x + dx < 0 || y + dy < 0 ||
				x + dx >= w || y + dy >= h) {
				return;
			}
			int value = getOutValueAt(x + dx, y + dy);
			final int distance = (dx == 0 || dy == 0 ? 3 : 4);
			value += distance * (value < 0 ? -1 : 1);
			setPosition(cout, x, y);
			if (Math.abs(cout.get().get()) > Math.abs(value)) {
				cout.get().set(value);
			}
		}

		private final void propagate() {
			for (int j = 0; j < h; j++)
				for (int i = 0; i < w; i++) {
					idt(i, j, -1, 0);
					idt(i, j, -1, -1);
					idt(i, j, 0, -1);
				}

			for (int j = h - 1; j >= 0; j--)
				for (int i = w - 1; i >= 0; i--) {
					idt(i, j, +1, 0);
					idt(i, j, +1, +1);
					idt(i, j, 0, +1);
				}

			for (int i = w - 1; i >= 0; i--)
				for (int j = h - 1; j >= 0; j--) {
					idt(i, j, +1, 0);
					idt(i, j, +1, +1);
					idt(i, j, 0, +1);
				}

			for (int i = 0; i < w; i++)
				for (int j = 0; j < h; j++) {
					idt(i, j, -1, 0);
					idt(i, j, -1, -1);
					idt(i, j, 0, -1);
				}
		}

		private final boolean isBoundary(final int x, final int y) {
			if (getSrcValueAt(x, y) == 0)
				return false;
			if (x <= 0 || getSrcValueAt(x - 1, y) == 0)
				return true;
			if (x >= w - 1 || getSrcValueAt(x + 1, y) == 0)
				return true;
			if (y <= 0 || getSrcValueAt(x, y - 1) == 0)
				return true;
			if (y >= h - 1 || getSrcValueAt(x, y + 1) == 0)
				return true;
			if (x <= 0 || y <= 0 || getSrcValueAt(x - 1, y - 1) == 0)
				return true;
			if (x <= 0 || y >= h - 1 || getSrcValueAt(x - 1, y + 1) == 0)
				return true;
			if (x >= w - 1 || y <= 0 || getSrcValueAt(x + 1, y - 1) == 0)
				return true;
			if (x >= w - 1 || y >= h - 1 || getSrcValueAt(x + 1, y + 1) == 0)
				return true;
			return false;
		}

		private final boolean isJustOutside(final int x, final int y) {
			if (getSrcValueAt(x, y) != 0)
				return false;
			if (x > 0 && getSrcValueAt(x - 1, y) != 0)
				return true;
			if (x < w - 1 && getSrcValueAt(x + 1, y) != 0)
				return true;
			if (y > 0 && getSrcValueAt(x, y - 1) != 0)
				return true;
			if (y < h - 1 && getSrcValueAt(x, y + 1) != 0)
				return true;
			if (x > 0 && y > 0 && getSrcValueAt(x - 1, y - 1) != 0)
				return true;
			if (x > 0 && y < h - 1 && getSrcValueAt(x - 1, y + 1) != 0)
				return true;
			if (x < w - 1 && y > 0 && getSrcValueAt(x + 1, y - 1) != 0)
				return true;
			if (x < w - 1 && y < h - 1 && getSrcValueAt(x + 1, y + 1) != 0)
				return true;
			return false;
		}
	}

	@Override
	public Img<BitType> getResult() {
		return interpolated;
	}
	
	@Override
	public boolean checkInput() {
		if (img1.numDimensions() < 2 || img2.numDimensions() < 2) {
			errorMessage = "Need at least 2 dimensions";
			return false;
		}
		if (img1.dimension(0) != img2.dimension(0) || img1.dimension(1) != img2.dimension(1)) {
			errorMessage = "Dimensions do not match";
			return false;
		}
		if (weight < 0 || weight > 1) {
			errorMessage = "Weight must be between 0 and 1, both inclusive.";
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	private final class NewITD2D implements Callable<IDT2D> {
		private final Img<BitType> img;
		NewITD2D(final Img<BitType> img) {
			this.img = img;
		}
		@Override
		public IDT2D call() throws Exception {
			return new IDT2D(img);
		}
	}

	/** After changing the weight, it's totally valid to call process() again,
	 * and then getResult(). */
	public void setWeight(final float weight) throws IllegalArgumentException {
		if (weight < 0 || weight > 1) {
			throw new IllegalArgumentException("Weight must be between 0 and 1, both inclusive.");
		}
		this.weight = weight;
	}

	@Override
	public boolean process() {
		this.interpolated = process(this.weight);
		return null != this.interpolated;
	}
	
	/** The first time, it will prepare the distance transform images, which are computed only once. */
	public Img<BitType> process(final float weight)
	{
		synchronized (this) {
			if (null == idt1 || null == idt2) {
				ExecutorService exec = Executors.newFixedThreadPool(Math.min(2, Runtime.getRuntime().availableProcessors()));
				Future<IDT2D> fu1 = exec.submit(new NewITD2D(img1));
				Future<IDT2D> fu2 = exec.submit(new NewITD2D(img2));
				exec.shutdown();

				try {
					this.idt1 = fu1.get();
					this.idt2 = fu2.get();
				} catch (InterruptedException ie) {
					throw new RuntimeException(ie);
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		}

		// Cannot just img1.createNewImage() because the container may not be able to receive data,
		// such as the ShapeList container.
		final ImgFactory<BitType> f = new ArrayContainerFactory<BitType>();
		final Img<BitType> interpolated = f.create(new long[]{img1.dimension(0), img1.dimension(1)}, new BitType());

		if (img1.equalIterationOrder(img2)) {
			final ImgCursor<IntType> c1 = idt1.result.cursor();
			final ImgCursor<IntType> c2 = idt2.result.cursor();
			final ImgCursor<BitType> ci = interpolated.cursor();

			while (ci.hasNext()) {
				c1.fwd();
				c2.fwd();
				ci.fwd();

				if ((c1.get().get() * weight) + (c2.get().get() * (1 - weight)) > 0) {
					ci.get().set(true);
				}
			}
		} else {
			System.out.println("using option 2");
			final ImgRandomAccess<IntType> c1 = idt1.result.randomAccess();
			final ImgRandomAccess<IntType> c2 = idt2.result.randomAccess();
			final ImgCursor<BitType> ci = interpolated.cursor();

			while (ci.hasNext()) {
				ci.fwd();
				c1.setPosition(ci);
				c2.setPosition(ci);

				if (0 <= c1.get().get() * weight + c2.get().get() * (1 - weight)) {
					ci.get().set(true);
				}
			}
		}

		return interpolated;
	}
}
