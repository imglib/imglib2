package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;

/**
 * Mother interface for local neighborhoods.
 * <p>
 * A local neighborhood is a finite interval that can be positioned on a {@link RandomAccessible}
 * through the {@link Positionable} interface.
 * It can then be iterated over and will return the underlying type of the source 
 * {@link RandomAccessible}, in a hyper-volume defined by the concrete implementation, 
 * through the {@link IterableInterval} interface.
 * It can also return its position through the {@link Localizable} interface. 
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 * @author Christian Dietz
 * @author Tobias Pietzsch 
 *
 * @param <T>  the type of the interval iterated
 * @param <IN>  the source over which is positioned this interval
 */
public interface Neighborhood <T, IN extends RandomAccessible<T>> extends Positionable, IterableInterval<T>, Localizable {

	/**
	 * @return a new instance of the concrete implementation on the same source, 
	 * at the same position with the same dimension.
	 */
	public Neighborhood<T, IN> copy();
	
	/**
	 * @return a new instance of the same concrete implementation on a new different source. 
	 */
	public <R, JN extends RandomAccessible<R>> Neighborhood<R, JN> copyOn(JN source);
	
	/**
	 * Change the source over which this neighborhood is positioned.
	 * @param source
	 */
	public void updateSource(IN source);

}
