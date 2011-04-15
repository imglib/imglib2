package mpicbg.imglib.roi;

import java.util.ArrayList;
import java.util.Arrays;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPoint;

public class PolygonRegionOfInterest extends AbstractIterableRegionOfInterest {

	protected ArrayList<RealPoint> points = new ArrayList<RealPoint>();
	
	public PolygonRegionOfInterest() {
		super(2);
	}

	/**
	 * @return the number of vertices in the polygon which is equal to the number of
	 * edges.
	 */
	public int getVertexCount() {
		return points.size();
	}
	
	/**
	 * Get a vertex by index
	 * @param index index of the vertex to get
	 * @return the vertex
	 */
	public RealLocalizable getVertex(int index) {
		return points.get(index);
	}
	
	/**
	 * Insert a point into the polygon at the given index
	 * @param p point to be inserted
	 * @param index index of point.
	 */
	public void addVertex(int index, RealLocalizable p) {
		points.add(index, new RealPoint(p));
		invalidateCachedState();
	}
	
	/**
	 * Remove a vertex from the polygon
	 * 
	 * @param index index of the vertex to remove
	 */
	public void removeVertex(int index) {
		points.remove(index);
		invalidateCachedState();
	}
	
	/**
	 * Change the position of a vertex
	 * @param index index of the vertex in question
	 * @param position
	 */
	public void setVertexPosition(int index, double [] position) {
		points.get(index).setPosition(position);
		invalidateCachedState();
	}
	
	/**
	 * Change the position of a vertex
	 * @param index index of the vertex in question
	 * @param position
	 */
	public void setVertexPosition(int index, float [] position) {
		points.get(index).setPosition(position);
		invalidateCachedState();
	}
	
	/**
	 * Change the position of a vertex using a localizable
	 * @param index index of the vertex in question
	 * @param localizable containing the new position
	 */
	public void setVertexPosition(int index, RealLocalizable localizable) {
		points.get(index).setPosition(localizable);
		invalidateCachedState();
	}
	
	@Override
	protected void getRealExtrema(double[] minima, double[] maxima) {
		Arrays.fill(minima, Double.MAX_VALUE);
		Arrays.fill(maxima, Double.MIN_VALUE);
		for (int i=0; i < points.size(); i++) {
			RealPoint p = points.get(i);
			for (int j=0; j < 2; j++) {
				double v = p.getDoublePosition(j);
				if (v < minima[j]) minima[j] = v;
				if (v > maxima[j]) maxima[j] = v;
			}
		}
	}

	@Override
	protected long size() {
		// The algorithm determines whether an edge is a left or right
		// edge. If left, add the area between the edge and the 
		// left edge of the bounding box. If right, subtract the area
		// between the edge and the right edge of the bounding box.
		//
		// If horizontal, ignore.
		//
		long accumulator = 0;
		for (int i=0; i< points.size(); i++) {
			RealLocalizable pStart = getEdgeStart(i);
			RealLocalizable pEnd = getEdgeEnd(i);
			double y_start = pStart.getDoublePosition(1);
			double y_end = pEnd.getDoublePosition(1);
			if (y_start == y_end) continue;
			double x_start = pStart.getDoublePosition(0);
			double x_end = pEnd.getDoublePosition(0);
			if (y_start < y_end) {
				accumulator += getAreaOnBehalfOfSize(y_start, x_start, y_end, x_end);
			} else {
				accumulator -= getAreaOnBehalfOfSize(y_end, x_end, y_start, x_start);
			}
		}
		return accumulator;
	}
	
	private long getAreaOnBehalfOfSize(double y0, double x0, double y1, double x1) {
		long x_max = this.max(0) + 1;
		long x_min = (long)Math.min(x0, x1);
		// Renormalize
		x0 = x0 - x_min;
		x1 = x1 - x_min;
		x_max = x_max - x_min;
		//
		// A pixel at x,y is in the area if
		// y >= y0
		// y <= y1
		// x >= x0 + (y - y0) * (x1 - x0) / (y1 - y0)
		// x <= x_max
		//
		// So the number of pixels at a given y is
		//
		// x_max - ceil(x0 + (y - y0) * (x1 - x0) / (y1 - y0))
		// 
		// y goes from ceil(y0) to floor(y1) inclusive so
		//
		// area = x_max * (floor(y1) - ceil(y0) + 1) -
		//        sum(ceil(x0 + (y - y0) * (x1 - x0) / (y1 - y0)), ceil(y0), floor(y1))
		//
		// and this can be computed by taking the average of the length
		// of the line at ceil(y0) and at floor(y1), multiplying by floor(y1) - ceil(y0) + 1
		// and rounding down.
		//
		long y1_floor = (long)y1;
		long y0_ceiling = (long)Math.ceil(y0);
		long height = (y1_floor - y0_ceiling + 1);
		long area = x_max * height;
		double inv_slope = (x1 - x0) / (y1 - y0);
		long x1_ceiling = (long)Math.ceil(x0 + (y1_floor - y0) * inv_slope);
		long x0_ceiling = (long)Math.ceil(x0 + (y0_ceiling - y0) * inv_slope);
		area = (x1_ceiling + x0_ceiling) * height / 2;
		return area;
	}

	@Override
	protected void getExtrema(long[] minima, long[] maxima) {
		for (int i=0; i<2; i++) {
			minima[i] = (long)(this.realMin(i));
			maxima[i] = (long)(this.realMax(i));
		}
	}

