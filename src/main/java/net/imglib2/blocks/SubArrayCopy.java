package net.imglib2.blocks;

import net.imglib2.type.PrimitiveType;
import net.imglib2.util.Cast;
import net.imglib2.util.IntervalIndexer;

/**
 * Copy sub-region between flattened arrays (of different sizes).
 * <p>
 * The {@link #copy(Object, int[], int[], Object, int[], int[], int[]) SubArrayCopy.copy}
 * method requires the nD size of the flattened source and target arrays, the nD
 * starting position and nD size of the source region to copy, and the nD
 * starting position in the target to copy to.
 * <p>
 * The {@link #copy(Object, int[], int[], Object, int[], int[], int[])
 * SubArrayCopy.copy} method determines the appropriate implementation by
 * looking at the types of the {@code src} and {@code dest} arguments.
 * <p>
 * If for repeated calls the types are known beforehand, this implementation
 * lookup can be done once, up-front. {@link #forPrimitiveType(PrimitiveType,
 * boolean, boolean) SubArrayCopy.forPrimitiveType} returns a {@link
 * SubArrayCopy.Typed} instance with explicit {@code src} and {@code dest}
 * types.
 */
public interface SubArrayCopy
{
	/**
	 * Copy a nD region from {@code src} to {@code dest}, where {@code src} and
	 * {@code dest} are flattened nD array of dimensions {@code srcSize} and
	 * {@code destSize}, respectively.
	 *
	 * @param src
	 * 		flattened nD source array
	 * @param srcSize
	 * 		dimensions of src
	 * @param srcPos
	 * 		starting position, in src, of the range to copy
	 * @param dest
	 * 		flattened nD destination array
	 * @param destSize
	 * 		dimensions of dest
	 * @param destPos
	 * 		starting position, in dest, of the range to copy
	 * @param size
	 * 		size of the range to copy
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	static void copy( Object src, int[] srcSize, int[] srcPos, Object dest, int[] destSize, int[] destPos, int[] size )
	{
		final MemCopy memcopy = MemCopy.forClasses( src.getClass(), dest.getClass() );

		final int n = srcSize.length;
		assert srcPos.length == n;
		assert destSize.length == n;
		assert destPos.length == n;
		assert size.length == n;

		final int[] srcStrides = IntervalIndexer.createAllocationSteps( srcSize );
		final int[] destStrides = IntervalIndexer.createAllocationSteps( destSize );
		final int oSrc = IntervalIndexer.positionToIndex( srcPos, srcSize );
		final int oDest = IntervalIndexer.positionToIndex( destPos, destSize );

		memcopy.copyNDRangeRecursive( n - 1,
				src, srcStrides, oSrc,
				dest, destStrides, oDest,
				size );
	}

	static < T > Typed< T, T > forPrimitiveType( final PrimitiveType primitiveType )
	{
		MemCopy< T, T > memcopy = Cast.unchecked( MemCopy.forPrimitiveType( primitiveType, false, false ) );
		return memcopy::copyNDRangeRecursive;
	}

	static < S, T > Typed< S, T > forPrimitiveType( final PrimitiveType primitiveType, final boolean fromBuffer, final boolean toBuffer )
	{
		MemCopy< S, T > memcopy = Cast.unchecked( MemCopy.forPrimitiveType( primitiveType, fromBuffer, toBuffer ) );
		return memcopy::copyNDRangeRecursive;
	}

	/**
	 * @param <S>
	 * 		the source type. Must be a primitive array or buffer type (e.g., {@code double[]} or {@code IntBuffer})
	 * @param <T>
	 * 		the target type. Must be a primitive array or buffer type (e.g., {@code double[]} or {@code IntBuffer})
	 */
	interface Typed< S, T >
	{
		/**
		 * Copy a nD region from {@code src} to {@code dest}, where {@code src} and
		 * {@code dest} are flattened nD array of dimensions {@code srcSize} and
		 * {@code destSize}, respectively.
		 *
		 * @param src
		 * 		flattened nD source array
		 * @param srcSize
		 * 		dimensions of src
		 * @param srcPos
		 * 		starting position, in src, of the range to copy
		 * @param dest
		 * 		flattened nD destination array
		 * @param destSize
		 * 		dimensions of dest
		 * @param destPos
		 * 		starting position, in dest, of the range to copy
		 * @param size
		 * 		size of the range to copy
		 */
		default void copy(
				final S src, final int[] srcSize, final int[] srcPos,
				final T dest, final int[] destSize, final int[] destPos,
				final int[] size )
		{
			final int n = srcSize.length;
			assert srcPos.length == n;
			assert destSize.length == n;
			assert destPos.length == n;
			assert size.length == n;

			final int[] srcStrides = IntervalIndexer.createAllocationSteps( srcSize );
			final int[] destStrides = IntervalIndexer.createAllocationSteps( destSize );
			final int oSrc = IntervalIndexer.positionToIndex( srcPos, srcSize );
			final int oDest = IntervalIndexer.positionToIndex( destPos, destSize );

			copyNDRangeRecursive( n - 1,
					src, srcStrides, oSrc,
					dest, destStrides, oDest,
					size );
		}

		/**
		 * Recursively copy a {@code (d+1)} dimensional region from {@code src} to
		 * {@code dest}, where {@code src} and {@code dest} are flattened nD array
		 * with strides {@code srcStrides} and {@code destStrides}, respectively.
		 * <p>
		 * For {@code d=0}, a 1D line of length {@code size[0]} is copied
		 * (equivalent to {@code System.arraycopy}). For {@code d=1}, a 2D plane of
		 * size {@code size[0] * size[1]} is copied, by recursively copying 1D
		 * lines, starting {@code srcStrides[1]} (respectively {@code
		 * destStrides[1]}) apart. For {@code d=2}, a 3D box is copied by
		 * recursively copying 2D planes, etc.
		 *
		 * @param d
		 * 		current dimension
		 * @param src
		 * 		flattened nD source array
		 * @param srcStrides
		 *      nD strides of src
		 * @param srcPos
		 * 		flattened index (in src) to start copying from
		 * @param dest
		 * 		flattened nD destination array
		 * @param destStrides
		 *      nD strides of dest
		 * @param destPos
		 * 		flattened index (in dest) to start copying to
		 * @param size
		 * 		nD size of the range to copy
		 */
		void copyNDRangeRecursive(
				int d,
				S src, int[] srcStrides, int srcPos,
				T dest, int[] destStrides, int destPos,
				int[] size );
	}
}
