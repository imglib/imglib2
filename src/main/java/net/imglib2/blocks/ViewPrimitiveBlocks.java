package net.imglib2.blocks;

import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveType;
import net.imglib2.util.Cast;
import net.imglib2.util.Intervals;

import static net.imglib2.blocks.PrimitiveBlocksUtils.extractOobValue;

class ViewPrimitiveBlocks< T extends NativeType< T >, R extends NativeType< R > > implements PrimitiveBlocks< T >
{
	private final ViewProperties< T, R > props;

	// copies from view root. root type primitive equivalent
	private final RangeCopier copier;

	// root primitive type
	private final TempArray< R > tempArrayPermute;

	// root primitive type
	private final TempArray< R > tempArrayConvert;

	private final PermuteInvert permuteInvert;

	private final Convert convert;

	public ViewPrimitiveBlocks( final ViewProperties< T, R > props )
	{
		this.props = props;
		final PrimitiveType primitiveType = props.getRootType().getNativeTypeFactory().getPrimitiveType();
		final MemCopy memCopy = MemCopy.forPrimitiveType( primitiveType );
		final Extension extension = props.getExtension() != null ? props.getExtension() : Extension.border();
		final Object oob = extractOobValue( props.getRootType(), extension );
		final Ranges findRanges = Ranges.forExtension( extension );
		copier = RangeCopier.create( props.getRoot(), findRanges, memCopy, oob );
		tempArrayConvert = Cast.unchecked( TempArray.forPrimitiveType( primitiveType ) );
		tempArrayPermute = Cast.unchecked( TempArray.forPrimitiveType( primitiveType ) );
		permuteInvert = new PermuteInvert( memCopy, props.getPermuteInvertTransform() );
		convert = props.hasConverterSupplier()
				? Convert.create( props.getRootType(), props.getViewType(), props.getConverterSupplier() )
				: null;
	}

	@Override
	public T getType()
	{
		return props.getViewType();
	}

	/**
	 * @param srcPos
	 * 		min coordinates of block to copy from src Img.
	 * @param dest
	 * 		destination array. Type is {@code byte[]}, {@code float[]},
	 * 		etc, corresponding to the src Img's native type.
	 * @param size
	 * 		dimensions of block to copy from src Img.
	 */
	public void copy( final long[] srcPos, final Object dest, final int[] size )
	{
		final long[] destPos;
		final int[] destSize;
		if ( props.hasTransform() )
		{
			final MixedTransform transform = props.getTransform();
			final int n = transform.numTargetDimensions();
			destPos = new long[ n ];
			destSize = new int[ n ];
			for ( int d = 0; d < n; d++ )
			{
				final int t = ( int ) transform.getTranslation( d );
				if ( transform.getComponentZero( d ) )
				{
					destPos[ d ] = t;
					destSize[ d ] = 1;
				}
				else
				{
					final int c = transform.getComponentMapping( d );
					destPos[ d ] = transform.getComponentInversion( d )
							? t - srcPos[ c ] - size[ c ] + 1
							: t + srcPos[ c ];
					destSize[ d ] = size[ c ];
				}
			}
		}
		else
		{
			destPos = srcPos;
			destSize = size;
		}

		final boolean doPermute = props.hasPermuteInvertTransform();
		final boolean doConvert = props.hasConverterSupplier();
		final int length = ( int ) Intervals.numElements( size );
		if ( doPermute && doConvert )
		{
			final Object copyDest = tempArrayPermute.get( length );
			final Object permuteDest = tempArrayConvert.get( length );
			copier.copy( destPos, copyDest, destSize );
			permuteInvert.permuteAndInvert( copyDest, permuteDest, size );
			convert.convert( permuteDest, dest, length );
		}
		else if ( doPermute )
		{
			final Object copyDest = tempArrayConvert.get( length );
			copier.copy( destPos, copyDest, destSize );
			permuteInvert.permuteAndInvert( copyDest, dest, size );
		}
		else if ( doConvert )
		{
			final Object copyDest = tempArrayPermute.get( length );
			copier.copy( destPos, copyDest, destSize );
			convert.convert( copyDest, dest, length );
		}
		else
		{
			copier.copy( destPos, dest, destSize );
		}
	}

	@Override
	public PrimitiveBlocks< T > threadSafe()
	{
		return PrimitiveBlocksUtils.threadSafe( this::newInstance );
	}

	ViewPrimitiveBlocks< T, R > newInstance()
	{
		return new ViewPrimitiveBlocks<>( this );
	}

	private ViewPrimitiveBlocks( final ViewPrimitiveBlocks< T, R > blocks )
	{
		props = blocks.props;
		copier = blocks.copier.newInstance();
		permuteInvert = blocks.permuteInvert.newInstance();
		convert = blocks.convert == null ? null : blocks.convert.newInstance();
		tempArrayConvert = blocks.tempArrayConvert.newInstance();
		tempArrayPermute = blocks.tempArrayPermute.newInstance();
	}
}
