/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Lee Kamentsky
 *
 */
package mpicbg.imglib.labeling;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.roi.AbstractIterableRegionOfInterest;
import mpicbg.imglib.roi.IterableRegionOfInterest;
import mpicbg.imglib.roi.RegionOfInterest;

/**
 * A relatively conservative strategy suitable for blobby objects - 
 * retain the bounding boxes and raster starts and reconstruct the 
 * cursors by scanning.
 * 
 * @author leek
 *
 * @param <T> - the type used to label the space
 * @param <L> - the labeling class that will use this strategy for cursors and random access.
 */
public class DefaultROIStrategy<T extends Comparable<T>, L extends Labeling<T>>
		implements LabelingROIStrategy<T, L> {

	final protected L labeling;
	protected long generation;
	
	private class LabelStatistics extends BoundingBox {
		private long [] rasterStart;
		private long area = 0;
		public LabelStatistics(int dimensions) {
			super(dimensions);
			rasterStart = new long [dimensions];
			Arrays.fill(rasterStart, Integer.MAX_VALUE);
		}
		
		public void getRasterStart(long[] start) {
			System.arraycopy(rasterStart, 0, start, 0, rasterStart.length);
		}
		public long getArea() {
			return area;
		}
		public void update(long[] position) {
			super.update(position);
			area++;
			for (int i = 0; i<rasterStart.length; i++) {
				if (rasterStart[i] > position[i]) {
					System.arraycopy(position, 0, rasterStart, 0, rasterStart.length);
					return;
				} else if (rasterStart[i] < position[i]) {
					return;
				}
			}
		}
	}
	protected Map<T, LabelStatistics> statistics;
	public DefaultROIStrategy(L labeling) {
		this.labeling = labeling;
		generation = Long.MIN_VALUE;
	}

	/**
	 * Compute all statistics on the labels if cache is dirty.
	 */
	protected void computeStatistics() {
		LabelingType<T> type = labeling.firstElement();
		if ((type == null) || (type.getGeneration() != generation)) {
			statistics = new HashMap<T, LabelStatistics>();
			long [] position = new long [labeling.numDimensions()];
			LabelStatistics last = null;
			T lastLabel = null;
			Cursor<LabelingType<T>> c = labeling.localizingCursor();
			while(c.hasNext()) {
				type = c.next();
				c.localize(position);
				for (T label: type.getLabeling()) {
					if ((last == null) || (! label.equals(lastLabel))) {
						lastLabel = label;
						last = statistics.get(label);
						if (last == null) {
							last = new LabelStatistics(labeling.numDimensions());
							statistics.put(label, last);
						}
					}
					last.update(position);
				}
			}
			generation = type.getGeneration();
		}
	}

	@Override
	public boolean getExtents(T label, long[] minExtents, long[] maxExtents) {
		computeStatistics();
		LabelStatistics stats = statistics.get(label);
		if (stats == null) {
			if (minExtents != null)
				Arrays.fill(minExtents, 0);
			if (maxExtents != null)
				Arrays.fill(maxExtents, 0);
			return false;
		} else {
			stats.getExtents(minExtents, maxExtents);
			return true;
		}
	}

	@Override
	public boolean getRasterStart(T label, long[] start) {
		computeStatistics();
		LabelStatistics stats = statistics.get(label);
		if (stats == null) {
			Arrays.fill(start, 0);
			return false;
		} else {
			stats.getRasterStart(start);
			return true;
		}
	}

	@Override
	public long getArea(T label) {
		computeStatistics();
		LabelStatistics stats = statistics.get(label);
		if (stats == null) {
			return 0;
		}
		return stats.getArea();
	}

	@Override
	public Collection<T> getLabels() {
		computeStatistics();
		return statistics.keySet();
	}
	
	/**
	 * Implement a region of interest by linking to the statistics.
	 * 
	 * @author leek
	 *
	 */
	class DefaultRegionOfInterest extends AbstractIterableRegionOfInterest {
		/* (non-Javadoc)
		 * @see mpicbg.imglib.roi.AbstractIterableRegionOfInterest#size()
		 */
		T label;
		final RandomAccess<LabelingType<T>> randomAccess;
		final LabelStatistics labelStats;
		final long [] min;
		final long [] max;
		final long [] firstRaster;
		final double [] real_min;
		final double [] real_max;
		DefaultRegionOfInterest(T label) {
			super(labeling.numDimensions());
			this.label = label;
			randomAccess = new LabelingOutOfBoundsRandomAccess<T>(labeling);
			computeStatistics();
			labelStats = statistics.get(label);
			min = new long [labeling.numDimensions()];
			max = new long [labeling.numDimensions()];
			firstRaster = new long [labeling.numDimensions()];
			labelStats.getExtents(min, max);
			labelStats.getRasterStart(firstRaster);
			real_min = new double [labeling.numDimensions()];
			real_max = new double [labeling.numDimensions()];
			
			labelStats.getExtents(min, max);
			for (int i = 0; i < labeling.numDimensions(); i++) {
				real_min[i] = min[i];
				real_max[i] = max[i];
			}
		}

		@Override
		protected long size() {
			return labelStats.getArea();
		}

		@Override
		protected boolean isMember(double[] position) {
			for (int i = 0; i < position.length; i++) {
				randomAccess.setPosition((int)position[i], i);
			}
			return randomAccess.get().getLabeling().contains(label);
		}

		@Override
		protected void getExtrema(long[] minima, long[] maxima) {
			System.arraycopy(min, 0, minima, 0, numDimensions());
			System.arraycopy(max, 0, maxima, 0, numDimensions());
		}

		@Override
		protected boolean nextRaster(long[] position, long[] end) {
			for (int i=numDimensions()-1; i>=0; i--) {
				if (position[i] < min[i]) {
					System.arraycopy(min, 0, position, 0, i+1);
					// Pre-decrement in anticipation of one increment.
					position[0]--;
					break;
				}
			}
			do {
				int i;
				for (i = 0; i < numDimensions(); i++) {
					if (position[i] >= max[i]-1) {
						position[i] = min[i];
					} else {
						position[i]++;
						break;
					}
				}
				if (i == numDimensions()) return false;
				randomAccess.setPosition(position);
			} while (! randomAccess.get().getLabeling().contains(label));
			System.arraycopy(position, 0, end, 0, numDimensions());
			do {
				end[0]++;
				randomAccess.setPosition(end);
			} while ((end[0] < max[0]) && (randomAccess.get().getLabeling().contains(label)));
			return true;
		}

	}

	@Override
	public RegionOfInterest createRegionOfInterest(T label) {
		return new DefaultRegionOfInterest(label);
	}

	@Override
	public IterableRegionOfInterest createIterableRegionOfInterest(T label) {
		return new DefaultRegionOfInterest(label);
	}

}
