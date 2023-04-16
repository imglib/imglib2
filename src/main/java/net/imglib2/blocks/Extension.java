package net.imglib2.blocks;

import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;

import static net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary.SINGLE;

interface Extension
{
	enum Type
	{
		CONSTANT(true),
		BORDER(false),
		MIRROR_SINGLE(false),
		MIRROR_DOUBLE(false),
		UNKNOWN( true );

		private final boolean isValueDependent;

		Type( final boolean isValueDependent )
		{
			this.isValueDependent = isValueDependent;
		}

		/**
		 * Whether this extension depends on the pixel value. E.g., {@code
		 * BORDER}, {@code MIRROR_SINGLE}, {@code MIRROR_DOUBLE} are only
		 * dependent on position ({@code isValueDependent()==false}), while
		 * {@code CONSTANT} is dependent on the out-of-bounds value ({@code
		 * isValueDependent()==true}).
		 */
		public boolean isValueDependent()
		{
			return isValueDependent;
		}
	}

	Type type();

	static Extension border()
	{
		return ExtensionImpl.border;
	}

	static Extension mirrorSingle()
	{
		return ExtensionImpl.mirrorSingle;
	}

	static Extension mirrorDouble()
	{
		return ExtensionImpl.mirrorDouble;
	}

	static < T > Extension constant( T oobValue )
	{
		return new ExtensionImpl.ConstantExtension<>( oobValue );
	}

	static Extension of( OutOfBoundsFactory< ?, ? > oobFactory )
	{
		if ( oobFactory instanceof OutOfBoundsBorderFactory )
		{
			return border();
		}
		else if ( oobFactory instanceof OutOfBoundsMirrorFactory )
		{
			final OutOfBoundsMirrorFactory.Boundary boundary = ( ( OutOfBoundsMirrorFactory< ?, ? > ) oobFactory ).getBoundary();
			return boundary == SINGLE ? mirrorSingle() : mirrorDouble();
		}
		else if ( oobFactory instanceof OutOfBoundsConstantValueFactory )
		{
			return constant( ( ( OutOfBoundsConstantValueFactory ) oobFactory ).getValue() );
		}
		else
		{
			return new ExtensionImpl.UnknownExtension<>( oobFactory );
		}
	}
}
