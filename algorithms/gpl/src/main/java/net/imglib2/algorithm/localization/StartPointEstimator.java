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
package net.imglib2.algorithm.localization;

import net.imglib2.Localizable;

/**
 * Interface for classes that are able to provide a start point for the parameters
 * of a curve fitting problem on image data. 
 * <p>
 * Implementations of this interface are used to pass <i>a priori</i> knowledge
 * to fitting problems. They are specific to a particular class of problems, 
 * and required to provide for two values:
 * <ul>
 * 	<li> They must be able to provide a domain size. That is the size of the image
 * patch around the peak to fit, and whose content is to be used in the fit.
 * 	<li> They must be able to provide a starting point to the curve fitting solver,
 * based on the image data around the coarse peak location.
 * </ul>
 * Depending on the problem they are taylored for, implementations can be very crude:
 * One can return plain constants if the typical parameters of all peaks are known
 * and uniform. Refined method are also possible. 
 * <p>
 * Start point parameters are returned in a double array; it is therefore important
 * the meaning and order of each element in the returned array is the same as the
 * one expected by the curve fitter that will be used downstream.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> - 2013
 */
public interface StartPointEstimator {
	
	/**
	 * Returns the domain size that will be sampled around each peak for curve
	 * fitting.
	 * <p>
	 * Domain size is provided as a <code>long[]</code> array, one element per
	 * dimension. The size must be understood a radius span: the actual
	 * rectangle size is <code>2 x span[d] + 1</code>. For instance, if in a 2D
	 * problem a value of <code>[2, 2]</code> is provided, the actual rectangle
	 * that will be sampled will be <code>5 x 5</code>
	 * 
	 * @return a long array specifying the span of the domain to sample for curve
	 * fitting.
	 */
	public long[] getDomainSpan();
	
	/**
	 * Returns a new double array containing an starting point estimate for a
	 * specific curve fitting problem. Depending on the implementation, this
	 * estimate can be calculated from the specified point and the specified
	 * image data.
	 * <p>
	 * This same data object, specified as an {@link Observation} object, 
	 * will later be used by the {@link FunctionFitter},
	 * so convoluted implementations can and may modify 
	 * it in a clever way to optimize the subsequent fitting step.
	 * <p>
	 * It is important that this method instantiates a new double array, for its
	 * elements will be evolved by the {@link FunctionFitter}, but the reference
	 * to the array will be shared for the specified peak.
	 * <p>
	 * Since the estimates are returned in a double array, each element has a
	 * meaning only in the view of a particular curve fitting problem, and will
	 * be applicable only to a specific {@link FunctionFitter}.
	 * 
	 * @param point the coarse localization of the peak whose parameters are to be
	 *            estimated.
	 * @param data  the image data around the peak to estimate, given as an
	 *            {@link Observation} object.
	 * @return a new double array containing the parameters estimates.
	 */
	public double[] initializeFit(Localizable point, Observation data);

}
