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

package net.imglib2.algorithm.pde;

import java.util.Vector;

import net.imglib2.Cursor;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class IsotropicDiffusionTensor <T extends RealType<T>>  extends MultiThreadedBenchmarkAlgorithm 
implements OutputAlgorithm<Img<FloatType>> {

	private static final String BASE_ERROR_MESSAGE = "["+IsotropicDiffusionTensor.class.getSimpleName()+"] ";
	private final float val;
	private final long[] dimensions;
	private final Img<FloatType> D;

	public IsotropicDiffusionTensor(final long[] dimensions, float val) {
		this.dimensions = dimensions;
		this.val = val;
		// Instantiate tensor holder, and initialize cursors
		long[] tensorDims = new long[dimensions.length + 1];
		for (int i = 0; i < dimensions.length; i++) {
			tensorDims[i] = dimensions[i];
		}
		tensorDims[dimensions.length] = dimensions.length * (dimensions.length - 1);

		double size = 1;
		for (long d : dimensions) {
			size *= d;
		}
		ImgFactory< FloatType > factory;
		if ( size >= Integer.MAX_VALUE ) {
			factory = new CellImgFactory<FloatType>();
		} else {
			factory = new ArrayImgFactory< FloatType >();
		}

		this.D = factory.create(tensorDims, new FloatType());
	}

	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public boolean process() {

		long start = System.currentTimeMillis();
		
		final int tensorDim = dimensions.length; // the dim to write the tensor components to.
		Vector<Chunk> chunks = SimpleMultiThreading.divideIntoChunks(D.size(), numThreads);
		Thread[] threads = SimpleMultiThreading.newThreads(numThreads);

		for (int i = 0; i < threads.length; i++) {

			final Chunk chunk = chunks.get(i);

			threads[i] = new Thread(""+BASE_ERROR_MESSAGE+"thread "+i) {

				@Override
				public void run() {
					
					Cursor<FloatType> cursor = D.localizingCursor();
					cursor.jumpFwd(chunk.getStartPosition());
					for(long step = 0; step < chunk.getLoopSize(); step++) {
						cursor.fwd();
						if (cursor.getIntPosition(tensorDim) < dimensions.length) {
							// diagonal terms only
							cursor.get().set(val);
						} else {
							cursor.get().setZero();
						}
					}
				}
			};
		}
		
		SimpleMultiThreading.startAndJoin(threads);
		
		processingTime = System.currentTimeMillis() - start;
		return true;
	}

	@Override
	public Img<FloatType> getResult() {
		return D;
	}

}
