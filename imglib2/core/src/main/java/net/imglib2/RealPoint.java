package net.imglib2;

/**
 * @author leek
 *
 * A point is a location in EuclideanSpace.
 */
public class RealPoint implements RealLocalizable, RealPositionable
{
	final protected double [] position;
	
	/**
	 * Create a point in <i>nDimensional</i> space initialized to 0,0,...
	 * @param nDimensions - # of dimensions of the space
	 */
	public RealPoint(final int nDimensions) {
		position = new double[nDimensions];
	}
	
	/**
	 * Create a point at a definite location in a space of the
	 * dimensionality of the position.
	 * 
	 * @param position - position of the point
	 */
	public RealPoint(final double [] position) {
		this.position = position.clone();
	}
	
	/**
	 * Create a point at a definite position
	 * @param position the initial position. The length of the array determines the dimensionality of the space.
	 */
	public RealPoint(final float [] position) {
		this.position = new double[position.length];
		for (int i=0; i < position.length; ++i)
			this.position[i] = position[i];
	}
	
	/**
	 * Create a point using the position of a localizable
	 * @param localizable get position from here
	 */
	public RealPoint(final RealLocalizable localizable) {
		position = new double[localizable.numDimensions()];
		localizable.localize(position);
	}
	
	@Override
	public void fwd(final int d) {
		position[d] += 1;
	}

	@Override
	public void bck(final int d) {
		position[d] -= 1;
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
		for (int i=0; i < position.length; ++i)
			position[i] += localizable.getDoublePosition(i);
	}

	@Override
	public void move(final int[] pos) {
		for (int i=0; i < position.length; ++i) {
			position[i] += pos[i];
		}
	}

	@Override
	public void move(final long[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] += pos[i];
	}

	@Override
	public void setPosition(final Localizable localizable) {
		for (int i=0; i < position.length; ++i)
			position[i] = localizable.getDoublePosition(i);
	}

	@Override
	public void setPosition(final int[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] = pos[i];
	}

	@Override
	public void setPosition(final long[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] = pos[i];
	}

	@Override
	public void setPosition(final int pos, final int d) {
		position[d] = pos;
	}

	@Override
	public void setPosition(final long pos, final int d) {
		position[d] = pos;
	}

	@Override
	public int numDimensions() {
		return position.length;
	}

	@Override
	public void move(final float distance, final int d) {
		position[d] += distance;
	}

	@Override
	public void move(final double distance, final int d) {
		position[d] += distance;
	}

	@Override
	public void move(final RealLocalizable localizable) {
		for (int i=0; i < position.length; ++i)
			position[i] += localizable.getDoublePosition(i);
	}

	@Override
	public void move(final float[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] += pos[i];
	}

	@Override
	public void move(final double[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] += pos[i];
	}

	@Override
	public void setPosition(final RealLocalizable localizable) {
		for (int i=0; i < position.length; ++i)
			position[i] = localizable.getDoublePosition(i);
	}

	@Override
	public void setPosition(final float[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] = pos[i];
	}

	@Override
	public void setPosition(final double[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] = pos[i];
	}

	@Override
	public void setPosition(final float pos, final int d) {
		position[d] = pos;
	}

	@Override
	public void setPosition(final double pos, final int d) {
		position[d] = pos;
	}

	@Override
	public void localize(final float[] pos) {
		for (int i=0; i < position.length; ++i)
			pos[i] = (float)(position[i]);
	}

	@Override
	public void localize(final double[] pos) {
		for (int i=0; i < position.length; ++i)
			pos[i] = position[i];
	}

	@Override
	public float getFloatPosition(final int d) {
		return (float)(position[d]);
	}

	@Override
	public double getDoublePosition(final int d) {
		return position[d];
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		char c = '(';
		for (int i=0; i<numDimensions(); i++) {
			sb.append(c);
			sb.append(position[i]);
			c = ',';
		}
		sb.append(")");
		return sb.toString();
	}
}
