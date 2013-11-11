package net.imglib2.ops.descriptors.moments.zernike.helper;

/**
 * class used to represent a zernike moment polynomial.
 */
public class Polynom 
{
    /** the array of polynom coefficients. */
    private final int[] m_coefficients;

    /** the degree of the polynom. */
    private final int m_degree;

    /**
     * default constructor.
     * 
     * @param degree the degree of the polynom
     */
    public Polynom(final int degree) 
    {
        m_degree = degree;
        m_coefficients = new int[m_degree + 1];
        for (int i = 0; i <= m_degree; ++i) 
        {
            setCoefficient(i, 0);
        }
    }

    /**
     * set the coefficient at a position.
     * 
     * @param pos the position (the power of the monom)
     * @param coef the coefficient
     */
    public void setCoefficient(final int pos, final int coef) 
    {
        m_coefficients[pos] = coef;
    }

    /**
     * return the coefficient at a given position.
     * 
     * @param pos the position
     * @return the coefficient
     */
    public int getCoefficient(final int pos) 
    {
        return m_coefficients[pos];
    }

    /**
     * return the value of the polynom in a given point.
     * 
     * @param x the point
     * @return the value of the polynom
     */
    public double evaluate(final double x) 
    {
        double power = 1.0;
        double result = 0.0;
        for (int i = 0; i <= m_degree; ++i) 
        {
            result += m_coefficients[i] * power;
            power *= x;
        }
        return result;
    }

    /**
     * provide a String representation of this polynom. mostly for debugging purposes and for the JUnit test case
     * 
     * @return the String representation
     */
    @Override
    public String toString() 
    {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i <= m_degree; ++i) 
        {
            if (m_coefficients[i] != 0) 
            {
                result.append(m_coefficients[i] + "X^" + i + "  ");
            }
        }
        return result.toString();
    }
}