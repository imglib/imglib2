package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Cursor;
import net.imglib2.Sampler;

/**
 * This class implements a {@link Cursor} that iterates over all the pixel within the volume
 * of a 3D ellipsoid. It is made so that
 * if the ellipsoid volume is made of N pixels, this cursor will go exactly over N iterations 
 * before exhausting. 
 * <p>
 * The center of the sphere is set by a {@link EllipsoidNeighborhood} position, allowing
 * to use this cursor in neighborhood processing operations. The two cursors (this one and 
 * the main one) are not linked in any way, so the method to move the sphere center must
 * be called every time. For instance:
 * <pre>
 * Img<T> img;
 * // ...
 * long[] span = new long[] { 10, 20, 5 };
 * EllipsoidNeighborhood< T > ellipsoid = new EllipsoidNeighborhood< T >(img);
 * ellipsoid.setSpan(span);
 * Cursor< T > cursor = ellipsoid.cursor();
 * while (mainCursor.hasNext()) {
 * 	mainCursor.fwd();
 * 	ellipsoid.setPosition(mainCursor);
 * 	cursor.reset();
 * 	while (cursor.hasNext()) {
 * 		cursor.fwd();
 * 		// Have fun here ...
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * The iteration order is always the same. Iteration starts from the middle Z plane, and fill circles
 * away from this plane in alternating fashion: <code>Z = 0, 1, -1, 2, -2, ...</code>. For each 
 * circle, lines are drawn in the X positive direction from the middle line and away from it also in
 * an alternating fashion: <code>Y = 0, 1, -1, 2, -2, ...</code>. To parse all the pixels,
 * a line-scan algorithm is used, relying on McIlroy's algorithm to compute ellipse bounds
 * efficiently. It makes intensive use
 * of states to avoid calling the {@link Math#sqrt(double)} method. 
 * 
 * @author Jean-Yves Tinevez (jeanyves.tinevez@gmail.com) -  August, 2010 - 2012
 * @see EllipsoidNeighborhood
 *
 * @param <T>
 */
public class EllipsoidCursor<T> extends AbstractNeighborhoodCursor<T> {

	private CursorState state, nextState;
	/** Store the position index. */
	/** For mirroring, indicate if we must take the mirror in the Z direction. */
	private boolean mirrorZ;
	/** When drawing a line, the line length. */
	private int rx;
	/** The XY circle radius at height Z. */
	private int ry;
	/** Store XY circle radiuses for all Z */
	private int[] rys;
	/** Store X line bounds for all Y */
	private int[] rxs;
	/** Indicate whether we finished all Z planes. */
	private boolean doneZ = false;
	/** Is true when all Z and Y have been done, just the last line is to be drawn. */
	private boolean allDone;
	private boolean hasNext;
	/** Current relative position of the cursor, with respect to the ellipsoid center. */
	protected int[] position;

	private final int smallAxisdim, largeAxisDim;

	/**
	 * Indicates what state the cursor is currently in, so as to choose the right routine 
	 * to get coordinates */
	private enum CursorState {
		DRAWING_LINE					,
		INITIALIZED						,
		INCREMENT_Y						,
		MIRROR_Y						,
		INCREMENT_Z						;
	}
	/*
	 * CONSTRUCTOR
	 */

	public EllipsoidCursor(AbstractNeighborhood<T> ellipsoid) {
		super(ellipsoid);
		/* We have to check what is the smallest dimension between the 1st and the 2nd one.
		 * The precision of the calculation we make to find the ellipse bounds is only
		 * acceptable when we pass it the largest dimension. */
		if (ellipsoid.span[1] < ellipsoid.span[0]) {
			smallAxisdim = 1;
			largeAxisDim = 0;
		} else {
			smallAxisdim = 0;
			largeAxisDim = 1; // ydim is the large axis
		}

		// Instantiate it once, and with large size, so that we do not have to instantiate every time we move in Z
		rxs = new int [ (int) (ellipsoid.span[largeAxisDim]  +  1) ];
		rys = new int[(int) (ellipsoid.span[2] + 1)];
		reset();
	}

	/*
	 * METHODS
	 */


	@Override
	public void reset() {
		ra.setPosition(neighborhood.center);
		state = CursorState.INITIALIZED;
		mirrorZ = false;
		doneZ = false;
		allDone = false;
		position = new int[3];
		hasNext = true;	
	}

