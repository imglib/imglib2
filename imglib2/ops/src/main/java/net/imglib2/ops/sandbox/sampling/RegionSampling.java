package net.imglib2.ops.sandbox.sampling;

import net.imglib2.roi.RegionOfInterest;


// For instance with this class you can sample an ellipse with a sampling (like
// a regular grid).

// TODO : compound regions. compound samplings. sampling from the center of a
// region outwards. sampling from any point in a region outwards. a sampling
// that is not oriented along default axes. a polar sampling.

// TODO - the internals of iteration could be replaced with a
// ConditionalSampling with test being "inside region". This is preferable but
// as implemented there would be double copying of the "curr" member. For speed
// we want the "curr" memeber to be allocated from outside and just used. It's
// a somewhat big change and can be done when it is deemed important. Skipping
// for now.

public class RegionSampling extends AbstractSampling {

	private final RegionOfInterest region;
	private final Sampling sampling;

	public RegionSampling(RegionOfInterest region, Sampling sampling) {
		super(region.numDimensions());
		this.region = region;
		this.sampling = sampling;
	}

	@Override
	public SamplingIterator iterator() {
		return new RegionSamplingIterator();
	}

	@Override
	public long size() {
		SamplingIterator iter = iterator();
		long num = 0;
		while (iter.hasNext()) {
			iter.fwd();
			num++;
		}
		return num;
	}

	@Override
	public double realMin(int d) {
		// TODO - this is wrong. it depends on region and sampling. Its the grid
		// point that is in region and nearest the region's min for this dim
		return region.realMin(d);
	}

	@Override
	public double realMax(int d) {
		// TODO - this is wrong. it depends on region and sampling. Its the grid
		// point that is in region and nearest the region's max for this dim
		return region.realMax(d);
	}

	@Override
	public int numDimensions() {
		return region.numDimensions();
	}

	private class RegionSamplingIterator extends AbstractSamplingIterator {

		private final SamplingIterator iter;
		private boolean cached;

		public RegionSamplingIterator() {
			super(n);
			iter = sampling.iterator();
			cached = false;
		}

		@Override
		public SamplingIterator copyCursor() {
			return new RegionSamplingIterator();
		}

		@Override
		public SamplingIterator copy() {
			return new RegionSamplingIterator();
		}

		@Override
		public boolean hasNext() {
			if (cached) return true;
			return positionToNext();
		}

		@Override
		public void fwd() {
			if (cached) {
				cached = false;
				return;
			}
			if (!positionToNext()) {
				throw new IllegalArgumentException("cannot fwd() beyond end");
			}
		}

		@Override
		public void reset() {
			iter.reset();
			cached = false;
		}

		private boolean positionToNext() {
			cached = false;
			while (iter.hasNext()) {
				double[] pos = iter.next();
				if (region.contains(pos)) {
					cached = true;
					for (int i = 0; i < n; i++)
						curr[i] = pos[i];
					return true;
				}
			}
			return false;
		}
	}

}
