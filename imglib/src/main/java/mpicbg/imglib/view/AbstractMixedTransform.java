package mpicbg.imglib.view;


public abstract class AbstractMixedTransform implements Mixed, BoundingBoxTransform
{
	/**
	 * dimension of target vector.
	 */
	protected final int numTargetDimensions;

	protected AbstractMixedTransform( int numTargetDimensions )
	{
		this.numTargetDimensions = numTargetDimensions;
	}
	
	@Override
	public int numSourceDimensions()
	{
		return numTargetDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return numTargetDimensions;
	}

	@Override
	public void getTranslation( final long[] translation )
	{
		assert translation.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			translation[ d ] = 0;
	}

	@Override
	public long getTranslation( final int d )
	{
		return 0;
	}

	@Override
	public void getComponentZero( boolean[] zero )
	{
		assert zero.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			zero[ d ] = false;
	}

	@Override
	public boolean getComponentZero( final int d )
	{
		return false;
	}

	@Override
	public void getComponentMapping( int[] component )
	{
		assert component.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			component[ d ] = d;
	}

	@Override
	public int getComponentMapping( final int d )
	{
		return d;
	}

	@Override
	public void getComponentInversion( boolean[] invert )
	{
		assert invert.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			invert[ d ] = false;
	}

	@Override
	public boolean getComponentInversion( final int d )
	{
		return false;
	}

	@Override
	public BoundingBox transform( BoundingBox boundingBox )
	{
		assert boundingBox.numDimensions() == numSourceDimensions();

		if ( numSourceDimensions() == numTargetDimensions )
		{ // apply in-place
			long[] tmp = new long[ numTargetDimensions ];
			boundingBox.min( tmp );
			apply( tmp, boundingBox.min );
			boundingBox.max( tmp );
			apply( tmp, boundingBox.max );
			return boundingBox;
		}
		else
		{ // create new BoundingBox with target dimensions
			BoundingBox b = new BoundingBox( numTargetDimensions );
			apply( boundingBox.min, b.min );
			apply( boundingBox.max, b.max );
			return b;
		}
	}
}
