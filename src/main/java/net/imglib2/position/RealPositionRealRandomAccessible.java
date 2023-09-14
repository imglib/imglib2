/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.position;

import net.imglib2.RealInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * A {@link RealRandomAccessible} over the <em>d</em>-th position of real
 * coordinate space.
 *
 * <p>
 * TODO deprecate his implementation for
 * {@code RealRandomAccessible<DoubleType> realPositionRealRandomAccessible = new FunctionRealRandomAccessible<DoubleType>(3, (x, y) -> y.set(x.getDoublePosition(d)), DoubleType::new);}
 * </p>
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 */
public class RealPositionRealRandomAccessible implements RealRandomAccessible< DoubleType >
{
	private final int n;
	private final int d;

	public RealPositionRealRandomAccessible( final int numDimensions, final int d )
	{
		this.n = numDimensions;
		this.d = d;
	}

	public class RealPositionRealRandomAccess extends RealPoint implements RealRandomAccess< DoubleType >
	{
		private final DoubleType t = new DoubleType();

		public RealPositionRealRandomAccess()
		{
			super( RealPositionRealRandomAccessible.this.n );
		}

		@Override
		public DoubleType get()
		{
			t.set( position[ d ] );
			return t;
		}

		@Override
		public RealPositionRealRandomAccess copy()
		{
			return new RealPositionRealRandomAccess();
		}
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public RealPositionRealRandomAccess realRandomAccess()
	{
		return new RealPositionRealRandomAccess();
	}

	@Override
	public RealPositionRealRandomAccess realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
	}
}
