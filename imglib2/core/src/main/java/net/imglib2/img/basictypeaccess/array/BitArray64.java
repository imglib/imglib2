/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.img.basictypeaccess.array;

import net.imglib2.img.basictypeaccess.BitAccess;

/**
 * @author Stephan Preibisch
 */
public class BitArray64 implements BitAccess, ArrayDataAccess< BitArray64 >
{
	final static protected int bitsPerEntity = Long.SIZE;

	final protected int n;

	protected long data[];

	public BitArray64( final int numEntities )
	{
		this.n = numEntities;

		final int numElements;

		if ( this.n % bitsPerEntity == 0 )
			numElements = this.n / bitsPerEntity;
		else
			numElements = this.n / bitsPerEntity + 1;

		this.data = new long[ numElements ];
	}

	@Override
	public boolean getValue( final int index )
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;

		final long entry = data[ arrayIndex ];
		final long value = ( entry & ( 1 << arrayOffset ) );

		return value != 0;
	}

	@Override
	public void setValue( final int index, final boolean value )
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;

		if ( value )
			data[ arrayIndex ] = data[ arrayIndex ] | ( 1 << arrayOffset );
		else
			data[ arrayIndex ] = data[ arrayIndex ] & ~( 1 << arrayOffset );
	}

	@Override
	public long[] getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public BitArray64 createArray( final int numEntities )
	{
		return new BitArray64( numEntities );
	}

}
