package net.imglib2.algorithm.region.localneighborhood;

public final class DiscCursor <T>  extends RectangleCursor<T> {

	/** The state of the cursor. */
	protected CursorState state, nextState;
	/** When drawing a line, the line length. */
	protected int rx;
	/** Store X line bounds for all Y */
	protected int[] rxs;

	protected boolean allDone;
	protected final double[] calibration;
	protected final double radius;
	private boolean hasNext;
	
	/**
	 * Indicates what state the cursor is currently in, so as to choose the right routine 
	 * to get coordinates */
	private enum CursorState {
		DRAWING_LINE					,
		INITIALIZED						,
		INCREMENT_Y						,
		MIRROR_Y						;
	}
	
	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Construct a {@link DiscCursor} on an image with a given spatial calibration.
	 * @param img  the image
	 * @param center  the disc center, in physical units
	 * @param radius  the disc radius, in physical units
	 * @param calibration  the spatial calibration (pixel size); if <code>null</code>, 
	 * a calibration of 1 in all directions will be used
	 * @param outOfBoundsFactory  the {@link OutOfBoundsStrategyFactory} that will be used to handle off-bound locations
	 */
	public DiscCursor(DiscNeighborhood<T> disc) {
		super(disc);
		this.calibration = disc.calibration;
		this.radius = disc.radius;
		rxs = new int [ (int) (Math.max(Math.ceil(radius / calibration[0]), Math.ceil(radius / calibration[1]))  +  1) ];
		reset();
	}

	/*
	 * METHODS
	 */

	/**
	 * Return the azimuth of the spherical coordinates of this cursor, with respect 
	 * to its center. Will be in the range ]-π, π].
	 * <p>
	 * In cylindrical coordinates, the azimuth is the angle measured between 
	 * the X axis and the line OM where O is the sphere center and M is the cursor location
	 */
	public final double getPhi() {
		return Math.atan2(position[1]*calibration[1], position[0]*calibration[0]);
	}
	
	/**
	 * Return the square distance measured from the center of the ellipsoid to the current
	 * cursor position, in <b>calibrated</b> units.
	 */
	public double getDistanceSquared() {
		double sum = 0;
		for (int i = 0; i < 2; i++)
			sum += calibration[i] * calibration[i] * position[i] * position[i];
		return sum;
	}

	@Override
	public void reset() {
		ra.setPosition(neighborhood.center);
		state = CursorState.INITIALIZED;
		position = new long[2];
		hasNext = true;
		allDone = false;
	}
	
	@Override
	public void fwd() {
		switch(state) {

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
			Utils.getXYEllipseBounds( (int) Math.round(radius/calibration[0]), (int) Math.round(radius/calibration[1]), rxs);
			
			rx = rxs[0] ; 
			ra.setPosition(neighborhood.center);
			ra.setPosition(neighborhood.center[0] - rx, 0);
			position[0] = -rx;
			state = CursorState.DRAWING_LINE;
			nextState = CursorState.INCREMENT_Y;
			break;

		case INCREMENT_Y:

			position[1] = -position[1] + 1; // y should be negative (coming from mirroring or init = 0)
			rx = rxs[(int) position[1]];

			ra.setPosition(neighborhood.center[1] + position[1], 1);
			position[0] = -rx;
			ra.setPosition(neighborhood.center[0] - rx, 0);
			nextState = CursorState.MIRROR_Y;
			if (rx ==0)
				state = CursorState.MIRROR_Y;
			else
				state = CursorState.DRAWING_LINE;				
			break;

		case MIRROR_Y:

			position[0] = -rx;
			position[1] = - position[1];
			ra.setPosition(neighborhood.center[1] + position[1], 1);
			ra.setPosition(neighborhood.center[0] - rx, 0);
			if (position[1] <= - Math.round(radius/calibration[1]))
				allDone  = true;
			else 
				nextState = CursorState.INCREMENT_Y;
			if (rx ==0)
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
	public DiscCursor<T> copy() {
		return new DiscCursor<T>((DiscNeighborhood<T>) neighborhood);
	}
	
	@Override
	public DiscCursor<T> copyCursor() {
		return copy();
	}
	
}
