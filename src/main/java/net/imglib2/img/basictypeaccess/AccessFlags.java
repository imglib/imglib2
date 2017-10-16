package net.imglib2.img.basictypeaccess;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileAccess;

/**
 * Flags that specify variants of {@link ArrayDataAccess} underlying primitive
 * types. {@link #DIRTY} means that an access implements {@link Dirty}.
 * {@link #VOLATILE} means that an access implements {@link VolatileAccess}.
 *
 * @author Tobias Pietzsch
 */
public enum AccessFlags
{
	DIRTY, VOLATILE;

	public static AccessFlags[] of( final Object access )
	{
		final boolean dirtyAccesses = ( access instanceof Dirty );
		final boolean volatileAccesses = ( access instanceof VolatileAccess );
		return fromBooleansDirtyVolatile( dirtyAccesses, volatileAccesses );
	}

	public static AccessFlags[] fromBooleansDirtyVolatile( final boolean dirtyAccesses, final boolean volatileAccesses )
	{
		return dirtyAccesses
				? ( volatileAccesses
						? flags_DIRTY_VOLATILE
						: flags_DIRTY )
				: ( volatileAccesses
						? flags_VOLATILE
						: flags_NONE );
	}

	public static boolean isDirty( final AccessFlags[] flags )
	{
		for ( final AccessFlags flag : flags )
			if ( flag == DIRTY )
				return true;
		return false;
	}

	public static boolean isVolatile( final AccessFlags[] flags )
	{
		for ( final AccessFlags flag : flags )
			if ( flag == VOLATILE )
				return true;
		return false;
	}

	static AccessFlags[] flags_DIRTY_VOLATILE = new AccessFlags[] { DIRTY, VOLATILE };
	static AccessFlags[] flags_DIRTY = new AccessFlags[] { DIRTY };
	static AccessFlags[] flags_VOLATILE = new AccessFlags[] { VOLATILE };
	static AccessFlags[] flags_NONE = new AccessFlags[] {};
}
