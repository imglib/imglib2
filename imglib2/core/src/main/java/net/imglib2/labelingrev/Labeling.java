package net.imglib2.labelingrev;

import java.util.Collection;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.readwrite.WriteConvertedCursor;
import net.imglib2.converter.readwrite.WriteConvertedRandomAccessibleInterval;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.view.Views;

// TODO: SubsetIntervalIterable
public class Labeling<L extends Comparable<L>> extends
		WriteConvertedRandomAccessibleInterval<IntegerType<?>, LabelingType<L>>
		implements IterableInterval<LabelingType<L>> {

	private IterableInterval<IntegerType<?>> iterable;

	public Labeling(RandomAccessibleInterval<IntegerType<?>> source) {
		super(source, new LabelingTypeSamplerConverter<L>(
				new LabelingMapping<L>(Views.iterable(source).firstElement())));
		this.iterable = Views.iterable(source);
	}

	@Override
	public long size() {
		return iterable.size();
	}

	@Override
	public LabelingType<L> firstElement() {
		return cursor().next();
	}

	@Override
	public Object iterationOrder() {
		return iterable.iterationOrder();
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return iterable.iterationOrder().equals(f.iterationOrder());
	}

	@Override
	public Iterator<LabelingType<L>> iterator() {
		return cursor();
	}

	@Override
	public Cursor<LabelingType<L>> cursor() {
		return new WriteConvertedCursor<IntegerType<?>, LabelingType<L>>(
				iterable.cursor(), converter);
	}

	@Override
	public Cursor<LabelingType<L>> localizingCursor() {
		return new WriteConvertedCursor<IntegerType<?>, LabelingType<L>>(
				iterable.localizingCursor(), converter);
	}

	/**
	 * A labeling provides methods to get at ROIs representing each of the
	 * labeled objects in addition to image-like methods to discover the labels
	 * at given pixels.
	 * 
	 * @author Lee Kamentsky
	 * @author Christian Dietz
	 */

	/**
	 * Return the mapping which is used to convert from indices to labeling
	 * 
	 * @return
	 */
	public LabelingMapping<L> getMapping() {
		;
	}

	/**
	 * Find all labels in the space
	 * 
	 * @return a collection of the labels.
	 */
	public Collection<L> getLabels() {
	}

	/**
	 * Get a ROI that represents the pixels with the given label
	 * 
	 * @param label
	 * @return
	 */
	public IterableRegionOfInterest getIterableRegionOfInterest(L label){
	}

		/**
		 * Factory
		 * 
		 * @return create new labeling
		 */
		public <L2 extends Comparable<L2>> LabelingFactory<L2> factory();

	}
}