	@Override
	public void fwd() {
		
		switch(state) {

		case DRAWING_LINE:

			ra.fwd(smallAxisdim);
			position[smallAxisdim]++;
			if (position[smallAxisdim] >= rx) {
				state = nextState;
				if (allDone)
					hasNext = false;
			}
			break;

		case INITIALIZED:

			// Compute XY circle radiuses for all Z in advance
			Utils.getXYEllipseBounds((int) neighborhood.span[largeAxisDim], (int) neighborhood.span[2], rys); 
			ry = rys[0] ;

			Utils.getXYEllipseBounds((int) neighborhood.span[smallAxisdim], (int) neighborhood.span[largeAxisDim], rxs); 
			rx = rxs[0];
			
			ra.setPosition(neighborhood.center[smallAxisdim] - rx, smallAxisdim);
			ra.setPosition(neighborhood.center[largeAxisDim], largeAxisDim);
			ra.setPosition(neighborhood.center[2], 2);
			position[smallAxisdim] = -rx;
			
			if (rx > 0) {
				state = CursorState.DRAWING_LINE;
			} else {
				state = CursorState.INCREMENT_Y;
			}
			nextState = CursorState.INCREMENT_Y;
			break;
			

		case INCREMENT_Y:

			position[largeAxisDim] = -position[largeAxisDim] + 1; // y should be negative (coming from mirroring or init = 0)
			rx = rxs[position[largeAxisDim]];

			ra.setPosition(neighborhood.center[largeAxisDim] + position[largeAxisDim], largeAxisDim);
			position[smallAxisdim] = -rx;
			ra.setPosition(neighborhood.center[smallAxisdim] - rx, smallAxisdim);
			nextState = CursorState.MIRROR_Y;
			if (rx == 0)
				state = CursorState.MIRROR_Y;
			else
				state = CursorState.DRAWING_LINE;		

			break;


		case MIRROR_Y:

			position[ smallAxisdim ] = -rx;
			position[ largeAxisDim ] = - position[ largeAxisDim ];
			ra.setPosition(neighborhood.center[ largeAxisDim ] + position[ largeAxisDim ], largeAxisDim);
			ra.setPosition(neighborhood.center[ smallAxisdim ] - rx, smallAxisdim);
			if (position[largeAxisDim] <= - ry) {
				if (doneZ) 
					allDone  = true ;
				else {
					if (neighborhood.span[2] > 0) {
						nextState = CursorState.INCREMENT_Z;
					} else {
						allDone  = true ;
					}
				}
			}
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

		case INCREMENT_Z:


			if (mirrorZ) {

				position[2] = - position[2];
				mirrorZ = false;
				if (position[2] <= - neighborhood.span[2]) 
					doneZ = true;

			} else {

				position[2] = - position[2] + 1;
				ry = rys[position[2]];
				mirrorZ = true;
			}



			Utils.getXYEllipseBounds(Math.round( (float) ry * neighborhood.span[smallAxisdim] / neighborhood.span[largeAxisDim]), ry, rxs); 
			rx = rxs[0];

			ra.setPosition(neighborhood.center[ smallAxisdim ] - rx, smallAxisdim);
			ra.setPosition(neighborhood.center[ largeAxisDim ], largeAxisDim);
			ra.setPosition(neighborhood.center[2] + position[2], 2);
			position[ smallAxisdim ] = -rx;
			position[ largeAxisDim ] = 0;

			if ( rx == 0 ) {
				
				state = CursorState.INCREMENT_Y;
				
			} else {

				state = CursorState.DRAWING_LINE;
				nextState = CursorState.INCREMENT_Y;

			}
			break;
		}	
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Cursor<T> copyCursor() {
		return new EllipsoidCursor<T>(this.neighborhood);
	}

	@Override
	public Sampler<T> copy() {
		return copyCursor();
	}

	@Override
	public void jumpFwd(long steps) {
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public T next() {
		fwd();
		return get();
	}

	
	
	/**
	 * Return the current inclination with respect to this ellipsoid center. Will be in
	 * the range [0, π]. 
	 * <p>
	 * In spherical coordinates, the inclination is the angle 
	 * between the Z axis and the line OM where O is the sphere center and M is 
	 * the point location.
	 */
	public double getTheta() {
		return Math.acos( position[2] / getDistanceSquared() );
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
		return Math.atan2( position[1] , position[0] );
	}
	

	/**
	 * Return the square distance measured from the center of the ellipsoid to the current
	 * cursor position, in pixel units.
	 */
	public double getDistanceSquared() {
		double sum = 0;
		for (int i = 0; i < position.length; i++)
			sum += position[i] * position[i];
		return sum;
	}

}
