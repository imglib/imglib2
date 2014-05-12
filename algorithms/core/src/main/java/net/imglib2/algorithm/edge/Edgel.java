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

package net.imglib2.algorithm.edge;

import net.imglib2.AbstractRealLocalizable;

/**
 * An oriented point representing a sub-pixel localized segment of a
 * (hyper-)edge.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Edgel extends AbstractRealLocalizable
{

	private final double[] gradient;

	private final double magnitude;

	/**
	 * Create an edgel.
	 * 
	 * @param position
	 *            the sub-pixel position of the edgel.
	 * @param gradient
	 *            the gradient direction (a unit vector) at the edgel position.
	 *            This is perpendicular to the edge direction.
	 * @param magnitude
	 *            the gradient magnitude at the edgel position.
	 */
	public Edgel( final double[] position, final double[] gradient, final double magnitude )
	{
		super(position.clone());
		this.gradient = gradient.clone();
		this.magnitude = magnitude;
	}

	/**
	 * Get the gradient direction at the edgel position. This is a unit vector
	 * perpendicular to the edge direction.
	 * 
	 * @return the gradient direction at the edgel position.
	 */
	public double[] getGradient()
	{
		return gradient;
	}

	/**
	 * Get the gradient magnitude at the edgel position.
	 * 
	 * @return the gradient magnitude at the edgel position.
	 */
	public double getMagnitude()
	{
		return magnitude;
	}

	public String toString()
	{
		return String.format("Edgel: pos (%.2f,%.2f,%.2f) grad (%.2f,%.2f,%.2f) mag (%.2f)", 
				position[0], position[1], position[2], 
				gradient[0], gradient[1], gradient[2], 
				magnitude);
	}
	
}
