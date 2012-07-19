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
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.view.Views;

/**
 * Abstract class for a local neighborhood which can be positioned using calibrated distance.
 * The typical use-case is with a source {@link ImgPlus} image that has a physical calibration.
 * This class allows specifying the position of the center of the neighborhood in physical,
 * calibrated units, using the calibration field of the source.
 * <p>
 * The physical center coordinates, that are given by the {@link AbstractNeighborhood#center}
 * multiplied by the spatial {@link #calibration} array. Though the resulting {@link Iterable}
 * does not have sub-pixel interpolation capabilities, we keep a position as double array as a field so
 * that two succeeding {@link #move(double, int)} commands with a distance of 0.4 actually
 * result in a net displacement of 1 pixel, and not 0.
 * <p>
 * Otherwise, the center pixel location is simply obtained by rounding the double position
 * times the calibration.  
 */
public abstract class RealPositionableAbstractNeighborhood<T> extends AbstractNeighborhood<T> implements Positionable,  IterableInterval<T>, RealPositionable {

	protected final double[] calibration;
	protected final double[] center;
	protected ImgPlus<T> source;
	protected ExtendedRandomAccessibleInterval<T, ImgPlus<T>> extendedSource;
	/** The span of this neighborhood, such that the size of the bounding box in dimension 
	 * <code>d</code> will be <code>2 x span[d] + 1</code>. */
	protected long[] span;

	public RealPositionableAbstractNeighborhood(ImgPlus<T> source,	OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		super(source, outOfBounds);
		this.source = source;
		this.extendedSource = Views.extend(source, outOfBounds);
		this.span = new long[source.numDimensions()];
		this.calibration = new double[source.numDimensions()];
		for (int d = 0; d < source.numDimensions(); d++) {
			calibration[ d ] = source.calibration(d); 
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
	 * The neighborhood span is such that the size of the neighborhood in dimension 
	 * <code>d</code> will be <code>2 x span[d] + 1</code>.
	 * @param span  this array content will be copied to the neighborhood internal field.
	 */
	public void setSpan(long[] span) {
		for (int d = 0; d < span.length; d++) {
			this.span[ d ] = span[ d ];
		}
	}
	
	
	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(float distance, int d) {
		center[d] += distance * calibration[d] ;
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(double distance, int d) {
		center[d] += distance * calibration[d] ;
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(RealLocalizable localizable) {
		for (int d = 0; d < calibration.length; d++) {
			center[d] += localizable.getDoublePosition(d) * calibration[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(float[] distance) {
		for (int d = 0; d < distance.length; d++) {
			center[d] += distance[d] * calibration[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(double[] distance) {
		for (int d = 0; d < distance.length; d++) {
			center[d] += distance[d] * calibration[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(RealLocalizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] = localizable.getDoublePosition(d) * calibration[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(float[] position) {
		for (int d = 0; d < position.length; d++) {
			center[d] = position[d] * calibration[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(double[] position) {
		for (int d = 0; d < position.length; d++) {
			center[d] = position[d] * calibration[d];
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(float position, int d) {
		center[d] =position * calibration[d];
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(double position, int d) {
		center[d] = position * calibration[d];
	}


	@Override
	public int numDimensions() {
		return source.numDimensions();
	}


	@Override
	public void fwd(int d) {
		center[d] += 1d;
	}


	@Override
	public void bck(int d) {
		center[d] -= 1d;
	}


	@Override
	public void move(int distance, int d) {
		center[d] += distance;
	}


	@Override
	public void move(long distance, int d) {
		center[d] += distance;
	}


	@Override
	public void move(Localizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] += localizable.getDoublePosition(d);
		}
	}


	@Override
	public void move(int[] distance) {
		for (int d = 0; d < center.length; d++) {
			center[d] += distance[d];
		}
	}


	@Override
	public void move(long[] distance) {
		for (int d = 0; d < center.length; d++) {
			center[d] += distance[d];
		}
	}


	@Override
	public void setPosition(Localizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] = localizable.getDoublePosition(d);
		}
	}


	@Override
	public void setPosition(int[] position) {
		for (int d = 0; d < center.length; d++) {
			center[d] = position[d];
		}
	}


	@Override
	public void setPosition(long[] position) {
		for (int d = 0; d < center.length; d++) {
			center[d] = position[d];
		}
	}


	@Override
	public void setPosition(int position, int d) {
		center[d] = position;
	}


	@Override
	public void setPosition(long position, int d) {
		center[d] = position;
	}

	@Override
	public T firstElement() {
		RandomAccess<T> ra = source.randomAccess();
		for (int d = 0; d < span.length; d++) {
			ra.setPosition( round(center[d] - span[ d ]), d);
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
		RectangleNeighborhood<?> other = (RectangleNeighborhood<?>) f;
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

	@Override
	public double realMin(int d) {
		return center[ d ] - span[ d ];
	}

	@Override
	public void realMin(double[] min) {
		for (int d = 0; d < center.length; d++) {
			min[ d ] = center[ d ] - span [ d ];
		}
		
	}

	@Override
	public void realMin(RealPositionable min) {
		for (int d = 0; d < center.length; d++) {
			min.setPosition(center[ d ] - span [ d ], d);
		}
	}

	@Override
	public double realMax(int d) {
		return center[ d ] + span[ d ];
	}

	@Override
	public void realMax(double[] max) {
		for (int d = 0; d < center.length; d++) {
			max[ d ] = center[ d ] + span [ d ];
		}
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int d = 0; d < center.length; d++) {
			max.setPosition(center[ d ] + span [ d ], d);
		}
	}

	@Override
	public long min(int d) {
		return round(center[d] - span[d]);
	}

	@Override
	public void min(long[] min) {
		for (int d = 0; d < center.length; d++) {
			min[ d ] = round(center[ d ] - span [ d ]);
		}
	}

	@Override
	public void min(Positionable min) {
		for (int d = 0; d < center.length; d++) {
			min.setPosition( round(center[ d ] - span [ d ]), d);
		}
	}

	@Override
	public long max(int d) {
		return round(center[ d ] + span[ d ]);
	}

	@Override
	public void max(long[] max) {
		for (int d = 0; d < center.length; d++) {
			max[ d ] = round(center[ d ] + span [ d ]);
		}
	}

	@Override
	public void max(Positionable max) {
		for (int d = 0; d < center.length; d++) {
			max.setPosition( round(center[ d ] + span [ d ]), d);
		}
	}

	@Override
	public void dimensions(long[] dimensions) {
		for (int d = 0; d < span.length; d++) {
			dimensions[d] = 2 * span[d] + 1; 
		}
	}

	@Override
	public long dimension(int d) {
		return (2 * span[d] + 1);
	}
}