	@Override
	protected boolean nextRaster(long[] position, long[] end) {
		int [] indexes = new int[2];
		double [] x_intercepts = new double[2];
		if (getEdges(new double [] { position[0], position[1]}, indexes, x_intercepts)) {
			position[0] = (long)(x_intercepts[0]);
			end[0] = (long)(x_intercepts[1]) + 1;
			end[1] = position[1];
			return true;
		}
		// First case to check: the position given is wholly to the left.
		return false;
	}

	@Override
	protected boolean isMember(double[] position) {
		return getEdges(position, null, null);
	}

	/**
	 * Get the coordinates of the first vertex of the indexed edge
	 * @param start the index of the edge
	 * @return the first vertex, going clockwise
	 */
	public RealLocalizable getEdgeStart(int start) {
		return points.get(start);
	}
	/**
	 * Get the coordinates of the second vertex of the indexed edge
	 * @param start the index of the edge
	 * @return the second vertex, going clockwise
	 */
	public RealLocalizable getEdgeEnd(int start) {
		return points.get((start + 1) % points.size());
	}
	
	/**
	 * Determine whether the given edge is horizontal. If so,
	 * interpolateEdgeXAtY won't work because the X is then
	 * indeterminate.
	 * 
	 * @param index index of edge to examine
	 * @return true if horizontal (Y coordinates of start and end are identical)
	 */
	public boolean isHorizontal(int index) {
		return getEdgeStart(index).getDoublePosition(1) == getEdgeEnd(index).getDoublePosition(1);
	}
	/**
	 * Given an edge and a Y coordinate, find its X coordinate at that Y coordinate.
	 * 
	 * NOTE: this assumes that isHorizontal(start) is false, else the X coordinate
	 * is indeterminate.
	 * 
	 * @param start index of the edge
	 * @return the X coordinate
	 */
	public double interpolateEdgeXAtY(int start, double y) {
		RealLocalizable p_start = getEdgeStart(start);
		RealLocalizable p_end = getEdgeEnd(start);
		double x_start = p_start.getDoublePosition(0);
		double y_start = p_start.getDoublePosition(1);
		double x_end = p_end.getDoublePosition(0);
		double y_end = p_end.getDoublePosition(1);
		return x_start + (y - y_start) * (x_end - x_start) / (y_end - y_start); 
	}
	
	/**
	 * Get the closest edge to the left of this one 
	 * @param position position of interest
	 * @param indexes pass in an array of size 2. On output, the first is the index
	 *        of the edge to the left, the second is the index of the edge to the right.
	 *        Either of these might be -1 to signify that there is no edge to the left or right.
	 *        Can be null if getEdges is only used to determine set-membership.
	 *        
	 *        If the point is outside the polygon, the edges are the bounding edges
	 *        that exclude the point, otherwise, they are the bounding edges
	 *        that include the point.
	 * @param x_intercepts x-intercepts of the edges at the Y position passed in
	 * @return true if point is within polygon.
	 */
	protected boolean getEdges(double [] position, int [] indexes, double [] x_intercepts) {
		double best_x_min = Double.MIN_VALUE;
		double best_x_max = Double.MAX_VALUE;
		boolean left_is_good = false;
		boolean right_is_good = false;
		if (indexes != null)
			Arrays.fill(indexes, -1);
		for (int i=0; i < getVertexCount(); i++) {
			double y_start = getEdgeStart(i).getDoublePosition(1);
			double y_end = getEdgeEnd(i).getDoublePosition(1);
			if (y_start == y_end) {
				if ( y_start == position[1]) {
					double x_start = getEdgeStart(i).getDoublePosition(0);
					double x_end = getEdgeEnd(i).getDoublePosition(0);
					if ((x_start <= position[0]) && (x_end >= position[0])){
						if (indexes != null)
							indexes[0] = indexes[1] = i;
						if (x_intercepts != null) {
							x_intercepts[0] = x_start;
							x_intercepts[1] = x_end;
						}
						return true;
					}
				}
				continue;
			}
			// Check to see if it's an edge to the left
			if ((y_start <= position[1]) && (y_end >= position[1])) {
				double x_intercept = interpolateEdgeXAtY(i, position[1]);
				if (x_intercept <= position[0] && x_intercept >= best_x_min) {
					best_x_min = x_intercept;
					left_is_good = true;
					if (indexes != null)
						indexes[0] = i;
				}
				// Check to see if it's an excluding edge to the right
				if (x_intercept >= position[0] && x_intercept < best_x_max) {
					best_x_max = x_intercept;
					right_is_good = false;
					if (indexes != null) 
						indexes[1] = i;
				}
			}
			// Check to see if it's an edge to the right (upside-down)
			if ((y_end >= position[1]) && (y_start <= position[1])) {
				double x_intercept = interpolateEdgeXAtY(i, position[1]);
				if (x_intercept >= position[0] && x_intercept < best_x_max) {
					best_x_max = x_intercept;
					right_is_good = true;
					if (indexes != null)
						indexes[1] = i;
				}
				if (x_intercept <= position[0] && x_intercept > best_x_min) {
					best_x_min = x_intercept;
					left_is_good = false;
					if (indexes != null)
						indexes[0] = i;
				}
			}
		}
		if (x_intercepts != null) {
			x_intercepts[0] = best_x_min;
			x_intercepts[1] = best_x_max;
		}
		return left_is_good && right_is_good;
	}
}
