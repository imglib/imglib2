package script.imglib.algorithm.fn;

import java.util.Iterator;

import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.numeric.NumericType;

public class ImgProxy<T extends NumericType<T>> implements Img<T> {

	private final Img<T> img;
	
	public ImgProxy(final Img<T> img) {
		this.img = img;
	}
	
	/** Return the {@link Img} wrapped by this proxy. */
	public Img<T> image() {
		return img;
	}
	
	@Override
	public int numDimensions() {
		return img.numDimensions();
	}

	@Override
	public long size() {
		return img.size();
	}

	@Override
	public T firstElement() {
		return img.firstElement();
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return img.equalIterationOrder(f);
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
	public Iterator<T> iterator() {
		return img.iterator();
	}

	@Override
	public long min(int d) {
		return img.min(d);
	}

	@Override
	public void min(long[] min) {
		img.min(min);
	}

	@Override
	public long max(int d) {
		return img.max(d);
	}

	@Override
	public void max(long[] max) {
		img.max(max);
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
	public ImgRandomAccess<T> randomAccess() {
		return img.randomAccess();
	}

	@Override
	public ImgRandomAccess<T> randomAccess(OutOfBoundsFactory<T, Img<T>> factory) {
		return img.randomAccess(factory);
	}

	@Override
	public ImgCursor<T> cursor() {
		return img.cursor();
	}

	@Override
	public ImgCursor<T> localizingCursor() {
		return img.localizingCursor();
	}

	@Override
	public ImgFactory<T> factory() {
		return img.factory();
	}
}