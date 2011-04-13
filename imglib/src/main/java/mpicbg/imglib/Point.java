package mpicbg.imglib;

/**
 * @author leek
 *
 *A Point is a position in Euclidean space specified in integer coordinates.
 */
public class Point implements Localizable, Positionable, EuclideanSpace {

	private long [] position;
	/**
	 * Create a point at 0 in <i>nDimensional</i> space.
	 * @param nDimensions # of dimensions in the space
	 */
	public Point(int nDimensions) {
		position = new long [nDimensions];
	}
	
	/**
	 * Create a point at a definite position
	 * @param position the initial position. The length of the array determines the dimensionality of the space.
	 */
	public Point(long [] position) {
		this.position = position.clone();
	}

	/**
	 * Create a point at a definite position
	 * @param position the initial position. The length of the array determines the dimensionality of the space.
	 */
	public Point(int [] position) {
		this.position = new long[position.length];
		for (int i=0; i<position.length; i++) {
			this.position[i] = position[i];
		}
	}
	@Override
	public void localize(float[] position) {
		for (int i=0; (i<numDimensions()) && (i<position.length); i++)
			position[i] = this.position[i]; 
	}

	@Override
	public void localize(double[] position) {
		for (int i=0; (i<numDimensions()) && (i<position.length); i++)
			position[i] = this.position[i]; 
	}

	@Override
	public float getFloatPosition(int d) {
		if (d >= numDimensions()) return 0; 
		return position[d];
	}

	@Override
	public double getDoublePosition(int d) {
		if (d >= numDimensions()) return 0; 
		return position[d];
	}

	@Override
	public int numDimensions() {
		return position.length;
	}

	@Override
	public void fwd(int d) {
		if (d < position.length)
			position[d] += 1;
	}

	@Override
	public void bck(int d) {
		if (d < position.length)
			position[d] -= 1;

	}

	@Override
	public void move(int distance, int d) {
		if (d < position.length)
			position[d] += distance;
	}

	@Override
	public void move(long distance, int d) {
		if (d < position.length)
			position[d] += distance;
	}

	@Override
	public void move(Localizable localizable) {
		for (int i=0; (i < localizable.numDimensions()) && (i < numDimensions()); i++) {
			position[i] += localizable.getLongPosition(i);
		}
	}

	@Override
	public void move(int[] position) {
		for (int i=0; (i < position.length) && (i < numDimensions()); i++) {
			this.position[i] += position[i];
		}
	}

	@Override
	public void move(long[] position) {
		for (int i=0; (i < position.length) && (i < numDimensions()); i++) {
			this.position[i] += position[i];
		}
	}

	@Override
	public void setPosition(Localizable localizable) {
		localizable.localize(position);
	}

	@Override
	public void setPosition(int[] position) {
		for (int i=0; (i < position.length) && (i < numDimensions()); i++) {
			this.position[i] = position[i];
		}
	}

	@Override
	public void setPosition(long[] position) {
		for (int i=0; (i < position.length) && (i < numDimensions()); i++) {
			this.position[i] = position[i];
		}
	}

	@Override
	public void setPosition(int position, int d) {
		if (d < numDimensions())
			this.position[d] = position;
	}

	@Override
	public void setPosition(long position, int d) {
		if (d < numDimensions())
			this.position[d] = position;
	}

	@Override
	public void localize(int[] position) {
		for (int i=0; (i < position.length) && (i < numDimensions()); i++) {
			position[i] = (int)(this.position[i]);
		}
	}

	@Override
	public void localize(long[] position) {
		for (int i=0; (i < position.length) && (i < numDimensions()); i++) {
			position[i] = this.position[i];
		}
	}

	@Override
	public int getIntPosition(int d) {
		if (d >= numDimensions()) return 0;
		return (int)(position[d]);
	}

	@Override
	public long getLongPosition(int d) {
		if (d >= numDimensions()) return 0;
		return position[d];
	}

}
