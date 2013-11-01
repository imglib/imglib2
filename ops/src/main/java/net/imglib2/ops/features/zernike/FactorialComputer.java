/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * --------------------------------------------------------------------- *
 *
 */
package net.imglib2.ops.features.zernike;

import java.util.ArrayList;

/**
 * TODO: Auto-generated
 * 
 * @author <a href="mailto:dietzc85@googlemail.com">Christian Dietz</a>
 * @author <a href="mailto:horn_martin@gmx.de">Martin Horn</a>
 * @author <a href="mailto:michael.zinsmaier@googlemail.com">Michael Zinsmaier</a>
 */
class FactorialComputer {
    /** the highest number this thing can handle. */
    private final int m_high;

    /** array of prime numbers up to m_high. */
    private final ArrayList<Integer> m_primes;

    /** array to memorize the current value. */
    private final int[] m_values;

    /**
     * test for primality.
     * 
     * @param n the number to test
     * @return is the number prime?
     */
    private boolean isPrime(final int n) {
        for (int i = 0; i < m_primes.size(); ++i) {
            if ((n % m_primes.get(i)) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * default constructor.
     * 
     * @param high the highest number we want to handle
     */
    public FactorialComputer(final int high) {
        m_high = high;

        m_primes = new ArrayList<Integer>();
        for (int i = 2; i <= high; ++i) {
            if (isPrime(i)) {
                m_primes.add(i);
            }
        }
        m_values = new int[m_primes.size()];
    }

    /**
     * multiply the result by a single number (the number given as parameter).
     * 
     * @param nParam the number
     */
    public void multiplyBy(final int nParam) {
        int n = nParam;
        if ((nParam < 0) || (nParam > m_high)) {
            throw new IllegalArgumentException("Multiplication with out of range number!");
        }
        for (int i = 0; i < m_primes.size(); ++i) {
            while ((n % m_primes.get(i)) == 0) {
                n /= m_primes.get(i);
                m_values[i]++;
            }
        }
    }

    /**
     * divide the result by a single number (the number given as parameter).
     * 
     * @param nParam the number
     */
    public void divideBy(final int nParam) {
        int n = nParam;
        if ((nParam < 0) || (nParam > m_high)) {
            throw new IllegalArgumentException("Division with out of range number!");
        }
        for (int i = 0; i < m_primes.size(); ++i) {
            while ((n % m_primes.get(i)) == 0) {
                n /= m_primes.get(i);
                m_values[i]--;
            }
        }
    }

    /**
     * multiply the result by the factorial of the number given as parameter.
     * 
     * @param n the factorial
     */
    public void multiplyByFactorialOf(final int n) {
        if ((n < 0) || (n > m_high)) {
            throw new IllegalArgumentException("Not able to handle multiplication by " + " factorial of " + n);
        }
        for (int i = 2; i <= n; ++i) {
            multiplyBy(i);
        }
    }

    /**
     * divide the result by the factorial of the number given as parameter.
     * 
     * @param n the factorial
     */
    public void divideByFactorialOf(final int n) {
        if ((n < 0) || (n > m_high)) {
            throw new IllegalArgumentException("Not able to handle division by factorial of " + n);
        }
        for (int i = 2; i <= n; ++i) {
            divideBy(i);
        }
    }

    /**
     * compute the current value kept here.
     * 
     * @return the value computed so far
     */
    public int value() {
        int result = 1;
        for (int i = 0; i < m_primes.size(); ++i) {
            if (m_values[i] < 0) {
                throw new IllegalArgumentException("Result is not an integer");
            }
            for (int j = 0; j < m_values[i]; ++j) {
                final int oldResult = result;
                result *= m_primes.get(i);
                if ((result / m_primes.get(i)) != oldResult) {
                    throw new IllegalArgumentException("Overflow while computing factorial!");
                }
            }
        }
        return result;
    }
}
