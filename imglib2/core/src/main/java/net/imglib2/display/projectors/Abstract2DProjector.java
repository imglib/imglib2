package net.imglib2.display.projectors;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.display.Projector;
import net.imglib2.display.projectors.specializedprojectors.ArrayImgXYByteProjector;

/**
 * Base class for 2D projectors. Projecting means in this case projecting from a source
 * format to a target format (A->B).
 * 2D hints that the result is something 2 dimensional.
 * The base class provides methods to select a reference point in a multi-dimensional data object. Sub
 * classes like  {@link Projector2D}, {@link DimProjector2D} or {@link ArrayImgXYByteProjector} specify a mapping that uses the
 * reference point to project data into a 2 dimensional representation.
 * <br>
 * A basic example is the extraction of a data plain (containing the reference point) by sampling two axes
 * 
 * 
 * 
 * @author zinsmaie
 *
 * @param <A>
 * @param <B>
 */
public abstract class Abstract2DProjector<A, B> implements Projector<A, B>,
		Positionable, Localizable {

	protected final long[] position;
	protected final long[] min;
	protected final long[] max;
	protected final int numDimensions;
	
	/**
	 * initializes a reference point with the specified number of dimensions.
	 * Start position is 0,0,...,0
	 * @param numDims
	 */
	public Abstract2DProjector(int numDims) {

		// as this is an 2D projector, we need at least two dimensions,
		// even if the source is one-dimensional
		position = new long[Math.max(2, numDims)];
		min = new long[position.length];
		max = new long[position.length];
		this.numDimensions = numDims;
	}

	@Override
	public void bck(final int d) {
		position[d] -= 1;
	}

	@Override
	public void fwd(final int d) {
		position[d] += 1;
	}

	@Override
	public void move(final int distance, final int d) {
		position[d] += distance;
	}

	@Override
	public void move(final long distance, final int d) {
		position[d] += distance;
	}

	@Override
	public void move(final Localizable localizable) {
		for (int d = 0; d < position.length; ++d)
			position[d] += localizable.getLongPosition(d);
	}

	@Override
	public void move(final int[] p) {
		for (int d = 0; d < position.length; ++d)
			position[d] += p[d];
	}

	@Override
	public void move(final long[] p) {
		for (int d = 0; d < position.length; ++d)
			position[d] += p[d];
	}

	@Override
	public void setPosition(final Localizable localizable) {
		for (int d = 0; d < position.length; ++d)
			position[d] = localizable.getLongPosition(d);
	}

	@Override
	public void setPosition(final int[] p) {
		for (int d = 0; d < position.length; ++d)
			position[d] = p[d];
	}

	@Override
	public void setPosition(final long[] p) {
		for (int d = 0; d < position.length; ++d)
			position[d] = p[d];
	}

	@Override
	public void setPosition(final int p, final int d) {
		position[d] = p;
	}

	@Override
	public void setPosition(final long p, final int d) {
		position[d] = p;
	}

	@Override
	public int numDimensions() {
		return position.length;
	}

	@Override
	public int getIntPosition(final int d) {
		return (int) position[d];
	}

	@Override
	public long getLongPosition(final int d) {
		return position[d];
	}

	@Override
	public void localize(final int[] p) {
		for (int d = 0; d < p.length; ++d)
			p[d] = (int) position[d];
	}

	@Override
	public void localize(final long[] p) {
		for (int d = 0; d < p.length; ++d)
			p[d] = position[d];
	}

	@Override
	public double getDoublePosition(final int d) {
		return position[d];
	}

	@Override
	public float getFloatPosition(final int d) {
		return position[d];
	}

	@Override
	public void localize(final float[] p) {
		for (int d = 0; d < p.length; ++d)
			p[d] = position[d];
	}

	@Override
	public void localize(final double[] p) {
		for (int d = 0; d < p.length; ++d)
			p[d] = position[d];
	}

}
