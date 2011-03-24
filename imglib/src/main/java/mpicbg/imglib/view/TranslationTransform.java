package mpicbg.imglib.view;

import Jama.Matrix;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.concatenate.Concatenable;
import mpicbg.imglib.concatenate.PreConcatenable;

public class TranslationTransform extends AbstractMixedTransform implements Translation, Concatenable< Translation >, PreConcatenable< Translation >
{
	/**
	 * target = source + translation.
	 */
	protected final long[] translation;

	public TranslationTransform( final int targetDim )
	{
		super( targetDim );
		translation = new long[ targetDim ];
	}

	public TranslationTransform( final long[] translation )
	{
		super( translation.length );
		this.translation = translation.clone();
	}

	@Override
	public void getTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			t[ d ] = translation[ d ];
	}

	@Override
	public long getTranslation( final int d )
	{
		assert d <= numTargetDimensions;
		return translation[ d ];
	}

	public void setTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			translation[ d ] = t[ d ];
	}

	@Override
	public void apply( long[] source, long[] target )
	{
		assert source.length == numTargetDimensions;
		assert target.length == numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ d ] + translation[ d ];
	}

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length == numTargetDimensions;
		assert target.length == numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ d ] + ( int ) translation[ d ];
	}

	@Override
	public void apply( Localizable source, Positionable target )
	{
		assert source.numDimensions() == numTargetDimensions;
		assert target.numDimensions() == numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target.setPosition( source.getLongPosition( d ) + translation[ d ] , d );
	}

	@Override
	public TranslationTransform concatenate( Translation t )
	{
		assert numTargetDimensions == t.numTargetDimensions();
			
		TranslationTransform result = new TranslationTransform( numTargetDimensions );
		for ( int d = 0; d < numTargetDimensions; ++d )
			result.translation[ d ] = this.translation[ d ] + t.getTranslation( d );
		return result;
	}

	@Override
	public Class< Translation > getConcatenableClass()
	{
		return Translation.class;
	}

	@Override
	public TranslationTransform preConcatenate( Translation t )
	{
		return concatenate( t );
	}

	@Override
	public Class< Translation > getPreConcatenableClass()
	{
		return Translation.class;
	}

	/**
	 * set parameters to <code>transform</code>.
	 * 
	 * @param transform
	 */
	public void set( final Translation transform )
	{
		assert numTargetDimensions == transform.numTargetDimensions();

		transform.getTranslation( translation );
	}

	/**
	 * Get the matrix that transforms homogeneous source points to homogeneous
	 * target points. For testing purposes.
	 */
	@Override
	public Matrix getMatrix()
	{
		Matrix mat = new Matrix( numTargetDimensions + 1, numTargetDimensions + 1 );

		mat.set( numTargetDimensions, numTargetDimensions, 1 );

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			mat.set( d, numTargetDimensions, translation[ d ] );
			mat.set( d, d, 1 );
		}

		return mat;
	}
}
