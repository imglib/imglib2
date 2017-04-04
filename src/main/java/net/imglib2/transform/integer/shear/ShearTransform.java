/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */
package net.imglib2.transform.integer.shear;


/**
 * Invertible shear transform that shears by whole pixel units.
 * coordinate[shearDimension] += coordinate[referenceDimension] * shearFactor
 *
 * @author Philipp Hanslovsky
 * @author Keith Schulze
 *
 */
public class ShearTransform extends AbstractShearTransform
{

	/**
	 * @param nDim						Number of dimensions (source and target dimensions must be the same)
	 * @param shearDimension			Dimension to be sheared.
	 * @param referenceDimension		Dimension used as reference for shear.
	 * @param shearFactor				Interval for shear i.e., amount to shift shear dimension with respect
	 *									an increment in the reference referenceDimension.
	 */
	public ShearTransform(
			int nDim,
			int shearDimension,
			int referenceDimension,
			int shearFactor)
	{
		super(nDim, shearDimension, referenceDimension, shearFactor);
	}


	/**
	 * Auxillary constructor where the shearFactor defaults to 1.
	 *
	 * @param nDim					Number of dimensions (source and target dimensions must be the same)
	 * @param shearDimension		Dimension to be sheared.
	 * @param referenceDimension	Dimension used as reference for shear.
	 */
	public ShearTransform(
			int nDim,
			int shearDimension,
			int referenceDimension)
	{
		this( nDim, shearDimension, referenceDimension, 1 );
	}
}
