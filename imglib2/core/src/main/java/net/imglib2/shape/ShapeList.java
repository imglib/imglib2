/**
 * 
 */
package net.imglib2.shape;

import java.awt.Shape;
import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * @author Albert Cardona and Stephan Saalfeld
 *
 */
public class ShapeList<T> implements RealRandomAccessible<T>
{

	final protected ArrayList< Shape > shapeList = new ArrayList<Shape>();
	final protected ArrayList< T >  typeList = new ArrayList<T>();
	final protected T background;
	
	public ShapeList(final T background) {
		this.background = background;
	}
	
	public void add(final Shape s, final T t) {
		this.shapeList.add(s);
		this.typeList.add(t);
	}
	
	/** Always returns 2. */
	@Override
	public final int numDimensions() {
		return 2;
	}

	@Override
	public RealRandomAccess<T> realRandomAccess() {
		return new ShapeListRealRandomAccess();
	}
	
	protected class ShapeListRealRandomAccess implements RealRandomAccess<T>
	{
		
		/** Stateful. */
		protected final double[] position = new double[2];

		@Override
		public void localize(float[] pos) {
			pos[0] = (float) this.position[0];
			pos[1] = (float) this.position[1];
		}

		@Override
		public void localize(double[] pos) {
			pos[0] = this.position[0];
			pos[1] = this.position[1];
		}

		@Override
		public float getFloatPosition(int d) {
			return (float) this.position[d];
		}

		@Override
		public double getDoublePosition(int d) {
			return this.position[d];
		}

		/** Always returns 2. */
		@Override
		public int numDimensions() {
			return 2;
		}

		@Override
		public void move(float distance, int d) {
			this.position[d] += distance;
		}

		@Override
		public void move(double distance, int d) {
			this.position[d] += distance;
		}

		@Override
		public void move(RealLocalizable localizable) {
			position[0] += localizable.getDoublePosition(0);
			position[1] += localizable.getDoublePosition(1);
		}

		@Override
		public void move(float[] distance) {
			position[0] += distance[0];
			position[1] += distance[1];
		}

		@Override
		public void move(double[] distance) {
			position[0] += distance[0];
			position[1] += distance[1];
		}

		@Override
		public void setPosition(RealLocalizable localizable) {
			position[0] = localizable.getDoublePosition(0);
			position[1] = localizable.getDoublePosition(1);
		}

		@Override
		public void setPosition(float[] pos) {
			position[0] = pos[0];
			position[1] = pos[1];
		}

		@Override
		public void setPosition(double[] pos) {
			position[0] = pos[0];
			position[1] = pos[1];
		}

		@Override
		public void setPosition(float pos, int d) {
			position[d] = pos;
		}

		@Override
		public void setPosition(double pos, int d) {
			position[d] = pos;
		}

		@Override
		public void fwd(int d) {
			++position[d];
		}

		@Override
		public void bck(int d) {
			--position[d];
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
			position[0] += localizable.getDoublePosition(0);
			position[1] += localizable.getDoublePosition(1);
		}

		@Override
		public void move(int[] distance) {
			position[0] += distance[0];
			position[1] += distance[1];
		}

		@Override
		public void move(long[] distance) {
			position[0] += distance[0];
			position[1] += distance[1];
		}

		@Override
		public void setPosition(Localizable localizable) {
			position[0] = localizable.getDoublePosition(0);
			position[1] = localizable.getDoublePosition(1);
		}

		@Override
		public void setPosition(int[] pos) {
			position[0] = pos[0];
			position[1] = pos[1];
		}

		@Override
		public void setPosition(long[] pos) {
			position[0] = pos[0];
			position[1] = pos[1];
		}

		@Override
		public void setPosition(int pos, int d) {
			position[d] = pos;
		}

		@Override
		public void setPosition(long pos, int d) {
			position[d] = pos;
		}

		/** Unsynchronized with {#add}. */
		@Override
		public T get() {
			for (int i=0; i<shapeList.size(); ++i) {
				if (shapeList.get(i).contains(position[0], position[1])) {
					return typeList.get(i);
				}
			}
			return background;
		}

		@Override
		public ShapeListRealRandomAccess copy() {
			final ShapeListRealRandomAccess s = new ShapeListRealRandomAccess();
			s.position[0] = this.position[0];
			s.position[1] = this.position[1];
			return s;
		}

		@Override
		public RealRandomAccess<T> copyRealRandomAccess() {
			return copy();
		}
		
	}
}
