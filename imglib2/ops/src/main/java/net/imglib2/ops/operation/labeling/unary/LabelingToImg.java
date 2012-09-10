package net.imglib2.ops.operation.labeling.unary;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingMapping;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class LabelingToImg< L extends Comparable< L >, T extends RealType< T >> implements UnaryOperation< Labeling< L >, Img< T >>
{

	@Override
	public Img< T > compute( Labeling< L > op, Img< T > r )
	{
		T ref = r.firstElement().createVariable();
		final Cursor< T > rc = r.cursor();
		final Cursor< LabelingType< L >> opc = op.cursor();
		LabelingMapping< L > map = op.firstElement().getMapping();
		while ( opc.hasNext() )
		{
			opc.fwd();
			rc.fwd();

			int idx = map.indexOf( opc.get().getLabeling() );
			if ( idx < ref.getMinValue() )
				idx = ( int ) ref.getMinValue();
			else if ( idx > ref.getMaxValue() )
				idx = ( int ) ref.getMaxValue();

			rc.get().setReal( idx );

		}
		return r;
	}

	@Override
	public UnaryOperation< Labeling< L >, Img< T >> copy()
	{
		return new LabelingToImg< L, T >();
	}
}
