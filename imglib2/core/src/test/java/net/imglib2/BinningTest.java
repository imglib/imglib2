/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.imglib2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aivar Grislis
 */
public class BinningTest {
    private static final String MAX_DIGITS = "fffffffffffff";
    private static final String MIN_DIGITS = "0000000000000";
    private static final int LAST_HEX_DIGIT_INDEX = 12;
    
    public BinningTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test looking up some simple values.
     */
    @Test
    public void testValues() {
        System.out.println("testValues");
        
        testSomeValues(256);
        testSomeValues(1000);
        testSomeValues(32767);
        testSomeValues(65534);
    }

    /**
     * Test edge cases, near min and max.
     */
    @Test
    public void testEdges() {
        System.out.println("testEdges");

        double d = 1.0234; // testing arbitrary values
        testSomeEdges(256, d);
        testSomeEdges(1000, d);
        testSomeEdges(32767, d);
        testSomeEdges(65534, d);
        
        d = 9.87654321;
        testSomeEdges(256, d);
        testSomeEdges(1000, d);
        testSomeEdges(32767, d);
        testSomeEdges(65534, d);
        
        d = -1.0234;
        testSomeEdges(256, d);
        testSomeEdges(1000, d);
        testSomeEdges(32767, d);
        testSomeEdges(65534, d);

        d = -9.87654321;
        testSomeEdges(256, d);
        testSomeEdges(1000, d);
        testSomeEdges(32767, d);
        testSomeEdges(65534, d);
    }
    
    /**
     * Tests bins are evenly distributed.
     */
    @Test
    public void testDistribution() {
        System.out.println("testDistribution");
        
        long bins = 256;
        double min = 0.0;
        double max = 1.0;
        double inc = 0.0001;
        testHistogram(min, max, inc, bins);

        bins = 1024;
        min = 0.0;
        max = 1.0;
        inc = 0.00001;
        testHistogram(min, max, inc, bins);
    }
    
    /**
     * Helper function checks values map reasonably.
     * 
     * @param bins 
     */    
    private void testSomeValues(long bins) {
        double min;
        double max;
        double value;
        long bin;

        // check that value min maps to first bin
        min = 0.0;
        max = 1.0;
        value = min;
        bin = Binning.valueToBin(bins, min, max, value);
        assertEquals(0, bin);

        // check that value max maps to last bin
        min = 0.0;
        max = 1.0;
        value = max;
        bin = Binning.valueToBin(bins, min, max, value);
        assertEquals(bins - 1, bin);

        // check that value (max - min) / 2 maps to middle bin 
        min = 0.0;
        max = 1.0;      
        value = (max - min) / 2;
        bin = Binning.valueToBin(bins, min, max, value);
        assertEquals(bins / 2, bin);

        // check that if max == min == value get middle bin 
        min = 0.0;
        max = min;      
        value = min;
        bin = Binning.valueToBin(bins, min, max, value);
        assertEquals(bins / 2, bin);
    }

    /**
     * Tests that values less than min or more than max map outside the range
     * of bins.
     * 
     * @param bins
     * @param d1 
     */
    private void testSomeEdges(long bins, double d1) {
        double min;
        double max;
        double value;
        long bin;

        // get two adjacent doubles
        double d2 = nextDouble(d1);
        if (d2 < d1) {
            // allow for negative numbers
            double tmp = d1;
            d1 = d2;
            d2 = tmp;
        }
        System.out.println("d1 is " + d1 + " d2 " + d2);

        // value is just before min
        value = d1;
        min = d2;
        max = d2 + 1.0;
        bin = Binning.valueToBin(bins, min, max, value);
        // inclusive, get first bin
        assertEquals(0, bin);
        bin = Binning.exclusiveValueToBin(bins, min, max, value);
        // exclusive, get bin -1
        assertEquals(-1, bin);

        // value is just after max
        max = d1;
        min = d1 - 1.0;
        value = d2;
        bin = Binning.valueToBin(bins, min, max, value);
        // inclusive, get last bin
        assertEquals(bins - 1, bin);
        bin = Binning.exclusiveValueToBin(bins, min, max, value);
        // exclusive, get last bin + 1
        assertEquals(bins, bin);
    }

    /**
     * Given a double value returns the next representable double.
     * 
     * @param value
     * @return 
     */
    private double nextDouble(double value) {
        double nextValue = 0.0;
        String hexString = nextDoubleHexString(Double.toHexString(value));
        try {
            nextValue = Double.parseDouble(hexString);
        }
        catch (Exception e) {
            System.out.println("Error parsing " + hexString);
        }
        return nextValue;
    }

    /**
     * Parses double as hex string and generates next double hex string.
     * 
     * @param hex
     * @return 
     */
    private String nextDoubleHexString(String hex) {
        String returnValue = null;
        boolean negative = false;
        
        if ('-' == hex.charAt(0)) {
            negative = true;
            hex = hex.substring(1);
        }

        String powerDigits = hex.substring(18);
        int power = 0;
        try {
            power = Integer.parseInt(powerDigits);
        } catch (Exception e) {
            System.out.println("Error parsing " + powerDigits + " " + e);
        }

        String hexDigits = hex.substring(4, 17);
        System.out.print(hexDigits + "p" + power + " -> ");
        if (MAX_DIGITS.equals(hexDigits)) {
            ++power;
            hexDigits = MIN_DIGITS;
        } else {
            char[] hexChars = hexDigits.toCharArray();

            int i = LAST_HEX_DIGIT_INDEX;
            boolean carry;
            do {
                ++hexChars[i];

                if (hexChars[i] == 'g') {
                    hexChars[i] = '0';

                    // carry to preceding digit
                    --i;
                    carry = true;
                } else {
                    carry = false;

                    if (hexChars[i] == '9' + 1) {
                        hexChars[i] = 'a';
                    }
                }
            } while (carry);

            hexDigits = new String(hexChars);
        }
        System.out.println(" " + hexDigits + "p" + power);

        return (negative ? "-" : "") + "0x1." + hexDigits + "p" + power;
    }

    /**
     * Tests the uniformity of bin distribution in a histogram situation.
     * 
     * @param min
     * @param max
     * @param inc
     * @param bins 
     */
    private void testHistogram(double min, double max, double inc, long bins) {
        long[] histogram = new long[(int) bins];

        // fill up the histogram with uniform values
        for (double value = min; value <= max; value += inc) {
            int bin = (int) Binning.valueToBin(bins, min, max, value);
            ++histogram[bin];
        }

        // make a list of histogram count values
        List<Long> values = new ArrayList<Long>();
        Arrays.sort(histogram);
        long previousCount = -1;
        for (long count : histogram) {
            if (count != previousCount) {
                values.add(count);
                previousCount = count;
            }
        }

        // should get only two values
        assertEquals(2, values.size());

        // values should be adjacent
        long v1 = values.get(0);
        long v2 = values.get(1);
        assertEquals(v1 + 1, v2);
    }

}
