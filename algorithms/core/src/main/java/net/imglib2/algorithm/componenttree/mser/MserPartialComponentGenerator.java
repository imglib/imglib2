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

package net.imglib2.algorithm.componenttree.mser;

import java.util.LinkedList;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.PartialComponent;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.LongType;

/**
 * Implementation of {@link PartialComponent.Generator} for creating
 * {@link MserPartialComponent} components. According to the dimensions of the
 * input image it creates a {@link LongType} {@link Img} to store the
 * {@link PixelList} used in the components.
 *
 * @param <T>
 *            value type of the input image.
 *
 * @author Tobias Pietzsch
 */
final class MserPartialComponentGenerator< T extends Type< T > > implements PartialComponent.Generator< T, MserPartialComponent< T > >
{
	final T maxValue;

	/**
	 * Dimensions of the {@link LinkedList} image.
	 */
	final long[] dimensions;

	/**
	 * Represents a singly-linked list of pixel locations {@see PixelList}.
	 */
	final Img< LongType > linkedList;

	/**
	 * According to the dimensions of the input image, create a {@link LongType}
	 * {@link Img} to store the {@link PixelList} used in the components
	 * generated {@link #createComponent(Type)}.
	 *
	 * @param maxValue
	 *            a value (e.g., grey-level) greater than any occurring in the
	 *            input image.
	 * @param input
	 *            input image.
	 * @param imgFactory
	 *            used to create PixelList image.
	 */
	public MserPartialComponentGenerator( final T maxValue, final RandomAccessibleInterval< T > input, final ImgFactory< LongType > imgFactory )
	{
		this.maxValue = maxValue;
		dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );
		linkedList = imgFactory.create( dimensions, new LongType() );
	}

	@Override
	public MserPartialComponent< T > createComponent( final T value )
	{
		return new MserPartialComponent< T >( value, this );
	}

	@Override
	public MserPartialComponent< T > createMaxComponent()
	{
		return new MserPartialComponent< T >( maxValue, this );
	}
}
