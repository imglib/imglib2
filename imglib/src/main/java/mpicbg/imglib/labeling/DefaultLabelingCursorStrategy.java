package mpicbg.imglib.labeling;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.type.label.FakeType;

/**
 * A relatively conservative strategy suitable for blobby objects - 
 * retain the bounding boxes and raster starts and reconstruct the 
 * cursors by scanning.
 * 
 * @author leek
 *
 * @param <T>
 * @param <L>
 */
public class DefaultLabelingCursorStrategy<T extends Comparable<T>, L extends Labeling<T>>
		implements LabelingCursorStrategy<T, L> {

	final protected L labeling;
	protected long generation;
	protected LabelingType<T> type = null;
	
	private class LabelStatistics extends BoundingBox {
		private int [] rasterStart;
		private long area = 0;
		public LabelStatistics(int dimensions) {
			super(dimensions);
			rasterStart = new int [dimensions];
			Arrays.fill(rasterStart, Integer.MAX_VALUE);
		}
		
		public void getRasterStart(int [] dest) {
			System.arraycopy(rasterStart, 0, dest, 0, rasterStart.length);
		}
		public long getArea() {
			return area;
		}
		public void update(int [] coordinates) {
			super.update(coordinates);
			area++;
			for (int i = 0; i<rasterStart.length; i++) {
				if (rasterStart[i] > coordinates[i]) {
					System.arraycopy(coordinates, 0, rasterStart, 0, rasterStart.length);
					return;
				} else if (rasterStart[i] < coordinates[i]) {
					return;
				}
			}
		}
	}
	protected Map<T, LabelStatistics> statistics;
	public DefaultLabelingCursorStrategy(L labeling) {
		this.labeling = labeling;
		generation = Long.MIN_VALUE;
	}

	/**
	 * Compute all statistics on the labels if cache is dirty.
	 */
	protected void computeStatistics() {
		if ((type == null) || (type.getGeneration() != generation)) {
			statistics = new HashMap<T, LabelStatistics>();
			LocalizableCursor<LabelingType<T>> c = labeling.createLocalizableCursor();
			if (type == null) {
				type = c.getType();
			}
			int [] position = c.createPositionArray();
			LabelStatistics last = null;
			T lastLabel = null;
			for (LabelingType<T> t:c) {
				c.getPosition(position);
				for (T label:t.getLabeling()) {
					if ((last == null) || (! label.equals(lastLabel))) {
						lastLabel = label;
						last = statistics.get(label);
						if (last == null) {
							last = new LabelStatistics(c.getNumDimensions());
							statistics.put(label, last);
						}
					}
					last.update(position);
				}
			}
			generation = type.getGeneration();
			c.close();
		}
	}
	@Override
	public LocalizableCursor<FakeType> createLocalizableLabelCursor(T label) {
		int [] offset = new int[labeling.getNumDimensions()];
		int [] size = new int[labeling.getNumDimensions()];
		getExtents(label, offset, size);
		for (int i=0; i<offset.length; i++) size[i] -= offset[i];
		
		LocalizableByDimCursor<LabelingType<T>> c = labeling.createLocalizableByDimCursor();
		RegionOfInterestCursor<LabelingType<T>> roiCursor = new RegionOfInterestCursor<LabelingType<T>>(c, offset, size);
		return new LocalizableLabelingCursor<T>(roiCursor, offset, label);
	}

	@Override
	public LocalizableCursor<FakeType> createLocalizablePerimeterCursor(T label) {
		int [] offset = new int[labeling.getNumDimensions()];
		int [] size = new int[labeling.getNumDimensions()];
		getExtents(label, offset, size);
		for (int i=0; i<offset.length; i++) size[i] -= offset[i];
		LocalizableByDimCursor<LabelingType<T>> c = labeling.createLocalizableByDimCursor();
		RegionOfInterestCursor<LabelingType<T>> roiCursor = new RegionOfInterestCursor<LabelingType<T>>(c, offset, size);
		LocalizableByDimCursor<LabelingType<T>> cc = labeling.createLocalizableByDimCursor();
		return new LocalizableLabelingPerimeterCursor<T>(roiCursor, offset, cc, label);
	}

	@Override
	public boolean getExtents(T label, int[] minExtents, int[] maxExtents) {
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
	public boolean getRasterStart(T label, int[] start) {
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

}
