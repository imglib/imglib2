package net.imglib2.type;

import java.util.function.Function;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;

/**
 * Binds a {@link PrimitiveType} enum constant to a {@code Access} interface
 * ({@code ByteAccess}, {@code DoubleAccess}, and so on). Instances can only be
 * constructed via static methods {@link #BYTE(Function)},
 * {@link #DOUBLE(Function)}, etc. to prevent non-matching combinations of
 * {@code Access} interface and {@link PrimitiveType} constant.
 * <p>
 * The second purpose of {@link PrimitiveTypeInfo} is to
 * {@link #createLinkedType(NativeImg) create} a linked type {@code T} for a
 * matching {@link NativeImg}.
 *
 * @param <T>
 *            the {@link NativeType} this is attached to
 * @param <A>
 *            the {@code Access} family ({@code ByteAccess},
 *            {@code DoubleAccess}, and so on)
 *
 * @author Tobias Pietzsch
 */
public final class PrimitiveTypeInfo< T extends NativeType< T >, A >
{
	private final PrimitiveType primitiveType;

	private final Function< NativeImg< T, ? extends A >, T > createLinkedType;

	/**
	 * @param primitiveType
	 *            the {@link PrimitiveType} enum constant matching {@code A}.
	 * @param createLinkedType
	 *            given a matching {@link NativeImg} creates a linked
	 *            {@link NativeType} {@code T}.
	 */
	private PrimitiveTypeInfo(
			final PrimitiveType primitiveType,
			final Function< NativeImg< T, ? extends A >, T > createLinkedType )
	{
		this.primitiveType = primitiveType;
		this.createLinkedType = createLinkedType;
	}

	public PrimitiveType getPrimitiveType()
	{
		return primitiveType;
	}

	public T createLinkedType( final NativeImg< T, ? extends A > img )
	{
		return createLinkedType.apply( img );
	}

	public static < T extends NativeType< T > > PrimitiveTypeInfo< T, ByteAccess > BYTE( final Function< NativeImg< T, ? extends ByteAccess >, T > createLinkedType )
	{
		return new PrimitiveTypeInfo<>( PrimitiveType.BYTE, createLinkedType );
	}

	public static < T extends NativeType< T > > PrimitiveTypeInfo< T, CharAccess > CHAR( final Function< NativeImg< T, ? extends CharAccess >, T > createLinkedType )
	{
		return new PrimitiveTypeInfo<>( PrimitiveType.CHAR, createLinkedType );
	}

	public static < T extends NativeType< T > > PrimitiveTypeInfo< T, ShortAccess > SHORT( final Function< NativeImg< T, ? extends ShortAccess >, T > createLinkedType )
	{
		return new PrimitiveTypeInfo<>( PrimitiveType.SHORT, createLinkedType );
	}

	public static < T extends NativeType< T > > PrimitiveTypeInfo< T, IntAccess > INT( final Function< NativeImg< T, ? extends IntAccess >, T > createLinkedType )
	{
		return new PrimitiveTypeInfo<>( PrimitiveType.INT, createLinkedType );
	}

	public static < T extends NativeType< T > > PrimitiveTypeInfo< T, LongAccess > LONG( final Function< NativeImg< T, ? extends LongAccess >, T > createLinkedType )
	{
		return new PrimitiveTypeInfo<>( PrimitiveType.LONG, createLinkedType );
	}

	public static < T extends NativeType< T > > PrimitiveTypeInfo< T, FloatAccess > FLOAT( final Function< NativeImg< T, ? extends FloatAccess >, T > createLinkedType )
	{
		return new PrimitiveTypeInfo<>( PrimitiveType.FLOAT, createLinkedType );
	}

	public static < T extends NativeType< T > > PrimitiveTypeInfo< T, DoubleAccess > DOUBLE( final Function< NativeImg< T, ? extends DoubleAccess >, T > createLinkedType )
	{
		return new PrimitiveTypeInfo<>( PrimitiveType.DOUBLE, createLinkedType );
	}
}
