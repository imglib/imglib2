/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.interpolation;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.sampler.Sampler;
import mpicbg.imglib.type.Type;

public interface Interpolator<T extends Type<T>> extends Sampler< T >
{
	/**
	 * Returns the typed interpolator factory the Interpolator has been instantiated with.
	 * 
	 * @return - the interpolator factory
	 */
	public InterpolatorFactory<T> getInterpolatorFactory();
	
	/**
	 * Returns the {@link OutOfBoundsStrategyFactory} used for interpolation
	 * 
	 * @return - the {@link OutOfBoundsStrategyFactory}
	 */
	public OutOfBoundsStrategyFactory<T> getOutOfBoundsStrategyFactory();
	
	/**
	 * Returns the typed image the interpolator is working on
	 * 
	 * @return - the image
	 */
	public Image<T> getImage();
	
	/**
	 * Moves the interpolator to a random position inside or out of image bounds.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param position - the floating position of the same dimensionality as the image
	 */
	public void moveTo( float[] position );

	/**
	 * Moves the interpolator a certain distance given by the vector to a random position inside or out of image bounds.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param vector - the floating vector of the same dimensionality as the image
	 */
	public void moveRel( float[] vector );
	
	/**
	 * Sets the interpolator to a random position inside or out of image bounds.
	 * This method is typically less efficient than moving the position
	 * 
	 * @param position - the floating position of the same dimensionality as the image
	 */
	public void setPosition( float[] position );

	/**
	 * Returns the positon of the interpolator.
	 * 
	 * @param position - the floating position of the same dimensionality as the image
	 */
	public void getPosition( float[] position );

	/**
	 * Returns the positon of the interpolator.
	 * 
	 * @return - the floating position of the same dimensionality as the image (as a new object)
	 */
	public float[] getPosition();
	
	/**
	 * Closes the interpolator and with it any cursors or other containers, images or datastructures
	 * that might have been created to make the interpolation work
	 */
	public void close();	
}
