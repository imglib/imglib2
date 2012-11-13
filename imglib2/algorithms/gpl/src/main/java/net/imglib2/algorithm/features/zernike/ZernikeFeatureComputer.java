/* -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2010
 * Universitaet Konstanz, Germany.
 * Lehrstuhl fuer Angewandte Informatik
 * Prof. Dr. Michael R. Berthold
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner.
 * -------------------------------------------------------------------
 *
 * History
 *   Feb 3, 2006 (Stefan): created
 */
package net.imglib2.algorithm.features.zernike;

import java.util.ArrayList;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;

/**
 * Class responsable for handling the zernike moments calculation.
 * 
 * @author Stefan, University of Konstanz
 */
public class ZernikeFeatureComputer<T extends RealType<T>> {

        /** height and width of the image. */
        private int m_width;

        /** height of the image. */
        private int m_height;

        /** pixels range between 0 and this value. */
        private static final double MAX_PIXEL_VALUE = 255.0;

        private final IterableInterval<T> m_interval;

        /**
         * default constructor. initializes the data structures
         * 
         * @param image
         *                the image
         * @param mask
         *                the mask
         */
        public ZernikeFeatureComputer(IterableInterval<T> interval) {
                m_interval = interval;
                m_width = (int) interval.dimension(0);
                m_height = (int) interval.dimension(1);
                // if (mask.getWidth() != m_width || mask.getHeight() !=
                // m_height) {
                // throw new IllegalArgumentException(
                // "Mask and image dimensions do not match");
                // }

        }

        // /**
        // * constructor used for testing purposes, where we have no mask image,
        // and
        // * such consider all pixels as not being masked.
        // *
        // * @param image
        // * the image
        // */
        // public ZernikeFeatureComputer(final Img<T> image,
        // OutOfBoundsFactory<T, Img<T>> factory) {
        // m_width = (int) image.dimension(0);
        // m_height = (int) image.dimension(1);
        //
        // m_mask = new Segment(2);
        //
        // LocalizableByDimCursor<T> cur;
        // if (factory != null)
        // cur = image.createLocalizableByDimCursor(factory);
        // else
        // cur = image.createLocalizableByDimCursor();
        //
        // while (cur.hasNext()) {
        // cur.fwd();
        // m_mask.addPosition(new int[] { cur.getPosition(0),
        // cur.getPosition(1) });
        // }
        // cur.close();
        // m_image = image;
        // }

        /**
         * compute F(m, n, s). see zernike documentation for more.
         * 
         * @param m
         *                the "order"
         * @param n
         *                the "repetition"
         * @param s
         *                the index
         * @return the Coefficient of r^(m-2*s) from R_mn(r)
         */
        public static int computeF(final int m, final int n, final int s) {
                assert (m + Math.abs(n)) % 2 == 0;
                assert (m - Math.abs(n)) % 2 == 0;
                assert m - Math.abs(n) >= 0;
                assert (m - Math.abs(n)) / 2 - s >= 0;

                int absN = Math.abs(n);

                FactorialComputer fc = new FactorialComputer(m);
                fc.multiplyByFactorialOf(m - s);
                fc.divideByFactorialOf(s);
                fc.divideByFactorialOf((m + absN) / 2 - s);
                fc.divideByFactorialOf((m - absN) / 2 - s);

                return fc.value();
        }

        /**
         * create the polynom R_mn. see zernike documentation for more.
         * 
         * @param m
         *                the "order"
         * @param n
         *                the "repetition"
         * @return the F polynom
         */
        public static Polynom createR(final int m, final int n) {
                Polynom result = new Polynom(m);
                int sign = 1;
                for (int s = 0; s <= (m - Math.abs(n)) / 2; ++s) {
                        int pos = m - 2 * s;
                        result.setCoefficient(pos, sign * computeF(m, n, s));
                        sign = -sign;
                }
                return result;
        }

