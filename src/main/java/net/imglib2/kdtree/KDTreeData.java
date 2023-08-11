package net.imglib2.kdtree;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.imglib2.FinalRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.Img;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;

import static net.imglib2.kdtree.KDTreeData.PositionsLayout.FLAT;

/**
 * Stores the KDTree data, that is, positions and values.
 * <p>
 * Positions are stored in either {@code FLAT} or {@code NESTED} {@link
 * PositionsLayout layout}. With {@code NESTED} layout, positions are stored as
 * a nested {@code double[][]} array where {@code positions[d][i]} is dimension
 * {@code d} of the {@code i}-th point. With {@code FLAT} layout, positions are
 * stored as a flat {@code double[]} array, where {@code positions[d + i*n]} is
 * dimension {@code d} of the {@code i}-th point, with {@code n} the number of
 * dimensions.
 * <p>
 * Values (of type {@code T}) are stored as either a 1D {@code
 * RandomAccessibleInterval<T>}, or a {@code List<T>}. Individual values can be
 * accessed by {@link #valuesSupplier()}{@code .get().apply(i)}. {@code
 * valueSupplier().get()} returns a reusable {@code IntFunction<T>}. Here {@code
 * T} maybe a proxy that is reused in subsequent {@code apply(i)}.
 * <p>
 * {@link #values()} returns all values as a 1D {@code
 * RandomAccessibleInterval<T>}. (If data is stored as {@code List<T>}, it is
 * wrapped into a {@code ListImg}.)
 * <p>
 * {@link #positions()} returns positions in nested {@code double[][]} (which is
 * created if internal storage is {@code FLAT}). {@link #flatPositions()}
 * returns flat {@code double[]} if internal storage is {@code FLAT}, otherwise
 * {@code null}.
 *
 * @param <T>
 * 		the type of values stored in the tree.
 */
public class KDTreeData< T >
{
	public enum PositionsLayout
	{
		FLAT,
		NESTED
	}

	private final int numDimensions;
	private final int numPoints;

	private final PositionsLayout layout;
	private final double[][] positions;
	private final double[] flatPositions;
	private final KDTreePositions newPositions;

	private final KDTreeValues<T> values;

	private volatile RealInterval boundingBox;

	public KDTreeData( double[][] positions, List< T > values )
	{
		numPoints = values.size();
		numDimensions = positions.length;

		layout = PositionsLayout.NESTED;
		this.positions = positions;
		flatPositions = null;
		newPositions = new KDTreePositions.Nested(positions);

		this.values = new KDTreeValuesList<>(values);
	}

	public KDTreeData( double[][] positions, List< T > values, RealInterval boundingBox )
	{
		this( positions, values );
		this.boundingBox = boundingBox;
	}

	public KDTreeData( double[][] positions, RandomAccessibleInterval< T > values )
	{
		numPoints = ( int ) values.dimension( 0 );
		numDimensions = positions.length;

		layout = PositionsLayout.NESTED;
		this.positions = positions;
		flatPositions = null;
		newPositions = new KDTreePositions.Nested(positions);

		this.values = new KDTreeValuesImg<>(values);
	}

	public KDTreeData( double[][] positions, RandomAccessibleInterval< T > values, RealInterval boundingBox )
	{
		this( positions, values );
		this.boundingBox = boundingBox;
	}

	public KDTreeData( double[] positions, List< T > values )
	{
		numPoints = values.size();
		numDimensions = positions.length / numPoints;

		layout = FLAT;
		this.positions = null;
		flatPositions = positions;
		newPositions = new KDTreePositions.Flat(positions, numDimensions);

		this.values = new KDTreeValuesList<>(values);
	}

	public KDTreeData( double[] positions, List< T > values, RealInterval boundingBox )
	{
		this( positions, values );
		this.boundingBox = boundingBox;
	}

	public KDTreeData( double[] positions, RandomAccessibleInterval< T > values )
	{
		numPoints = ( int ) values.dimension( 0 );
		numDimensions = positions.length / numPoints;

		layout = FLAT;
		this.positions = null;
		flatPositions = positions;
		newPositions = new KDTreePositions.Flat(positions, numDimensions);

		this.values = new KDTreeValuesImg<>(values);
	}

	public KDTreeData( double[] positions, RandomAccessibleInterval< T > values, RealInterval boundingBox )
	{
		this( positions, values );
		this.boundingBox = boundingBox;
	}

	// TODO could also be Class<T> instead? What is more useful?
	public T type()
	{
		return values.type();
	}

	/**
	 * Get positions of points in the tree as a nested {@code double[][]} array
	 * where {@code positions[d][i]} is dimension {@code d} of the {@code i}-th
	 * point.
	 * <p>
	 * For serialisation and usage by the tree.
	 * <p>
	 * Internal storage may be flattened into single {@code double[]} array. In
	 * this case, the nested {@code double[][]} array is created here.
	 */
	public double[][] positions()
	{
		return layout == FLAT ? KDTreeUtils.unflatten( flatPositions, numDimensions ) : positions;
	}

