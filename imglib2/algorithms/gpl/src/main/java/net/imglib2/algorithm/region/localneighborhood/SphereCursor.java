package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Bounded;
import net.imglib2.Cursor;

/**
 * This class implements a {@link Cursor} that iterates over all the pixel within the volume
 * of a 3D ball, whose center and radius are given by a {@link SphereNeighborhood}. It is made so that
 * if the ball volume is made of N pixels, this cursor will go exactly over N iterations 
 * before exhausting. 
 * It takes a spatial calibration into account, which may be non-isotropic, so that the region
 * iterated over is a sphere in <b>physical coordinates</b>.
 * <p>
 * The iteration order is always the same. Iteration starts from the middle Z plane, and fill circles
 * away from this plane in alternating fashion: <code>Z = 0, 1, -1, 2, -2, ...</code>. For each 
 * circle, lines are drawn in the X positive direction from the middle line and away from it also in
 * an alternating fashion: <code>Y = 0, 1, -1, 2, -2, ...</code>. To parse all the pixels,
 * a line-scan algorithm is used, relying on McIlroy's algorithm to compute ellipse bounds
 * efficiently.
 * 
 * @author Jean-Yves Tinevez (jeanyves.tinevez@gmail.com) -  2012
 */
public final class SphereCursor<T>implements RealPositionableNeighborhoodCursor<T>, Bounded {

	protected final double[] calibration;
	/** A matching {@link EllipsoidNeighborhood} that has the same dimension in pixels that the desired sphere. */
	protected final EllipsoidNeighborhood<T> ellipsoid;
	/** The cursor that does all the work, linked to its {@link #ellipsoid}. */
	protected EllipsoidCursor<T> ellipsoidCursor;
	protected final SphereNeighborhood<T> neighborhood;
	/** Utility holder for the neighborhood center expressed in <b>pixel units</b>. */
	protected final long[] pixelCenter;

	/*
	 * CONSTRUCTORS
	 */

	public SphereCursor(SphereNeighborhood<T> neighborhood) {
		this.neighborhood = neighborhood;
		this.calibration = neighborhood.calibration;
		this.pixelCenter = new long[neighborhood.numDimensions()];
		this.ellipsoid = new EllipsoidNeighborhood<T>(neighborhood.source, neighborhood.outOfBounds);
		reset();
	}

	/*
	 * METHODS adapted for calibrated units
	 */
	
	/**
	 * Return the square distance measured from the center of the sphere to the current
	 * cursor position, in <b>calibrated</b> units.
	 */
	public double getDistanceSquared() {
		double sum = 0;
		for (int i = 0; i < numDimensions(); i++)
			sum += calibration[i] * calibration[i] * ellipsoidCursor.position[i] * ellipsoidCursor.position[i];
		return sum;
	}
	
	/**
	 * Return the current inclination with respect to this sphere center. Will be in
	 * the range [0, π]. 
	 * <p>
	 * In spherical coordinates, the inclination is the angle 
	 * between the Z axis and the line OM where O is the sphere center and M is 
	 * the point location.
	 */
	public double getTheta() {
		return Math.acos( ellipsoidCursor.position[2] * calibration[2] / Math.sqrt( getDistanceSquared() ) );
	}
	
	/**
	 * Return the azimuth of the spherical coordinates of this cursor, with respect 
	 * to its center. Will be in the range ]-π, π].
	 * <p>
	 * In spherical coordinates, the azimuth is the angle measured in the plane XY between 
	 * the X axis and the line OH where O is the sphere center and H is the orthogonal 
	 * projection of the point M on the XY plane.
	 */
	public double getPhi() {
		return Math.atan2(ellipsoidCursor.position[1] * calibration[1], ellipsoidCursor.position[0] * calibration[0]);
	}
	
	@Override
	public SphereCursor<T> copy() {
		return new SphereCursor<T>(neighborhood);
	}
	
	@Override
	public SphereCursor<T> copyCursor() {
		return copy();
	}

	/**
	 * Return the relative <b>calibrated</b> distance from the center
	 * as a double position array.
	 */
	public void getRelativePosition(double[] position) {
		for (int d = 0; d < numDimensions(); d++)
			position[d] = calibration[d] * ellipsoidCursor.getDoublePosition(d);
	}

	@Override
	public void localize(float[] position) {
		for (int d = 0; d < numDimensions(); d++) {
			position[d] = (float) (calibration[d] * ellipsoidCursor.getDoublePosition(d));
		}
	}

	@Override
	public void localize(double[] position) {
		for (int d = 0; d < numDimensions(); d++) {
			position[d] = calibration[d] * ellipsoidCursor.getDoublePosition(d);
		}
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) (calibration[d] * ellipsoidCursor.getDoublePosition(d));
	}

	@Override
	public double getDoublePosition(int d) {
		return calibration[d] * ellipsoidCursor.getDoublePosition(d);
	}

	@Override
	public int numDimensions() {
		return neighborhood.numDimensions();
	}

	@Override
	public T get() {
		return ellipsoidCursor.get();
	}

	@Override
	public void jumpFwd(long steps) {
		ellipsoidCursor.jumpFwd(steps);
	}

	@Override
	public void fwd() {
		ellipsoidCursor.fwd();
	}

	@Override
	public void reset() {
		for (int d = 0; d < pixelCenter.length; d++) {
			pixelCenter[d] = Math.round(neighborhood.center[d] / calibration[d]);
		}
		ellipsoid.setPosition(pixelCenter);
		ellipsoid.setSpan(neighborhood.span);
		ellipsoidCursor = ellipsoid.cursor();
	}

	@Override
	public boolean hasNext() {
		return ellipsoidCursor.hasNext();
	}

	@Override
	public T next() {
		fwd();
		return get();
	}

	@Override
	public void localize(int[] position) {
		ellipsoidCursor.localize(position);
	}

	@Override
	public void localize(long[] position) {
		ellipsoidCursor.localize(position);
	}

	@Override
	public int getIntPosition(int d) {
		return ellipsoidCursor.getIntPosition(d);
	}

	@Override
	public long getLongPosition(int d) {
		return ellipsoidCursor.getLongPosition(d);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove() is not implemented for "+getClass().getCanonicalName());
	}

	@Override
	public boolean isOutOfBounds() {
		return ellipsoidCursor.isOutOfBounds();
	}
	
}
