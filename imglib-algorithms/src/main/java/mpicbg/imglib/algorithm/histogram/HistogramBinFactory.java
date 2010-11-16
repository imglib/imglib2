/**
 * Copyright (c) 2010, Larry Lindsey
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
 * @author Larry Lindsey
 */
package mpicbg.imglib.algorithm.histogram;

import mpicbg.imglib.type.Type;

/**
 * The HistogramBinFactory interface is used by {@link Histogram} to generate
 * {@link HistogramBin}s and {@link HistogramKey}s.  In addition, a
 * HistogramBinFactory implements a method used to determine if two Type's are
 * equivalent to each other, much as in .equals(). 
 * @author Larry Lindsey
 *
 * @param <T> The type of {@link Type} that this HistogramBinFactory pertains
 * to.
 */
public interface HistogramBinFactory <T extends Type<T>>{

	/**
	 * Create a {@link HistogramKey} to be used for a {@link HashTable}, to key
	 * a {@link HistogramBin}.
	 * @param type the {@link Type} to which the generated HistogramKey will
	 * correspond.
	 * @return A HistogramKey corresponding to the given Type.
	 */
	 public HistogramKey<T> createKey(T type);
	
	 /**
	  * Determines whether two {@link Type}s are equivalent in the sense that
	  * they would be binned into the same HistogramBin.
	  * @param type1 the first Type for comparison
	  * @param type2 the second Type for comparison
	  * @return true, if the the types in question are equivalent, generally
	  * meaning that the values that they contain would be placed into the same
	  * histogram bin, and false otherwise.
	  */
	 public boolean equivalent(T type1, T type2);
	 
	 /**
	  * Create a {@link HistogramBin} corresponding to the give {@link Type},
	  * in the sense that for the purposes of the {@link Histogram} class,
	  * the given Type would be binned into the returned HistogramBin.
	  * @param type the prototype Type used to create the HistogramBin
	  * @return a HistogramBin, into which the given type would be binned.
	  */
	 public HistogramBin<T> createBin(T type);
}
