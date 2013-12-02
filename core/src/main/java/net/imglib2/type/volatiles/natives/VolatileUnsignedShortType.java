package net.imglib2.type.volatiles.natives;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class VolatileUnsignedShortType extends AbstractVolatileNativeRealType< UnsignedShortType, VolatileUnsignedShortType >
{
	final protected NativeImg< ?, ? extends VolatileShortAccess > img;

	// this is the constructor if you want it to read from an array
	public VolatileUnsignedShortType( final NativeImg< ?, ? extends VolatileShortAccess > img )
	{
		super( new UnsignedShortType( img ), false );
		this.img = img;
	}

	// this is the constructor if you want to specify the wrapped type
	public VolatileUnsignedShortType( final UnsignedShortType t, final boolean valid )
	{
		super( t, valid );
		this.img = null;
	}

	// this is the constructor if you want to specify the dataAccess
	public VolatileUnsignedShortType( final VolatileShortAccess access )
	{
		super( new UnsignedShortType( access ), access.isValid() );
		this.img = null;
	}

	// this is the constructor if you want it to be a variable
	public VolatileUnsignedShortType( final int value )
	{
		this( new UnsignedShortType( new VolatileShortArray( 1, true ) ), true );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public VolatileUnsignedShortType()
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
		t.updateContainer( c );
		setValid( img.update( c ).isValid() );
	}

	@Override
	public NativeImg< VolatileUnsignedShortType, ? > createSuitableNativeImg( final NativeImgFactory< VolatileUnsignedShortType > storageFactory, final long[] dim )
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public VolatileUnsignedShortType duplicateTypeOnSameNativeImg()
	{
		return new VolatileUnsignedShortType( img );
	}

	@Override
	public VolatileUnsignedShortType createVariable()
	{
		return new VolatileUnsignedShortType( new UnsignedShortType( new VolatileShortArray( 1, true ) ), true );
	}

	@Override
	public VolatileUnsignedShortType copy()
	{
		final VolatileUnsignedShortType v = createVariable();
		v.set( this );
		return v;
	}
}
