/**
 * 
 */
package net.imglib2.realtransform;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 *
 */
public class AffineTransform2DTest {

	protected Random rnd = new Random( 0 );
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		 rnd.setSeed( 0 );
	}

	@Test
	public void testRotate()
	{
		final AffineTransform2D affine = new AffineTransform2D();
		affine.set(
				rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble(),
				rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble() );
		
		final AffineTransform2D dR = new AffineTransform2D();
		
		for ( int i = 0; i < 100; ++i )
		{
			final double d = rnd.nextDouble() * 8 * Math.PI - 4 * Math.PI;
			final double dcos = Math.cos( d );
			final double dsin = Math.sin( d );
			
			dR.set(
					dcos, -dsin, 0.0,
					dsin, dcos, 0.0 );
			
			dR.concatenate( affine );
			affine.rotate( d );
			
			assertArrayEquals( dR.getRowPackedCopy(), affine.getRowPackedCopy(), 0.001 );
		}
	}
}
