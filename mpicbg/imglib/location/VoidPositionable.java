/**
 * Copyright (c) 2009--2010, Stephan Saalfeld
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

import mpicbg.imglib.IntegerLocalizable;
import mpicbg.imglib.IntegerRandomAccess;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealRandomAccess;

/**
 * A {@link IntegerRandomAccess} that just does nothing.  This is the default
 * linked {@link IntegerRandomAccess} of any {@link IntegerRandomAccess}.  This
 * object doing nothing, it is implemented as a Singleton.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class VoidPositionable implements RealRandomAccess
{
	final static private VoidPositionable instance = new VoidPositionable();
	
	private VoidPositionable(){}
	final static public VoidPositionable getInstance(){ return instance; }
	
	@Override
	final public void bck( final int dim ){}
	
	@Override
	final public void fwd( final int dim ){}
	
	@Override
	final public void move( final double distance, final int dim ){}
	
	@Override
	final public void move( final float distance, final int dim ){}
	
	@Override
	final public void move( final int distance, final int dim ){}
	
	@Override
	final public void move( final long distance, final int dim ){}
	
	@Override
	final public void move( final RealLocalizable localizable ){}
	
	@Override
	final public void move( final IntegerLocalizable localizable ){}
	
	@Override
	final public void move( final double[] position ){}
	
	@Override
	final public void move( final float[] position ){}
	
	@Override
	final public void moveTo( final int[] position ){}
	
	@Override
	final public void moveTo( final long[] position ){}
	
	@Override
	final public void setPosition( final RealLocalizable localizable ){}
	
	@Override
	final public void setPosition( final IntegerLocalizable localizable ){}
	
	@Override
	final public void setPosition( final double[] position ){}
	
	@Override
	final public void setPosition( final float[] position ){}
	
	@Override
	final public void setPosition( final int[] position ){}
	
	@Override
	final public void setPosition( final long[] position ){}
	
	@Override
	final public void setPosition( final double position, final int dim ){}
	
	@Override
	final public void setPosition( final float position, final int dim ){}
	
	@Override
	final public void setPosition( final int position, final int dim ){}
	
	@Override
	final public void setPosition( final long position, final int dim ){}
	
	@Override
	final public int numDimensions(){ return 0; }
}
