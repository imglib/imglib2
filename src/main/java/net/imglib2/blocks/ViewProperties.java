/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.blocks;

import java.util.function.Supplier;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;
import net.imglib2.view.TransformBuilder;

/**
 * Data that describes {@code RandomAccessible} View that can be copied from using
 * {@link ViewPrimitiveBlocks}.
 * <p>
 * Use {@link ViewAnalyzer#getViewProperties(RandomAccessible)} to (try to)
 * extract {@code ViewProperties} for a given {@code RandomAccessible}.
 *
 * @param <T>
 * 		type of the view {@code RandomAccessible}
 * @param <R>
 * 		type of the root {@code NativeImg}
 */
class ViewProperties< T extends NativeType< T >, R extends NativeType< R > >
{
	private final T viewType;

	private final NativeImg< R, ? > root;

	private final R rootType;

	private final Extension extension;

	private final MixedTransform transform;

	private final boolean hasTransform;

	private final MixedTransform permuteInvertTransform;

	private final boolean hasPermuteInvertTransform;

	private final Supplier< Converter< R, T > > converterSupplier;

	/**
	 * Create {@code ViewProperties}.
	 *
	 * @param viewType pixel type of the View to copy from
	 * @param root the {@code NativeImg} at the root of the View chain
	 * @param rootType pixel type of the root {@code NativeImg}
	 * @param extension out-of-bounds extension to apply to the root
	 * @param transform the concatenated transform from the final View to the root.
	 * @param permuteInvertTransform captures axis permutation and inversion part in {@code transform}.
	 * @param converterSupplier creates {@code Converter} from {@code rootType} to {@code viewType}.
	 */
	ViewProperties(
			final T viewType,
			final NativeImg< R, ? > root,
			final R rootType,
			final Extension extension,
			final MixedTransform transform,
			final MixedTransform permuteInvertTransform,
			final Supplier< ? extends Converter< ?, ? > > converterSupplier )
	{
		this.viewType = viewType;
		this.root = root;
		this.rootType = rootType;
		this.extension = extension;
		this.transform = transform;
		hasTransform = !TransformBuilder.isIdentity( transform );
		this.permuteInvertTransform = permuteInvertTransform;
		hasPermuteInvertTransform = !TransformBuilder.isIdentity( permuteInvertTransform );
		this.converterSupplier = converterSupplier == null ? null : () -> ( Converter< R, T > ) converterSupplier.get();
	}

	@Override
	public String toString()
	{
		return "ViewProperties{" +
				"viewType=" + viewType.getClass().getSimpleName() +
				", root=" + root +
				", rootType=" + rootType.getClass().getSimpleName() +
				", extension=" + extension +
				", transform=" + transform +
				", hasPermuteInvertTransform=" + hasPermuteInvertTransform +
				", permuteInvertTransform=" + permuteInvertTransform +
				", converterSupplier=" + converterSupplier +
				'}';
	}

	public T getViewType()
	{
		return viewType;
	}

	public NativeImg< R, ? > getRoot()
	{
		return root;
	}

	public R getRootType()
	{
		return rootType;
	}

	public ArrayDataAccess< ? > getRootAccessType()
	{
		return ( ArrayDataAccess< ? > ) getDataAccess( root );
	}

	/*
	 * TODO: There should be a better way to get straight to a DataAccess instance.
	 *       This is similar to the getType() problem.
	 *       For now, this will work.
	 *       Make an issue about getDataAccess() ...
	 */
	private static < A > A getDataAccess( NativeImg< ?, A > img )
	{
		return img.update( img.cursor() );
	}

	public Extension getExtension()
	{
		return extension;
	}

	/**
	 * Returns {@code true} if there is a non-identity {@link #getTransform() transform}.
	 *
	 * @return {@code true} iff the {@link #getTransform() transform} is not identity.
	 */
	public boolean hasTransform()
	{
		return hasTransform;
	}

	public MixedTransform getTransform()
	{
		return transform;
	}

	/**
	 * Returns {@code true} if there is a non-identity {@link
	 * #getPermuteInvertTransform() permute-invert} transform.
	 *
	 * @return {@code true} iff the {@link #getPermuteInvertTransform()
	 * permute-invert} transform is not identity.
	 */
	public boolean hasPermuteInvertTransform()
	{
		return hasPermuteInvertTransform;
	}

	public MixedTransform getPermuteInvertTransform()
	{
		return permuteInvertTransform;
	}

	public boolean hasConverterSupplier()
	{
		return converterSupplier != null;
	}

	public Supplier< Converter< R, T > > getConverterSupplier()
	{
		return converterSupplier;
	}
}
