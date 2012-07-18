package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.img.ImgPlus;
import net.imglib2.outofbounds.OutOfBoundsFactory;

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
public abstract class RealPositionableAbstractNeighborhood<T> extends AbstractNeighborhood<T> implements RealPositionable {

	protected final double[] calibration;
	protected final double[] position;

	public RealPositionableAbstractNeighborhood(ImgPlus<T> source,	OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		super(source, outOfBounds);
		this.calibration = new double[source.numDimensions()];
		for (int d = 0; d < source.numDimensions(); d++) {
			calibration[ d ] = source.calibration(d); 
		}
		this.position = new double[source.numDimensions()];
	}



	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(float distance, int d) {
		position[d] += distance;
		center[d] = Math.round(position[d] * calibration[d]);
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(double distance, int d) {
		position[d] += distance;
		center[d] = Math.round(position[d] * calibration[d]);
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(RealLocalizable localizable) {
		for (int d = 0; d < localizable.numDimensions(); d++) {
			position[d] += localizable.getDoublePosition(d);
			center[d] = Math.round(position[d] * calibration[d]);
		}
		
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(float[] distance) {
		for (int d = 0; d < distance.length; d++) {
			position[d] += distance[d];
			center[d] = Math.round(position[d] * calibration[d]);
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void move(double[] distance) {
		for (int d = 0; d < distance.length; d++) {
			position[d] += distance[d];
			center[d] = Math.round(position[d] * calibration[d]);
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(RealLocalizable localizable) {
		for (int d = 0; d < localizable.numDimensions(); d++) {
			position[d] = localizable.getDoublePosition(d);
			center[d] = Math.round(position[d] * calibration[d]);
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(float[] position) {
		for (int d = 0; d < position.length; d++) {
			position[d] = position[d];
			center[d] = Math.round(position[d] * calibration[d]);
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(double[] position) {
		for (int d = 0; d < position.length; d++) {
			position[d] = position[d];
			center[d] = Math.round(position[d] * calibration[d]);
		}
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(float position, int d) {
		this.position[d] = position;
		center[d] = Math.round(position * calibration[d]);
	}

	/**
	 * Update the center of this local neighborhood using <b>physical units</b> and
	 * the {@link ImgPlus#calibration(double[])} values.
	 */
	@Override
	public void setPosition(double position, int d) {
		this.position[d] = position;
		center[d] = Math.round(position * calibration[d]);
	}}
