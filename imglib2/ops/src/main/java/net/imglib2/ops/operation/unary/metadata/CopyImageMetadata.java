package net.imglib2.ops.operation.unary.metadata;

import net.imglib2.meta.ImageMetadata;
import net.imglib2.ops.UnaryOperation;

public class CopyImageMetadata< M extends ImageMetadata > implements UnaryOperation< M, M >
{

	@Override
	public M compute( M input, M output )
	{
		output.setValidBits( input.getValidBits() );
		output.setCompositeChannelCount( input.getCompositeChannelCount() );

		for ( int c = 0; c < output.getCompositeChannelCount(); c++ )
		{
			output.setChannelMinimum( c, input.getChannelMinimum( c ) );
			output.setChannelMaximum( c, input.getChannelMaximum( c ) );
		}

		output.initializeColorTables( input.getColorTableCount() );
		for ( int n = 0; n < input.getColorTableCount(); n++ )
		{
			output.setColorTable( input.getColorTable16( n ), n );
			output.setColorTable( input.getColorTable8( n ), n );
		}

		return output;
	}

	@Override
	public UnaryOperation< M, M > copy()
	{
		return new CopyImageMetadata< M >();
	}

}
