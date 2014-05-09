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

import java.awt.image.BufferedImage;

import net.imglib2.ui.overlay.BufferedImageOverlayRenderer;

/**
 * Receiver for a {@link BufferedImage} (to be drawn onto a canvas later).
 * <p>
 * A {@link Renderer} will render source data into a {@link BufferedImage} and
 * provide this to a {@link RenderTarget}.
 * <p>
 * See {@link BufferedImageOverlayRenderer}, which is a {@link RenderTarget} and
 * also an {@link OverlayRenderer} that draws the {@link BufferedImage}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface RenderTarget
{
	/**
	 * Set the {@link BufferedImage} that is to be drawn on the canvas.
	 * 
	 * @param bufferedImage
	 *            image to draw (may be null).
	 * @return a previously set {@link BufferedImage} that is currently not
	 *         being painted or null. Used for double-buffering.
	 */
	public BufferedImage setBufferedImage( final BufferedImage bufferedImage );

	/**
	 * Get the current canvas width.
	 * 
	 * @return canvas width.
	 */
	public int getWidth();

	/**
	 * Get the current canvas height.
	 * 
	 * @return canvas height.
	 */
	public int getHeight();
}
