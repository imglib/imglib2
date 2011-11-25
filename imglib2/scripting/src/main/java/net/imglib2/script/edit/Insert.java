package net.imglib2.script.edit;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.math.fn.FloatImageOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.Views;

/**
 * Lazy copy one image into another, with a positive or negative offset.
 * None of the two given images are altered; rather, this is an {@link IFunction}
 * that, when executed, generates a new image with the combination of the two input images.
 * 
 * @author Albert Cardona
 *
 * @param <T>
 */
public class Insert<T extends RealType<T>, RI extends IterableInterval<T> & RandomAccessible<T>> extends FloatImageOperation {

	private final RI source;
	private final IterableInterval<T> target;
	private final Cursor<T> tc;
	private final long[] offset, min, max;
	private final Copier copier;
	
	static private final boolean inside(final long[] pos, final long[] min, final long[] max) {
		for (int i=0; i<pos.length; ++i) {
			if (pos[i] < min[i] || pos[i] > max[i]) return false;
		}
		return true;
	}

	private abstract class Copier
	{
		protected abstract double eval();
	}
	
	private final class CompatibleCopier extends Copier
	{
		private final RandomAccessibleIntervalCursor<T> sc;
		private final long[] position;
		
		CompatibleCopier(final RandomAccessibleInterval<T> view) {
			this.sc = new RandomAccessibleIntervalCursor<T>(view);
			this.position = new long[view.numDimensions()];
		}
		@Override
		protected final double eval() {
			tc.fwd();
			tc.localize(position);
			return inside(position, min, max) ?
					  sc.next().getRealDouble()
					: tc.get().getRealDouble();
		}
	}
	private final class RandomAccessCopier extends Copier
	{
		private final RandomAccess<T> sc;
		private final long[] position;
		
		RandomAccessCopier(final RandomAccessibleInterval<T> view) {
			this.sc = view.randomAccess();
			this.position = new long[view.numDimensions()];
		}
		@Override
		protected final double eval() {
			tc.fwd();
			tc.localize(position);
			if (inside(position, min, max)) {
				for (int i=0; i<position.length; ++i) {
					position[i] -= min[i];
				}
				sc.setPosition(position);
				return sc.get().getRealDouble();
			}
			return tc.get().getRealDouble();
		}
	}
	
	/**
	 * 
	 * @param source The image to copy from.
	 * @param offset The offset in the image to copy from.
	 * @param target The image to copy into.
	 */
	public Insert(final RI source, final IterableInterval<T> target, final long[] offset) {
		this.source = source;
		this.offset = offset;
		this.target = target;
		this.tc = target.cursor();
		this.min = new long[source.numDimensions()];
		this.max = new long[source.numDimensions()];
		for (int i=0; i<this.min.length; ++i) {
			this.min[i] = (long) Math.min(offset[i], target.realMax(i));
			this.max[i] = (long) Math.min(offset[i] + source.realMax(i), target.realMax(i));
		}
		RandomAccessibleInterval<T> view = Views.interval(source, min, max);
		this.copier = source.equalIterationOrder(target) ? new CompatibleCopier(view) : new RandomAccessCopier(view);
	}

	@Override
	public final double eval() {
		return this.copier.eval();
	}

	@Override
	public void findCursors(Collection<RealCursor<?>> cursors) {
		cursors.add(this.tc);
	}

	@Override
	public void findImgs(Collection<IterableRealInterval<?>> iris) {
		iris.add(target);
	}

	@Override
	public IFunction duplicate() throws Exception {
		return new Insert<T,RI>(source, target, offset);
	}
}
