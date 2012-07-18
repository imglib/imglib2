package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.img.ImgPlus;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;

public class DiscNeighborhood<T> extends RectangleNeighborhood<T> implements RealPositionable {
	
	/** The radius of the sphere, in calibrated units. */
	protected double radius;
	/** The spatial calibration of the first 3 dimensions. 	 */
	protected final double[] calibration = new double[3];

	/*
	 * CONSTRUCTORS
	 */
	
	public DiscNeighborhood(final ImgPlus<T> source, final double radius, final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		super(source, outOfBounds);
		this.radius = radius;
		for (int d = 0; d < 2; d++) {
			calibration[ d ] = source.calibration(d); 
		}
		setRadius(radius);
	}

	public DiscNeighborhood(final ImgPlus<T> source, final double radius) {
		this(source, radius, new OutOfBoundsMirrorFactory<T, RandomAccessibleInterval<T>>(Boundary.DOUBLE));
	}
	
	/*
	 * METHODS
	 */
	
	/**
	 * Overridden not to do anything.
	 * @see #setRadius(double)
	 */
	@Override
	public void setSpan(long[] span) {	}
	
	/**
	 * Change the radius of this neighborhood.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
		// Compute span
		final long[] span = new long[2];
		for (int d = 0; d < span.length; d++) {
			span[ d ] = Math.round( radius / calibration[d] ) ;
		}
		super.setSpan(span);
	}	
	
	@Override
	public long size() {
		long pixel_count = 0;
		final int[] local_rxs = new int [ (int) (span[1]  +  1) ];
		int local_rx;

		Utils.getXYEllipseBounds((int) span[0], (int) span[1], local_rxs);
		local_rx = local_rxs[0]; // middle line
		pixel_count += 2 * local_rx + 1;
		for (int i = 1; i <= span[1]; i++) {
			local_rx = local_rxs[i];
			pixel_count += 2 * (2 * local_rx + 1); // Twice because we mirror
		}

		return pixel_count;	
	}

	@Override
	public DiscCursor<T> cursor() {
		return new DiscCursor<T>(this);
	}

	@Override
	public DiscCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public DiscCursor<T> iterator() {
		return cursor();
	}

	@Override
	public void move(float distance, int d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(double distance, int d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(RealLocalizable localizable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(float[] distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(double[] distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(RealLocalizable localizable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(float[] position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(double[] position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(float position, int d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(double position, int d) {
		// TODO Auto-generated method stub
		
	}

}
