package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Bounded;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.meta.Metadata;
import net.imglib2.outofbounds.OutOfBounds;

public final class DiscCursor<T, IN extends RandomAccessibleInterval<T> & Metadata>
		implements RealPositionableNeighborhoodCursor<T>, Bounded {

	/** The state of the cursor. */
	protected CursorState state, nextState;
	/** When drawing a line, the line length. */
	protected int rx;
	/** Store X line bounds for all Y */
	protected int[] rxs;

	protected boolean allDone;
	protected final double[] calibration;
	protected final double radius;
	protected boolean hasNext;
	protected long[] position;
	protected final DiscNeighborhood<T, IN> neighborhood;
	protected final OutOfBounds<T> ra;
	/**
	 * Utility holder for the neighborhood center expressed in <b>pixel
	 * units</b>.
	 */
	protected final long[] pixelCenter;

	/**
	 * Indicates what state the cursor is currently in, so as to choose the
	 * right routine to get coordinates
	 */
	private enum CursorState {
		DRAWING_LINE, INITIALIZED, INCREMENT_Y, MIRROR_Y;
	}

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Construct a {@link DiscCursor} on an image with a given spatial
	 * calibration.
	 * 
	 * @param img
	 *            the image
	 * @param center
	 *            the disc center, in physical units
	 * @param radius
	 *            the disc radius, in physical units
	 * @param calibration
	 *            the spatial calibration (pixel size); if <code>null</code>, a
	 *            calibration of 1 in all directions will be used
	 * @param outOfBoundsFactory
	 *            the {@link OutOfBoundsStrategyFactory} that will be used to
	 *            handle off-bound locations
	 */
	public DiscCursor(DiscNeighborhood<T, IN> disc) {
		this.neighborhood = disc;
		this.calibration = disc.calibration;
		this.radius = disc.radius;
		this.ra = disc.extendedSource.randomAccess();
		this.pixelCenter = new long[neighborhood.numDimensions()];
		rxs = new int[(int) (Math.max(Math.ceil(radius / calibration[0]),
				Math.ceil(radius / calibration[1])) + 1)];
		reset();
	}

	/*
	 * METHODS
	 */

	/**
	 * Return the azimuth of the spherical coordinates of this cursor, with
	 * respect to its center. Will be in the range ]-π, π].
	 * <p>
	 * In cylindrical coordinates, the azimuth is the angle measured between the
	 * X axis and the line OM where O is the sphere center and M is the cursor
	 * location
	 */
	public final double getPhi() {
		return Math.atan2(position[1] * calibration[1], position[0]
				* calibration[0]);
	}

	/**
	 * Return the square distance measured from the center of the disc to the
	 * current cursor position, in <b>calibrated</b> units.
	 */
	public double getDistanceSquared() {
		double sum = 0;
		for (int i = 0; i < numDimensions(); i++)
			sum += calibration[i] * calibration[i] * position[i] * position[i];
		return sum;
	}

	@Override
	public void reset() {
		for (int d = 0; d < pixelCenter.length; d++) {
			pixelCenter[d] = Math
					.round(neighborhood.center[d] / calibration[d]);
		}
		ra.setPosition(pixelCenter);
		state = CursorState.INITIALIZED;
		position = new long[numDimensions()];
		hasNext = true;
		allDone = false;
	}

	@Override
	public void fwd() {
		switch (state) {

		case DRAWING_LINE:

			ra.fwd(0);
			position[0]++;
			if (position[0] >= rx) {
				state = nextState;
				if (allDone)
					hasNext = false;
			}
			break;

		case INITIALIZED:

			// Compute circle radiuses in advance
			Utils.getXYEllipseBounds((int) Math.round(radius / calibration[0]),
					(int) Math.round(radius / calibration[1]), rxs);

			rx = rxs[0];
			ra.setPosition(pixelCenter);
			ra.setPosition(pixelCenter[0] - rx, 0);
			position[0] = -rx;
			state = CursorState.DRAWING_LINE;
			nextState = CursorState.INCREMENT_Y;
			break;

		case INCREMENT_Y:

			position[1] = -position[1] + 1; // y should be negative (coming from
											// mirroring or init = 0)
			rx = rxs[(int) position[1]];

			ra.setPosition(pixelCenter[1] + position[1], 1);
			position[0] = -rx;
			ra.setPosition(pixelCenter[0] - rx, 0);
			nextState = CursorState.MIRROR_Y;
			if (rx == 0)
				state = CursorState.MIRROR_Y;
			else
				state = CursorState.DRAWING_LINE;
			break;

		case MIRROR_Y:

			position[0] = -rx;
			position[1] = -position[1];
			ra.setPosition(pixelCenter[1] + position[1], 1);
			ra.setPosition(pixelCenter[0] - rx, 0);
			if (position[1] <= -Math.round(radius / calibration[1]))
				allDone = true;
			else
				nextState = CursorState.INCREMENT_Y;
			if (rx == 0)
				if (allDone)
					hasNext = false;
				else
					state = nextState;
			else
				state = CursorState.DRAWING_LINE;

			break;
		}
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public DiscCursor<T, IN> copy() {
		return new DiscCursor<T, IN>((DiscNeighborhood<T, IN>) neighborhood);
	}

	@Override
	public DiscCursor<T, IN> copyCursor() {
		return copy();
	}

	/**
	 * Return the relative <b>calibrated</b> distance from the center as a
	 * double position array.
	 */
	public void getRelativePosition(double[] position) {
		for (int d = 0; d < numDimensions(); d++)
			position[d] = calibration[d] * this.position[d];
	}

	@Override
	public void localize(float[] position) {
		for (int d = 0; d < numDimensions(); d++) {
			position[d] = (float) (calibration[d] * ra.getDoublePosition(d));
		}
	}

	@Override
	public void localize(double[] position) {
		for (int d = 0; d < numDimensions(); d++) {
			position[d] = calibration[d] * ra.getDoublePosition(d);
		}
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) (calibration[d] * ra.getDoublePosition(d));
	}

	@Override
	public double getDoublePosition(int d) {
		return calibration[d] * ra.getDoublePosition(d);
	}

	@Override
	public int numDimensions() {
		return ra.numDimensions();
	}

	@Override
	public T get() {
		return ra.get();
	}

	@Override
	public void jumpFwd(long steps) {
		for (int i = 0; i < steps; i++) {
			fwd();
		}
	}

	@Override
	public T next() {
		fwd();
		return ra.get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"remove() is not implemented for "
						+ getClass().getCanonicalName());
	}

	@Override
	public void localize(int[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(long[] position) {
		ra.localize(position);
	}

	@Override
	public int getIntPosition(int d) {
		return ra.getIntPosition(d);
	}

	@Override
	public long getLongPosition(int d) {
		return ra.getLongPosition(d);
	}

	@Override
	public boolean isOutOfBounds() {
		return ra.isOutOfBounds();
	}

}
