package net.imglib2.interpolation.neighborsearch;

import java.util.function.DoubleUnaryOperator;

import net.imglib2.KDTree;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.type.numeric.NumericType;

/**
 * An {@link InterpolatorFactory} for {@link KDTree}s using a radial function.
 * <p>
 * The resulting {@link RealRandomAccess} produced by this InterpolatorFactory
 * returns values as a linear combination of all points within a certain radius.
 * The contribution of each point is weighted according to a function of its
 * squared radius.
 */
public class RadialKDTreeInterpolatorFactory<T extends NumericType<T>> implements InterpolatorFactory<T, KDTree<T>> {

	protected final double maxRadius;
	protected final DoubleUnaryOperator squaredRadiusFunction;
	protected final T val;

	public RadialKDTreeInterpolatorFactory(
			final DoubleUnaryOperator squaredRadiusFunction, final double maxRadius, T t) {

		this.maxRadius = maxRadius;
		this.squaredRadiusFunction = squaredRadiusFunction;
		this.val = t;
	}

	@Override
	public RadialKDTreeInterpolator<T> create(final KDTree<T> tree) {

		return new RadialKDTreeInterpolator<T>(tree, squaredRadiusFunction, maxRadius, val);
	}

	@Override
	public RealRandomAccess<T> create(final KDTree<T> tree, final RealInterval interval) {

		return create(tree);
	}

}
