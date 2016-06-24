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
package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;

/**
 * {@link SubsampleIntervalView} is a view that provides access to only every
 * <em>s<sub>d</sub></em><sup>th</sup> value of a source
 * {@link RandomAccessibleInterval}. Its transformed origin is at the min
 * coordinate of the source {@link Interval}. This is effectively an integer
 * scaling and optional offset transformation. Localization calls to the
 * {@link RandomAccess} and {@link Interval} dimension calls to the
 * {@link SubsampleIntervalView} return scaled and translated coordinates that
 * are generated on-the-fly. Localization is thus moderately inefficient to the
 * benefit of faster positioning. Don't ask for what you already know ;).
 * 
 * @author Stephan Saalfeld (saalfeld@mpi-cbg.de)
 */
public class SubsampleIntervalView< T > extends SubsampleView< T > implements RandomAccessibleInterval< T >
{
	final protected long[] dimensions;

	final protected long[] max;

	public SubsampleIntervalView( final RandomAccessibleInterval< T > source, final long step )
	{
		super( Views.zeroMin( source ), step );

		dimensions = new long[ steps.length ];
		max = new long[ steps.length ];
		for ( int d = 0; d < steps.length; ++d )
		{
			steps[ d ] = step;
			dimensions[ d ] = source.dimension( d ) / step;
			max[ d ] = dimensions[ d ] - 1;
		}
	}

	public SubsampleIntervalView( final RandomAccessibleInterval< T > source, final long... steps )
	{
		super( Views.zeroMin( source ), steps );

		dimensions = new long[ steps.length ];
		max = new long[ steps.length ];
		for ( int d = 0; d < steps.length; ++d )
		{
			this.steps[ d ] = steps[ d ];
			dimensions[ d ] = source.dimension( d ) / steps[ d ];
			max[ d ] = dimensions[ d ] - 1;
		}
	}

	@Override
	public long min( final int d )
	{
		return 0;
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < steps.length; ++d )
			min[ d ] = 0;
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < steps.length; ++d )
			min.setPosition( 0, d );
	}

	@Override
	public long max( final int d )
	{
		return max[ d ];
	}

	@Override
	public void max( final long[] m )
	{
		for ( int d = 0; d < steps.length; ++d )
			m[ d ] = this.max[ d ];
	}

	@Override
	public void max( final Positionable m )
	{
		for ( int d = 0; d < steps.length; ++d )
			m.setPosition( this.max[ d ], d );
	}

	@Override
	public double realMin( final int d )
	{
		return 0;
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < steps.length; ++d )
			min[ d ] = 0;
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < steps.length; ++d )
			min.setPosition( 0, d );
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < steps.length; ++d )
			m[ d ] = this.max[ d ];
	}

	@Override
	public void realMax( final RealPositionable m )
	{
		for ( int d = 0; d < steps.length; ++d )
			m.setPosition( this.max[ d ], d );
	}

	@Override
	public void dimensions( final long[] dim )
	{
		for ( int d = 0; d < steps.length; ++d )
			dim[ d ] = this.dimensions[ d ];

	}

	@Override
	public long dimension( final int d )
	{
		return dimensions[ d ];
	}
}
