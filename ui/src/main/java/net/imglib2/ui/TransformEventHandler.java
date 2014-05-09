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
package net.imglib2.ui;

/**
 * Change a transformation in response to user input (mouse events, key events,
 * etc.). Report to a {@link TransformListener} when the transformation changes.
 * The {@link TransformEventHandler} receives notifications about changes of the
 * canvas size (it may react for example by changing the scale of the
 * transformation accordingly).
 * 
 * @param <A>
 *            type of transformation.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface TransformEventHandler< A >
{
	/**
	 * Get (a copy of) the current source-to-screen transform.
	 * 
	 * @return current transform.
	 */
	public A getTransform();

	/**
	 * Set the current source-to-screen transform.
	 */
	public void setTransform( final A transform );

	/**
	 * This is called, when the screen size of the canvas (the component
	 * displaying the image and generating mouse events) changes. This can be
	 * used to determine screen coordinates to keep fixed while zooming or
	 * rotating with the keyboard, e.g., set these to
	 * <em>(width/2, height/2)</em>. It can also be used to update the current
	 * source-to-screen transform, e.g., to change the zoom along with the
	 * canvas size.
	 * 
	 * @param width
	 *            the new canvas width.
	 * @param height
	 *            the new canvas height.
	 * @param updateTransform
	 *            whether the current source-to-screen transform should be
	 *            updated. This will be <code>false</code> for the initial
	 *            update of a new {@link TransformEventHandler} and
	 *            <code>true</code> on subsequent calls. If <code>true</code>,
	 *            an update to its {@link TransformListener} should be
	 *            triggered.
	 */
	public void setCanvasSize( final int width, final int height, final boolean updateTransform );

	/**
	 * Set the {@link TransformListener} who will receive updated
	 * transformations.
	 * 
	 * @param transformListener
	 *            will receive transformation updates.
	 */
	public void setTransformListener( TransformListener< A > transformListener );

	/**
	 * Get description of how mouse and keyboard actions map to transformations.
	 */
	public String getHelpString();
}
