package net.imglib2.blocks;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

class FallbackPrimitiveBlocks< T extends NativeType< T >, A extends ArrayDataAccess< A > > implements PrimitiveBlocks< T >
{
	private final RandomAccessible< T > source;

	private final T type;

	private final PrimitiveTypeProperties< ?, A > primitiveTypeProperties;

	private final NativeTypeFactory< T, A > nativeTypeFactory;

	public FallbackPrimitiveBlocks( final FallbackProperties< T > props )
	{
		this( props.getView(), props.getViewType() );
	}

	public FallbackPrimitiveBlocks( final RandomAccessible< T > source, final T type )
	{
		this.source = source;
		this.type = type;

		if ( type.getEntitiesPerPixel().getRatio() != 1 )
			throw new IllegalArgumentException( "Types with entitiesPerPixel != 1 are not supported" );

		nativeTypeFactory = ( NativeTypeFactory< T, A > ) type.getNativeTypeFactory();
		primitiveTypeProperties = ( PrimitiveTypeProperties< ?, A > ) PrimitiveTypeProperties.get( nativeTypeFactory.getPrimitiveType() );
	}

	@Override
	public T getType()
	{
		return type;
	}

	@Override
	public void copy( final long[] srcPos, final Object dest, final int[] size )
	{
		final ArrayImg< T, A > img = new ArrayImg<>( primitiveTypeProperties.wrap( dest ), Util.int2long( size ), type.getEntitiesPerPixel() );
		img.setLinkedType( nativeTypeFactory.createLinkedType( img ) );
		final FinalInterval interval = FinalInterval.createMinSize( srcPos, Util.int2long( size ) );
		LoopBuilder.setImages( Views.interval( source, interval ), img ).forEachPixel( ( a, b ) -> b.set( a ) );
	}

	@Override
	public PrimitiveBlocks< T > threadSafe()
	{
		return this;
	}
}
