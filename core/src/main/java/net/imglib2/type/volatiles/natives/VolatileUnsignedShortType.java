package net.imglib2.type.volatiles.natives;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class VolatileUnsignedShortType extends AbstractVolatileNativeRealType< UnsignedShortType, VolatileUnsignedShortType >
{
	final protected NativeImg< ?, ? extends VolatileShortAccess > img;

	public VolatileUnsignedShortType( final NativeImg< ?, ? extends VolatileShortAccess > img )
	{
		super( new UnsignedShortType( img ), false );
		this.img = img;
	}

	public VolatileUnsignedShortType( final UnsignedShortType t, final boolean valid )
	{
		super( t, valid );
		this.img = null;
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
