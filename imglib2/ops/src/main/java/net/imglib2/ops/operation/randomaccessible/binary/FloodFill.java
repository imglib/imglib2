package net.imglib2.ops.operation.randomaccessible.binary;

import java.util.Arrays;
import java.util.LinkedList;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.numeric.IntegerType;

public final class FloodFill< T extends IntegerType< T >, K extends RandomAccessible< T > & IterableInterval< T >> implements BinaryOperation< K, Localizable, K >
{

	private final ConnectedType m_type;

	public FloodFill( ConnectedType type )
	{
		m_type = type;
	}

	/**
	 * 
	 * @param r
	 *            The segmentation image.
	 * @param op0
	 *            Source intensity image.
	 * @param op1
	 *            Start position.
	 * @return
	 */
	@Override
	public final K compute( final K op0, final Localizable op1, final K r )
	{
		final long[] op1pos = new long[ op1.numDimensions() ];
		op1.localize( op1pos );
		compute( op0, op1pos, r );

		return r;
	}

	/**
	 * 
	 * @param r
	 *            The segmentation image.
	 * @param op0
	 *            Source intensity image.
	 * @param op1
	 *            Start position.
	 */
	public final void compute( K op0, final long[] op1, final K r )
	{
		final RandomAccess< T > rc = r.randomAccess();
		final RandomAccess< T > op0c = op0.randomAccess();
		op0c.setPosition( op1 );
		final T floodVal = op0c.get().copy();
		final LinkedList< long[] > q = new LinkedList< long[] >();
		q.addFirst( op1.clone() );
		long[] pos, nextPos;
		long[] perm = new long[ r.numDimensions() ];
		while ( !q.isEmpty() )
		{
			pos = q.removeLast();
			rc.setPosition( pos );
			if ( rc.get().compareTo( floodVal ) == 0 )
			{
				continue;
			}
			op0c.setPosition( pos );
			if ( op0c.get().compareTo( floodVal ) == 0 )
			{
				// set new label
				rc.get().set( floodVal );
				switch ( m_type )
				{
				case EIGHT_CONNECTED:
					Arrays.fill( perm, -1 );
					int i = r.numDimensions() - 1;
					boolean add;
					while ( i > -1 )
					{
						nextPos = pos.clone();
						add = true;
						// Modify position
						for ( int j = 0; j < r.numDimensions(); j++ )
						{
							nextPos[ j ] += perm[ j ];
							// Check boundaries
							if ( nextPos[ j ] < 0 || nextPos[ j ] >= r.dimension( j ) )
							{
								add = false;
								break;
							}
						}
						if ( add )
						{
							q.addFirst( nextPos );
						}
						// Calculate next permutation
						for ( i = perm.length - 1; i > -1; i-- )
						{
							if ( perm[ i ] < 1 )
							{
								perm[ i ]++;
								for ( int j = i + 1; j < perm.length; j++ )
								{
									perm[ j ] = -1;
								}
								break;
							}
						}
					}
					break;
				case FOUR_CONNECTED:
				default:
					for ( int j = 0; j < r.numDimensions(); j++ )
					{
						if ( pos[ j ] + 1 < r.dimension( j ) )
						{
							nextPos = pos.clone();
							nextPos[ j ]++;
							q.addFirst( nextPos );
						}
						if ( pos[ j ] - 1 >= 0 )
						{
							nextPos = pos.clone();
							nextPos[ j ]--;
							q.addFirst( nextPos );
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public BinaryOperation< K, Localizable, K > copy()
	{
		return new FloodFill< T, K >( m_type );
	}
}
