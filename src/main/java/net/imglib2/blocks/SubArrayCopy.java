package net.imglib2.blocks;

import net.imglib2.type.PrimitiveType;
import net.imglib2.type.numeric.integer.IntType;

public interface SubArrayCopy
{
	// TODO: Can we inline everything and make it work for arbitrary
	//       dimensionality? (get rid of steps arrays and pass everything as
	//       arguments to the recursive run() call).
	static SubArrayCopy create( final int n, final PrimitiveType primitiveType )
	{
		return new SubArrayCopyImpl<>( n, MemCopy.forPrimitiveType( primitiveType ) );
	}

	/**
	 * @param src
	 * 		flat src array
	 * @param o_src
	 * 		origin in src
	 * @param size_src
	 * 		dimensions of src
	 * @param dst
	 * 		flat dst array
	 * @param o_dst
	 * 		origin in dst
	 * @param size_dst
	 * 		dimensions of dst
	 * @param size
	 * 		size of the region to copy
	 */
	void copy(
			final Object src, final int[] o_src, final int[] size_src,
			final Object dst, final int[] o_dst, final int[] size_dst,
			final int[] size );

	public static void main( String[] args )
	{
		int[] data_src = new int[] {
				11, 12, 13, 14, 15,
				21, 22, 23, 24, 25,
				31, 32, 33, 34, 35,
				41, 42, 43, 44, 45,
		};

		int[] data_dst = new int[ 10 * 10 ];

		SubArrayCopy c = SubArrayCopy.create( 2, new IntType().getNativeTypeFactory().getPrimitiveType() );
		c.copy(
				data_src, new int[] { 1, 2 }, new int[] { 5, 4 },
				data_dst, new int[] { 3, 7 }, new int[] { 10, 10 },
				new int[] { 2, 2 } );
		print( data_dst, 10 );
	}

	static void print( final int[] data, final int w )
	{
		for ( int i = 0; i < data.length; ++i )
		{
			System.out.print( data[ i ] );
			if ( i % w == w - 1 )
				System.out.println();
			else
				System.out.print( ", " );
		}
	}
}

class SubArrayCopyImpl< S, T > implements SubArrayCopy
{
	private final int[] steps_src;

	private final int[] steps_dst;

	private final MemCopy< S, T > memCopy;

	private S src;

	private T dst;

	private int[] size;

	SubArrayCopyImpl( final int n, MemCopy< S, T > memCopy )
	{
		steps_src = new int[ n ];
		steps_dst = new int[ n ];
		this.memCopy = memCopy;
	}

	/**
	 * @param src
	 * 		flat src array
	 * @param o_src
	 * 		origin in src
	 * @param size_src
	 * 		dimensions of src
	 * @param dst
	 * 		flat dst array
	 * @param o_dst
	 * 		origin in dst
	 * @param size_dst
	 * 		dimensions of dst
	 * @param size
	 * 		size of the region to copy
	 */
	@Override
	public void copy(
			final Object src, final int[] o_src, final int[] size_src,
			final Object dst, final int[] o_dst, final int[] size_dst,
			final int[] size )
	{
		this.src = ( S ) src;
		this.dst = ( T ) dst;
		this.size = size;

		steps_src[ 0 ] = 1;
		steps_dst[ 0 ] = 1;
		final int n = steps_src.length;
		for ( int d = 0; d < n - 1; ++d )
		{
			steps_src[ d + 1 ] = steps_src[ d ] * size_src[ d ];
			steps_dst[ d + 1 ] = steps_dst[ d ] * size_dst[ d ];
		}

		int origin_offset_src = o_src[ 0 ];
		int origin_offset_dst = o_dst[ 0 ];
		for ( int d = 0; d < n - 1; ++d )
		{
			origin_offset_src += o_src[ d + 1 ] * steps_src[ d + 1 ];
			origin_offset_dst += o_dst[ d + 1 ] * steps_dst[ d + 1 ];
		}

		run( n - 1, origin_offset_src, origin_offset_dst );
	}

	private void run( final int d, final int offset_src, final int offset_dst )
	{
		final int len = size[ d ];
		if ( d > 0 )
		{
			final int stride_src = steps_src[ d ];
			final int stride_dst = steps_dst[ d ];
			for ( int i = 0; i < len; ++i )
				run( d - 1, offset_src + i * stride_src, offset_dst + i * stride_dst );
		}
		else
		{
			memCopy.copyForward( src, offset_src, dst, offset_dst, len );
		}
	}
}
