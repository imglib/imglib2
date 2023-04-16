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
