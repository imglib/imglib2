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



/**
 * An <em>n</em>-dimensional translation vector whose fields can be accessed
 * through their dimension index or as a double array.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface TranslationGet extends AffineGet
{
	/**
	 * Get a field of the <em>n</em>-dimensionsional translation vector.
	 * 
	 * @param d
	 * @return
	 */
	public double getTranslation( final int d );
	
	/**
	 * Get a copy of the <em>n</em>-dimensionsional translation vector.
	 *  
	 * @return
	 */
	public double[] getTranslationCopy();
	
	// NB: Ideally, we would utilize covariant inheritance to narrow the return
	// type of a single inverse() method here, rather than needing separate
	// methods inverse(), inverseAffine().  Unfortunately, due to a Javac bug
	// with multiple interface inheritance, we must avoid doing so for now. For
	// details, see:
	//     http://bugs.sun.com/view_bug.do?bug_id=6656332
	// The bug is fixed in JDK7.
	public TranslationGet inverseTranslation();
//	@Override
//	TranslationGet inverse();
}
