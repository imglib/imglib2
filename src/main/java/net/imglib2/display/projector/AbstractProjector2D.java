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
package net.imglib2.display.projector;

import net.imglib2.Point;
import net.imglib2.display.projector.sampler.SamplingProjector2D;
import net.imglib2.display.projector.specialized.ArrayImgXYByteProjector;

/**
 * Base class for 2D projectors. Projecting means in this case projecting from a
 * source format to a target format. 2D hints that the result is something 2
 * dimensional. The base class provides methods to select a reference point in a
 * multi-dimensional data object. Sub classes like
 * {@link IterableIntervalProjector2D}, {@link SamplingProjector2D} or
 * {@link ArrayImgXYByteProjector} specify a mapping that uses the reference
 * point to project data into a 2 dimensional representation. <br>
 * A basic example is the extraction of a data plain (containing the reference
 * point) by sampling two axes
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 */
public abstract class AbstractProjector2D extends Point implements Projector
{
	protected final long[] min;

	protected final long[] max;

	/**
	 * initializes a reference point with the specified number of dimensions.
	 * Start position is 0,0,...,0
	 * 
	 * @param numDims
	 */
	public AbstractProjector2D( final int numDims )
	{
		// as this is an 2D projector, we need at least two dimensions,
		// even if the source is one-dimensional
		super( Math.max( 2, numDims ) );

		min = new long[ n ];
		max = new long[ n ];
	}

}
