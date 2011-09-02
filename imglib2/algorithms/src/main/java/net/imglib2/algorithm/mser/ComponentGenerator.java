package net.imglib2.algorithm.mser;

public interface ComponentGenerator< T, C extends Component< T > >
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
