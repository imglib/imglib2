package mpicbg.imglib.transform.integer;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.concatenate.Concatenable;
import mpicbg.imglib.concatenate.PreConcatenable;
import Jama.Matrix;

public class TranslationTransform extends AbstractMixedTransform implements Translation, Concatenable< Translation >, PreConcatenable< Translation >
{
	/**
	 * target = source + translation.
	 */
	protected final long[] translation;
	
	protected final InverseTranslationTransform inverse;	

	public TranslationTransform( final int targetDim )
	{
		super( targetDim );
		translation = new long[ targetDim ];
		this.inverse = new InverseTranslationTransform( targetDim );
	}

	public TranslationTransform( final long[] translation )
	{
		super( translation.length );
		this.translation = translation.clone();
		this.inverse = new InverseTranslationTransform( numTargetDimensions );
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
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ d ] + translation[ d ];
	}

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ d ] + ( int ) translation[ d ];
	}

	@Override
	public void apply( Localizable source, Positionable target )
	{
		assert source.numDimensions() >= numTargetDimensions;
		assert target.numDimensions() >= numTargetDimensions;

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

	@Override
	public void applyInverse( long[] source, long[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			source[ d ] = target[ d ] - translation[ d ];
	}

	@Override
	public void applyInverse( int[] source, int[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			source[ d ] = target[ d ] - ( int )translation[ d ];
	}

	@Override
	public void applyInverse( Positionable source, Localizable target )
	{
		assert source.numDimensions() >= numTargetDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			source.setPosition( target.getLongPosition( d ) - translation[ d ] , d );
	}

	public class InverseTranslationTransform extends AbstractMixedTransform implements Translation, Concatenable< Translation >, PreConcatenable< Translation >
	{
		InverseTranslationTransform( int targetDim )
		{
			super( targetDim );
		}

		@Override
		public void apply( long[] source, long[] target )
		{
			TranslationTransform.this.applyInverse( target, source );
		}

		@Override
		public void apply( int[] source, int[] target )
		{
			TranslationTransform.this.applyInverse( target, source );
		}

		@Override
		public void apply( Localizable source, Positionable target )
		{
			TranslationTransform.this.applyInverse( target, source );
		}

		@Override
		public void applyInverse( long[] source, long[] target )
		{
			TranslationTransform.this.apply( target, source );
		}

		@Override
		public void applyInverse( int[] source, int[] target )
		{
			TranslationTransform.this.apply( target, source );
		}

		@Override
		public void applyInverse( Positionable source, Localizable target )
		{
			TranslationTransform.this.apply( target, source );
		}

		@Override
		public Matrix getMatrix()
		{
			Matrix mat = new Matrix( numTargetDimensions + 1, numTargetDimensions + 1 );

			mat.set( numTargetDimensions, numTargetDimensions, 1 );

			for ( int d = 0; d < numTargetDimensions; ++d )
			{
				mat.set( d, numTargetDimensions, -translation[ d ] );
				mat.set( d, d, 1 );
			}

			return mat;
		}

		@Override
		public TranslationTransform inverse()
		{
			return TranslationTransform.this;
		}

		@Override
		public void getTranslation( final long[] t )
		{
			assert t.length == numTargetDimensions;
			for ( int d = 0; d < numTargetDimensions; ++d )
				t[ d ] = -TranslationTransform.this.translation[ d ];
		}

		@Override
		public long getTranslation( final int d )
		{
			assert d <= numTargetDimensions;
			return -TranslationTransform.this.translation[ d ];
		}

		@Override
		public TranslationTransform concatenate( Translation t )
		{
			assert numTargetDimensions == t.numTargetDimensions();
			TranslationTransform result = new TranslationTransform( numTargetDimensions );
			for ( int d = 0; d < numTargetDimensions; ++d )
				result.translation[ d ] = t.getTranslation( d ) - TranslationTransform.this.translation[ d ];
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
	};
	
	@Override
	public InverseTranslationTransform inverse()
	{
		return inverse;
	}
}
