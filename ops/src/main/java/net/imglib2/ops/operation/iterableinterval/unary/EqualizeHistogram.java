/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
 * #L%
 */

package net.imglib2.ops.operation.iterableinterval.unary;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;


//TODO Fix the algorithm their should't be values < 0 or > binCount
/**
 * TODO
 * 
 * @author Martin Horn (University of Konstanz)
 */
public class EqualizeHistogram<T extends RealType<T>> implements
        UnaryOperation<IterableInterval<T>, IterableInterval<T>> {

    private final int numBins;

    public EqualizeHistogram(int numBins) {
        this.numBins = numBins;

    }

    @Override
    public IterableInterval<T> compute(IterableInterval<T> in,
            IterableInterval<T> r) {

        assert (in.iterationOrder().equals(r.iterationOrder()));

        Histogram1d<T> histo = new MakeHistogram<T>(numBins).compute(in);
        T val = r.firstElement().createVariable();

		long min = (long)val.getMaxValue();
		if (Long.MAX_VALUE < val.getMaxValue()) {
			min = Long.MAX_VALUE;
		}

        long[] histoArray = histo.toLongArray();
        
        // calc cumulated histogram
        for (int i = 1; i < histo.getBinCount(); i++) {
            histoArray[i] = histoArray[i] + histoArray[i-1];
            if (histoArray[i] != 0) {
                min = Math.min(min, histoArray[i]);
            }
        }

        double gmax = histo.getBinCount();

        Cursor<T> cin = in.cursor();
        Cursor<T> cout = r.cursor();

        long numPix = r.size();

        while (cin.hasNext()) {
            cin.fwd();
            cout.fwd();

            val = cin.get();
            long p = histoArray[(int) histo.map(val)];
            double t = (p - min);
            t /= numPix - min;
            t *= gmax;
            p = (int)Math.round(t);
			//TODO fix algorithm
			//code accesses for n bins the n+1 th bin
			//that should be properly fixed by adapting the algorithm
			if (p >= histo.getBinCount()) {
				p = histo.getBinCount() -1;
			}
			if (p < 0) {
				p = 0;
			}
			histo.getCenterValue(p, cout.get());
        }
        return r;

    }

    @Override
    public UnaryOperation<IterableInterval<T>, IterableInterval<T>> copy() {
        return new EqualizeHistogram<T>(numBins);
    }

    public static void main(String[] args) {
        Img<FloatType> test =
                new ArrayImgFactory<FloatType>().create(new int[]{10, 10},
                        new FloatType());
        Random rand = new Random();
        for (FloatType t : test) {
            t.setReal(rand.nextDouble() * Float.MAX_VALUE);
        }
        new EqualizeHistogram<FloatType>(256).compute(test, test);
    }
}
