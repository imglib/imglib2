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
