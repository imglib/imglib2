/**
 * 
 */
package net.imglib2.roi;

import java.util.Iterator;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;

/**
 * @author leek
 *
 */
public class BinaryMaskRegionOfInterest<T extends BitType, I extends Img<T>> extends
		AbstractRegionOfInterest implements IterableRegionOfInterest {
	final I img;
	/*
	 * One RandomAccess per thread so that the thread can call setPosition.
	 */
	final ThreadLocal<RandomAccess<T>> randomAccess;
	long cached_size = -1;
	long [] firstPosition;
	long [] minima;
	long [] maxima;
	
	protected class BMROIIterableInterval<TT extends Type<TT>> implements IterableInterval<TT> {
		final RandomAccess<TT> src;
		
		/**
		 * @author leek
		 * 
		 * The cursor works by managing a cursor from the original image. It
		 * advances the underlying cursor to the next true position
		 * with each fwd() step. 
		 */
		protected class BMROICursor extends AbstractCursor<TT> {
			boolean nextIsValid;
			boolean cursorHasNext;
			Cursor<T> cursor;
			final long [] position;

			protected BMROICursor() {
				super(BMROIIterableInterval.this.numDimensions());
				cursor = img.localizingCursor();
				position = new long[BMROIIterableInterval.this.numDimensions()];
			}
			
			@Override
			public TT get() {
				src.setPosition(this);
				return src.get();
			}

			@Override
			public void fwd() {
				validateNext();
				cursor.localize(position);
				nextIsValid = false;
			}

			@Override
			public void reset() {
				cursor.reset();
				nextIsValid = false;
			}

			@Override
			public boolean hasNext() {
				validateNext();
				return cursorHasNext;
			}

			@Override
			public void localize(long[] position) {
				System.arraycopy(this.position, 0, position, 0, numDimensions());
			}

			@Override
			public long getLongPosition(int d) {
				return this.position[d];
			}

			@Override
			public AbstractCursor<TT> copy() {
				return copyCursor();
			}

			@Override
			public AbstractCursor<TT> copyCursor() {
				BMROICursor c = new BMROICursor();
				c.cursor = cursor.copyCursor();
				System.arraycopy(position, 0, c.position, 0, numDimensions());
				c.nextIsValid = nextIsValid;
				return c;
			}
			
			private void validateNext() {
				if (! nextIsValid) {
					while(cursor.hasNext()) {
						if (cursor.next().get()) {
							nextIsValid = true;
							cursorHasNext = true;
							return;
						}
					}
					nextIsValid = true;
					cursorHasNext = false;
				}
			}
		}
		protected BMROIIterableInterval(RandomAccess<TT> src) {
			this.src = src;
		}

		@Override
		public long size() {
			return getCachedSize();
		}

		@Override
		public TT firstElement() {
			src.setPosition(getFirstPosition());
			return src.get();
		}

		@Override
		public boolean equalIterationOrder(IterableRealInterval<?> f) {
			if (f instanceof BMROIIterableInterval) {
				BMROIIterableInterval<?> other = (BMROIIterableInterval<?>) f;
				return other.getImg() == img;
			}
			return false;
		}

		@Override
		public double realMin(int d) {
			return img.realMin(d);
		}

		@Override
		public void realMin(double[] min) {
			img.realMin(min);
		}

		@Override
		public double realMax(int d) {
			return img.realMax(d);
		}

		@Override
		public void realMax(double[] max) {
			img.realMax(max);
		}

		@Override
		public int numDimensions() {
			return BinaryMaskRegionOfInterest.this.numDimensions();
		}

		@Override
		public Iterator<TT> iterator() {
			return new BMROICursor();
		}

		@Override
		public long min(int d) {
			validate();
			return minima[d];
		}

		@Override
		public void min(long[] min) {
			validate();
			System.arraycopy(minima, 0, min, 0, numDimensions());
		}

		@Override
		public long max(int d) {
			validate();
			return maxima[d];
		}

		@Override
		public void max(long[] max) {
			validate();
			System.arraycopy(maxima, 0, max, 0, numDimensions());
		}

		@Override
		public void dimensions(long[] dimensions) {
			img.dimensions(dimensions);
		}

		@Override
		public long dimension(int d) {
			return img.dimension(d);
		}

		@Override
		public Cursor<TT> cursor() {
			return new BMROICursor();
		}

		@Override
		public Cursor<TT> localizingCursor() {
			return new BMROICursor();
		}
		
		protected I getImg() {
			return img;
		}
	}
	public BinaryMaskRegionOfInterest(final I img) {
		super(img.numDimensions());
		this.img = img;
		randomAccess = new ThreadLocal<RandomAccess<T>>() {

			/* (non-Javadoc)
			 * @see java.lang.ThreadLocal#initialValue()
			 */
			@Override
			protected RandomAccess<T> initialValue() {
				return img.randomAccess();
			}
		};
	}
	@Override
	public <TT extends Type<TT>> IterableInterval<TT> getIterableIntervalOverROI(
			RandomAccessible<TT> src) {
		return new BMROIIterableInterval<TT>(src.randomAccess());
	}
	/* (non-Javadoc)
	 * @see net.imglib2.roi.AbstractRegionOfInterest#isMember(double[])
	 */
	@Override
	protected boolean isMember(double[] position) {
		/*
		 * Quantize by nearest-neighbor (-0.5 < x < 0.5)
		 */
		for (int i=0; i<numDimensions(); i++) {
			randomAccess.get().setPosition(Math.round(position[i]), i);
		}
		return randomAccess.get().get().get();
	}
	@Override
	protected void getRealExtrema(double[] minima, double[] maxima) {
		validate();
		for (int i=0; i<numDimensions(); i++) {
			minima[i] = this.minima[i];
			maxima[i] = this.maxima[i];
		}
	}
	
	/**
	 * Scan the image, counting bits once, then return the cached value.
	 * @return
	 */
	protected long getCachedSize() {
		validate();
		return cached_size;
	}
	
	protected long [] getFirstPosition() {
		validate();
		return firstPosition;
	}

	protected void validate() {
		if (cached_size == -1) {
			cached_size = 0;
			minima = new long [numDimensions()];
			maxima = new long [numDimensions()];
			Cursor<T> c = img.localizingCursor();
			while(c.hasNext()) {
				if (c.next().get()) {
					cached_size = 1;
					firstPosition = new long[numDimensions()];
					c.localize(firstPosition);
					c.localize(minima);
					c.localize(maxima);
					break;
				}
			}
			while(c.hasNext()) {
				if (c.next().get()) {
					cached_size++;
					for (int i=0; i<numDimensions(); i++) {
						long pos = c.getLongPosition(i);
						minima[i] = Math.min(minima[i], pos);
						maxima[i] = Math.max(maxima[i], pos);
					}
				}
			}
		}
	}
}
