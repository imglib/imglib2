/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.img.planar;

import net.imglib2.LinearAccess;
import net.imglib2.img.planar.PlanarImg.PlanarContainerSampler;
import net.imglib2.type.NativeType;

/**
 *
 * Implementation of {@link LinearAccess} for {@link PlanarImg}
 *
 * @author Philipp Hanslovsky
 *
 * @param <T>
 */
public class PlanarLinearAccess< T extends NativeType< T > > implements LinearAccess< T >, PlanarContainerSampler
{
	protected final T type;

	private final int sliceSize;

	private final int lastIndex;

	private final int lastSliceIndex;

	private int index;

	private int sliceIndex;

	private PlanarLinearAccess( final PlanarLinearAccess< T > access )
	{
		this.type = access.type.duplicateTypeOnSameNativeImg();

		this.lastIndex = access.lastIndex;
		this.lastSliceIndex = access.lastSliceIndex;
		this.sliceSize = access.sliceSize;

		this.index = access.index;
		this.sliceIndex = access.sliceIndex;

		type.updateContainer( this );
		type.updateIndex( access.index );
	}

	public PlanarLinearAccess( final PlanarImg< T, ? > img )
	{
		this.type = img.createLinkedType();

		this.sliceSize = ( img.numDimensions() > 1 ? img.dimensions[ 1 ] : 1 ) * img.dimensions[ 0 ];
		this.lastIndex = this.sliceSize - 1;
		this.lastSliceIndex = img.numSlices() - 1;

		this.index = 0;
		this.sliceIndex = 0;

		type.updateContainer( this );
		type.updateIndex( this.index );
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public PlanarLinearAccess< T > copy()
	{
		return new PlanarLinearAccess<>( this );
	}

	@Override
	public void set( final long position )
	{
		set( ( int ) position );
	}

	@Override
	public void set( final int position )
	{
		final int sliceIndex = position / sliceSize;
		if ( sliceIndex != this.sliceIndex )
		{
			this.sliceIndex = sliceIndex;
			type.updateContainer( this );
		}

		this.index = position - this.sliceIndex * sliceSize;
		type.updateIndex( this.index );
	}

	@Override
	public long getPosition()
	{
		return this.sliceIndex * sliceSize + this.index;
	}

	@Override
	public int getCurrentSliceIndex()
	{
		return this.sliceIndex;
	}

}
