package net.imglib2.script.slice;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealCursor;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.script.math.fn.FloatImageOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

public class OrthoSlice<R extends RealType<R> & NativeType<R>> extends FloatImageOperation
{
	protected final Img<R> imgSlice;
	private final RealCursor<R> c;
	private final int fixedDimension;
	private final long startingPosition;
	
	private final class ImgSlice extends AbstractImg<R> {

		private final IterableRandomAccessibleInterval<R> irai;
		private final RandomAccessible<R> ra;

		public ImgSlice(final IterableRandomAccessibleInterval<R> irai, final RandomAccessible<R> ra) {
			super(Util.intervalDimensions(irai));
			this.irai = irai;
			this.ra = ra;
		}

		@Override
		public ImgFactory<R> factory() {
			return null;
		}

		@Override
		public Img<R> copy() {
			return null;
		}

		@Override
		public RandomAccess<R> randomAccess() {
			return ra.randomAccess();
		}

		@Override
		public Cursor<R> cursor() {
			return irai.cursor();
		}

		@Override
		public Cursor<R> localizingCursor() {
			return irai.localizingCursor();
		}

		@Override
		public boolean equalIterationOrder(IterableRealInterval<?> f) {
			return irai.equalIterationOrder(f);
		}
	}

	/**
	 * Perform a hyperslice, with img.numDimensions()-1 dimensions;
	 * this means for example a 2D slice for a 3D volume.
	 * 
	 * @param img
	 * @param fixedDimension
	 * @param pos
	 * @throws Exception
	 */
	public OrthoSlice(final Img<R> img, final int fixedDimension, final long startingPosition) throws Exception {
		this.fixedDimension = fixedDimension;
		this.startingPosition = startingPosition;
		final RandomAccessibleInterval<R> ra = Views.hyperSlice(img, fixedDimension, startingPosition);
		this.imgSlice = new ImgSlice(new IterableRandomAccessibleInterval<R>(ra), ra);
		this.c = this.imgSlice.cursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(this.c);
	}

	@Override
	public final void findImgs(final Collection<IterableRealInterval<?>> iris) {
		iris.add(this.imgSlice);
	}

	@Override
	public IFunction duplicate() throws Exception {
		return new OrthoSlice<R>(this.imgSlice, this.fixedDimension, this.startingPosition);
	}
}
