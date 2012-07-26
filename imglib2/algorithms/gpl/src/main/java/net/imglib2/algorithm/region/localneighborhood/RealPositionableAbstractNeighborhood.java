package net.imglib2.algorithm.region.localneighborhood;

import static java.lang.Math.round;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.img.ImgPlus;
import net.imglib2.meta.Metadata;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.view.Views;

/**
 * Abstract class for a local neighborhood which can be positioned using
 * calibrated distance. The typical use-case is with a source {@link ImgPlus}
 * image that has a physical calibration. This class allows specifying the
 * position of the center of the neighborhood in physical, calibrated units,
 * using the calibration field of the source.
 * <p>
 * The calibrated center coordinates are given by the
 * {@link AbstractNeighborhood#center} field. The size of the neighborhood is
 * set <b>in pixel units</b> by the {@link #span} long[] array. Which leads us
 * to:
 * <p>
 * <u>Dissociative identity disorder</u>: This class is a
 * {@link RealPositionable} and thus a {@link Positionable}. But the two groups
 * of methods have different meanings:
 * <ul>
 * <li>All the {@link #move(long[])} and co. methods that operate on
 * <code>int</code> or <code>long</code> arrays or on {@link Localizable}
 * operate on the <b>pixel</b> position of the neighborhood. So you can still
 * move pixel by pixel.
 * <li>All the {@link #move(double[])} and co. methods that operate on
 * <code>double</code> or <code>float</code> arrays or on
 * {@link RealLocalizable} operates on the <b>calibrated</b> position of the
 * neighborhood, owing to the calibration.
 * </ul>
 * <p>
 * An example: Suppose you have an {@link ImgPlus} spanning 100 x 100 pixels,
 * with a pixel size of 0.2 µm. If you use {@link #setPosition(50L, 0)} you will
 * move the center of this neighborhood to the center of the image. But using
 * {@link #setPosition(50f, 0)} will move you to the position x = 50 µm, which
 * is out of bounds. So be careful.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 * 
 */
