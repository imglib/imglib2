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
 * An {@link Iterator} that just does nothing.  This is the default linked
 * {@link Iterator} of any Iterable.  This object doing nothing, it is
 * implemented as a Singleton.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class VoidIterator implements Iterator< Object >
{
	final static private VoidIterator instance = new VoidIterator();
	
	private VoidIterator(){}
	final static public VoidIterator getInstance(){ return instance; }
	
	@Override
	final public void jumpFwd( final long steps ){}

	@Override
	final public void fwd(){}

	@Override
	final public void reset(){}

	@Override
	final public void linkIterator( Iterator< ? > iterable ){}

	@Override
	final public VoidIterator unlinkIterator(){ return this; }
	
	@Override
	final public boolean hasNext(){ return true; }
	
	@Override
	final public Object next(){ return null; }
	
	@Override
	final public void remove(){}
}
