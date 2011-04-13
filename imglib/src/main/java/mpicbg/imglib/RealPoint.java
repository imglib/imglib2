package mpicbg.imglib;

/**
 * @author leek
 *
 *A point is a location in EuclideanSpace.
 */
public class RealPoint implements RealLocalizable, RealPositionable, EuclideanSpace {

	private double [] position;
	/**
	 * Create a point in <i>nDimensional</i> space initialized to 0,0,...
	 * @param nDimensions - # of dimensions of the space
	 */
	public RealPoint(int nDimensions) {
		position = new double[nDimensions];
	}
	
	/**
	 * Create a point at a definite location in a space of the
	 * dimensionality of the position.
	 * 
	 * @param position - position of the point
	 */
	public RealPoint(double [] position) {
		this.position = position.clone();
	}
	
	/**
	 * Create a point at a definite position
	 * @param position the initial position. The length of the array determines the dimensionality of the space.
	 */
	public RealPoint(float [] position) {
		this.position = new double[position.length];
		for (int i=0; i<position.length; i++) {
			this.position[i] = position[i];
		}
	}
	
	@Override
	public void fwd(int d) {
		position[d] += 1;
	}

	@Override
	public void bck(int d) {
		position[d] -= 1;
	}

	@Override
	public void move(int distance, int d) {
		position[d] += distance;
	}

	@Override
	public void move(long distance, int d) {
		position[d] += distance;
	}

	@Override
	public void move(Localizable localizable) {
		for (int i = 0; (i < numDimensions()) && (i < localizable.numDimensions()); i++) {
			position[i] += localizable.getDoublePosition(i);
		}
	}

	@Override
	public void move(int[] position) {
		for (int i = 0; (i < numDimensions()) && (i < position.length); i++) {
			this.position[i] += position[i];
		}

	}

	@Override
	public void move(long[] position) {
		for (int i = 0; (i < numDimensions()) && (i < position.length); i++) {
			this.position[i] += position[i];
		}

	}

	@Override
	public void setPosition(Localizable localizable) {
		localizable.localize(this.position);
	}

	@Override
	public void setPosition(int[] position) {
		for (int i = 0; (i < numDimensions()) && (i < position.length); i++) {
			this.position[i] = position[i];
		}
	}

	@Override
	public void setPosition(long[] position) {
		for (int i = 0; (i < numDimensions()) && (i < position.length); i++) {
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
	public int numDimensions() {
		return position.length;
	}

	@Override
	public void move(float distance, int d) {
		if (d < numDimensions())
			position[d] += distance;
	}

	@Override
	public void move(double distance, int d) {
		if (d < numDimensions())
			position[d] += distance;
	}

	@Override
	public void move(RealLocalizable localizable) {
		for (int i = 0; (i < numDimensions()) && (i < localizable.numDimensions()); i++) {
			position[i] += localizable.getDoublePosition(i);
		}
	}

	@Override
	public void move(float[] position) {
		for (int i=0; (i < numDimensions()) && (i < position.length); i++) {
			this.position[i] += position[i];
		}

	}

	@Override
	public void move(double[] position) {
		for (int i=0; (i < numDimensions()) && (i < position.length); i++) {
			this.position[i] += position[i];
		}
	}

	@Override
	public void setPosition(RealLocalizable localizable) {
		localizable.localize(position);
	}

	@Override
	public void setPosition(float[] position) {
		for (int i=0; (i < numDimensions()) && (i < position.length); i++) {
			this.position[i] = position[i];
		}
	}

	@Override
	public void setPosition(double[] position) {
		for (int i=0; (i < numDimensions()) && (i < position.length); i++) {
			this.position[i] = position[i];
		}
	}

	@Override
	public void setPosition(float position, int d) {
		if (d < numDimensions())
			this.position[d] = position;
	}

	@Override
	public void setPosition(double position, int d) {
		if (d < numDimensions())
			this.position[d] = position;
	}

	@Override
	public void localize(float[] position) {
		for (int i=0; (i < numDimensions()) && (i < position.length); i++) {
			position[i] = (float)(this.position[i]);
		}
	}

	@Override
	public void localize(double[] position) {
		for (int i=0; (i < numDimensions()) && (i < position.length); i++) {
			position[i] = this.position[i];
		}
	}

	@Override
	public float getFloatPosition(int d) {
		if (d >= numDimensions())
			return 0;
		return (float)(position[d]);
	}

	@Override
	public double getDoublePosition(int d) {
		if (d >= numDimensions())
			return 0;
		return position[d];
	}

}
