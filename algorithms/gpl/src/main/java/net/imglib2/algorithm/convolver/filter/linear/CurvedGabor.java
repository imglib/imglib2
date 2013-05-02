/**
 * <p>
 * Copyright (C) 2008 Roy Liu, The Regents of the University of California <br />
 * All rights reserved.
 * </p>
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * </p>
 * <ul>
 * <li>Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.</li>
 * <li>Neither the name of the author nor the names of any contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.</li>
 * </ul>
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </p>
 */
package net.imglib2.algorithm.convolver.filter.linear;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.numeric.real.FloatType;

public class CurvedGabor extends ArrayImg<FloatType, FloatAccess> {

        public CurvedGabor(int supportRadius, double theta, double waveLength,
                        double curveRadius, boolean complexPart) {
                this(supportRadius, theta, waveLength, 0, curveRadius,
                                .3 * supportRadius, 2 * supportRadius,
                                complexPart);
        }

        public CurvedGabor(int supportRadius, double theta, double waveLength,
                        double phaseOffset, double curveRadius,
                        double sigmaxSqrt, double sigmaySqrt,
                        boolean complexPart) {
                super(new FloatArray(ArrayImgFactory.numEntitiesRangeCheck(
                                new long[] { supportRadius * 2 + 1,
                                                supportRadius * 2 + 1 }, 1)),
                                new long[] { supportRadius * 2 + 1,
                                                supportRadius * 2 + 1 }, 1);
                setLinkedType(new FloatType(this));
                final Cursor<FloatType> cur = cursor();
                while (cur.hasNext()) {
                        cur.next();
                        // TODO compare with seminar solution
                        double x = cur.getDoublePosition(0) - supportRadius;
                        double y = cur.getDoublePosition(1) - supportRadius;
                        // rotation
                        double xt = x * Math.cos(theta) + y * Math.sin(theta);
                        double yt = -x * Math.sin(theta) + y * Math.cos(theta);
                        // curve
                        double l = Math.sqrt(Math.pow(xt - curveRadius, 2) + yt
                                        * yt);
                        double xc = l - xt - curveRadius;
                        double yc = yt;
                        // gabor
                        double exp = Math.exp(-.5
                                        * ((xc * xc) / sigmaxSqrt + (yc * yc)
                                                        / sigmaySqrt));

                        double fac;
                        if (complexPart)
                                fac = Math.sin(2 * Math.PI * (xc / waveLength)
                                                + phaseOffset);
                        else
                                fac = Math.cos(2 * Math.PI * (xc / waveLength)
                                                + phaseOffset);

                        cur.get().setReal(exp * fac);
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equalIterationOrder(IterableRealInterval<?> f) {
                if (f.numDimensions() != this.numDimensions())
                        return false;
                if (ArrayImg.class.isInstance(f) || ListImg.class.isInstance(f)) {
                        final Interval a = (Interval) f;
                        for (int d = 0; d < n; ++d)
                                if (dimension[d] != a.dimension(d))
                                        return false;

                        return true;
                }
                return false;
        }

        // public static void main(String[] args) {
        // CurvedGabor g = new CurvedGabor(50, 0, 25, Math.PI / 2, 25, 50, 100,
        // true);
        // FilterTools.print2DMatrix(g);
        // new ImgNormalize<FloatType>(g.factory()).compute(g, g);
        // AWTImageTools.showInFrame(g, "", 10);
        // }
}
