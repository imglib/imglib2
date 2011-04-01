package mpicbg.imglib.interpolation.randomaccess;

import mpicbg.imglib.util.Util;

public class GrayCodeTest
{
	// dummy: our own position
	double[] position;
	
	// dummy: our floored position
	long[] targetPosition;

	// number of dimensions
	int n;

	/**
	 * Index into {@link weights} array.
	 * 
	 * <p>
	 * To visit the pixels that contribute to an interpolated value, we move in
	 * a (binary-reflected) Gray code pattern, such that only one dimension of
	 * the target position is modified per move.
	 * 
	 * <p>
	 * This index is the corresponding gray code bit pattern which will select
	 * the correct corresponding weight.
	 * 
	 * <p>
	 * {@see http://en.wikipedia.org/wiki/Gray_code}
	 */
	protected int code;

	/**
	 *  Weights for each pixel of the <em>2x2x...x2</em> hypercube
	 *  of pixels participating in the interpolation.
	 *  
	 *  Indices into this array are arranged in the standard iteration
	 *  order (as provided by {@link IntervalIndexer#positionToIndex}).
	 *  Element 0 refers to position <em>(0,0,...,0)</em>,
	 *  element 1 refers to position <em>(1,0,...,0)</em>,
	 *  element 2 refers to position <em>(0,1,...,0)</em>, etc.
	 */
	final protected double[] weights;

	GrayCodeTest( int numDimensions )
	{
		weights = new double [ 1 << numDimensions ];
		
		n = numDimensions;
		position = new double[n];
		targetPosition = new long[n];
		
		code = 0;
	}
	
	/**
	 * Fill the {@link weights} array.
	 * 
	 * <p>
	 * Let <em>w_d</em> denote the fraction of a pixel at which the sample
	 * position <em>p_d</em> lies from the floored position <em>pf_d</em> in
	 * dimension <em>d</em>. That is, the value at <em>pf_d</em> contributes
	 * with <em>(1 - w_d)</em> to the sampled value; the value at
	 * <em>( pf_d + 1 )</em> contributes with <em>w_d</em>.
	 * 
	 * <p>
	 * At every pixel, the total weight results from multiplying the weights of
	 * all dimensions for that pixel. That is, the "top-left" contributing pixel
	 * (position floored in all dimensions) gets assigned weight
	 * <em>(1-w_0)(1-w_1)...(1-w_n)</em>.
	 * 
	 * <p>
	 * We work through the weights array starting from the highest dimension.
	 * For the highest dimension, the first half of the weights contain the
	 * factor <em>(1 - w_n)</em> because this first half corresponds to floored
	 * pixel positions in the highest dimension. The second half contain the
	 * factor <em>w_n</em>. In this first step, the first weight of the first
	 * half gets assigned <em>(1 - w_n)</em>. The first element of the second
	 * half gets assigned <em>w_n</em>
	 * 
	 * <p>
	 * From their, we work recursively down to dimension 0. That is, each half
	 * of weights is again split recursively into two partitions. The first
	 * element of the second partitions is the first element of the half
	 * multiplied with <em>(w_d)</em>. The first element of the first partitions
	 * is multiplied with <em>(1 - w_d)</em>.
	 * 
	 * <p>
	 * When we have reached dimension 0, all weights will have a value assigned.
	 */
	protected void fillWeights()
	{
		weights[ 0 ] = 1.0d;

		for ( int d = n - 1; d >= 0; --d )
		{
			final double w    = position[ d ] - targetPosition[ d ];
			final double wInv = 1.0d - w;
			final int wInvIndexIncrement = 1 << d;
			final int loopCount = 1 << ( n - 1 - d );
			final int baseIndexIncrement = wInvIndexIncrement * 2;
			int baseIndex = 0;
			for (int i = 0; i < loopCount; ++i )
			{
				weights[ baseIndex + wInvIndexIncrement ] = weights[ baseIndex ] * w;
				weights[ baseIndex ] *= wInv;
				baseIndex += baseIndexIncrement;
			}
		}
	}
	
	private void graycodeFwdRecursive ( int dimension )
	{
		if ( dimension == 0 )
		{
			fwd ( 0 );
			code += 1;
			accumulate();
		}
		else
		{
			graycodeFwdRecursive ( dimension - 1 );
			fwd ( dimension );
			code += 1 << dimension;
			accumulate();
			graycodeBckRecursive ( dimension - 1 );
		}
	}

	private void graycodeBckRecursive ( int dimension )
	{
		if ( dimension == 0 )
		{
			bck ( 0 );
			code -= 1;
			accumulate();
	}
		else
		{
			graycodeFwdRecursive ( dimension - 1 );
			bck ( dimension );
			code -= 1 << dimension;
			accumulate();
			graycodeBckRecursive ( dimension - 1 );
		}
	}

	/**
	 * Get the interpolated value at the current position.
	 * 
	 * <p>
	 * To visit the pixels that contribute to an interpolated value, we move in
	 * a (binary-reflected) Gray code pattern, such that only one dimension of
	 * the target position is modified per move.
	 * 
	 * <p>
	 * {@see http://en.wikipedia.org/wiki/Gray_code}
	 */
	public void get()
	{
		fillWeights();
		printWeights();
		System.out.println();

		code = 0;
		accumulate();
		graycodeFwdRecursive( n - 1 );

		bck( n - 1 );
		System.out.println( "targetPosition after get(): " + Util.printCoordinates( targetPosition ) );
	}

	
	
	
	public void printWeights()
	{
		for ( int i = 0; i < weights.length; ++i )
			System.out.printf("weights [ %2d ] = %f\n", i, weights[ i ] );
	}

	public void printCode()
	{
		final int maxbits = 4;
		String binary = Integer.toBinaryString( code );
		for ( int i = binary.length(); i < maxbits; ++i )
			System.out.print("0");
		System.out.print ( binary );
	}

	// dummy: move target fwd
	public void fwd ( int dimension )
	{
		++targetPosition[ dimension ];
	}

	// dummy: move target bck
	public void bck ( int dimension )
	{
		--targetPosition[ dimension ];
	}

	// dummy: target.get(), multiply with weights[code], accumulate.
	private void accumulate()
	{
		System.out.print( "accumulating value at " + Util.printCoordinates( targetPosition ) );
		System.out.print( "with weights [" );
		printCode();
		System.out.printf( "] = %f" + "\n", weights[ code ] );
	}
	
	public static void main (String[] args)
	{
		System.out.println("Testing 4D");
		System.out.println("==============================");
		GrayCodeTest t = new GrayCodeTest(4);
		
		t.position[0] = 10.7;
		t.position[1] = 0.7;
		t.position[2] = 4.5;
		t.position[3] = -9.2;
		
		t.targetPosition[0] = 10;
		t.targetPosition[1] = 0;
		t.targetPosition[2] = 4;
		t.targetPosition[3] = -10;
		
		t.get();

		
		
		System.out.println();
		System.out.println();
		System.out.println("Testing 2D");
		System.out.println("==============================");
		t = new GrayCodeTest(2);
		
		t.position[0] = 10.7;
		t.position[1] = 0.7;
		
		t.targetPosition[0] = 10;
		t.targetPosition[1] = 0;
		
		t.get();
		
	}
}
