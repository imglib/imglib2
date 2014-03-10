/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
 * @param <T>
 *            value type of the input image.
 * 
 * @author Tobias Pietzsch
 */
public interface Component< T >
{
	/**
	 * Create new components.
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
		 * occurring in the input for the {@link ComponentTree}. This is used as
		 * a terminator element on the component stack.
		 * 
		 * @return new component
		 */
		public C createMaxComponent();
	}

	/**
	 * Handle completed components that are output by {@link ComponentTree}.
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
