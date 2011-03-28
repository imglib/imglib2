package mpicbg.imglib.transform.integer;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.concatenate.Concatenable;
import mpicbg.imglib.concatenate.PreConcatenable;
import Jama.Matrix;

/**
 * Map the components of the source vector to obtain the target vector, for
 * instance transform (x,y,z) to (x,z,y).
 * 
 * <p>
 * Although it's intended use is a dimension permutation, bijectivity of the
 * mapping is not enforced. The mapping is implemented as a inverse lookup,
 * i.e., every component of the target is read from a source component. The same
 * source component can be mapped to several target components.
 * </p>
 * 
 * @author Tobias Pietzsch
 */
public class ComponentMappingTransform extends AbstractMixedTransform implements ComponentMapping, Concatenable< ComponentMapping >, PreConcatenable< ComponentMapping >
{
	/**
	 * for each component of the target vector: from which source vector
	 * component should it be taken.
	 */
	protected final int[] component;

	public ComponentMappingTransform( final int targetDim )
	{
		super( targetDim );
		component = new int[ targetDim ];
		for ( int d = 0; d < targetDim; ++d )
			component[ d ] = d;
	}

	public ComponentMappingTransform( final int[] component )
	{
		super( component.length );
		this.component = component.clone();
	}

	@Override
	public void getComponentMapping( @SuppressWarnings( "hiding" ) final int[] component )
	{
		assert component.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			component[ d ] = this.component[ d ];
	}

	@Override
	public int getComponentMapping( final int d )
	{
		assert d <= numTargetDimensions;
		return component[ d ];
	}

	/**
	 * Set for each target dimensions from which source dimension it is taken.
	 * 
	 * <p>
	 * For instance, if the transform maps 3D (x,y,z) coordinates to 2D (z,x,y)
	 * coordinate this will be [2, 0, 1].
	 * </p>
	 * 
	 * @param component
	 *            array that says for each component of the target vector from
	 *            which source vector component it should be taken.
	 */
	public void setComponentMapping( final int[] component )
	{
		assert component.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			this.component[ d ] = component[ d ];
	}

	@Override
	public Matrix getMatrix()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void apply( long[] source, long[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ component[ d ] ];
	}

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ component[ d ] ];
	}

	@Override
	public void apply( Localizable source, Positionable target )
	{
		assert source.numDimensions() >= numTargetDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target.setPosition( source.getLongPosition( component[ d ] ), d );
	}

	@Override
	public ComponentMappingTransform concatenate( ComponentMapping t )
	{
		assert numTargetDimensions == t.numTargetDimensions();
		
		ComponentMappingTransform result = new ComponentMappingTransform( numTargetDimensions );

		for ( int d = 0; d < numTargetDimensions; ++d )
			result.component[ d ] = t.getComponentMapping( this.component[ d ] );

		return result;
	}

	@Override
	public Class< ComponentMapping > getConcatenableClass()
	{
		return ComponentMapping.class;
	}

	@Override
	public Concatenable< ComponentMapping > preConcatenate( ComponentMapping t )
	{
		assert numTargetDimensions == t.numTargetDimensions();
		
		ComponentMappingTransform result = new ComponentMappingTransform( numTargetDimensions );

		for ( int d = 0; d < numTargetDimensions; ++d )
			result.component[ d ] = this.component[ t.getComponentMapping( d ) ];

		return result;
	}

	@Override
	public Class< ComponentMapping > getPreConcatenableClass()
	{
		return ComponentMapping.class;
	}
}