        /**
         * implements the actual algoritm.
         * 
         * @param m
         *                the "order" of the Zernike moment to be computed
         * @param n
         *                the "repetition"
         * @return the complex value of the Zernike moment
         */
        public Complex computeZernikeMoment(final int m, final int n) {
                double real = 0;
                double imag = 0;

                if (m < 0 || (m - Math.abs(n)) % 2 != 0 || Math.abs(n) > m) {
                        throw new IllegalArgumentException(
                                        "m and n do not satisfy the"
                                                        + "Zernike moment properties");
                }

                int centerX = m_width / 2;
                int centerY = m_height / 2;
                int max = Math.max(centerX, centerY);
                double radius = Math.sqrt(2 * max * max);

                Polynom polynomOrthogonalRadial = createR(m, n);

                IterableInterval<T> ii = m_interval;

                Cursor<T> cur = ii.localizingCursor();

                while (cur.hasNext()) {
                        cur.fwd();
                        int x = cur.getIntPosition(0) - centerX;
                        int y = cur.getIntPosition(1) - centerY;

                        // compute polar coordinates for x and y
                        double r = Math.sqrt(x * x + y * y) / radius;
                        double ang = n * Math.atan2(y, x);

                        double value = polynomOrthogonalRadial.evaluate(r);
                        double pixel = cur.get().getRealDouble()
                                        / MAX_PIXEL_VALUE;

                        real += pixel * value * Math.cos(ang);
                        imag -= pixel * value * Math.sin(ang);

                }

                real = real * (m + 1) / Math.PI;
                imag = imag * (m + 1) / Math.PI;
                return new Complex(real, imag);
        }

        /**
         * return the number of zernike moment types that exist and have the
         * order smaller than or equal to the parameter.
         * 
         * @param orderMax
         *                the maximal order
         * @return the number of zernike moments which have the order smaller
         *         than or equal to this one
         */
        public static int countZernikeMoments(final int orderMax) {
                return (orderMax + 1) * (orderMax + 2) / 2;
        }

        /**
         * return the order of the i'th zernike moment from the string of
         * moments which have the order <= the parameter. indexes start with 0.
         * 
         * @param orderMax
         *                the maximal order
         * @param index
         *                the index of the zernike moment in the string of
         *                moments with order <= orderMax
         * @return the order of the requested zernike moment
         */
        public static int giveZernikeOrder(final int orderMax, final int index) {
                int sum = 0;
                for (int i = 0; i <= orderMax; ++i) {
                        sum += i + 1;
                        if (index < sum) {
                                return i;
                        }
                }
                assert "Index for Zernike moment out of range" == "";
                return -1;
        }

        /**
         * return the order of the i'th zernike moment from the string of
         * moments which have the order <= the parameter. indexes start with 0.
         * 
         * @param orderMax
         *                the maximal order
         * @param index
         *                the index of the zernike moment in the string of
         *                moments with order <= orderMax
         * @return the repetition of the requested zernike moment
         */
        public static int giveZernikeRepetition(final int orderMax,
                        final int index) {
                // 0, -1, 1, -2, 0, 2, -3, -1, 1, 3, ...
                int sum = 0;
                for (int i = 0; i <= orderMax; ++i) {
                        sum += i + 1;
                        if (index < sum) {
                                return -i + 2 * (i + 1 - (sum - index));
                        }
                }
                assert "Index for Zernike moment out of range" == "";
                return -1;
        }

