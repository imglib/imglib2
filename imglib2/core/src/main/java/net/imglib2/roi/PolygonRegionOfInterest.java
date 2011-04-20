package net.imglib2.roi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

public class PolygonRegionOfInterest extends AbstractIterableRegionOfInterest {

	protected ArrayList<RealPoint> points = new ArrayList<RealPoint>();
	
	/**
	 * @author leek
	 *
	 * We decompose the polygon into stripes from yMin to yMin which have
	 * arrays of xTop and xBottom describing the polygon boundary between yMin and yMax.
	 * Inside and outside is determined by whether you cross an even number of boundaries
	 * or an odd number to get where you're going.
	 * 
	 * There is no vertex (explicit or implied) that falls between yMin and yMax
	 * which makes it easy to binary search for your chunk.
	 * 
	 */
	static protected class Stripe {
		final public double yMin;
		public double yMax;
		final public ArrayList<Double> xTop = new ArrayList<Double>();
		final public ArrayList<Double> xBottom = new ArrayList<Double>();
		public Stripe(double yMin, double yMax) {
			this.yMin = yMin;
			this.yMax = yMax;
		}
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(String.format("y: %.2f<->%.2f\n", yMin, yMax));
			for (int i=0; i<xTop.size(); i++) {
				sb.append(String.format("%d: %.2f<->%.2f\n", i, xTop.get(i), xBottom.get(i)));
			}
			return sb.toString();
		}
	}
	
	ArrayList<Stripe> stripes;
	
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
	
	/**
	 * Build the cached list of stripes if necessary.
	 */
	protected void validate() {
		if (stripes == null) {
			SortedSet<Double> y = new TreeSet<Double>();
			for (RealPoint p:points) {
				y.add(p.getDoublePosition(1));
			}
			Double [] dy = new Double [y.size()];
			y.toArray(dy);
			stripes = new ArrayList<Stripe>();
			for (int i = 0; i < dy.length - 1; i++) {
				stripes.add(new Stripe(dy[i], dy[i+1]));
			}
			for (int i = 0; i < points.size(); i++) {
				RealLocalizable p0 = getEdgeStart(i);
				RealLocalizable p1 = getEdgeEnd(i);
				double x0 = p0.getDoublePosition(0);
				double y0 = p0.getDoublePosition(1);
				double x1 = p1.getDoublePosition(0);
				double y1 = p1.getDoublePosition(1);
				if (y0 == y1) continue;
				if (y0 > y1) {
					double temp = x0;
					temp = x0; x0 = x1; x1 = temp;
					temp = y0; y0 = y1; y1 = temp;
				}
				int index = findStripeIndex(y0);
				do {
					Stripe stripe = stripes.get(index);
					double xTop = x0 + (stripe.yMin - y0) * (x1 - x0) / (y1 - y0);
					double xBottom = x0 + (stripe.yMax - y0) * (x1 - x0) / (y1 - y0);
					/*
					 * Easy - if the stripe is empty, add the edge
					 */
					if (stripe.xTop.size() == 0) {
						stripe.xTop.add(xTop);
						stripe.xBottom.add(xBottom);
						index++;
						continue;
					}
					/*
					 * Find j = index of edge with greater or equal xTop.
					 */
					int j = 0;
					double stripe_xTop = Double.MIN_VALUE;
					for (j = 0; j < stripe.xTop.size(); j++) {
						stripe_xTop = stripe.xTop.get(j);
						if (stripe_xTop >= xTop) break; 
					}
					/*
					 * If our xTop is after all other xTop, check
					 * for xBottom before last and split if so.
					 */
					if (j == stripe.xTop.size()) {
						if (xBottom >= stripe.xBottom.get(j-1)) {
							stripe.xTop.add(xTop);
							stripe.xBottom.add(xBottom);
							index++;
						} else {
							splitStripe(index, j-1, xTop, xBottom);
						}
					}
					/*
					 * If our xTop is equal to some other xTop, then they
					 * share a vertex. We have to check that xBottom is at
					 * or after the previous xBottom and that it is at or before
					 * the succeeding xBottom
					 */
					else if (xTop == stripe_xTop) {
						if ((j < stripe.xTop.size() - 1) && 
							(xBottom > stripe.xBottom.get(j+1))) {
							splitStripe(index, j+1, xTop, xBottom);
						} else if ((j > 0) && (xBottom < stripe.xBottom.get(j-1))) {
							splitStripe(index, j-1, xTop, xBottom);
						} else {
							if (xBottom > stripe.xBottom.get(j)) {
								/*
								 * Put the new edge after the matching edge
								 * because the bottom is advanced.
								 */
								j++;
							}
							stripe.xTop.add(j, xTop);
							stripe.xBottom.add(j, xBottom);
							index++;
						}
					} 
					/*
					 * If our xBottom is greater than the stripe xBottom, 
					 * then the edges cross and need to be split. 
					 */
					else if (xBottom > stripe.xBottom.get(j)) {
						splitStripe(index, j, xTop, xBottom);
					} else
					/*
					 * If our xBottom is less than the previous edge's xBottom
					 * then this edge crosses the previous edge.
					 */
					if ((j > 0) && (xBottom < stripe.xBottom.get(j-1))){
						splitStripe(index, j-1, xTop, xBottom);
					} else {
						stripe.xTop.add(j, xTop);
						stripe.xBottom.add(j, xBottom);
						index++;
					}
				} while((index < stripes.size()) && (y1 > stripes.get(index).yMin));
			}
		}
	}
	
	/**
	 * Split a stripe in half because two edges cross
	 * 
	 * @param stripeIndex index of the stripe
	 * @param xIndex index of the crossing edge
	 * @param xTop xTop of the incoming edge
	 * @param xBottom xBottom of the incoming edge
	 */
	private void splitStripe(int stripeIndex, int xIndex, double xTop, double xBottom) {
		Stripe stripe = stripes.get(stripeIndex);
		double stripe_xTop = stripe.xTop.get(xIndex);
		double stripe_xBottom = stripe.xBottom.get(xIndex);
		double yTop = stripe.yMin;
		double yBottom = stripe.yMax;
		double dTop = Math.abs(xTop - stripe_xTop);
		double dBottom = Math.abs(xBottom - stripe_xBottom);
		/*
		 * yCross = crossing point.
		 * dTop = abs(xTop - stripe_xTop), dBottom is similar
		 * 
		 *  yCross - yTop    yBottom - yCross  
		 *  -------------- = ----------------
		 *  dTop               dBottom
		 *  
		 *  yCross - yTop = (dTop / dBottom) * yBottom - yCross(dTop / dBottom)
		 *  yCross(1 + dTop / dBottom) = yBottom * (dTop / dBottom) + yTop
		 *  yCross = yBottom * (dTop / dBottom) + yTop
		 *           ---------------------------------
		 *           (1 + dTop / dBottom)
		 *  
		 */
		double yCross = ((yBottom * dTop / dBottom) + yTop) / (1 + dTop / dBottom);
		stripe.yMax = yCross;
		Stripe newStripe = new Stripe(yCross, yBottom);
		stripes.add(stripeIndex+1, newStripe);
		for (int i=0; i<stripe.xTop.size(); i++) {
			double xT = stripe.xTop.get(i);
			double xB = stripe.xBottom.get(i);
			double xM = xT + (yCross - yTop) * (xB - xT) / (yBottom - yTop);
			stripe.xBottom.set(i, xM);
			newStripe.xTop.add(xM);
			newStripe.xBottom.add(xB);
		}
	}
	 
	/**
	 * Find the index of the stripe whose yMin is lower or the same as the given y
	 * @param y
	 * @return the index or -1 if all are greater.
	 * 
	 * Pseudocode borrowed from http://en.wikipedia.org/wiki/Binary_search_algorithm
	 */
	protected int findStripeIndex(double y) {
		if ((stripes.size() == 0) || (stripes.get(0).yMin > y)) return -1;
		int minimum = 0;
		int maximum = stripes.size()-1;
		while (minimum < maximum) {
			int test_index = (minimum + maximum) / 2;
			double yMin = stripes.get(test_index).yMin;
			if (y == yMin) {
				return test_index;
			}
			if (y > yMin) {
				minimum = test_index + 1;
			} else {
				maximum = test_index;
			}
		}
		if (stripes.get(minimum).yMin <= y) return minimum;
		return minimum - 1;
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
		validate();
		long accumulator = 0;
		if (stripes.size() == 0) return 0;
		for (Stripe stripe: stripes) {
			double yTop = stripe.yMin;
			double yBottom = stripe.yMax;
			for (int i=0; i < stripe.xTop.size(); i++) {
				double xTop = stripe.xTop.get(i);
				double xBottom = stripe.xBottom.get(i);
				long area = getAreaOnBehalfOfSize(yTop, xTop, yBottom, xBottom);
				if (i % 2 == 0) {
					accumulator += area;
				} else {
					accumulator -= area;
				}
			}
		}
		return accumulator;
	}
	
	private long getAreaOnBehalfOfSize(double y0, double x0, double y1, double x1) {
		long x_max = this.max(0) + 1;
		long x_min = (long)Math.min(x0, x1);
		// Renormalize x to prevent overflow errors
		x0 = x0 - x_min;
		x1 = x1 - x_min;
		x_max = x_max - x_min;
		double inv_slope = (x1 - x0) / (y1 - y0);
		double slope = 1 / inv_slope;
		// Renormalize y to integer bounds
		//
		long y1_floor = (long)y1;
		long y0_ceiling = (long)Math.ceil(y0);
		double x0_temp = x0 + (y0_ceiling - y0) * inv_slope;
		x1 = x0 + (y1_floor - y0) * inv_slope;
		x0 = x0_temp;
		y1 = y1_floor;
		y0 = y0_ceiling;
		//
		// A pixel at x,y is in the area if
		// y >= y0
		// y <= y1
		// x >= x0 + (y - y0) * (x1 - x0) / (y1 - y0)
		// x <= x_max
		//
		// So the number of pixels at a given y is
		//
		// x_max - floor(x0 + (y - y0) * (x1 - x0) / (y1 - y0))
		// 
		// y goes from ceil(y0) to floor(y1) inclusive so
		//
		// area = x_max * (floor(y1) - ceil(y0) + 1) -
		//        sum(floor(x0 + (y - y0) * (x1 - x0) / (y1 - y0)), ceil(y0), floor(y1))
		//
		// So you can approach this like the Bresenham algorithm. For the case
		// where s = (y1 - y0) / (x1 - x0), invS = (x1 - x0) / (y1 - y0), abs(invS) < 1
		//
		// there is an increase in x every floor(s) y with the first one occurring at
		// x0 - floor(x0) + invS * (y - y0) > 1
		// yf = ceil((1 - x0 + floor(x0)) * s + y0)
		//
		// We increment X thereafter for every floor(s) y, but there is an accumulated error
		// of e(y) = x0 + (yf - y0) * invS - floor(x0 + (yf - y0) * invS) + (y - yf) * (s - floor(s))
		//
		// and at the end, we find that our x is off by e(floor(y1)) for a total error of
		// floor(e(floor(y1)) * (floor(y1) - yf + 1) / 2
		// 
		//TODO: get rid of this loop.
		long height = (y1_floor - y0_ceiling + 1);
		long area = x_max * height;
		for (long y = y0_ceiling; y <= y1_floor; y++) {
			area -= x0 + (y - y0) * inv_slope;
		}
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
		validate();
		if (stripes.size() == 0) return false;
		
		long x = position[0];
		long y = position[1];
		Stripe stripe = null;
		int index = 0;
		while(true) {
			if ((stripe == null) || stripe.yMax < y) {
				index = findStripeIndex(y);
				if (index == -1) {
					/*
					 *  Position is before any stripe. Set up at the
					 *  first raster and try again.
					 */
					stripe = stripes.get(0);
					index = 0;
					x = Long.MIN_VALUE;
					y = (long)Math.ceil(stripe.yMin);
					continue;
				} else {
					stripe = stripes.get(index);
				}
			}
			if (stripe.yMax <= y) {
				/*
				 * Previous stripe is wholly before this one.
				 * Go to next stripe if any.
				 */
				if (stripes.size() == index+1) return false;
				y = (long)Math.ceil(stripes.get(index + 1).yMin);
				continue;
			}
			int xIndex;
			boolean inside = false;
			long xInterpolatedLast = Long.MIN_VALUE;
			for (xIndex = 0; xIndex < stripe.xTop.size(); xIndex++ ) {
				double xTop = stripe.xTop.get(xIndex);
				double xBottom = stripe.xBottom.get(xIndex);
				double xInterpolated = xTop + (xBottom - xTop) * (y - stripe.yMin) / (stripe.yMax - stripe.yMin);
				if (! inside) {
					xInterpolatedLast = (long)Math.ceil(xInterpolated);
					inside = true;
				} else {
					xInterpolated = Math.floor(xInterpolated) + 1;
					if ((x < xInterpolated) && (xInterpolated > xInterpolatedLast)) {
						position[0] = xInterpolatedLast;
						position[1] = (long)y;
						end[0] = (long)xInterpolated;
						end[1] = position[1];
						return true;
					}
					inside = false;
				}
			}
			/*
			 * If we fall through, x is after the stripe.
			 */
			y += 1;
			x = Long.MIN_VALUE;
		}
	}

	@Override
	protected boolean isMember(double[] position) {
		return getEdges(position, null);
	}

	/**
	 * Get the coordinates of the first vertex of the indexed edge
	 * @param start the index of the edge
	 * @return the first vertex, going clockwise
	 */
	public RealLocalizable getEdgeStart(int start) {
		if (start < 0) {
			start = (start % points.size()) + points.size();
		} else if (start >= points.size()) {
			start = start % points.size();
		}
		return points.get(start);
	}
	/**
	 * Get the coordinates of the second vertex of the indexed edge
	 * @param start the index of the edge
	 * @return the second vertex, going clockwise
	 */
	public RealLocalizable getEdgeEnd(int start) {
		return getEdgeStart(start+1);
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
	 * @param x_intercepts x-intercepts of the edges at the Y position passed in
	 * @return true if point is within polygon.
	 */
	protected boolean getEdges(double [] position, double [] x_intercepts) {
		if (x_intercepts == null) {
			x_intercepts = new double [2];
		}
		x_intercepts[0] = Double.MIN_VALUE;
		x_intercepts[1] = Double.MAX_VALUE;
		int count_to_left = 0;
		for (int i=0; i < getVertexCount(); i++) {
			double y_start = getEdgeStart(i).getDoublePosition(1);
			double y_end = getEdgeEnd(i).getDoublePosition(1);
			double x_start = getEdgeStart(i).getDoublePosition(0);
			double x_end = getEdgeEnd(i).getDoublePosition(0);
			if (y_start == y_end) {
				if ( y_start == position[1]) {
					if ((x_start <= position[0]) && (x_end >= position[0])){
						x_intercepts[0] = x_start;
						x_intercepts[1] = x_end;
						return true;
					}
				}
				continue;
			}
			double x_intercept = interpolateEdgeXAtY(i, position[1]);
			// Check to see if it's an edge to the left
			if (x_intercept <= position[0]) {
				count_to_left ++;
				if (x_intercept > x_intercepts[0]) {
					x_intercepts[0] = x_intercept;
				}
			} else if (x_intercept < x_intercepts[1]) {
				x_intercepts[1] = x_intercept;
			}
		}
		return (count_to_left % 2) == 1;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		char c = '(';
		for (RealPoint p: points) {
			sb.append(c);
			sb.append(p.toString());
			c = ',';
		}
		sb.append(")");
		return sb.toString();
	}
}
