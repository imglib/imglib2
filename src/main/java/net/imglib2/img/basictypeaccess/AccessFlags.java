/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
