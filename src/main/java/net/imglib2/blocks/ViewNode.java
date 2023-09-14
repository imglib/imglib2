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
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.converter.read.ConvertedRandomAccessible;
import net.imglib2.converter.read.ConvertedRandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.transform.integer.BoundingBox;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.MixedTransformView;

interface ViewNode
{
	enum ViewType
	{
		NATIVE_IMG,
		IDENTITY, // for wrappers like ImgPlus, ImgView
		INTERVAL, //
		CONVERTER, //
		MIXED_TRANSFORM, // for Mixed transforms
		EXTENSION // oob extensions
	}

	ViewType viewType();

	RandomAccessible< ? > view();

	Interval interval();

	default BoundingBox bbox()
	{
		return interval() == null ? null : new BoundingBox( interval() );
	}


	// -----------------------------------------------
	// implementations
	// -----------------------------------------------


	abstract class AbstractViewNode< V extends RandomAccessible< ? > > implements ViewNode
	{
		final ViewType viewType;

		final V view;

		final Interval interval;

		AbstractViewNode( final ViewType viewType, final V view )
		{
			this.viewType = viewType;
			this.view = view;
			this.interval = view instanceof Interval ? ( Interval ) view : null;
		}

		@Override
		public ViewType viewType()
		{
			return viewType;
		}

		@Override
		public RandomAccessible< ? > view()
		{
			return view;
		}

		@Override
		public Interval interval()
		{
			return interval;
		}
	}

	class DefaultViewNode extends AbstractViewNode< RandomAccessible< ? > >
	{
		DefaultViewNode( final ViewType viewType, final RandomAccessible< ? > view )
		{
			super( viewType, view );
		}

		@Override
		public String toString()
		{
			return "DefaultViewNode{viewType=" + viewType + ", view=" + view + ", interval=" + interval + '}';
		}
	}

	class MixedTransformViewNode extends AbstractViewNode< MixedTransformView< ? > >
	{
		MixedTransformViewNode( final MixedTransformView< ? > view )
		{
			super( ViewType.MIXED_TRANSFORM, view );
		}

		public MixedTransform getTransformToSource()
		{
			return view.getTransformToSource();
		}

		@Override
		public String toString()
		{
			return "MixedTransformViewNode{viewType=" + viewType + ", view=" + view + ", interval=" + interval + ", transformToSource=" + getTransformToSource() + '}';
		}
	}

	class ExtensionViewNode extends AbstractViewNode< ExtendedRandomAccessibleInterval< ?, ? > >
	{
		ExtensionViewNode( final ExtendedRandomAccessibleInterval< ?, ? > view )
		{
			super( ViewType.EXTENSION, view );
		}

		public OutOfBoundsFactory< ?, ? > getOutOfBoundsFactory()
		{
			return view.getOutOfBoundsFactory();
		}

		@Override
		public String toString()
		{
			return "ExtensionViewNode{viewType=" + viewType + ", view=" + view + ", interval=" + interval + ", oobFactory=" + getOutOfBoundsFactory() + '}';
		}
	}

	class ConverterViewNode< A, B > extends AbstractViewNode< RandomAccessible< B > >
	{
		private final Supplier< ? extends B > destinationSupplier;

		private final Supplier< Converter< ? super A, ? super B > > converterSupplier;

		ConverterViewNode( final ConvertedRandomAccessibleInterval< A, B > view )
		{
			super( ViewType.CONVERTER, view );
			converterSupplier = view.getConverterSupplier();
			destinationSupplier = view.getDestinationSupplier();
		}

		ConverterViewNode( final ConvertedRandomAccessible< A, B > view )
		{
			super( ViewType.CONVERTER, view );
			converterSupplier = view.getConverterSupplier();
			destinationSupplier = view.getDestinationSupplier();
		}

		public Supplier< ? extends B > getDestinationSupplier()
		{
			return destinationSupplier;
		}

		public Supplier< Converter< ? super A, ? super B > > getConverterSupplier()
		{
			return converterSupplier;
		}

		@Override
		public String toString()
		{
			return "ConverterViewNode{viewType=" + viewType + ", view=" + view + ", interval=" + interval + '}';
		}
	}
}
