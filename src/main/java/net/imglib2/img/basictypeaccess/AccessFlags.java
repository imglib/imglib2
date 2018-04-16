package net.imglib2.img.basictypeaccess;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

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

	public static Set< AccessFlags > ofAccess( final Object access )
	{
		final boolean dirtyAccesses = ( access instanceof Dirty );
		final boolean volatileAccesses = ( access instanceof VolatileAccess );
		return fromBooleansDirtyVolatile( dirtyAccesses, volatileAccesses );
	}

	public static Set< AccessFlags > setOf()
	{
		return flags_NONE;
	}

	public static Set< AccessFlags > setOf( final AccessFlags f1 )
	{
		return f1 == DIRTY ? flags_DIRTY : flags_VOLATILE;
	}

	public static Set< AccessFlags > setOf( final AccessFlags f1, final AccessFlags f2 )
	{
		return f1 == DIRTY
				? ( f2 == DIRTY ? flags_DIRTY : flags_DIRTY_VOLATILE )
				: ( f2 == DIRTY ? flags_DIRTY_VOLATILE : flags_VOLATILE);
	}

	public static Set< AccessFlags > setOf( final AccessFlags... flags )
	{
		boolean dirtyAccesses = false;
		boolean volatileAccesses = false;
		for ( AccessFlags flag : flags )
		{
			if ( flag == DIRTY )
				dirtyAccesses = true;
			else if ( flag == VOLATILE )
				volatileAccesses = true;
		}
		return fromBooleansDirtyVolatile( dirtyAccesses, volatileAccesses );
	}

	public static Set< AccessFlags > fromBooleansDirtyVolatile( final boolean dirtyAccesses, final boolean volatileAccesses )
	{
		return dirtyAccesses
				? ( volatileAccesses ? flags_DIRTY_VOLATILE : flags_DIRTY )
				: ( volatileAccesses ? flags_VOLATILE : flags_NONE );
	}

	private final static Set< AccessFlags > flags_DIRTY_VOLATILE = Collections.unmodifiableSet( EnumSet.of( DIRTY, VOLATILE ) );
	private final static Set< AccessFlags > flags_DIRTY = Collections.unmodifiableSet( EnumSet.of( DIRTY ) );
	private final static Set< AccessFlags > flags_VOLATILE = Collections.unmodifiableSet( EnumSet.of( VOLATILE ) );
	private final static Set< AccessFlags > flags_NONE = Collections.unmodifiableSet( EnumSet.noneOf( AccessFlags.class ) );
}
