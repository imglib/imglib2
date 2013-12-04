package net.imglib2.type.numeric.real;

import net.imglib2.Volatile;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.volatiles.VolatileFloatAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileFloatArray;
import net.imglib2.type.volatiles.AbstractVolatileNativeRealType;

/**
 * A {@link Volatile} variant of {@link FloatType}. It uses an
 * underlying {@link FloatType} that maps into a
 * {@link VolatileFloatAccess}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class VolatileFloatType extends AbstractVolatileNativeRealType< FloatType, VolatileFloatType >
{
	final protected NativeImg< ?, ? extends VolatileFloatAccess > img;

	// this is the constructor if you want it to read from an array
	public VolatileFloatType( final NativeImg< ?, ? extends VolatileFloatAccess > img )
	{
		super( new FloatType( img ), false );
		this.img = img;
	}

	// this is the constructor if you want to specify the wrapped type
	public VolatileFloatType( final FloatType t, final boolean valid )
	{
		super( t, valid );
		this.img = null;
	}

	// this is the constructor if you want to specify the dataAccess
	public VolatileFloatType( final VolatileFloatAccess access )
	{
		super( new FloatType( access ), access.isValid() );
		this.img = null;
	}

	// this is the constructor if you want it to be a variable
	public VolatileFloatType( final int value )
	{
		this( new FloatType( new VolatileFloatArray( 1, true ) ), true );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public VolatileFloatType()
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
		final VolatileFloatAccess a = img.update( c );
		t.dataAccess = a;
		setValid( a.isValid() );
	}

	@Override
	public NativeImg< VolatileFloatType, ? extends VolatileFloatAccess > createSuitableNativeImg( final NativeImgFactory< VolatileFloatType > storageFactory, final long[] dim )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public VolatileFloatType duplicateTypeOnSameNativeImg()
	{
		return new VolatileFloatType( img );
	}

	@Override
	public VolatileFloatType createVariable()
	{
		return new VolatileFloatType( new FloatType( new VolatileFloatArray( 1, true ) ), true );
	}

	@Override
	public VolatileFloatType copy()
	{
		final VolatileFloatType v = createVariable();
		v.set( this );
		return v;
	}
}
