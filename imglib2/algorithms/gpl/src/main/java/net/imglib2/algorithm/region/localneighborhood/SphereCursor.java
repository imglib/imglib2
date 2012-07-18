package net.imglib2.algorithm.region.localneighborhood;

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
public final class SphereCursor<T> extends EllipsoidCursor<T> {

	protected final double[] calibration;

	/*
	 * CONSTRUCTORS
	 */

	public SphereCursor(SphereNeighborhood<T> neighborhood) {
		super(neighborhood);
		this.calibration = neighborhood.calibration;
	}

	/*
	 * METHODS adapted for calibrated units
	 */
	
	/**
	 * Return the current inclination with respect to this sphere center. Will be in
	 * the range [0, π]. 
	 * <p>
	 * In spherical coordinates, the inclination is the angle 
	 * between the Z axis and the line OM where O is the sphere center and M is 
	 * the point location.
	 */
	public double getTheta() {
		return Math.acos( position[2] * calibration[2] / Math.sqrt( getDistanceSquared() ) );
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
		return Math.atan2(position[1] * calibration[1], position[0] * calibration[0]);
	}
	
	/**
	 * Return the square distance measured from the center of the ellipsoid to the current
	 * cursor position, in <b>calibrated</b> units.
	 */
	public double getDistanceSquared() {
		double sum = 0;
		for (int i = 0; i < position.length; i++)
			sum += calibration[i] * calibration[i] * position[i] * position[i];
		return sum;
	}

	@Override
	public SphereCursor<T> copy() {
		return new SphereCursor<T>( (SphereNeighborhood<T>) neighborhood);
	}
	
	@Override
	public SphereCursor<T> copyCursor() {
		return copy();
	}
	
}
