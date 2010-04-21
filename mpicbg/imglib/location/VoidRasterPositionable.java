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
package mpicbg.imglib.location;

/**
 * A {@link RasterPositionable} that just does nothing.  This is the default
 * linked {@link RasterPositionable} of any {@link RasterPositionable}.  This
 * object doing nothing, it is implemented as a Singleton.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class VoidRasterPositionable implements RasterPositionable
{
	final static private VoidRasterPositionable instance = new VoidRasterPositionable();
	
	private VoidRasterPositionable(){}
	final static public VoidRasterPositionable getInstance(){ return instance; }
	
	@Override
	final public void bck( final int dim ){}
	
	@Override
	final public void fwd( final int dim ){}
	
	@Override
	final public void linkRasterPositionable( final RasterPositionable rasterPositionable ){}
	
	@Override
	final public void move( final int distance, final int dim ){}
	
	@Override
	final public void move( final long distance, final int dim ){}
	
	@Override
	final public void moveTo( final RasterLocalizable localizable ){}
	
	@Override
	final public void moveTo( final int[] position ){}
	
	@Override
	final public void moveTo( final long[] position ){}
	
	@Override
	final public void setPosition( final RasterLocalizable localizable ){}
	
	@Override
	final public void setPosition( final int[] position ){}
	
	@Override
	final public void setPosition( final long[] position ){}
	
	@Override
	final public void setPosition( final int position, final int dim ){}
	
	@Override
	final public void setPosition( final long position, final int dim ){}
	
	@Override
	final public RasterPositionable unlinkRasterPositionable(){ return this; }
}