	/**
	 * Get the values as a 1D {@code RandomAccessibleInterval}, for
	 * serialization. (If the underlying storage is a {@code List<T>}, it will
	 * be wrapped as a {@code ListImg}.)
	 */
	public RandomAccessibleInterval< T > values()
	{
		return values.values();
	}

	/**
	 * Get a {@code Supplier} that return {@code IntFunction<T>} to provide
	 * values for a given node indices.. If the returned {@code IntFunction<T>}
	 * is stateful ({@code T} maybe a proxy that is reused in subsequent {@code
	 * apply(i)}} every {@link Supplier#get()} creates a new instance of the
	 * {@code IntFunction<T>}.
	 */
	public Supplier< IntFunction< T > > valuesSupplier()
	{
		return values.valuesSupplier();
	}

	/**
	 * Get positions of points in the tree as a flat {@code double[]} where
	 * {@code positions[d + i*n]} is dimension {@code d} of the {@code i}-th
	 * point, with {@code n} the number of dimensions.
	 * <p>
	 * For serialisation and usage by the tree.
	 * <p>
	 * Internal storage may be a {@code NESTED} {@code double[][]} array. In
	 * this case, {@code flatPositions()} returns {@code null}.
	 */
	public double[] flatPositions()
	{
		return flatPositions;
	}

	/**
	 * Get the internal layout of positions.
	 * <p>
	 * Positions are stored in either {@code FLAT} or {@code NESTED} {@link
	 * PositionsLayout layout}. With {@code NESTED} layout, positions are stored
	 * as a nested {@code double[][]} array where {@code positions[d][i]} is
	 * dimension {@code d} of the {@code i}-th point. With {@code FLAT} layout,
	 * positions are stored as a flat {@code double[]} array, where {@code
	 * positions[d + i*n]} is dimension {@code d} of the {@code i}-th point,
	 * with {@code n} the number of dimensions.
	 */
	public PositionsLayout layout()
	{
		return layout;
	}

	/**
	 * @return dimensionality of points in the tree
	 */
	public int numDimensions()
	{
		return numDimensions;
	}

	/**
	 * @return number of points in the tree
	 */
	public int size()
	{
		return numPoints;
	}

	public RealInterval boundingBox()
	{
		if ( boundingBox == null )
			boundingBox = createBoundingBox();
		return boundingBox;
	}

	private RealInterval createBoundingBox()
	{
		final double[] min = new double[ numDimensions ];
		final double[] max = new double[ numDimensions ];
		if ( layout == FLAT )
			KDTreeUtils.computeMinMax( flatPositions, min, max );
		else
			KDTreeUtils.computeMinMax( positions, min, max );
		return FinalRealInterval.wrap( min, max );
	}

	/**
	 * Create {@link KDTreeData} from the given {@code values} and {@code positions}).
	 * (copies {@code positions} and sorts into a KDTree structure).
	 *
	 * @param numPoints
	 * 		number of points (number of elements in {@code values} and {@code positions}).
	 * @param values
	 * 		values associated with points
	 * @param positions
	 * 		points positions
	 * @param storeValuesAsNativeImg
	 * 		If {@code true} and {@code T} is a {@code NativeType},
	 * 		store values into {@code NativeImg}.
	 * 		Otherwise, store values as a {@code List<T>}.
	 */
	public static < L extends RealLocalizable, T > KDTreeData< T > create(
			final int numPoints,
			final Iterable< T > values,
			final Iterable< L > positions,
			final boolean storeValuesAsNativeImg )
	{
		if ( numPoints <= 0 )
			throw new IllegalArgumentException( "At least one point is required to construct a KDTree." );
		final int numDimensions = KDTreeUtils.getNumDimensions( positions );
		final double[][] points = KDTreeUtils.initPositions( numDimensions, numPoints, positions );
		final int[] tree = KDTreeUtils.makeTree( points );
		final int[] invtree = KDTreeUtils.invert( tree );

		final boolean useFlatLayout = ( long ) numDimensions * numPoints <= KDTreeUtils.MAX_ARRAY_SIZE;
		if ( storeValuesAsNativeImg && KDTreeUtils.getType( values ) instanceof NativeType )
		{
			@SuppressWarnings( "unchecked" )
			final Img< T > treeValues = ( Img< T > ) KDTreeUtils.orderValuesImg( invtree, ( Iterable ) values );
			if ( useFlatLayout )
				return new KDTreeData<>(KDTreeUtils.reorderToFlatLayout( points, tree ), treeValues);
			else
				return new KDTreeData<>(KDTreeUtils.reorder( points, tree ), treeValues);
		}
		else
		{
			final List< T > treeValues = KDTreeUtils.orderValuesList( invtree, values );
			if ( useFlatLayout )
				return new KDTreeData<>(KDTreeUtils.reorderToFlatLayout( points, tree ), treeValues);
			else
				return new KDTreeData<>(KDTreeUtils.reorder( points, tree ), treeValues);
		}
	}
}
