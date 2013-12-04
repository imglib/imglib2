package net.imglib2.type.numeric.integer;

import net.imglib2.Volatile;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.volatiles.VolatileByteAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileByteArray;
import net.imglib2.type.volatiles.AbstractVolatileNativeRealType;

/**
 * A {@link Volatile} variant of {@link UnsignedByteType}. It uses an
 * underlying {@link UnsignedByteType} that maps into a
 * {@link VolatileByteAccess}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class VolatileUnsignedByteType extends AbstractVolatileNativeRealType< UnsignedByteType, VolatileUnsignedByteType >
{
	final protected NativeImg< ?, ? extends VolatileByteAccess > img;

	// this is the constructor if you want it to read from an array
	public VolatileUnsignedByteType( final NativeImg< ?, ? extends VolatileByteAccess > img )
	{
		super( new UnsignedByteType( img ), false );
		this.img = img;
	}

	// this is the constructor if you want to specify the wrapped type
	public VolatileUnsignedByteType( final UnsignedByteType t, final boolean valid )
	{
		super( t, valid );
		this.img = null;
	}

	// this is the constructor if you want to specify the dataAccess
	public VolatileUnsignedByteType( final VolatileByteAccess access )
	{
		super( new UnsignedByteType( access ), access.isValid() );
		this.img = null;
	}

	// this is the constructor if you want it to be a variable
	public VolatileUnsignedByteType( final int value )
	{
		this( new UnsignedByteType( new VolatileByteArray( 1, true ) ), true );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public VolatileUnsignedByteType()
	{
		this( 0 );
	}

	public void set( final int value )
	{
		get().set( value );
	}

	@Override
	public void updateContainer( final Object c )
	{
		final VolatileByteAccess a = img.update( c );
		t.dataAccess = a;
		setValid( a.isValid() );
	}

	@Override
	public NativeImg< VolatileUnsignedByteType, ? extends VolatileByteAccess > createSuitableNativeImg( final NativeImgFactory< VolatileUnsignedByteType > storageFactory, final long[] dim )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public VolatileUnsignedByteType duplicateTypeOnSameNativeImg()
	{
		return new VolatileUnsignedByteType( img );
	}

	@Override
	public VolatileUnsignedByteType createVariable()
	{
		return new VolatileUnsignedByteType( new UnsignedByteType( new VolatileByteArray( 1, true ) ), true );
	}

	@Override
	public VolatileUnsignedByteType copy()
	{
		final VolatileUnsignedByteType v = createVariable();
		v.set( this );
		return v;
	}
}
