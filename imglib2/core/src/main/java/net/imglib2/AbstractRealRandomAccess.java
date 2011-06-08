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
 */
package net.imglib2;

/**
 * @author Lee Kamentsky
 *
 */
public abstract class AbstractRealRandomAccess<T> extends
		AbstractRealLocalizableSampler<T> implements RealRandomAccess<T> {

	protected AbstractRealRandomAccess(int nDimensions) {
		super(nDimensions);
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#fwd(int)
	 */
	@Override
	public void fwd(int d) {
		position[d] ++;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#bck(int)
	 */
	@Override
	public void bck(int d) {
		position[d]--;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(int, int)
	 */
	@Override
	public void move(int distance, int d) {
		position[d] += distance;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(long, int)
	 */
	@Override
	public void move(long distance, int d) {
		position[d] += distance;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(net.imglib2.Localizable)
	 */
	@Override
	public void move(Localizable localizable) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] += localizable.getDoublePosition(i);
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(int[])
	 */
	@Override
	public void move(int[] distance) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] += distance[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(long[])
	 */
	@Override
	public void move(long[] distance) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] += distance[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(net.imglib2.Localizable)
	 */
	@Override
	public void setPosition(Localizable localizable) {
		localizable.localize(position);
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(int[])
	 */
	@Override
	public void setPosition(int[] position) {
		for (int i=0; i < numDimensions(); i++) {
			this.position[i] = position[i]; 
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(long[])
	 */
	@Override
	public void setPosition(long[] position) {
		for (int i=0; i < numDimensions(); i++) {
			this.position[i] = position[i]; 
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(int, int)
	 */
	@Override
	public void setPosition(int position, int d) {
		this.position[d] = position;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(long, int)
	 */
	@Override
	public void setPosition(long position, int d) {
		this.position[d] = position;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(float, int)
	 */
	@Override
	public void move(float distance, int d) {
		this.position[d] += distance;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(double, int)
	 */
	@Override
	public void move(double distance, int d) {
		this.position[d] += distance;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(net.imglib2.RealLocalizable)
	 */
	@Override
	public void move(RealLocalizable localizable) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] += localizable.getDoublePosition(i);
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(float[])
	 */
	@Override
	public void move(float[] distance) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] += distance[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(double[])
	 */
	@Override
	public void move(double[] distance) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] += distance[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(net.imglib2.RealLocalizable)
	 */
	@Override
	public void setPosition(RealLocalizable localizable) {
		localizable.localize(position);
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(float[])
	 */
	@Override
	public void setPosition(float[] position) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] = position[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(double[])
	 */
	@Override
	public void setPosition(double[] position) {
		for (int i=0; i< numDimensions(); i++) {
			this.position[i] = position[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(float, int)
	 */
	@Override
	public void setPosition(float position, int d) {
		this.position[d] = position;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(double, int)
	 */
	@Override
	public void setPosition(double position, int d) {
		this.position[d] = position;
	}
}
