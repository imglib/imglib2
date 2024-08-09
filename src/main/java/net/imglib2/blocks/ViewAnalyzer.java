/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.imglib2.RandomAccessible;
import net.imglib2.blocks.ViewNode.ConverterViewNode;
import net.imglib2.blocks.ViewNode.DefaultViewNode;
import net.imglib2.blocks.ViewNode.ExtensionViewNode;
import net.imglib2.blocks.ViewNode.MixedTransformViewNode;
import net.imglib2.converter.Converter;
import net.imglib2.converter.read.ConvertedRandomAccessible;
import net.imglib2.converter.read.ConvertedRandomAccessibleInterval;
import net.imglib2.img.ImgView;
import net.imglib2.img.NativeImg;
import net.imglib2.img.WrappedImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.view.fluent.RandomAccessibleView;
import net.imglib2.transform.integer.BoundingBox;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.util.Cast;
import net.imglib2.util.Intervals;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.MixedTransformView;

class ViewAnalyzer
{
	/**
	 * The target {@code RandomAccessible}, that is, the View to analyze.
	 */
	private final RandomAccessible< ? > ra;

	/**
	 * View sequence of the target {@code RandomAccessible}. The first element
	 * is the target {@code RandomAccessible} itself. The last element is the
	 * source {@code NativeImg} where the View sequence originates.
	 */
	private final List< ViewNode > nodes = new ArrayList<>();

	private final StringBuilder errorDescription = new StringBuilder();

	private ViewAnalyzer( final RandomAccessible< ? > ra )
	{
		this.ra = ra;
	}

	/**
	 * Check whether the pixel {@code Type} of the View is supported. All {@code
	 * NativeType}s with {@code entitiesPerPixel==1} are supported.
	 *
	 * @return {@code true}, if the view's pixel type is supported.
	 */
	private < T extends Type< T > > boolean checkViewTypeSupported()
	{
		final T type = Cast.unchecked( ra.getType() );
		if ( type instanceof NativeType
				&& ( ( NativeType ) type ).getEntitiesPerPixel().getRatio() == 1 )
		{
			return true;
		}
		else
		{
			errorDescription.append(
					"The pixel Type of the View must be a NativeType with entitiesPerPixel==1. (Found "
							+ type.getClass().getSimpleName() + ")" );
			return false;
		}
	}

