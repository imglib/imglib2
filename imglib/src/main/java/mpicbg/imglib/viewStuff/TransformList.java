package mpicbg.imglib.viewStuff;

import java.util.ArrayList;
import java.util.List;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.concatenate.Concatenable;
import mpicbg.imglib.transform.Transform;

/**
 * With respect to matrix notation keeps the list in reverse order. That is, if
 * <em>x<sub>target</sub> = T<sub>2</sub> T<sub>1</sub> x<sub>source</sub></em>
 * then the list is maintained as [<em>T<sub>1</sub></em>, <em>T<sub>2</sub></em>].
 * 
 * TODO: should this really implement Transform?
 * TODO: should concatenate/preConcatenate behave the other way around?
 * 
 * @author Tobias Pietzsch
 */
public class TransformList implements Transform, Concatenable< Transform >
{
	final protected List< Transform > transforms;

	protected int numSourceDimensions;

	protected int numTargetDimensions;

	public TransformList( int numDimensions )
	{
		transforms = new ArrayList< Transform >();
		this.numSourceDimensions = numDimensions;
		this.numTargetDimensions = numDimensions;
	}

	public void append( Transform t )
	{
		assert t.numSourceDimensions() == numTargetDimensions;
		
		transforms.add( t );
		numTargetDimensions = t.numTargetDimensions();
	}

	public void prepend( Transform t )
	{
		assert t.numTargetDimensions() == numSourceDimensions;
		
		transforms.add( 0, t );
		numSourceDimensions = t.numSourceDimensions();
	}

	public List< Transform > getList( final List< Transform > preAllocatedList )
	{
		final List< Transform > returnList = ( preAllocatedList == null ) ? new ArrayList< Transform >() : preAllocatedList;
		returnList.addAll( transforms );
		return returnList;
	}

	@Override
	public int numSourceDimensions()
	{
		return numSourceDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return numTargetDimensions;
	}

	@Override
	public void apply( long[] source, long[] target )
	{
		assert source.length == numSourceDimensions;
		assert target.length == numTargetDimensions;
		
		long[] tmpSource = source;
		long[] tmpTarget = source;
		for ( final Transform t : transforms )
		{
			tmpTarget = new long[ t.numTargetDimensions() ]; 
			t.apply( tmpSource, tmpTarget );
			tmpSource = tmpTarget;
		}
		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = tmpTarget[ d ];
	}

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length == numSourceDimensions;
		assert target.length == numTargetDimensions;
		
		int[] tmpSource = source;
		int[] tmpTarget = source;
		for ( final Transform t : transforms )
		{
			tmpTarget = new int[ t.numTargetDimensions() ]; 
			t.apply( tmpSource, tmpTarget );
			tmpSource = tmpTarget;
		}
		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = tmpTarget[ d ];
	}

	@Override
	public void apply( Localizable source, Positionable target )
	{
		assert source.numDimensions() == numSourceDimensions;
		assert target.numDimensions() == numTargetDimensions;

		long[] tmpSource = new long[ numSourceDimensions ];
		long[] tmpTarget = new long[ numTargetDimensions ];
		source.localize( tmpSource );
		apply( tmpSource, tmpTarget );
		target.setPosition( tmpTarget );
	}

	@Override
	public void concatenate( Transform a )
	{
		append( a );
	}

	@Override
	public void preConcatenate( Transform a )
	{
		prepend( a );
	}

	@Override
	public Class< Transform > getConcatenableClass()
	{
		return Transform.class;
	}
}
