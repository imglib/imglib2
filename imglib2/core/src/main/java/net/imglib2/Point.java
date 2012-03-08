package net.imglib2;

/**
 * A Point is a position in Euclidean space specified in integer coordinates.
 *
 * @author Lee Kamentsky
 */
public class Point implements Localizable, Positionable {

	final protected long[] position;
	
	/**
	 * Protected constructor that re-uses the passed position array.
	 * 
	 * @param position
	 * @param x unused parameter that changes the method signature
	 */
	protected Point( final long[] position, final Object x )
	{
		this.position = position;
	}
	
	/**
	 * Create a point at 0 in <i>nDimensional</i> space.
	 * @param nDimensions # of dimensions in the space
	 */
	public Point( final int nDimensions ) {
		position = new long [nDimensions];
	}
	
	/**
	 * Create a point at a definite position
	 * @param position the initial position. The length of the array determines the dimensionality of the space.
	 */
	public Point( final long... position ) {
		this.position = position.clone();
	}

	/**
	 * Create a point at a definite position
	 * @param position the initial position. The length of the array determines the dimensionality of the space.
	 */
	public Point( final int... position ) {
		this.position = new long[position.length];
		for (int i=0; i < position.length; ++i) {
			this.position[i] = position[i];
		}
	}
	
	/**
	 * Create a point using the position of a {@link Localizable}
	 * @param localizable get position from here
	 */
	public Point( final Localizable localizable ) {
		position = new long [localizable.numDimensions()];
		localizable.localize(position);
	}
	
	@Override
	public void localize(final float[] pos) {
		for (int i=0; i < position.length; ++i)
			pos[i] = position[i]; 
	}

	@Override
	public void localize(final double[] pos) {
		for (int i=0; i < position.length; ++i)
			pos[i] = position[i];
	}

	@Override
	public float getFloatPosition(final int d) {
		return position[d];
	}

	@Override
	public double getDoublePosition(final int d) {
		return position[d];
	}

	@Override
	public int numDimensions() {
		return position.length;
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
			position[i] += localizable.getLongPosition(i);
	}

	@Override
	public void move(final int[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] += pos[i];
	}

	@Override
	public void move(final long[] pos) {
		for (int i=0; i < position.length; ++i)
			position[i] += pos[i];
	}

	@Override
	public void setPosition(final Localizable localizable) {
		for (int i=0; i < position.length; ++i)
			position[i] = localizable.getLongPosition(i);
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
	public void localize(final int[] pos) {
		for (int i=0; i < position.length; ++i)
			pos[i] = (int)(this.position[i]);
	}

	@Override
	public void localize(final long[] pos) {
		for (int i=0; i < position.length; ++i)
			pos[i] = position[i];
	}

	@Override
	public int getIntPosition(final int d) {
		return (int)(position[d]);
	}

	@Override
	public long getLongPosition(final int d) {
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
	
	static public Point wrap( final long[] position )
	{
		return new Point( position, null );
	}
}
