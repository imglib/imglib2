package net.imglib2.roi;

/**
 * @author leek
 *
 * An N-dimensional (hyper) rectangle whose edges are
 * orthogonal to the coordinate system.
 */
public class RectangleRegionOfInterest extends AbstractIterableRegionOfInterest {

	private final double [] origin;
	private final double [] extent;
	public RectangleRegionOfInterest(double [] origin, double [] extent ) {
		super(origin.length);
		this.origin = origin;
		this.extent = extent;
	}

	@Override
	protected boolean nextRaster(long[] position, long[] end) {
		/*
		 * Check for before
		 */
		for (int i=numDimensions()-1; i >= 0; i--) {
			if (position[i] < min(i)){
				for (; i >= 0; i--) {
					position[i] = end[i] = min(i);
				}
				end[0] = max(0)+1;
				return true;
			}
		}
		position[0] = min(0);
		end[0] = max(0) + 1;
		for (int i=1; i<numDimensions(); i++) {
			position[i] = end[i] = position[i] + 1;
			if (position[i] <= max(i)) return true;
			position[i] = end[i] = min(i);
		}
		return false;
	}

	@Override 
	protected boolean isMember(double[] position) {
		for (int i=0; i<numDimensions(); i++) {
			if (position[i] < realMin(i)) return false;
			if (position[i] >= realMax(i)) return false;
		}
		return true;
	}

	@Override
	protected long size() {
		long product = 1;
		for (int i=0; i<numDimensions(); i++) {
			product *= max(i) - min(i) + 1;
		}
		return product;
	}

	@Override
	protected void getExtrema(long[] minima, long[] maxima) {
		for (int i=0; i<numDimensions(); i++) {
			minima[i] = (long)Math.ceil(origin[i]);
			maxima[i] = (long)Math.ceil(origin[i] + extent[i])-1;
		}
	}

	@Override
	protected void getRealExtrema(double[] minima, double[] maxima) {
		System.arraycopy(origin, 0, minima, 0, numDimensions());
		for (int i=0; i < numDimensions(); i++) {
			maxima[i] = origin[i] + extent[i];
		}
	}

}
