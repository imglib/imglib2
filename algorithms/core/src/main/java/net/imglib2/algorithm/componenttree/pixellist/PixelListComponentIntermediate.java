/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.algorithm.componenttree.pixellist;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.type.Type;

/**
 * Implementation of {@link Component} that stores a list of associated pixels
 * in a {@link PixelList}.
 * 
 * @param <T>
 *            value type of the input image.
 * 
 * @author Tobias Pietzsch
 */
final class PixelListComponentIntermediate< T extends Type< T > > implements Component< T >
{
	/**
	 * Threshold value of the connected component.
	 */
	private final T value;

	/**
	 * Pixels in the component.
	 */
	final PixelList pixelList;

	/**
	 * A list of PixelListComponentIntermediate merged into this one since it
	 * was last emitted. (For building up component tree.)
	 */
	final ArrayList< PixelListComponentIntermediate< T > > children;

	/**
	 * The PixelListComponent assigned to this PixelListComponentIntermediate
	 * when it was last emitted. (For building up component tree.)
	 */
	PixelListComponent< T > emittedComponent;

	/**
	 * Create new empty component.
	 * 
	 * @param value
	 *            (initial) threshold value {@see #getValue()}.
	 * @param generator
	 *            the {@link PixelListComponentGenerator#linkedList} is used to
	 *            store the {@link #pixelList}.
	 */
	PixelListComponentIntermediate( final T value, final PixelListComponentGenerator< T > generator )
	{
		pixelList = new PixelList( generator.linkedList.randomAccess(), generator.dimensions );
		this.value = value.copy();
		children = new ArrayList< PixelListComponentIntermediate< T > >();
		emittedComponent = null;
	}

	@Override
	public void addPosition( final Localizable position )
	{
		pixelList.addPosition( position );
	}

	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}

	@Override
	public void merge( final Component< T > component )
	{
		final PixelListComponentIntermediate< T > c = ( PixelListComponentIntermediate< T > ) component;
		pixelList.merge( c.pixelList );
		children.add( c );
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : ";
		boolean first = true;
		for ( final Localizable l : pixelList )
		{
			if ( first )
			{
				first = false;
			}
			else
			{
				s += ", ";
			}
			s += l.toString();
		}
		return s + "}";
	}
}
