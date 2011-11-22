package net.imglib2.algorithm.componenttree;

import net.imglib2.Localizable;

/**
 * This interface is used by {@link ComponentTree} to build the component tree
 * of an image. In the algorithm described by D. Nister and H. Stewenius in
 * "Linear Time Maximally Stable Extremal Regions" (ECCV 2008) a stack of
 * incomplete components is maintained while visiting the pixels of the input
 * image. {@link Component} represents an element on the component stack, i.e.,
 * a connected component in the making.
 *
 * It provides methods to get/set the threshold value for the connected
 * component, to add pixels to the component, and to merge it with another
 * component.
 *
 * {@link ComponentTree} uses a {@link Component.Generator} to create new
 * components and emits completed components to a {@link Component.Handler}.
 *
 * @author Tobias Pietzsch
 *
 * @param <T>
 *            value type of the input image.
 */
public interface Component< T >
{
	/**
	 * Create new components.
	 * 
	 * @author Tobias Pietzsch
	 *
	 * @param <T>
	 *            value type of the input image.
	 * @param <C>
	 *            component type.
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
	 *            component type.
	 */
	public interface Handler< C >
	{
		/**
		 * {@link ComponentTree} calls this for every completed component. NOTE
		 * THAT THE COMPONENT IS RE-USED BY {@link ComponentTree}! That is,
		 * after calling emit() new pixels may be added, etc. Do not store the
		 * component object but rather copy the relevant data!
		 *
		 * @param component
		 *            a completed component
		 */
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