        /**
         * given the first few moments of an image, reconstruct it.
         * 
         * @param width
         *                the width of the desired image
         * @param height
         *                the height of the image
         * @param features
         *                the first few zernike features. for any given order
         *                the features must for all repetitions
         * @return the reconstructed image
         */
        public static Img<ByteType> reconstructImage(final int width,
                        final int height, final ArrayList<Complex> features) {
                int centerX = width / 2;
                int centerY = height / 2;
                int max = Math.max(centerX, centerY);
                double radius = Math.sqrt(2 * max * max);

                double[][] image = new double[width][height];

                int indexFeature = 0;
                for (int order = 0; indexFeature < features.size(); ++order) {
                        for (int rep = -order; rep <= order; rep += 2) {
                                Complex moment = features.get(indexFeature);
                                Polynom polynomOrthogonalRadial = createR(
                                                order, rep);

                                for (int i = 0; i < width; ++i) {
                                        for (int j = 0; j < height; ++j) {
                                                int x = i - centerX;
                                                int y = j - centerY;

                                                // compute polar coordinates for
                                                // x and y
                                                double r = Math.sqrt(x * x + y
                                                                * y)
                                                                / radius;
                                                double ang = rep
                                                                * Math.atan2(y,
                                                                                x);
                                                double valueRnm = polynomOrthogonalRadial
                                                                .evaluate(r);

                                                Complex valueVnm = new Complex(
                                                                Math.cos(ang)
                                                                                * valueRnm,
                                                                Math.sin(ang)
                                                                                * valueRnm);
                                                Complex toAdd = moment
                                                                .multiplyTo(valueVnm);

                                                // assert
                                                // Math.abs(toAdd.getImaginary())
                                                // < 0.1;

                                                image[i][j] += toAdd.getReal();
                                        }
                                }
                                indexFeature++;
                        }
                }

                double imageMax = 0;
                double imageMin = 0;
                for (int i = 0; i < width; ++i) {
                        for (int j = 0; j < height; ++j) {
                                imageMax = Math.max(image[i][j], imageMax);
                                imageMin = Math.min(image[i][j], imageMin);
                        }
                }

                Img<ByteType> res = new ArrayImgFactory<ByteType>().create(
                                new int[] { width, height }, new ByteType());
                Cursor<ByteType> cur = res.localizingCursor();

                while (cur.hasNext()) {
                        cur.fwd();
                        cur.get()
                                        .set((byte) (((image[cur
                                                        .getIntPosition(0)][cur
                                                        .getIntPosition(1)] - imageMin) / (imageMax - imageMin)) * 255));
                }
                return res;
        }

        /**
         * represent a complex number with double coefficients.
         * 
         * @author Stefan, University of Konstanz
         */
        public static class Complex {
                /** real part. */
                private double m_real;

                /** imaginary part. */
                private double m_imaginary;

                /**
                 * constructor for number with imaginary part = 0.
                 * 
                 * @param real
                 *                the real part
                 */
                public Complex(final double real) {
                        m_real = real;
                        m_imaginary = 0;
                }

                /**
                 * constructor.
                 * 
                 * @param real
                 *                the real part
                 * @param imaginary
                 *                the imaginary part
                 */
                public Complex(final double real, final double imaginary) {
                        m_real = real;
                        m_imaginary = imaginary;
                }

                /**
                 * @return the real part of the complex number.
                 */
                public double getReal() {
                        return m_real;
                }

                /**
                 * @return the imaginary part of the complex number.
                 */
                public double getImaginary() {
                        return m_imaginary;
                }

                /**
                 * immutably multiply this complex number with the parameter.
                 * 
                 * @return the result of the multiplication
                 * @param c
                 *                the thing to multiply this by
                 */
                public Complex multiplyTo(final Complex c) {
                        return new Complex(this.m_real * c.m_real
                                        - this.m_imaginary * c.m_imaginary,
                                        this.m_real * c.m_imaginary
                                                        + this.m_imaginary
                                                        * c.m_real);
                }

                /**
                 * mutably add the parameter to this.
                 * 
                 * @param c
                 *                the thing to add with.
                 */
                public void add(final Complex c) {
                        m_real += c.m_real;
                        m_imaginary += c.m_imaginary;
                }

                /**
                 * return the conjugate of this number.
                 * 
                 * @return the conjugate
                 */
                public Complex conjugate() {
                        return new Complex(this.m_real, this.m_imaginary);
                }

                /**
                 * return the absolute value of this complex number.
                 * 
                 * @return the abs value
                 */
                public double abs() {
                        return Math.sqrt(m_real * m_real + m_imaginary
                                        * m_imaginary);
                }
        }

}
