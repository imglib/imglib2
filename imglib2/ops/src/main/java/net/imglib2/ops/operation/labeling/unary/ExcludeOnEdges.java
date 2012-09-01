package net.imglib2.ops.operation.labeling.unary;

import java.util.HashSet;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;

public class ExcludeOnEdges< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	@Override
	public Labeling< L > compute( Labeling< L > inLabeling, Labeling< L > outLabeling )
	{

		if ( inLabeling.numDimensions() != 2 ) { throw new IllegalArgumentException( "Exclude on edges works only on two dimensional images" ); }

		long[] dims = new long[ inLabeling.numDimensions() ];
		inLabeling.dimensions( dims );

		HashSet< List< L >> indices = new HashSet< List< L >>();

		RandomAccess< LabelingType< L >> outRndAccess = outLabeling.randomAccess();
		RandomAccess< LabelingType< L >> inRndAccess = inLabeling.randomAccess();

		Cursor< LabelingType< L >> cur = inLabeling.cursor();

		long[] pos = new long[ inLabeling.numDimensions() ];

		for ( int d = 0; d < dims.length; d++ )
		{

			for ( int i = 0; i < Math.pow( 2, dims.length - 1 ); i++ )
			{

				int offset = 0;
				for ( int dd = 0; dd < dims.length; dd++ )
				{
					if ( dd == d )
					{
						offset++;
						continue;
					}
					pos[ dd ] = ( i % Math.pow( 2, dd - offset + 1 ) == 0 ) ? 0 : dims[ dd ] - 1;
				}

				pos[ d ] = 0;
				for ( int k = 0; k < dims[ d ]; k++ )
				{
					pos[ d ] = k;
					inRndAccess.setPosition( pos );

					if ( 0 != inRndAccess.get().getLabeling().size() )
					{
						indices.add( inRndAccess.get().getLabeling() );
					}
				}
			}
		}

		while ( cur.hasNext() )
		{
			cur.fwd();
			if ( !indices.contains( cur.get().getLabeling() ) )
			{
				cur.localize( pos );
				outRndAccess.setPosition( pos );
				outRndAccess.get().setLabeling( cur.get().getLabeling() );
			}
		}
		return outLabeling;

	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new ExcludeOnEdges< L >();
	}

}
