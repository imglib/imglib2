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
 *   Feb 8, 2006 (Stefan): created
 */
package net.imglib2.algorithm.features.zernike;

/**
 * class used to represent a zernike moment polynomial.
 */
public class Polynom {
        /** the array of polynom coefficients. */
        private int[] m_coefficients;

        /** the degree of the polynom. */
        private int m_degree;

        /**
         * default constructor.
         * 
         * @param degree
         *                the degree of the polynom
         */
        public Polynom(final int degree) {
                m_degree = degree;
                m_coefficients = new int[m_degree + 1];
                for (int i = 0; i <= m_degree; ++i) {
                        setCoefficient(i, 0);
                }
        }

        /**
         * set the coefficient at a position.
         * 
         * @param pos
         *                the position (the power of the monom)
         * @param coef
         *                the coefficient
         */
        public void setCoefficient(final int pos, final int coef) {
                m_coefficients[pos] = coef;
        }

        /**
         * return the coefficient at a given position.
         * 
         * @param pos
         *                the position
         * @return the coefficient
         */
        public int getCoefficient(final int pos) {
                return m_coefficients[pos];
        }

        /**
         * return the value of the polynom in a given point.
         * 
         * @param x
         *                the point
         * @return the value of the polynom
         */
        public double evaluate(final double x) {
                double power = 1.0;
                double result = 0.0;
                for (int i = 0; i <= m_degree; ++i) {
                        result += m_coefficients[i] * power;
                        power *= x;
                }
                return result;
        }

        /**
         * provide a String representation of this polynom. mostly for debugging
         * purposes and for the JUnit test case
         * 
         * @return the String representation
         */
        public String toString() {
                String result = "";
                for (int i = 0; i <= m_degree; ++i) {
                        if (m_coefficients[i] != 0) {
                                result += m_coefficients[i] + "X^" + i + "  ";
                        }
                }
                return result;
        }
}
