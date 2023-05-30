/*-
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
package net.imglib2.blocks;

import net.imglib2.transform.integer.MixedTransform;

class PermuteInvert
{
	private final MemCopy memCopy;

	private final int n;

	private final int[] scomp;

	private final boolean[] sinv;

	private final int[] ssize;

	private final int[] ssteps;

	private final int[] tsteps;

	private final int[] csteps;

	private int cstart;

	public PermuteInvert( final MemCopy memCopy, MixedTransform transform )
	{
		this.memCopy = memCopy;
		this.n = transform.numSourceDimensions();
		scomp = new int[ n ];
		sinv = new boolean[ n ];
		transform.getComponentMapping( scomp );
		transform.getComponentInversion( sinv );
		ssize = new int[ n ];
		ssteps = new int[ n ];
		tsteps = new int[ n ];
		csteps = new int[ n ];
	}

	// TODO: Object --> T
	public void permuteAndInvert( Object src, Object dest, int[] destSize )
	{
		final int[] tsize = destSize;

		for ( int d = 0; d < n; ++d )
			ssize[ d ] = tsize[ scomp[ d ] ];

		ssteps[ 0 ] = 1;
		for ( int d = 0; d < n - 1; ++d )
			ssteps[ d + 1 ] = ssteps[ d ] * ssize[ d ];

		tsteps[ 0 ] = 1;
		for ( int d = 0; d < n - 1; ++d )
			tsteps[ d + 1 ] = tsteps[ d ] * tsize[ d ];

		cstart = 0;
		for ( int d = 0; d < n; ++d )
		{
			final int c = scomp[ d ];
			csteps[ d ] = sinv[ d ] ? -tsteps[ c ] : tsteps[ c ];
			cstart += sinv[ d ] ? ( tsize[ c ] - 1 ) * tsteps[ c ] : 0;
		}

		copyRecursively( src, 0, dest, cstart, n - 1 );
	}

	private void copyRecursively( final Object src, final int srcPos, final Object dest, final int destPos, final int d )
	{
		if ( d == 0 )
		{
			final int length = ssize[ d ];
			final int stride = csteps[ d ];
			memCopy.copyStrided( src, srcPos, dest, destPos, stride, length );
		}
		else
		{
			final int length = ssize[ d ];
			final int srcStride = ssteps[ d ];
			final int destStride = csteps[ d ];
			for ( int i = 0; i < length; ++i )
				copyRecursively( src, srcPos + i * srcStride, dest, destPos + i * destStride, d - 1 );
		}
	}

	// creates an independent copy of {@code copier}
	private PermuteInvert( PermuteInvert copier )
	{
		memCopy = copier.memCopy;
		n = copier.n;
		scomp = copier.scomp;
		sinv = copier.sinv;
		ssize = new int[ n ];
		ssteps = new int[ n ];
		tsteps = new int[ n ];
		csteps = new int[ n ];
	}

	PermuteInvert newInstance()
	{
		return new PermuteInvert( this );
	}
}
