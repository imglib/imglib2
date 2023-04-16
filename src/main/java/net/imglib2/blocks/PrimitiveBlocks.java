package net.imglib2.blocks;

import net.imglib2.RandomAccessible;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;

import static net.imglib2.blocks.PrimitiveBlocks.OnFallback.FAIL;
import static net.imglib2.blocks.PrimitiveBlocks.OnFallback.WARN;


/**
 * Copy blocks of data out of a {@code NativeType<T>} source into primitive
 * arrays (of the appropriate type).
 * <p>
 * Use the static method {@link PrimitiveBlocks#of(RandomAccessible)
 * PrimitiveBlocks.of} to create a {@code PrimitiveBlocks} accessor for an
 * arbitrary {@code RandomAccessible} source.
 * Then use the {@link PrimitiveBlocks#copy} method, to copy blocks out of the
 * source into flat primitive arrays.
 * <p>
 * {@link PrimitiveBlocks#of(RandomAccessible) PrimitiveBlocks.of} understands a
 * lot of View constructions (that ultimately end in {@code CellImg}, {@code
 * ArrayImg}, etc) and will try to build an optimized copier. For example, the
 * following will work:
 * <pre>{@code
 * 		CellImg< UnsignedByteType, ? > cellImg3D;
 * 		RandomAccessible< FloatType > view = Converters.convert(
 * 				Views.extendBorder(
 * 						Views.hyperSlice(
 * 								Views.zeroMin(
 * 										Views.rotate( cellImg3D, 1, 0 )
 * 								),
 * 								2, 80 )
 * 				),
 * 				new RealFloatConverter<>(),
 * 				new FloatType()
 * 		);
 * 		PrimitiveBlocks< FloatType > blocks = PrimitiveBlocks.of( view );
 *
 * 		final float[] data = new float[ 40 * 50 ];
 * 		blocks.copy( new int[] { 10, 20 }, data, new int[] { 40, 50 } );
 * }</pre>
 * <p>
 * If a source {@code RandomAccessible} cannot be understood, {@link
 * PrimitiveBlocks#of(RandomAccessible) PrimitiveBlocks.of} will return a
 * fall-back implementation (based on {@code LoopBuilder}).
 *
 * With the optional {@link OnFallback OnFallback} argument to {@link
 * PrimitiveBlocks#of(RandomAccessible, OnFallback) PrimitiveBlocks.of} it can
 * be configured, whether
 * fall-back should be silently accepted ({@link OnFallback#ACCEPT ACCEPT}),
 * a warning should be printed ({@link OnFallback#WARN WARN}), or
 * an {@code IllegalArgumentException} thrown ({@link OnFallback#FAIL FAIL}).
 * The warning/exception message explains why the input {@code RandomAccessible}
 * requires fall-back.
 * <p>
 * The only really un-supported case is if the pixel type {@code T} does not map
 * one-to-one to a primitive type. (For example, {@code ComplexDoubleType} or
 * {@code Unsigned4BitType} are not supported.)
 * <p>
 * Implementations are not thread-safe in general. Use {@link #threadSafe()} to
 * obtain a thread-safe instance (implemented using {@link ThreadLocal} copies).
 * E.g.,
 * <pre{@code
 * 		PrimitiveBlocks< FloatType > blocks = PrimitiveBlocks.of( view ).threadSafe();
 * }</pre>
 *
 * @param <T>
 * 		pixel type
 */
public interface PrimitiveBlocks< T extends NativeType< T > >
{
	T getType();

	/**
	 * Copy a block from the ({@code T}-typed) source into primitive arrays (of
	 * the appropriate type).
	 *
	 * @param srcPos
	 * 		min coordinate of the block to copy
	 * @param dest
	 * 		primitive array to copy into. Must correspond to {@code T}, for
	 *      example, if {@code T} is {@code UnsignedByteType} then {@code dest} must
	 *      be {@code byte[]}.
	 * @param size
	 * 		the size of the block to copy
	 */
	void copy( long[] srcPos, Object dest, int[] size );