	/**
	 * Deconstruct the View sequence of the target {@link #ra RandomAccessible}
	 * into a list of {@link #nodes ViewNodes}.
	 *
	 * @return {@code false}, if during the analysis a View type is encountered that can not be handled.
	 *         {@code true}, if everything went ok.
	 */
	private boolean analyze()
	{
		RandomAccessible< ? > source = ra;
		while ( source != null )
		{
			// NATIVE_IMG,
			if ( source instanceof NativeImg )
			{
				final NativeImg< ?, ? > view = ( NativeImg< ?, ? > ) source;
				nodes.add( new DefaultViewNode( ViewNode.ViewType.NATIVE_IMG, view ) );
				source = null;
			}
			// IDENTITY,
			else if ( source instanceof WrappedImg )
			{
				final WrappedImg< ? > view = ( WrappedImg< ? > ) source;
				nodes.add( new DefaultViewNode( ViewNode.ViewType.IDENTITY, source ) );
				source = view.getImg();
			}
			else if ( source instanceof ImgView )
			{
				final ImgView< ? > view = ( ImgView< ? > ) source;
				nodes.add( new DefaultViewNode( ViewNode.ViewType.IDENTITY, view ) );
				source = view.getSource();
			}
			else if ( source instanceof RandomAccessibleView )
			{
				final RandomAccessibleView< ?, ? > view = ( RandomAccessibleView< ?, ? > ) source;
				nodes.add( new DefaultViewNode( ViewNode.ViewType.IDENTITY, view ) );
				source = view.delegate();
			}
			// INTERVAL,
			else if ( source instanceof IntervalView )
			{
				final IntervalView< ? > view = ( IntervalView< ? > ) source;
				nodes.add( new DefaultViewNode( ViewNode.ViewType.INTERVAL, view ) );
				source = view.getSource();
			}
			// CONVERTER,
			else if ( source instanceof ConvertedRandomAccessible )
			{
				final ConvertedRandomAccessible< ?, ? > view = ( ConvertedRandomAccessible< ?, ? > ) source;
				nodes.add( new ConverterViewNode<>( view ) );
				source = view.getSource();
			}
			else if ( source instanceof ConvertedRandomAccessibleInterval )
			{
				final ConvertedRandomAccessibleInterval< ?, ? > view = ( ConvertedRandomAccessibleInterval< ?, ? > ) source;
				nodes.add( new ConverterViewNode<>( view ) );
				source = view.getSource();
			}
			// MIXED_TRANSFORM,
			else if ( source instanceof MixedTransformView )
			{
				final MixedTransformView< ? > view = ( MixedTransformView< ? > ) source;
				nodes.add( new MixedTransformViewNode( view ) );
				source = view.getSource();
			}
			// EXTENSION
			else if ( source instanceof ExtendedRandomAccessibleInterval )
			{
				ExtendedRandomAccessibleInterval< ?, ? > view = ( ExtendedRandomAccessibleInterval< ?, ? > ) source;
				nodes.add( new ExtensionViewNode( view ) );
				source = view.getSource();
			}
			// fallback
			else
			{
				errorDescription.append( "Cannot analyze view " + source + " of class " + source.getClass().getSimpleName() );
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether the root of the View sequence is supported. Supported roots
	 * are {@code PlanarImg}, {@code ArrayImg}, and {@code CellImg} variants.
	 *
	 * @return {@code true}, if the root is supported.
	 */
	private boolean checkRootSupported()
	{
		final ViewNode root = nodes.get( nodes.size() - 1 );
		if ( root.viewType() != ViewNode.ViewType.NATIVE_IMG )
		{
			errorDescription.append( "The root of the View sequence must be a NativeImg. (Found "
					+ root.view() + " of class " + root.view().getClass().getSimpleName() + ")" );
			return false;
		}
		if ( ( root.view() instanceof PlanarImg )
				|| ( root.view() instanceof ArrayImg )
				|| ( root.view() instanceof AbstractCellImg ) )
		{
			return true;
		}
		else
		{
			errorDescription.append(
					"The root of the View sequence must be PlanarImg, ArrayImg, or AbstractCellImg. (Found "
							+ root.view() + " of class " + root.view().getClass().getSimpleName() + ")" );
			return false;
		}
	}

	/**
	 * Check whether the pixel {@code Type} of the root of the View sequence is
	 * supported. All {@code NativeType}s with {@code entitiesPerPixel==1} are
	 * supported.
	 *
	 * @return {@code true}, if the root's pixel type is supported.
	 */
	private boolean checkRootTypeSupported()
	{
		final ViewNode root = nodes.get( nodes.size() - 1 );
		final NativeType< ? > type = ( NativeType< ? > ) ( ( NativeImg< ?, ? > ) root.view() ).createLinkedType();
		if ( type.getEntitiesPerPixel().getRatio() == 1 )
		{
			return true;
		}
		else
		{
			errorDescription.append(
					"The pixel Type of root of the View sequence must be a NativeType with entitiesPerPixel==1. (Found "
							+ type.getClass().getSimpleName() + ")" );
			return false;
		}
	}

	/**
	 * The index of the out-of-bounds extension in {@link #nodes}.
	 */
	private int oobIndex = -1;

	/**
	 * The description of the out-of-bounds extension.
	 */
	private Extension oobExtension = null;


	/**
	 * Check whether there is at most one out-of-bounds extension.
	 * If an extension is found, store its index into {@link #oobIndex},
	 * and its description into {@link #oobExtension}.
	 *
	 * @return {@code true}, if there is at most one out-of-bounds extension.
	 *         {@code false}, otherwise
	 */
	private boolean checkExtensions1()
	{
		// TODO: This could be weakened to allow for extensions that are
		//       "swallowed" by subsequent extensions on a fully contained
		//       sub-interval (i.e., the earlier extension doesn't really do
		//       anything).

		oobIndex = -1;
		for ( int i = 0; i < nodes.size(); i++ )
		{
			if ( nodes.get( i ).viewType() == ViewNode.ViewType.EXTENSION )
			{
				if ( oobIndex < 0 )
					oobIndex = i;
				else
				{
					errorDescription.append( "There must be at most one out-of-bounds extension." );
					return false;
				}
			}
		}

		if (oobIndex >= 0)
		{
			final ExtensionViewNode node = ( ExtensionViewNode ) nodes.get( oobIndex );
			oobExtension = Extension.of( node.getOutOfBoundsFactory() );
		}
		return true;
	}

	/**
	 * Check whether the out-of-bounds extension (if any) is of a supported type
	 * (constant-value, border, mirror-single, mirror-double).
	 *
	 * @return {@code true}, if the out-of-bounds extension is of a supported
	 *         type, or if there is no extension.
	 */
	private boolean checkExtensions2()
	{
		// TODO: This could be weakened to allow for unknown extensions, by using
		//       fast copying for in-bounds regions, and fall-back for the rest.

		if ( oobIndex < 0 ) // there is no extension
			return true;

		if ( oobExtension.type() != Extension.Type.UNKNOWN )
		{
			return true;
		}
		else
		{
			final ExtensionViewNode node = ( ExtensionViewNode ) nodes.get( oobIndex );
			errorDescription.append(
					"Only constant-value, border, mirror-single, mirror-double out-of-bounds extensions are supported. (Found "
							+ node.getOutOfBoundsFactory().getClass().getSimpleName() + ")" );
			return false;
		}
	}

	/**
	 * Check whether the interval at the out-of-bounds extension is compatible.
	 * The interval must be equal to the root interval carried through the
	 * transforms so far. This means that the extension can be applied to the
	 * root directly (assuming that extension method is the same for every
	 * axis.)
	 *
	 * @return {@code true}, if the out-of-bounds extension interval is
	 *         compatible, or if there is no extension.
	 */
	private boolean checkExtensions3()
	{
		// TODO: This could be weakened to allow intervals that are fully
		//       contained in the bottom interval. This would require revising
		//       the Ranges.findRanges() implementations.

		if ( oobIndex < 0 ) // there is no extension
			return true;

		BoundingBox bbExtension = nodes.get( oobIndex + 1 ).bbox();
		BoundingBox bb = nodes.get( nodes.size() - 1 ).bbox();
		for ( int i = nodes.size() - 1; i > oobIndex; --i )
		{
			final ViewNode node = nodes.get( i );

			// all other view types are ignored.
			if ( node.viewType() == ViewNode.ViewType.MIXED_TRANSFORM )
			{
				final MixedTransform t = ( ( MixedTransformViewNode ) node ).getTransformToSource();
				bb = transform( t, bb );
			}
		}

		if ( Intervals.equals( bb.getInterval(), bbExtension.getInterval() ) )
		{
			return true;
		}
		else
		{
			errorDescription.append(
					"The interval at the out-of-bounds extension must be equal to the root interval carried through the transforms so far." );
			return false;
		}
	}

	/**
	 * Apply the {@code transformToSource} to a target vector to obtain a
	 * source vector.
	 *
	 * @param transformToSource
	 * 		the transformToSource from target to source.
	 * @param source
	 * 		set this to the source coordinates.
	 * @param target
	 * 		target coordinates.
	 */
	private static void apply( MixedTransform transformToSource, long[] source, long[] target )
	{
		assert source.length >= transformToSource.numSourceDimensions();
		assert target.length >= transformToSource.numSourceDimensions();

		for ( int d = 0; d < transformToSource.numTargetDimensions(); ++d )
		{
			if ( !transformToSource.getComponentZero( d ) )
			{
				long v = target[ d ] - transformToSource.getTranslation( d );
				source[ transformToSource.getComponentMapping( d ) ] = transformToSource.getComponentInversion( d ) ? -v : v;
			}
		}
	}

	/**
	 * Apply the {@code transformToSource} to a target bounding box to obtain a
	 * source bounding box.
	 *
	 * @param transformToSource
	 * 		the transformToSource from target to source.
	 * @param boundingBox
	 * 		the target bounding box.
	 *
	 * @return the source bounding box.
	 */
	private static BoundingBox transform( final MixedTransform transformToSource, final BoundingBox boundingBox )
	{
		assert boundingBox.numDimensions() == transformToSource.numSourceDimensions();

		if ( transformToSource.numSourceDimensions() == transformToSource.numTargetDimensions() )
		{ // apply in-place
			final long[] tmp = new long[ transformToSource.numTargetDimensions() ];
			boundingBox.corner1( tmp );
			apply( transformToSource, boundingBox.corner1, tmp );
			boundingBox.corner2( tmp );
			apply( transformToSource, boundingBox.corner2, tmp );
			return boundingBox;
		}
		final BoundingBox b = new BoundingBox( transformToSource.numSourceDimensions() );
		apply( transformToSource, b.corner1, boundingBox.corner1 );
		apply( transformToSource, b.corner2, boundingBox.corner2 );
		return b;
	}

	/**
	 * Supplies Converter from root type to view type.
	 * Maybe {@code null}, if there is no conversion required.
	 */
	private Supplier< ? extends Converter< ?, ? > > converterSupplier;

	/**
	 * Connect all converters in the view sequence into a combined converter. If
	 * the out-of-bounds extension requires values of a specific type (like
	 * constant-value extension), then all converters have to <em>happen
	 * after</em> the out-of-bounds extension (that is, they have to occur
	 * earlier in the {@code nodes} sequence).
	 * <p>
	 * For example if a constant-value extension is applied to a {@code
	 * DoubleType} {@code RandomAccessible} the oob value will be of {@code
	 * DoubleType}. If the RA was converted from a {@code UnsignedByteType}
	 * {@code NativeImg} we don't know the {@code UnsignedByteType} oob value
	 * for the underlying {@code NativeImg} which would lead to the same effect.
	 * Therefore, this is not allowed.
	 * <p>
	 * If everything works, the combined converter is provided in {@link #converterSupplier}.
	 *
	 * @return {@code true}, if all converters could be combined and work with the out-of-bounds extension.
	 */
	private boolean checkConverters()
	{
		final boolean dontConvertBeforeExtend = oobExtension != null && oobExtension.type().isValueDependent();

		final List< ConverterViewNode< ?, ? > > converterViewNodes = new ArrayList<>();
		for ( int i = 0; i < nodes.size(); i++ )
		{
			final ViewNode node = nodes.get( i );
			if ( node.viewType() == ViewNode.ViewType.CONVERTER )
			{
				if ( i > oobIndex && dontConvertBeforeExtend )
				{
					errorDescription.append(
							"The out-of-bounds extension in the view sequence requires that no converter is applied before it." );
					return false;
				}
				else
				{
					converterViewNodes.add( ( ConverterViewNode< ?, ? > ) node );
				}
			}
		}

		if ( !converterViewNodes.isEmpty() )
			converterSupplier = accumulateConverters( converterViewNodes );

		return true;
	}

	private static Supplier< ? extends Converter< ?, ? > > accumulateConverters(final List< ConverterViewNode< ?, ? > > nodes )
	{
		final AccumulateConverters acc = new AccumulateConverters();
		for ( int i = nodes.size() - 1; i >= 0; --i )
			acc.append( nodes.get( i ) );
		return acc.converterSupplier;
	}

	private static class AccumulateConverters
	{
		private Supplier< ? extends Converter< ?, ? > > converterSupplier = null;

		private Supplier< ? > destinationSupplier = null;

		private < A, B, C > void append( ConverterViewNode< B, C > node )
		{
			if ( converterSupplier == null )
			{
				converterSupplier = node.getConverterSupplier();
				destinationSupplier = node.getDestinationSupplier();
			}
			else
			{
				Supplier< Converter< A, B > > pcs = ( Supplier< Converter< A, B > > ) converterSupplier;
				Supplier< ? extends B > pds = ( Supplier< ? extends B > ) destinationSupplier;
				converterSupplier = () -> new Converter< A, C >()
				{
					final Converter< A, B > cAB = pcs.get();

					final B b = pds.get();

					final Converter< ? super B, ? super C > cBC = node.getConverterSupplier().get();

					@Override
					public void convert( final A a, final C c )
					{
						cAB.convert( a, b );
						cBC.convert( b, c );
					}
				};
				destinationSupplier = node.getDestinationSupplier();
			}
		}
	}

	/**
	 * The concatenated transform from the View {@link #ra RandomAccessible} to the root.
	 */
	private MixedTransform transform;

	/**
	 * Compute the concatenated {@link #transform transform} from the View
	 * {@link #ra RandomAccessible} to the root.
	 *
	 * @return {@code true}
	 */
	private boolean concatenateTransforms()
	{
		final int n = ra.numDimensions();
		transform = new MixedTransform( n, n );
		for ( ViewNode node : nodes )
		{
			if ( node.viewType() == ViewNode.ViewType.MIXED_TRANSFORM )
			{
				final MixedTransformViewNode tnode = ( MixedTransformViewNode ) node;
				transform = transform.preConcatenate( tnode.getTransformToSource() );
			}
		}
		return true;
	}

	/**
	 * Check that all View dimensions are used (mapped to some root dimension).
	 *
	 * @return {@code true}, if all View dimensions are used.
	 */
	private boolean checkNoDimensionsAdded()
	{
		// TODO: Views.addDimension(...) is not allowed for now. This could be
		//       supported by replicating hyperplanes in the target block.

		if(transform.hasFullSourceMapping())
		{
			return true;
		}
		else
		{
			errorDescription.append(
					"All View dimensions must map to a dimension of the underlying NativeImg. "
							+ "That is Views.addDimension(...) is not allowed." );
			return false;
		}
	}

	private MixedTransform permuteInvertTransform;

	private MixedTransform remainderTransform;

	/**
	 * Split {@link #transform} into
	 * <ol>
	 * <li>{@link #permuteInvertTransform}, a pure axis permutation followed by inversion of some axes, and</li>
	 * <li>{@link #remainderTransform}, a remainder transformation,</li>
	 * </ol>
	 * such that {@code remainder * permuteInvert == transform}.
	 * <p>
	 * Block copying will then first use {@code remainderTransform} to extract a
	 * intermediate block from the root {@code NativeImg}. Then compute the
	 * final block by applying {@code permuteInvertTransform}.
	 *
	 * @return {@code true}
	 */
	private boolean splitTransform()
	{
		final MixedTransform[] split = PrimitiveBlocksUtils.split( transform );
		permuteInvertTransform = split[ 0 ];
		remainderTransform = split[ 1 ];
		return true;
	}

	private < T extends NativeType< T >, R extends NativeType< R > > ViewProperties< T, R > getViewProperties()
	{
		final T viewType = Cast.unchecked( ra.getType() );
		final NativeImg< R, ? > root = Cast.unchecked( nodes.get( nodes.size() - 1 ).view() );
		final R rootType = root.createLinkedType();
		return new ViewProperties<>( viewType, root, rootType, oobExtension, transform, permuteInvertTransform, converterSupplier );
	}

	private < T extends NativeType< T > > FallbackProperties< T > getFallbackProperties()
	{
		final RandomAccessible< T > view = Cast.unchecked( ra );
		return new FallbackProperties<>( view.getType(), view );
	}

	public static < T extends NativeType< T >, R extends NativeType< R > > ViewPropertiesOrError< T, R > getViewProperties( RandomAccessible< T > view )
	{
		final ViewAnalyzer v = new ViewAnalyzer( view );

		// Check whether the pixel type of ciew is supported (NativeType with entitiesPerPixel==1)
		final boolean supportsFallback = v.checkViewTypeSupported();
		if ( !supportsFallback )
		{
			return new ViewPropertiesOrError<>( null, null, v.errorDescription.toString() );
		}

		final boolean fullySupported =
				// Deconstruct the target view into a list of ViewNodes
				v.analyze()

				// check whether the root of the view is supported
				// (PlanarImg, ArrayImg, CellImg)
				&& v.checkRootSupported()

				// Check whether the pixel type of the root is supported
				// (NativeType with entitiesPerPixel==1)
				&& v.checkRootTypeSupported()

				// Check whether there is at most one out-of-bounds extension
				&& v.checkExtensions1()

				// Check whether the out-of-bounds extension (if any) is of a
				// supported type (constant-value, border, mirror-single, mirror-double)
				&& v.checkExtensions2()

				// Check whether the interval at the out-of-bounds extension is compatible.
				&& v.checkExtensions3()

				// Connect all converters in the view sequence into a combined converter
				&& v.checkConverters()

				// Compute the concatenated MixedTransform
				&& v.concatenateTransforms()

				// Check that all View dimensions are used (mapped to some root dimension)
				&& v.checkNoDimensionsAdded()

				// Split concatenated transform into remainder * permuteInvert
				&& v.splitTransform();
		if ( !fullySupported )
		{
			final String errorMessage = "The RandomAccessible " + view +
					" is only be supported through the fall-back implementation of PrimitiveBlocks. \n" +
					v.errorDescription;
			final FallbackProperties fallbackProperties = v.getFallbackProperties();
			return new ViewPropertiesOrError<>( null, fallbackProperties, errorMessage );
		}

		final ViewProperties viewProperties = v.getViewProperties();
		final FallbackProperties fallbackProperties = v.getFallbackProperties();
		return new ViewPropertiesOrError<>( viewProperties, fallbackProperties, "" );
	}
}
