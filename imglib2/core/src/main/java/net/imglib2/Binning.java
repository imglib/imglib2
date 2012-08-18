/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2;

/**
 * Static helper methods to convert from a value to a bin, useful for dealing
 * with histograms and LUTs.
 * 
 * @author Aivar Grislis
 */
public class Binning {

    /**
     * Convert value to bin number.<p>
     * This variant is inclusive, it assigns all values to the range 0..(bins-1).
     * 
     * @param bins
     * @param min
     * @param max
     * @param value
     * @return bin number 0...(bins-1)
     */
    public static long valueToBin(long bins, double min, double max, double value) {
        long bin = exclusiveValueToBin(bins, min, max, value);
        bin = Math.max(bin, 0);
        bin = Math.min(bin, bins - 1);
        return bin;
    }

    /**
     * Convert value to bin number.<p>
     * This variant is exclusive, not all values map to the range 0...(bins-1).
     * 
     * @param bins
     * @param min
     * @param max
     * @param value
     * @return 
     */
    public static long exclusiveValueToBin(long bins, double min, double max, double value) {
        long returnValue;
        if (max == min) {
            // degenerate case
            returnValue = bins / 2;
        }
        else if (value == max) {
            // special case, otherwise 1.0 * bins is bins
            returnValue = bins - 1;
        }
        else {
            double temp = (value - min) / (max - min);
            returnValue = (long)(temp * bins); // Not (bins - 1)!
            
            // handle edge case
            if (temp < 0 && returnValue == 0) {
                --returnValue;
            }
        }
        return returnValue;
    }
}
