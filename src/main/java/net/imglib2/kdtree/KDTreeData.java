package net.imglib2.kdtree;

import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;


/**
 * Stores the KDTree data, that is, positions and values.
 * <p>
 * Positions are stored as {@link KDTreePositions} as either
 * {@link KDTreePositions.Flat} or {@link KDTreePositions.Nested}.
 * <p>
 * Values (of type {@code T}) are stored as {@link KDTreeValues} either a
 * 1D {@code RandomAccessibleInterval<T>} in {@link KDTreeValues.ImgValues}, or
 * a {@code List<T>} in {@link KDTreeValues.ListValues}.
 * <p>
 * {@link #values()} returns all values as a 1D {@code
 * RandomAccessibleInterval<T>}. (If data is stored as {@link
 * KDTreeValues.ListValues}, it is wrapped into a {@code ListImg}.)
 *
 * @param <T>
 * 		the type of values stored in the tree.
 */
public class KDTreeData< T >
{
	private final int numDimensions;
	private final int numPoints;

	public final KDTreePositions positions;

	private final KDTreeValues<T> values;

	private volatile RealInterval boundingBox;

	public KDTreeData(KDTreePositions positions, KDTreeValues<T> values) {
		numPoints = positions.numPoints();
		numDimensions = positions.numDimensions();
		this.positions = positions;
		this.values = values;
	}

	public KDTreeData(KDTreePositions positions, KDTreeValues<T> values, RealInterval boundingBox) {
		this(positions, values);
		this.boundingBox = boundingBox;
	}

	// TODO could also be Class<T> instead? What is more useful?
	public T type()
	{
		return values.type();
	}

	public double[] getFlatPositions() {
		return positions.getFlatPositions();
	}

	public double[][] getNestedPositions() {
		return positions.getNestedPositions();
	}

	public boolean positionsIsFlatArray() {
		return (positions instanceof KDTreePositions.Flat);
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
		return (boundingBox != null) ? boundingBox : positions.boundingBox();
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

		final boolean storeAsImg = (storeValuesAsNativeImg && KDTreeUtils.getType(values) instanceof NativeType);
		@SuppressWarnings("unchecked")
		final KDTreeValues<T> treeValues = (storeAsImg)
				? new KDTreeValues.ImgValues<>((Img<T>)  KDTreeUtils.orderValuesImg(invtree, (Iterable) values))
				: new KDTreeValues.ListValues<>(KDTreeUtils.orderValuesList(invtree, values));

		final boolean useFlatLayout = (long) numDimensions * numPoints <= KDTreeUtils.MAX_ARRAY_SIZE;
		final KDTreePositions treePositions = (useFlatLayout)
				? new KDTreePositions.Flat(KDTreeUtils.reorderToFlatLayout(points, tree), numDimensions)
				: new KDTreePositions.Nested(KDTreeUtils.reorder(points, tree));

		return new KDTreeData<>(treePositions, treeValues);
	}
}
