/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Tobias Pietzsch
 */
package net.imglib2;


/**
 * A flat iteration order on an {@link IterableInterval}.
 * Flat iteration order means that cursors iterate line by line, plane by plane, etc.
 * For instance a 3D interval ranging from <em>(0,0,0)</em> to <em>(1,1,1)</em> is iterated like
 * <em>(0,0,0), (1,0,0), (0,1,0), (1,1,0), (0,0,1), (1,0,1), (0,1,1), (1,1,1)</em>
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FlatIterationOrder
{
	private final Interval interval;

	public FlatIterationOrder( final Interval interval )
	{
		this.interval = interval;
	}

	/**
	 * To be equal an object has to be a {@link FlatIterationOrder} on an
	 * interval of the same dimensions and with the same minimum.
	 *
	 * @return true, if obj is a compatible {@link FlatIterationOrder}.
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( ! ( obj instanceof FlatIterationOrder ) )
			return false;

		final Interval i = ( ( FlatIterationOrder ) obj ).interval;
		if ( i.numDimensions() != interval.numDimensions() )
			return false;

		final int n = interval.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( i.min( d ) != interval.min( d ) || i.dimension( d ) != interval.dimension( d ) )
				return false;

		return true;
	}
}
