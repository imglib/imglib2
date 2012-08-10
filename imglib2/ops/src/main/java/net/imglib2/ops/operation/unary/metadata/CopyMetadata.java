package net.imglib2.ops.operation.unary.metadata;

import net.imglib2.meta.Metadata;
import net.imglib2.ops.UnaryOperation;

/**
 * 
 * @author dietzc
 * 
 */
public class CopyMetadata implements UnaryOperation< Metadata, Metadata >
{

	private UnaryOperation< Metadata, Metadata >[] ops;

	public CopyMetadata( UnaryOperation< Metadata, Metadata >... ops )
	{
		this.ops = ops;
	}

	@SuppressWarnings( "unchecked" )
	public CopyMetadata()
	{
		ops = new UnaryOperation[ 4 ];
		ops[ 0 ] = new CopyCalibratedSpace< Metadata >();
		ops[ 1 ] = new CopyImageMetadata< Metadata >();
		ops[ 2 ] = new CopyNamed< Metadata >();
		ops[ 3 ] = new CopySourced< Metadata >();
	}

	@Override
	public Metadata compute( Metadata input, Metadata output )
	{
		for ( UnaryOperation< Metadata, Metadata > op : ops )
		{
			op.compute( input, output );
		}

		return output;
	}

	@Override
	public UnaryOperation< Metadata, Metadata > copy()
	{
		return new CopyMetadata();
	}

}
