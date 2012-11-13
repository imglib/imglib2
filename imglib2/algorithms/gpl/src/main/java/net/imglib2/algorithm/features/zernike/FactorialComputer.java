package net.imglib2.algorithm.features.zernike;

import java.util.ArrayList;

/** class to ease the multiplication and division or factorials. */
class FactorialComputer {
        /** the highest number this thing can handle. */
        private int m_high;

        /** array of prime numbers up to m_high. */
        private ArrayList<Integer> m_primes;

        /** array to memorize the current value. */
        private int[] m_values;

        /**
         * test for primality.
         * 
         * @param n
         *                the number to test
         * @return is the number prime?
         */
        private boolean isPrime(final int n) {
                for (int i = 0; i < m_primes.size(); ++i) {
                        if (n % m_primes.get(i) == 0) {
                                return false;
                        }
                }
                return true;
        }

        /**
         * default constructor.
         * 
         * @param high
         *                the highest number we want to handle
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
         * multiply the result by a single number (the number given as
         * parameter).
         * 
         * @param nParam
         *                the number
         */
        public void multiplyBy(final int nParam) {
                int n = nParam;
                if (nParam < 0 || nParam > m_high) {
                        throw new IllegalArgumentException(
                                        "Multiplication with out of range number!");
                }
                for (int i = 0; i < m_primes.size(); ++i) {
                        while (n % m_primes.get(i) == 0) {
                                n /= m_primes.get(i);
                                m_values[i]++;
                        }
                }
        }

        /**
         * divide the result by a single number (the number given as parameter).
         * 
         * @param nParam
         *                the number
         */
        public void divideBy(final int nParam) {
                int n = nParam;
                if (nParam < 0 || nParam > m_high) {
                        throw new IllegalArgumentException(
                                        "Division with out of range number!");
                }
                for (int i = 0; i < m_primes.size(); ++i) {
                        while (n % m_primes.get(i) == 0) {
                                n /= m_primes.get(i);
                                m_values[i]--;
                        }
                }
        }

        /**
         * multiply the result by the factorial of the number given as
         * parameter.
         * 
         * @param n
         *                the factorial
         */
        public void multiplyByFactorialOf(final int n) {
                if (n < 0 || n > m_high) {
                        throw new IllegalArgumentException(
                                        "Not able to handle multiplication by "
                                                        + " factorial of " + n);
                }
                for (int i = 2; i <= n; ++i) {
                        multiplyBy(i);
                }
        }

        /**
         * divide the result by the factorial of the number given as parameter.
         * 
         * @param n
         *                the factorial
         */
        public void divideByFactorialOf(final int n) {
                if (n < 0 || n > m_high) {
                        throw new IllegalArgumentException(
                                        "Not able to handle division by factorial of "
                                                        + n);
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
                                throw new IllegalArgumentException(
                                                "Result is not an integer");
                        }
                        for (int j = 0; j < m_values[i]; ++j) {
                                int oldResult = result;
                                result *= m_primes.get(i);
                                if (result / m_primes.get(i) != oldResult) {
                                        throw new IllegalArgumentException(
                                                        "Overflow while computing factorial!");
                                }
                        }
                }
                return result;
        }
}
