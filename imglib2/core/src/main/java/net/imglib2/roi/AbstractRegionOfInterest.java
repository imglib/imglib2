package net.imglib2.roi;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.type.logic.BitType;

/**
 * @author leek
 *
 *The AbstractRegionOfInterest implements the IterableRegionOfInterest
 *using a raster function and a membership function that are
 *implemented by a derived class.
 */
public abstract class AbstractRegionOfInterest implements RegionOfInterest {
	protected int nDimensions;
	private double [] cached_real_min;
	private double [] cached_real_max;
	/**
	 * @author leek
	 *The AROIRandomAccess inner class implements the random access part of the
	 *ROI, allowing random sampling of pixel membership in the ROI.
	 */
	protected class AROIRandomAccess implements RealRandomAccess<BitType> {

		private BitType bit_type = new BitType();
		private double [] position;
		
		protected AROIRandomAccess( final AROIRandomAccess randomAccess ) {
			position = randomAccess.position.clone();
		}
		
		public AROIRandomAccess() {
			position = new double[nDimensions];
		}
		@Override
		public void localize(float[] pos) {
			for (int i = 0; i < pos.length; i++) {
				pos[i] = (float)this.position[i];
			}
		}

		@Override
		public void localize(double[] pos) {
			for (int i = 0; i < pos.length; i++) {
				pos[i] = this.position[i];
			}
		}

		@Override
		public float getFloatPosition(int dim) {
			return (float)position[dim];
		}

		@Override
		public double getDoublePosition(int dim) {
			return position[dim];
		}

		@Override
		public int numDimensions() {
			// TODO Auto-generated method stub
			return nDimensions;
		}

		@Override
		public void move(float distance, int dim) {
			position[dim] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move(double distance, int dim) {
			position[dim] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move(int distance, int dim) {
			position[dim] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move(long distance, int dim) {
			position[dim] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move(RealLocalizable localizable) {
			for (int i = 0; i < position.length; i++) {
				position[i] += localizable.getDoublePosition(i);
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move(Localizable localizable) {
			for (int i = 0; i < position.length; i++) {
				position[i] += localizable.getDoublePosition(i);
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move(float[] pos) {
			for (int i = 0; i < pos.length; i++) {
				this.position[i] += pos[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move(double[] pos) {
			for (int i = 0; i < pos.length; i++) {
				this.position[i] += pos[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move(int[] pos) {
			for (int i = 0; i < pos.length; i++) {
				this.position[i] += pos[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move(long[] pos) {
			for (int i = 0; i < pos.length; i++) {
				this.position[i] += pos[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(RealLocalizable localizable) {
			localizable.localize(position);
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(Localizable localizable) {
			for (int i = 0; i < position.length; i++) {
				this.position[i] = localizable.getDoublePosition(i);
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(float[] position) {
			for (int i = 0; i < position.length; i++) {
				this.position[i] = position[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(double[] position) {
			for (int i = 0; i < position.length; i++) {
				this.position[i] = position[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(int[] position) {
			for (int i = 0; i < position.length; i++) {
				this.position[i] = position[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(long[] position) {
			for (int i = 0; i < position.length; i++) {
				this.position[i] = position[i];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(float position, int dim) {
			this.position[dim] = position;
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(double position, int dim) {
			this.position[dim] = position;
			updateCachedMembershipStatus();
		}


		@Override
		public void setPosition(int position, int dim) {
			this.position[dim] = position;
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition(long position, int dim) {
			this.position[dim] = position;
			updateCachedMembershipStatus();
		}

		protected void updateCachedMembershipStatus() {
			bit_type.set(isMember(position));
		}
		
		@Override
		public void fwd(int dim) {
			position[dim] += 1;
			updateCachedMembershipStatus();
		}

		@Override
		public void bck(int dim) {
			position[dim] -= 1;
			updateCachedMembershipStatus();
		}

		@Override
		public BitType get() {
			return bit_type;
		}	
		
		@Override
		public AROIRandomAccess copy()
		{
			return new AROIRandomAccess( this );
		}

		@Override
		public AROIRandomAccess copyRealRandomAccess()
		{
			return copy();
		}
	}
	
	protected AbstractRegionOfInterest(int nDimensions) {
		this.nDimensions = nDimensions;
	}
	
	/**
	 * Determine whether a point is a member of the region of interest
	 * @param position position in question
	 * @return true if a member
	 */
	abstract protected boolean isMember(double [] position);
	
	/**
	 * Get the minimum and maximum corners of a bounding hypercube
	 * using real coordinates (which might have fractional components)
	 * 
	 * @param minima
	 * @param maxima
	 */
	abstract protected void getRealExtrema(double [] minima, double [] maxima);

	protected void validateRealExtremaCache() {
		if (cached_real_min == null) {
			double [] cachedRealMin = new double[nDimensions];
			double [] cachedRealMax = new double[nDimensions];
			getRealExtrema(cachedRealMin, cachedRealMax);
			cached_real_min = cachedRealMin;
			cached_real_max = cachedRealMax;
		}
	}
	
	protected void invalidateCachedState() {
		cached_real_min = null;
		cached_real_max = null;
	}
	/* (non-Javadoc)
	 * @see net.imglib2.RealInterval#realMin(int)
	 */
	@Override
	public double realMin(int d) {
		validateRealExtremaCache();
		return cached_real_min[d];
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealInterval#realMin(double[])
	 */
	@Override
	public void realMin(double[] min) {
		validateRealExtremaCache();
		for (int i = 0; i < min.length; i++) {
			min[i] = cached_real_min[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealInterval#realMin(net.imglib2.RealPositionable)
	 */
	@Override
	public void realMin(RealPositionable min) {
		validateRealExtremaCache();
		min.setPosition( cached_real_min );
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealInterval#realMax(int)
	 */
	@Override
	public double realMax(int d) {
		validateRealExtremaCache();
		return cached_real_max[d];
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealInterval#realMax(double[])
	 */
	@Override
	public void realMax(double[] max) {
		validateRealExtremaCache();
		for (int i = 0; i < max.length; i++) {
			max[i] = cached_real_max[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealInterval#realMax(net.imglib2.RealPositionable)
	 */
	@Override
	public void realMax(RealPositionable max) {
		validateRealExtremaCache();
		max.setPosition( cached_real_max );
	}

	@Override
	public int numDimensions() {
		return nDimensions;
	}

	@Override
	public RealRandomAccess<BitType> realRandomAccess() {
		return new AROIRandomAccess();
	}

}