public abstract class RealPositionableAbstractNeighborhood<T, V extends RandomAccessibleInterval<T> & Metadata>
		implements IterableInterval<T>, RealPositionable {

	protected final double[] calibration;
	/** Neighborhood center coordinates, expressed in <b>calibrated</b> units. */
	protected final double[] center;
	protected V source;
	protected ExtendedRandomAccessibleInterval<T, V> extendedSource;
	/**
	 * The span of this neighborhood, in <b>pixel units</b>, such that the size
	 * of the bounding box in dimension <code>d</code> will be
	 * <code>2 x span[d] + 1</code>.
	 */
	protected long[] span;

	public RealPositionableAbstractNeighborhood(V source,
			OutOfBoundsFactory<T, V> outOfBounds) {
		this.source = source;
		this.extendedSource = Views.extend(source, outOfBounds);
		this.span = new long[source.numDimensions()];
		this.calibration = new double[source.numDimensions()];
		for (int d = 0; d < source.numDimensions(); d++) {
			calibration[d] = source.calibration(d);
		}
		this.center = new double[source.numDimensions()];
	}

	@Override
	public abstract RealPositionableNeighborhoodCursor<T> cursor();

	@Override
	public abstract RealPositionableNeighborhoodCursor<T> localizingCursor();

	/**
	 * Set the span of this neighborhood.
	 * <p>
	 * The neighborhood span is such that the size of the neighborhood in
	 * dimension <code>d</code> will be <code>2 x span[d] + 1</code>.
	 * 
	 * @param span
	 *            this array content will be copied to the neighborhood internal
	 *            field.
	 */
	public void setSpan(long[] span) {
		for (int d = 0; d < span.length; d++) {
			this.span[d] = span[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void move(float distance, int d) {
		center[d] += distance;
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void move(double distance, int d) {
		center[d] += distance;
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void move(RealLocalizable localizable) {
		for (int d = 0; d < calibration.length; d++) {
			center[d] += localizable.getDoublePosition(d);
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void move(float[] distance) {
		for (int d = 0; d < distance.length; d++) {
			center[d] += distance[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void move(double[] distance) {
		for (int d = 0; d < distance.length; d++) {
			center[d] += distance[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void setPosition(RealLocalizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] = localizable.getDoublePosition(d);
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b.
	 */
	@Override
	public void setPosition(float[] position) {
		for (int d = 0; d < position.length; d++) {
			center[d] = position[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void setPosition(double[] position) {
		for (int d = 0; d < position.length; d++) {
			center[d] = position[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void setPosition(float position, int d) {
		center[d] = position;
	}

	/**
	 * Update the center of this local neighborhood using <b>calibrated
	 * units</b>.
	 */
	@Override
	public void setPosition(double position, int d) {
		center[d] = position;
	}

	@Override
	public int numDimensions() {
		return source.numDimensions();
	}

	/**
	 * Move the center of this local neighborhood by <b>1 pixel</b> in dimension
	 * d.
	 */
	@Override
	public void fwd(int d) {
		center[d] += calibration[d];
	}

	/**
	 * Move the center of this local neighborhood by <b>-1 pixel</b> in
	 * dimension d.
	 */
	@Override
	public void bck(int d) {
		center[d] -= calibration[d];
	}

	/**
	 * Move the center of this local neighborhood using a distance in <b>pixel
	 * units</b> in dimension d.
	 */
	@Override
	public void move(int distance, int d) {
		center[d] += distance * calibration[d];
	}

	/**
	 * Move the center of this local neighborhood using a distance in <b>pixel
	 * units</b> in dimension d.
	 */
	@Override
	public void move(long distance, int d) {
		center[d] += distance * calibration[d];
	}

	/**
	 * Move the center of this local neighborhood using a distance in <b>pixel
	 * units</b>.
	 */
	@Override
	public void move(Localizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] += localizable.getDoublePosition(d) * calibration[d];
		}
	}

	/**
	 * Move the center of this local neighborhood using a distance in <b>pixel
	 * units</b>.
	 */
	@Override
	public void move(int[] distance) {
		for (int d = 0; d < center.length; d++) {
			center[d] += distance[d] * calibration[d];
		}
	}

	/**
	 * Move the center of this local neighborhood using a distance in <b>pixel
	 * units</b>.
	 */
	@Override
	public void move(long[] distance) {
		for (int d = 0; d < center.length; d++) {
			center[d] += distance[d] * calibration[d];
		}
	}

	/**
	 * Set the center of this local neighborhood using a position in <b>pixel
	 * units</b>.
	 */
	@Override
	public void setPosition(Localizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] = localizable.getDoublePosition(d) * calibration[d];
		}
	}

	/**
	 * Set the center of this local neighborhood using a position in <b>pixel
	 * units</b>.
	 */
	@Override
	public void setPosition(int[] position) {
		for (int d = 0; d < center.length; d++) {
			center[d] = position[d] * calibration[d];
		}
	}

	/**
	 * Set the center of this local neighborhood using a position in <b>pixel
	 * units</b>.
	 */
	@Override
	public void setPosition(long[] position) {
		for (int d = 0; d < center.length; d++) {
			center[d] = position[d] * calibration[d];
		}
	}

	/**
	 * Set the center of this local neighborhood using a position in <b>pixel
	 * units</b>.
	 */
	@Override
	public void setPosition(int position, int d) {
		center[d] = position * calibration[d];
	}

	/**
	 * Set the center of this local neighborhood using a position in <b>pixel
	 * units</b>.
	 */
	@Override
	public void setPosition(long position, int d) {
		center[d] = position * calibration[d];
	}

	@Override
	public T firstElement() {
		RandomAccess<T> ra = source.randomAccess();
		for (int d = 0; d < span.length; d++) {
			ra.setPosition(round(center[d] / calibration[d]) - span[d], d);
		}
		return ra.get();
	}

	@Override
	public Object iterationOrder() {
		return this;
	}

	@Override
	@Deprecated
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		if (!(f instanceof RealPositionableAbstractNeighborhood)) {
			return false;
		}
		RectangleNeighborhood<?, ?> other = (RectangleNeighborhood<?, ?>) f;
		if (other.numDimensions() != numDimensions()) {
			return false;
		}
		for (int d = 0; d < span.length; d++) {
			if (other.span[d] != span[d]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the min extend of this neighborhood in <b>calibrated units</b>.
	 */
	@Override
	public double realMin(int d) {
		return center[d] - span[d] * calibration[d];
	}

	/**
	 * Return the min extend of this neighborhood in <b>calibrated units</b>.
	 */
	@Override
	public void realMin(double[] min) {
		for (int d = 0; d < center.length; d++) {
			min[d] = center[d] - span[d] * calibration[d];
		}

	}

	/**
	 * Return the min extend of this neighborhood in <b>calibrated units</b>.
	 */
	@Override
	public void realMin(RealPositionable min) {
		for (int d = 0; d < center.length; d++) {
			min.setPosition(center[d] - span[d] * calibration[d], d);
		}
	}

	/**
	 * Return the max extend of this neighborhood in <b>calibrated units</b>.
	 */
	@Override
	public double realMax(int d) {
		return center[d] + span[d] * calibration[d];
	}

	/**
	 * Return the max extend of this neighborhood in <b>calibrated units</b>.
	 */
	@Override
	public void realMax(double[] max) {
		for (int d = 0; d < center.length; d++) {
			max[d] = center[d] + span[d] * calibration[d];
		}
	}

	/**
	 * Return the max extend of this neighborhood in <b>calibrated units</b>.
	 */
	@Override
	public void realMax(RealPositionable max) {
		for (int d = 0; d < center.length; d++) {
			max.setPosition(center[d] + span[d] * calibration[d], d);
		}
	}

	/**
	 * Return the min extend of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public long min(int d) {
		return round(center[d] / calibration[d]) - span[d];
	}

	/**
	 * Return the min extend of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public void min(long[] min) {
		for (int d = 0; d < center.length; d++) {
			min[d] = round(center[d] / calibration[d]) - span[d];
		}
	}

	/**
	 * Return the min extend of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public void min(Positionable min) {
		for (int d = 0; d < center.length; d++) {
			min.setPosition(round(center[d] / calibration[d]) - span[d], d);
		}
	}

	/**
	 * Return the max extend of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public long max(int d) {
		return round(center[d] / calibration[d]) + span[d];
	}

	/**
	 * Return the max extend of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public void max(long[] max) {
		for (int d = 0; d < center.length; d++) {
			max[d] = round(center[d] / calibration[d]) + span[d];
		}
	}

	/**
	 * Return the max extend of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public void max(Positionable max) {
		for (int d = 0; d < center.length; d++) {
			max.setPosition(round(center[d] / calibration[d]) + span[d], d);
		}
	}

	/**
	 * Return the dimension of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public void dimensions(long[] dimensions) {
		for (int d = 0; d < span.length; d++) {
			dimensions[d] = 2 * span[d] + 1;
		}
	}

	/**
	 * Return the dimension of this neighborhood in <b>pixel units</b>.
	 */
	@Override
	public long dimension(int d) {
		return (2 * span[d] + 1);
	}
}
