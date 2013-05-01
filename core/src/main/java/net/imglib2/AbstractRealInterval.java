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

package net.imglib2;

/**
 * Implementation of the {@link RealInterval} interface.
 *
 *
 * @author Stephan Preibisch
 */
public class AbstractRealInterval extends AbstractEuclideanSpace  implements RealInterval
{
	final protected double[] min;
	final protected double[] max;

	/**
	 * Creates a new {@link AbstractRealInterval} using an existing {@link RealInterval}
	 *
	 * @param interval
	 */
	public AbstractRealInterval( final RealInterval interval )
	{
		super( interval.numDimensions() );
		this.min = new double[ n ];
		this.max = new double[ n ];

		for ( int d = 0; d < n; ++d )
		{
			this.min[ d ] = min[ d ];
			this.max[ d ] = max[ d ];
		}
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 *
	 * @param min
	 * @param max
	 */
	public AbstractRealInterval( final double[] min, final double[] max )
	{
		super( min.length );
		this.min = min.clone();
		this.max = max.clone();
	}

	/**
	 * Creates a new zero-bounded {@link AbstractRealInterval} with a certain size
	 *
	 * @param dimensions
	 */
	public AbstractRealInterval( final double[] dimensions )
	{
		super( dimensions.length );
		this.min = new double[ n ];
		this.max = new double[ n ];

		for ( int d = 0; d < n; ++d )
			this.max[ d ] = dimensions[ d ] - 1;
	}

	@Override
	public double realMin( final int d) { return min[ d ]; }

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = this.min[ d ];
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( this.min[ d ], d );
	}

	@Override
	public double realMax( final int d ) { return max[ d ]; }

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( this.max[ d ], d );
	}
}
