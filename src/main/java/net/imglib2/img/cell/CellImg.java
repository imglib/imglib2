/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.type.NativeType;

/**
 * This {@link Img} stores an image in multiple linear arrays of basic types.
 * Much larger images can be represented than with {@link ArrayImg}. The number
 * of basic types that can be stored is {@link Integer#MAX_VALUE}<sup>2</sup>.
 * However, access is less efficient than for {@link ArrayImg}.
 * 
 * The image is divided into cells, that is, equally sized hyper-cubes (only
 * cells at the max boundary of the image may have non-standard sizes). Each
 * basic type array corresponds to a cell.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
final public class CellImg< T extends NativeType< T >, A, C extends AbstractCell< A > > extends AbstractCellImg< T, A, C, CellImgFactory< T > >
{

	public CellImg( final CellImgFactory< T > factory, final Cells< A, C > cells )
	{
		super( factory, cells );
	}

	@Override
	public CellImg< T, A, C > copy()
	{
		@SuppressWarnings( "unchecked" )
		final CellImg< T, A, C > copy = ( CellImg< T, A, C > ) factory().create( dimension, firstElement().createVariable() );
		copyDataTo( copy );
		return copy;
	}
}
