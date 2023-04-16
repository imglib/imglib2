package net.imglib2.blocks;

import net.imglib2.type.PrimitiveType;

/**
 * Provides a temporary array of type {@code T}.
 * <p>
 * {@link #get} returns an array of type {@code T} with at least the specified
 * length. If {@code get} is called multiple times, it will either return a
 * previously returned array if it has at least requested length, or allocate a
 * new array, if the requested length exceeds the previously allocated length.
 *
 * @param <T> a primitive array type
 */
// TODO: make public? This will be reused in blk algorithms probably?
interface TempArray< T >
{
	T get( final int minSize );

	TempArray<T> newInstance();

	static TempArray< ? > forPrimitiveType( PrimitiveType primitiveType )
	{
		return new TempArrayImpl<>( PrimitiveTypeProperties.get( primitiveType ) );
	}
}
