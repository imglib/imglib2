/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Tobias Pietzsch
 */
package net.imglib2.img.sparse;

import net.imglib2.img.basictypeaccess.LongAccess;

/**
 * LongAccess based on a {@link Ntree}<Longeger>.
 * 
 * @author Tobias Pietzsch
 */
public final class LongNtree implements LongAccess, NtreeAccess< Long, LongNtree >
{

	private long[] position;

	private Ntree< Long > data;

	/**
	 * Standard constructor called by factory
	 * 
	 * @param dimensions
	 *            The dimensions of the tree
	 * @param value
	 *            Uniform value of created nodes of the tree
	 */
	public LongNtree( long[] dimensions, long[] position, long value )
	{
		this.data = new Ntree< Long >( dimensions, value );

		this.position = position;
	}

	public LongNtree( Ntree< Long > data, long[] position )
	{
		this.data = data;
		this.position = position;
	}

	@Override
	public void close()
	{
		// TODO:
	}

	@Override
	public long getValue( final int index )
	{
		// ignore index, get tree position from RandomAccess/Cursor
		return data.getNode( position ).getValue();
	}

	@Override
	public void setValue( final int index, final long value )
	{
		// ignore index, get tree position from RandomAccess/Cursor
		data.createNodeWithValue( position, value );
	}

	@Override
	public Ntree< Long > getCurrentStorageNtree()
	{
		return data;
	}

	@Override
	public LongNtree createInstance( long[] position )
	{
		return new LongNtree( data, position );
	}
}
