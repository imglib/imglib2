package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgPlus;
import net.imglib2.meta.Metadata;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;

/**
 * This class implements a 3D spherical neighborhood, for a source
 * {@link ImgPlus} with <b>non-isotropic spatial calibration</b>. That is: if
 * the spatial calibration of the source is not the same for every direction,
 * the neighborhood will be an ellipsoid, but the physical coordinates will be
 * that of a sphere.
 * <p>
 * To achieve this, we simply wrap an {@link EllipsoidNeighborhood} and
 * calculate its bounds at construction. We also return a specialized cursor
 * with extra methods.
 * <p>
 * Only the first 3 dimensions are considered, whatever they are.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 */
public class SphereNeighborhood<T, IN extends RandomAccessibleInterval<T> & Metadata>
		extends RealPositionableAbstractNeighborhood<T, IN> {

	protected final OutOfBoundsFactory<T, IN> outOfBounds;
	/** The radius of the sphere, in calibrated units. */
	protected double radius;
	/**
	 * An {@link EllipsoidNeighborhood} in sync with this sphere, used for size
	 * computation.
	 */
	protected final EllipsoidNeighborhood<T, IN> ellipsoid;

	/*
	 * CONSTRUCTORS
	 */

	public SphereNeighborhood(final IN source, final double radius,
			final OutOfBoundsFactory<T, IN> outOfBounds) {
		super(source, outOfBounds);
		this.outOfBounds = outOfBounds;
		this.ellipsoid = new EllipsoidNeighborhood<T, IN>(source);
		setRadius(radius);
	}

	public SphereNeighborhood(final IN source, final double radius) {
		this(source, radius, new OutOfBoundsMirrorFactory<T, IN>(
				Boundary.SINGLE));
	}

	/*
	 * METHODS
	 */

	/**
	 * Overridden not to do anything.
	 * 
	 * @see #setRadius(double)
	 */
	@Override
	public void setSpan(long[] span) {
	}

	/**
	 * Change the radius of this neighborhood.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
		// Compute span
		final long[] span = new long[3];
		for (int d = 0; d < span.length; d++) {
			span[d] = Math.round(radius / calibration[d]);
		}
		super.setSpan(span);
		ellipsoid.setSpan(span);
	}

	@Override
	public SphereCursor<T, IN> cursor() {
		return new SphereCursor<T, IN>(this);
	}

	@Override
	public SphereCursor<T, IN> localizingCursor() {
		return cursor();
	}

	@Override
	public SphereCursor<T, IN> iterator() {
		return cursor();
	}

	@Override
	public long size() {
		return ellipsoid.size();
	}

}
