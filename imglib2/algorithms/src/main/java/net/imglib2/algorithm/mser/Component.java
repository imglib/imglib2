package net.imglib2.algorithm.mser;

import net.imglib2.Localizable;
import net.imglib2.type.Type;

public interface Component< T >
{
	/**
	 * Set the threshold value (e.g., grey-level) for this component.
	 * 
	 * @param value
	 *            the threshold value
	 */
	public abstract void setValue( final T value );

	/**
	 * Get the threshold value (e.g., grey-level) for this component.
	 * 
	 * @return the threshold value
	 */
	public abstract T getValue();

	/**
	 * Add a pixel to the set of pixels represented by this component.
	 * 
	 * @param position
	 *            a pixel position
	 */
	public abstract void addPosition( final Localizable position );

	/**
	 * Merge other component (of the same concrete type) into this component.
	 * 
	 * @param component
	 *            the other component
	 */
	public abstract void merge( final Component< T > component );
}
