/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package net.imglib2.realtransform;

import net.imglib2.RealLocalizable;


/**
 * An <em>n</em>-dimensional affine transformation whose
 * <em>n</em>&times;(<em>n</em>+1) affine transformation matrix can be accessed
 * via row and column index.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface AffineGet extends InvertibleRealTransform
{
	/**
	 * Get a field of the <em>n</em>&times;(<em>n</em>+1) affine transformation
	 * matrix.
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public double get( final int row, final int column );
	
	/**
	 * Get a copy of the <em>n</em>&times;(<em>n</em>+1) affine transformation
	 * matrix as a row packed array similar to Jama.
	 *  
	 * @return
	 */
	public double[] getRowPackedCopy();
	
	/**
	 * Get the constant partial differential vector for dimension d.
	 * 
	 * @param d
	 * @return
	 */
	public RealLocalizable d( int d );
	
	// NB: Ideally, we would utilize covariant inheritance to narrow the return
	// type of a single inverse() method here, rather than needing separate
	// methods inverse(), inverseAffine().  Unfortunately, due to a Javac bug
	// with multiple interface inheritance, we must avoid doing so for now. For
	// details, see:
	//     http://bugs.sun.com/view_bug.do?bug_id=6656332
	// The bug is fixed in JDK7.
	
	public AffineGet inverseAffine();
//	@Override
//	AffineGet inverse();
}
