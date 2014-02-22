/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;

public final class EllipseCursor <T> extends AbstractNeighborhoodCursor<T> {

	/** The state of the cursor. */
	protected CursorState state, nextState;
	/** When drawing a line, the line length. */
	protected int rx;
	/** Store X line bounds for all Y */
	protected int[] rxs;
	/** Store current relative position with respect to the ellipse center. */
	protected long[] position;

	protected boolean allDone;
	protected boolean hasNext;

	
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
	public EllipseCursor(AbstractNeighborhood<T> ellipse) {
		super(ellipse);
		rxs = new int [ (int) (Math.max( ellipse.span[0], ellipse.span[1] )  +  1) ];
		reset();
	}

	/*
	 * METHODS
	 */
	
	public double getDistanceSquared() {
		double sum = 0;
		for (int d = 0; d < position.length; d++) {
			sum += position[d] * position[d];
		}
		return sum;
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
		return Math.atan2(position[1], position[0]);
	}
	
	/*
	 * CURSOR METHODS
	 */

	@Override
	public void reset() {
		ra.setPosition(neighborhood.center);
		state = CursorState.INITIALIZED;
		position = new long[numDimensions()];
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
			Utils.getXYEllipseBounds( (int) neighborhood.span[0], (int) neighborhood.span[1], rxs);
			
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
			if (position[1] <= - neighborhood.span[1])
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
	public Cursor<T> copyCursor() {
		return new EllipseCursor<T>(neighborhood);
	}

	@Override
	public Sampler<T> copy() {
		return copyCursor();
	}
	

}
