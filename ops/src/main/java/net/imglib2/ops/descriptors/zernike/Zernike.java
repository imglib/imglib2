package net.imglib2.ops.descriptors.zernike;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.Module;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.geometric.centerofgravity.CenterOfGravity;
import net.imglib2.ops.descriptors.zernike.helper.Polynom;
import net.imglib2.ops.descriptors.zernike.helper.FactComputer;
import net.imglib2.type.numeric.RealType;

public class Zernike extends AbstractModule<double[]>
{
	private static final double MAX_PIXEL_VALUE = 255.0;
	
	@ModuleInput
	private IterableInterval< ? extends RealType< ? >> ii;
	
	@ModuleInput
	CenterOfGravity center;
	
	@ModuleInput
	ZernikeParameter param;
	
	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return Zernike.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public boolean hasCompatibleOutput( Class< ? > clazz )
	{
		return clazz.isAssignableFrom( double[].class );
	}

	protected double[] recompute() {
		
        double real = 0;
        double imag = 0;
		
        // order
		final int n = param.getN();
		// repetition
		final int m = param.getN();
		
		if ((m < 0) || (((m - Math.abs(n)) % 2) != 0) || (Math.abs(n) > m)) 
	            throw new IllegalArgumentException("m and n do not satisfy the" + "Zernike moment properties");

        final double centerX = center.get()[0];
        final double centerY = center.get()[1];
        final double max = Math.max(centerX, centerY);
        final double radius = Math.sqrt(2 * max * max);
        
        final Polynom polynomOrthogonalRadial = createR(m, n);
        
        final Cursor< ? extends RealType< ? > > it = ii.localizingCursor();
        
        while (it.hasNext()) 
        {
        	it.fwd();
            final double x = it.getIntPosition(0) - centerX;
            final double y = it.getIntPosition(1) - centerY;

            // compute polar coordinates for x and y
            final double r = Math.sqrt((x * x) + (y * y)) / radius;
            final double ang = n * Math.atan2(y, x);
            
            final double value = polynomOrthogonalRadial.evaluate(r);
            final double pixel = it.get().getRealDouble() / MAX_PIXEL_VALUE;

            real += pixel * value * Math.cos(ang);
            imag -= pixel * value * Math.sin(ang);

        }

        real = (real * (m + 1)) / Math.PI;
        imag = (imag * (m + 1)) / Math.PI;
        
        double[] res = {real, imag};
		return res;
	}
	
	   /**
     * create the polynom R_mn. see zernike documentation for more.
     * 
     * @param m the "order"
     * @param n the "repetition"
     * @return the F polynom
     */
    public static Polynom createR(final int m, final int n) 
    {
        final Polynom result = new Polynom(m);
        int sign = 1;
        for (int s = 0; s <= ((m - Math.abs(n)) / 2); ++s) 
        {
            final int pos = m - (2 * s);
            result.setCoefficient(pos, sign * computeF(m, n, s));
            sign = -sign;
        }
        return result;
    }
    
    /**
     * compute F(m, n, s). see zernike documentation for more.
     * 
     * @param m the "order"
     * @param n the "repetition"
     * @param s the index
     * @return the Coefficient of r^(m-2*s) from R_mn(r)
     */
    public static int computeF(final int m, final int n, final int s) 
    {
        assert ((m + Math.abs(n)) % 2) == 0;
        assert ((m - Math.abs(n)) % 2) == 0;
        assert (m - Math.abs(n)) >= 0;
        assert (((m - Math.abs(n)) / 2) - s) >= 0;

        final int absN = Math.abs(n);

        final FactComputer fc = new FactComputer(m);
        fc.multiplyByFactorialOf(m - s); 
        fc.divideByFactorialOf(s);
        fc.divideByFactorialOf(((m + absN) / 2) - s);
        fc.divideByFactorialOf(((m - absN) / 2) - s);

        return fc.value();
    }
}
