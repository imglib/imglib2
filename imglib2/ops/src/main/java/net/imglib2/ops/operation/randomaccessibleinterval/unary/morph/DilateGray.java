package net.imglib2.ops.operation.randomaccessibleinterval.unary.morph;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * Dilate operation on gray-level.
 * 
 * @author schoenenf
 * 
 * @param <T>
 */
public class DilateGray< T extends RealType< T >, I extends RandomAccessibleInterval< T > & IterableInterval< T >> implements UnaryOperation< I, I >
{

	private final long[][] m_struc;

	/**
	 * 
	 * @param structuringElement
	 */
	public DilateGray( final long[][] structuringElement )
	{
		m_struc = structuringElement;
	}

	@Override
	public I compute( final I input, final I output )
	{
		final T v = input.firstElement().createVariable();
		final StructuringElementCursor< T > inStructure = new StructuringElementCursor< T >( Views.extendValue( input, v ).randomAccess(), m_struc );
		final Cursor< T > out = output.localizingCursor();
		double m;
		while ( out.hasNext() )
		{
			out.next();
			inStructure.relocate( out );
			inStructure.next();
			m = inStructure.get().getRealDouble();
			while ( inStructure.hasNext() )
			{
				inStructure.next();
				m = Math.max( m, inStructure.get().getRealDouble() );
			}
			out.get().setReal( m );
		}
		return output;
	}

	@Override
	public DilateGray< T, I > copy()
	{
		return new DilateGray< T, I >( m_struc );
	}
}
