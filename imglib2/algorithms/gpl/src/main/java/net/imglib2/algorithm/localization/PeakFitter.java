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

package net.imglib2.algorithm.localization;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.imglib2.Localizable;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.region.localneighborhood.RectangleCursor;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodGPL;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * @author Jean-Yves Tinevez (tinevez@pasteur.fr) - 2013
 */
public class PeakFitter <T extends RealType<T>> implements MultiThreaded, OutputAlgorithm<Map<Localizable, double[]>> {

	private static final String BASE_ERROR_MESSAGE = "PeakFitter: ";

	private final Img<T> image;
	private final Collection<Localizable> peaks;
	private final int ndims;
	private final FunctionFitter fitter;
	private final FitFunction peakFunction;
	private final ConcurrentHashMap<Localizable, double[]> results;
	private int numThreads;
	private final StringBuffer errorHolder = new StringBuffer();



	/*
	 * CONSTRUCTOR
	 */

	/**
	 * @param image the image to operate on.
	 */
	public PeakFitter(final Img<T> image, Collection<Localizable> peaks, FunctionFitter fitter, FitFunction peakFunction) {
		this.image = image;
		this.ndims = image.numDimensions();
		this.fitter = fitter;
		this.peakFunction = peakFunction;
		this.peaks = peaks;
		this.results = new ConcurrentHashMap<Localizable, double[]>(peaks.size());
	}

	/*
	 * METHODS
	 */


	public void process(final Localizable point, final long[] pad_size, final double[] params) throws Exception {

		// Gather data around peak
		final Observation data = gatherObservationData(point, pad_size); 
		final double[][] X = data.X;
		final double[] I = data.I;
		fitter.fit(X, I, params, peakFunction);
	}


	@Override
	public boolean checkInput() {
		if (null == image) {
			errorHolder.append(BASE_ERROR_MESSAGE + "Image is null.");
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorHolder.toString();
	}

	@Override
	public boolean process() {

		ExecutorService workers = Executors.newFixedThreadPool(numThreads);
		for (final Localizable peak : peaks) {
			Runnable task = new Runnable() {

				@Override
				public void run() {
					long[] pad_size = null; // TODO how to do that?;
					double[] params = null; // TODO fit here first guess
					try {
						process(peak, pad_size, params);
					} catch (Exception e) {
						errorHolder.append(BASE_ERROR_MESSAGE + 
								"Problem fitting around " + peak +
								": " + e.getMessage() + ".\n");
					}
					results.put(peak, params);
				}

			};
			workers.execute(task);
		}

		workers.shutdown();
		boolean ok = true;
		try {
			ok = workers.awaitTermination(1, TimeUnit.HOURS);
			if (!ok) {
				System.err.println("Timeout reached while processing.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		return ok;
	}

	@Override
	public Map<Localizable, double[]> getResult() {
		return results;
	}

	@Override
	public void setNumThreads() {
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	@Override
	public int getNumThreads() {
		return numThreads;
	} 

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Collect the points to build the observation array, by iterating in a hypercube
	 * around the given location. The size of the cube is calculated by  
	 * <code>2 * 2 * ceil(typical_sigma) + 1)</code>. Points found out of the image are
	 * not included.
	 */
	private final Observation gatherObservationData(final Localizable point, final long[] pad_size) {

		RectangleNeighborhoodGPL<T, Img<T>> neighborhood = new RectangleNeighborhoodGPL<T, Img<T>>(image);
		neighborhood.setSpan(pad_size);
		neighborhood.setPosition(point);

		int n_pixels = (int) neighborhood.size();
		double[] tmp_I 		= new double[n_pixels];
		double[][] tmp_X 	= new double[n_pixels][ndims];


		RectangleCursor<T> cursor = neighborhood.localizingCursor();
		long[] pos = new long[image.numDimensions()];

		int index = 0;
		while (cursor.hasNext()) {

			cursor.fwd();
			cursor.localize(pos); // This is the absolute roi position
			if (cursor.isOutOfBounds()) {
				continue;
			}

			for (int i = 0; i < ndims; i++) {
				tmp_X[index][i] = pos[i];
			}

			tmp_I[index] = cursor.get().getRealDouble();
			index++;
		} 

		// Now we possibly resize the arrays, in case we have been too close to the 
		// image border.
		double[][] X = null;
		double[] I = null;
		if (index == n_pixels) {
			// Ok, we have gone through the whole square
			X 	= tmp_X;
			I 	= tmp_I;
		} else {
			// Re-dimension the arrays
			X 	= new double[index][ndims];
			I 	= new double[index];
			System.arraycopy(tmp_X, 0, X, 0, index);
			System.arraycopy(tmp_I, 0, I, 0, index);
		}

		Observation obs = new Observation();
		obs.I = I;
		obs.X = X;
		return obs;
	}

	/*
	 * INNER CLASSES
	 */

	/**
	 * A general-use class that can store the observations as a double array, and their 
	 * coordinates as a 2D double array. 
	 */
	private static class Observation {
		private double[] I;
		private double[][] X;
	}
}
