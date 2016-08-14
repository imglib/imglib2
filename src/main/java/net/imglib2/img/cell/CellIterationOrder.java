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

package net.imglib2.img.cell;

/**
 * Iteration order for a {@link CellImg}. A {@link CellImg} is iterated cell by
 * cell, where each cell is iterated in flat iteration order.
 * 
 * @author Tobias Pietzsch
 */
public class CellIterationOrder
{
	private final AbstractCellImg< ?, ?, ?, ? > img;

	public CellIterationOrder( final AbstractCellImg< ?, ?, ?, ? > img )
	{
		this.img = img;
	}

	/**
	 * To be equal an object has to be a {@link CellIterationOrder} on a
	 * {@link CellImg} of the same dimensions and cell-dimensions.
	 * 
	 * @return true, if obj is a compatible {@link CellIterationOrder}.
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof CellIterationOrder ) )
			return false;

		final AbstractCellImg< ?, ?, ?, ? > i = ( ( CellIterationOrder ) obj ).img;
		if ( i.numDimensions() != img.numDimensions() )
			return false;

		final int n = img.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( i.dimension( d ) != img.dimension( d ) || i.cellDims[ d ] != img.cellDims[ d ] )
				return false;

		return true;
	}
}
