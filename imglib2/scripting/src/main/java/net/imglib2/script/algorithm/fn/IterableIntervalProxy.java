package net.imglib2.script.algorithm.fn;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;

public class IterableIntervalProxy<T> implements IterableInterval<T>
{
	protected final IterableInterval<T> iti;
	
	public IterableIntervalProxy(final IterableInterval<T> iti) {
		this.iti = iti;
	}
	
	/** The {@link IterableInterval} underlying this proxy. */
	public IterableInterval<T> get() {
		return iti;
	}
	
	@Override
	public long size() {
		return iti.size();
	}

	@Override
	public T firstElement() {
		return iti.firstElement();
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return iti.equalIterationOrder(f);
	}

	@Override
	public double realMin(int d) {
		return iti.realMin(d);
	}

	@Override
	public void realMin(double[] min) {
		iti.realMin(min);
	}

	@Override
	public void realMin(RealPositionable min) {
		iti.realMin(min);
	}

	@Override
	public double realMax(int d) {
		return iti.realMax(d);
	}

	@Override
	public void realMax(double[] max) {
		iti.realMax(max);
	}

	@Override
	public void realMax(RealPositionable max) {
		iti.realMax(max);
	}

	@Override
	public int numDimensions() {
		return iti.numDimensions();
	}

	@Override
	public Iterator<T> iterator() {
		return iti.iterator();
	}

	@Override
	public long min(int d) {
		return iti.min(d);
	}

	@Override
	public void min(long[] min) {
		iti.min(min);
	}

	@Override
	public void min(Positionable min) {
		iti.min(min);
	}

	@Override
	public long max(int d) {
		return iti.max(d);
	}

	@Override
	public void max(long[] max) {
		iti.max(max);
	}

	@Override
	public void max(Positionable max) {
		iti.max(max);
	}

	@Override
	public void dimensions(long[] dimensions) {
		iti.dimensions(dimensions);
	}

	@Override
	public long dimension(int d) {
		return iti.dimension(d);
	}

	@Override
	public Cursor<T> cursor() {
		return iti.cursor();
	}

	@Override
	public Cursor<T> localizingCursor() {
		return iti.localizingCursor();
	}
}
