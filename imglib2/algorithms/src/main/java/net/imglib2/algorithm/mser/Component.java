package net.imglib2.algorithm.mser;

import net.imglib2.Localizable;

public interface Component< T >
{
	/**
	 * Create new components.
	 * 
	 * @author Tobias Pietzsch
	 *
	 * @param <T>
	 * @param <C>
	 */
	public interface Generator< T, C extends Component< T > >
	{
		/**
		 * Create a new empty component with the given value (e.g., grey-level).
		 * 
		 * @param value
		 *            value of the component
		 * @return new component
		 */
		public C createComponent( final T value );

		/**
		 * Create a component with a value (e.g., grey-level) greater than any
		 * occurring in the input for the {@link ComponentTree}. This is used as a
		 * terminator element on the component stack.
		 * 
		 * @return new component
		 */
		public C createMaxComponent();
	}

	/**
	 * Handle completed components that are output by {@link ComponentTree}.
	 * 
	 * @author Tobias Pietzsch
	 * 
	 * @param <C>
	 */
	public interface Handler< C >
	{
		public void emit( C component );
	}

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

