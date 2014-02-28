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
import java.util.Comparator;
import java.util.Iterator;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.algorithm.componenttree.ComponentTree;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.util.Util;

/**
 * Component tree of an image stored as a tree of {@link PixelListComponent}s.
 * This class is used both to represent and build the tree. For building the
 * tree {@link Component.Handler} is implemented to gather
 * {@link PixelListComponentIntermediate} emitted by {@link ComponentTree}.
 * 
 * <p>
 * <strong>TODO</strong> Add support for non-zero-min RandomAccessibleIntervals.
 * (Currently, we assume that the input image is a <em>zero-min</em> interval.)
 * </p>
 * 
 * @param <T>
 *            value type of the input image.
 * 
 * @author Tobias Pietzsch
 */
public final class PixelListComponentTree< T extends Type< T > > implements Component.Handler< PixelListComponentIntermediate< T > >, Iterable< PixelListComponent< T > >
{
	/**
	 * Build a component tree from an input image. Calls
	 * {@link #buildComponentTree(RandomAccessibleInterval, RealType, ImgFactory, boolean)}
	 * using an {@link ArrayImgFactory} or {@link CellImgFactory} depending on
	 * input image size.
	 * 
	 * @param input
	 *            the input image.
	 * @param type
	 *            a variable of the input image type.
	 * @param darkToBright
	 *            whether to apply thresholds from dark to bright (true) or
	 *            bright to dark (false)
	 * @return component tree of the image.
	 */
	public static < T extends RealType< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, final boolean darkToBright )
	{
		final ImgFactory< LongType > factory = Util.getArrayOrCellImgFactory( input, new LongType() );
		return buildComponentTree( input, type, factory, darkToBright );
	}

	/**
	 * Build a component tree from an input image.
	 * 
	 * @param input
	 *            the input image.
	 * @param type
	 *            a variable of the input image type.
	 * @param imgFactory
	 *            used for creating the {@link PixelList} image {@see
	 *            PixelListComponentGenerator}.
	 * @param darkToBright
	 *            whether to apply thresholds from dark to bright (true) or
	 *            bright to dark (false)
	 * @return component tree of the image.
	 */
	public static < T extends RealType< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, final ImgFactory< LongType > imgFactory, final boolean darkToBright )
	{
		final T max = type.createVariable();
		max.setReal( darkToBright ? type.getMaxValue() : type.getMinValue() );
		final PixelListComponentGenerator< T > generator = new PixelListComponentGenerator< T >( max, input, imgFactory );
		final PixelListComponentTree< T > tree = new PixelListComponentTree< T >();
		ComponentTree.buildComponentTree( input, generator, tree, darkToBright );
		return tree;
	}

	/**
	 * Build a component tree from an input image. Calls
	 * {@link #buildComponentTree(RandomAccessibleInterval, Type, Comparator, ImgFactory)}
	 * using an {@link ArrayImgFactory} or {@link CellImgFactory} depending on
	 * input image size.
	 * 
	 * @param input
	 *            the input image.
	 * @param maxValue
	 *            a value (e.g., grey-level) greater than any occurring in the
	 *            input image.
	 * @param comparator
	 *            determines ordering of threshold values.
	 * @return component tree of the image.
	 */
	public static < T extends Type< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T maxValue, final Comparator< T > comparator )
	{
		final ImgFactory< LongType > factory = Util.getArrayOrCellImgFactory( input, new LongType() );
		return buildComponentTree( input, maxValue, comparator, factory );
	}

	/**
	 * Build a component tree from an input image.
	 * 
	 * @param input
	 *            the input image.
	 * @param maxValue
	 *            a value (e.g., grey-level) greater than any occurring in the
	 *            input image.
	 * @param comparator
	 *            determines ordering of threshold values.
	 * @param imgFactory
	 *            used for creating the {@link PixelList} image {@see
	 *            PixelListComponentGenerator}.
	 * @return component tree of the image.
	 */
	public static < T extends Type< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T maxValue, final Comparator< T > comparator, final ImgFactory< LongType > imgFactory )
	{
		final PixelListComponentGenerator< T > generator = new PixelListComponentGenerator< T >( maxValue, input, imgFactory );
		final PixelListComponentTree< T > tree = new PixelListComponentTree< T >();
		ComponentTree.buildComponentTree( input, generator, tree, comparator );
		return tree;
	}

	private PixelListComponent< T > root;

	private final ArrayList< PixelListComponent< T > > nodes;

	private PixelListComponentTree()
	{
		root = null;
		nodes = new ArrayList< PixelListComponent< T > >();
	}

	@Override
	public void emit( final PixelListComponentIntermediate< T > intermediate )
	{
		final PixelListComponent< T > component = new PixelListComponent< T >( intermediate );
		root = component;
		nodes.add( component );
	}

	/**
	 * Returns an iterator over all connected components in the tree.
	 * 
	 * @return iterator over all connected components in the tree.
	 */
	@Override
	public Iterator< PixelListComponent< T > > iterator()
	{
		return nodes.iterator();
	}

	/**
	 * Get the root component.
	 * 
	 * @return root component.
	 */
	public PixelListComponent< T > root()
	{
		return root;
	}
}