	/**
	 * Copy a block from the ({@code T}-typed) source into primitive arrays (of
	 * the appropriate type).
	 *
	 * @param srcPos
	 * 		min coordinate of the block to copy
	 * @param dest
	 * 		primitive array to copy into. Must correspond to {@code T}, for
	 *      example, if {@code T} is {@code UnsignedByteType} then {@code dest} must
	 *      be {@code byte[]}.
	 * @param size
	 * 		the size of the block to copy
	 */
	default void copy( int[] srcPos, Object dest, int[] size )
	{
		copy( Util.int2long( srcPos ), dest, size );
	}

	/**
	 * Get a thread-safe version of this {@code PrimitiveBlocks}.
	 * (Implemented as a wrapper that makes {@link ThreadLocal} copies).
	 */
	PrimitiveBlocks< T > threadSafe();

	enum OnFallback
	{
		ACCEPT,
		WARN,
		FAIL
	}

	/**
	 * Create a {@code PrimitiveBlocks} accessor for an arbitrary {@code
	 * RandomAccessible} source. Many View constructions (that ultimately end in
	 * {@code CellImg}, {@code ArrayImg}, etc.) are understood and will be
	 * handled by an optimized copier.
	 * <p>
	 * If the source {@code RandomAccessible} cannot be understood, a warning is
	 * printed, and a fall-back implementation (based on {@code LoopBuilder}) is
	 * returned.
	 * <p>
	 * The returned {@code PrimitiveBlocks} is not thread-safe in general. Use
	 * {@link #threadSafe()} to obtain a thread-safe instance, e.g., {@code
	 * PrimitiveBlocks.of(view).threadSafe()}.
	 *
	 * @param ra the source
	 * @return a {@code PrimitiveBlocks} accessor for {@code ra}.
	 * @param <T> pixel type
	 */
	static < T extends NativeType< T > > PrimitiveBlocks< T > of(
			RandomAccessible< T > ra )
	{
		return of( ra, WARN );
	}

	/**
	 * Create a {@code PrimitiveBlocks} accessor for an arbitrary {@code
	 * RandomAccessible} source. Many View constructions (that ultimately end in
	 * {@code CellImg}, {@code ArrayImg}, etc.) are understood and will be
	 * handled by an optimized copier.
	 * <p>
	 * If the source {@code RandomAccessible} cannot be understood, a fall-back
	 * implementation (based on {@code LoopBuilder}) has to be used. The {@code
	 * onFallback} argument specifies how to handle this case:
	 * <ul>
	 *     <li>{@link OnFallback#ACCEPT ACCEPT}: silently accept fall-back</li>
	 *     <li>{@link OnFallback#WARN WARN}: accept fall-back, but print a warning explaining why the input {@code ra} requires fall-back</li>
	 *     <li>{@link OnFallback#FAIL FAIL}: throw {@code IllegalArgumentException} explaining why the input {@code ra} requires fall-back</li>
	 * </ul>
	 * The returned {@code PrimitiveBlocks} is not thread-safe in general. Use
	 * {@link #threadSafe()} to obtain a thread-safe instance, e.g., {@code
	 * PrimitiveBlocks.of(view).threadSafe()}.
	 *
	 * @param ra the source
	 * @return a {@code PrimitiveBlocks} accessor for {@code ra}.
	 * @param <T> pixel type
	 */
	static < T extends NativeType< T >, R extends NativeType< R > > PrimitiveBlocks< T > of(
			RandomAccessible< T > ra,
			OnFallback onFallback )
	{
		final ViewPropertiesOrError< T, R > props = ViewAnalyzer.getViewProperties( ra );
		if ( props.isFullySupported() )
		{
			return new ViewPrimitiveBlocks<>( props.getViewProperties() );
		}
		else if ( props.isSupported() && onFallback != FAIL )
		{
			if ( onFallback == WARN )
				System.err.println( props.getErrorMessage() );
			return new FallbackPrimitiveBlocks<>( props.getFallbackProperties() );
		}
		throw new IllegalArgumentException( props.getErrorMessage() );
	}
}
