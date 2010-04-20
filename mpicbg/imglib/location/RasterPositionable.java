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
package mpicbg.imglib.location;

/**
 * An element that can be positioned in n-dimensional discrete space.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de> and Stephan Preibisch
 */
public interface RasterPositionable
{
	/**
	 * Move by 1 in one dimension.
	 * 
	 * @param dim
	 */
	public void fwd( int dim );
	
	/**
	 * Move by -1 in one dimension.
	 * 
	 * @param dim
	 */
	public void bck( int dim );
	
	/**
	 * Move the element in one dimension for some distance.
	 *  
	 * @param distance
	 * @param dim
	 */
	public void move( int distance, int dim );
	
	/**
	 * Move the element in one dimension for some distance.
	 *  
	 * @param distance
	 * @param dim
	 */
	public void move( long distance, int dim );

	/**
	 * Move the element to the same location as a given {@link RasterLocalizable}.
	 * This method is expected to evaluates the distance to move for each
	 * dimension independently and then perform relative moves which can result
	 * in more efficient moves, particularly in containers where local parts of
	 * an image need to be requested on demand and switching between them is
	 * expensive.  It also is slightly faster for dimensionally sparse moves.
	 * 
	 * @param localizable
	 */
	public void moveTo( RasterLocalizable localizable );
	
	/**
	 * Move the element to some position.  This method is expected to evaluates
	 * the distance to move for each dimension independently and then perform
	 * relative moves which can result in more efficient moves, particularly in
	 * containers where local parts of an image need to be requested on demand
	 * and switching between them is expensive.  It also is slightly faster for
	 * dimensionally sparse moves.
	 * 
	 * @param position
	 */
	public void moveTo( int[] position );
	
	/**
	 * Move the element to some position.  This method is expected to evaluates
	 * the distance to move for each dimension independently and then perform
	 * relative moves which can result in more efficient moves, particularly in
	 * containers where local parts of an image need to be requested on demand
	 * and switching between them is expensive.  It also is slightly faster for
	 * dimensionally sparse moves.
	 * 
	 * @param position
	 */
	public void moveTo( long[] position );
	
	/**
	 * Place the element at the same location as a given {@link RasterLocalizable}
	 * 
	 * @param localizable
	 */
	public void setPosition( RasterLocalizable localizable );
	
	/**
	 * Set the position of the element.
	 * 
	 * @param position
	 */
	public void setPosition( int[] position );
	
	/**
	 * Set the position of the element.
	 * 
	 * @param position
	 */
	public void setPosition( long[] position );
	
	/**
	 * Set the position of the element for one dimension.
	 * 
	 * @param position
	 * @param dim
	 */
	public void setPosition( int position, int dim );		
	
	/**
	 * Set the position of the element for one dimension.
	 * 
	 * @param position
	 * @param dim
	 */
	public void setPosition( long position, int dim );
}
