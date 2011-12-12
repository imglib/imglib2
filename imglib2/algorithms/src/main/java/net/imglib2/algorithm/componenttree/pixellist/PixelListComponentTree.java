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

/**
 * Component tree of an image stored as a tree of {@link PixelListComponent}s.
 * This class is used both to represent and build the tree.
 * For building the tree {@link Component.Handler} is implemented to gather
 * {@link PixelListComponentIntermediate} emitted by {@link ComponentTree}.
 *
 * <p>
 * <strong>TODO</strong> Add support for non-zero-min RandomAccessibleIntervals.
 * (Currently, we assume that the input image is a <em>zero-min</em> interval.)
 * </p>
 *
 * @author Tobias Pietzsch
 *
 * @param <T>
 *            value type of the input image.
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
	public static < T extends RealType< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, boolean darkToBright )
	{
		final int numDimensions = input.numDimensions();
		long size = 1;
		for ( int d = 0; d < numDimensions; ++d )
			size *= input.dimension( d );
		if( size > Integer.MAX_VALUE ) {
			int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / new LongType().getEntitiesPerPixel(), 1.0 / numDimensions );
			return buildComponentTree( input, type, new CellImgFactory< LongType >( cellSize ), darkToBright );
		} else
			return buildComponentTree( input, type, new ArrayImgFactory< LongType >(), darkToBright );
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
	public static < T extends RealType< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, final ImgFactory< LongType > imgFactory, boolean darkToBright )
	{
		T max = type.createVariable();
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
		final int numDimensions = input.numDimensions();
		long size = 1;
		for ( int d = 0; d < numDimensions; ++d )
			size *= input.dimension( d );
		if( size > Integer.MAX_VALUE ) {
			int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / new LongType().getEntitiesPerPixel(), 1.0 / numDimensions );
			return buildComponentTree( input, maxValue, comparator, new CellImgFactory< LongType >( cellSize ) );
		} else
			return buildComponentTree( input, maxValue, comparator, new ArrayImgFactory< LongType >() );
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
	public void emit( PixelListComponentIntermediate< T > intermediate )
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
