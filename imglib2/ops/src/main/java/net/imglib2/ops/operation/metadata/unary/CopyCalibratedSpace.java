package net.imglib2.ops.operation.metadata.unary;

import net.imglib2.Interval;
import net.imglib2.meta.CalibratedSpace;
import net.imglib2.ops.operation.UnaryOperation;

public class CopyCalibratedSpace< CS extends CalibratedSpace > implements UnaryOperation< CS, CS >
{
	private Interval interval;

	public CopyCalibratedSpace()
	{
		interval = null;
	}

	public CopyCalibratedSpace( Interval interval )
	{
		this.interval = interval;
	}

	@Override
	public CS compute( CS input, CS output )
	{

		for ( int d = 0; d < input.numDimensions(); d++ )
		{
			if ( interval != null && interval.dimension( d ) == 1 )
				continue;

			output.setAxis( input.axis( d ), d );
			output.setCalibration( input.calibration( d ), d );
		}

		return output;
	}

	@Override
	public UnaryOperation< CS, CS > copy()
	{
		return new CopyCalibratedSpace< CS >();
	}

}
