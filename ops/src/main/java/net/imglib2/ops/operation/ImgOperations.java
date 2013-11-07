package net.imglib2.ops.operation;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.ops.img.UnaryObjectFactory;
import net.imglib2.type.numeric.RealType;

/**
 * Utility methods for operations on {@link Img}s, e.g. for conversion etc.
 */
public final class ImgOperations
{
	private ImgOperations()
	{
		// utility class
	}

	/**
	 * Wraps a unary operation based on {@link RandomAccessibleInterval} as
	 * input and output.
	 * 
	 * @param op
	 * @param outType
	 * @return a {@link UnaryOutputOperation} with ImgPlus as input and ouput
	 */
	public static < T extends RealType< T >, V extends RealType< V > > UnaryOutputOperation< ImgPlus< T >, ImgPlus< V > > wrapRA( UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< V >> op, V outType )
	{
		return new ImgPlusToImgPlusRAIWrapperOp< T, V >( op, outType );
	}

	/**
	 * Wraps a unary operation based on {@link IterableInterval} as input and
	 * output.
	 * 
	 * @param op
	 * @param outType
	 * @return a {@link UnaryOutputOperation} with ImgPlus as input and ouput
	 */
	public static < T extends RealType< T >, V extends RealType< V >> UnaryOutputOperation< ImgPlus< T >, ImgPlus< V > > wrapII( UnaryOperation< IterableInterval< T >, IterableInterval< V >> op, V outType )
	{
		return new ImgPlusToImgPlusIIWrapperOp< T, V >( op, outType );
	}

	/*
	 * Simple wrapper class to wrap UnaryOperations to UnaryOutputOperations
	 * which run on ImgPlus basis
	 */
	private static class ImgPlusToImgPlusRAIWrapperOp< T extends RealType< T >, V extends RealType< V >> implements UnaryOutputOperation< ImgPlus< T >, ImgPlus< V >>
	{

		private UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< V >> m_op;

		private V m_outType;

		/**
		 * @param op
		 * @param outType
		 */
		public ImgPlusToImgPlusRAIWrapperOp( final UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< V >> op, final V outType )
		{
			m_op = op;
			m_outType = outType;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ImgPlus< V > compute( final ImgPlus< T > input, final ImgPlus< V > output )
		{
			m_op.compute( input, output );
			return output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UnaryObjectFactory< ImgPlus< T >, ImgPlus< V >> bufferFactory()
		{
			return new ImgPlusFactory< T, V >( m_outType );
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UnaryOutputOperation< ImgPlus< T >, ImgPlus< V >> copy()
		{
			return new ImgPlusToImgPlusRAIWrapperOp< T, V >( m_op.copy(), m_outType );
		}

	}

	/*
	 * A {@link UnaryObjectFactory} producing new empty images (ImgPlus)F.
	 */
	private static class ImgPlusFactory< T extends RealType< T >, V extends RealType< V >> implements UnaryObjectFactory< ImgPlus< T >, ImgPlus< V >>
	{

		private final V m_outType;

		public ImgPlusFactory( final V outType )
		{
			this.m_outType = outType;
		}

		@Override
		public ImgPlus< V > instantiate( final ImgPlus< T > a )
		{
			Img< V > img;
			try
			{
				img = a.factory().imgFactory( m_outType ).create( a, m_outType );
			}
			catch ( IncompatibleTypeException e )
			{
				throw new RuntimeException( e );
			}
			return new ImgPlus< V >( img, a );
		}

	}

	/*
	 * Simple wrapper class to wrap UnaryOperations to UnaryOutputOperations
	 * which run on ImgPlus basis
	 */
	private static class ImgPlusToImgPlusIIWrapperOp< T extends RealType< T >, V extends RealType< V >> implements UnaryOutputOperation< ImgPlus< T >, ImgPlus< V >>
	{

		private UnaryOperation< IterableInterval< T >, IterableInterval< V >> m_op;

		private V m_outType;

		/**
		 * @param op
		 * @param outType
		 */
		public ImgPlusToImgPlusIIWrapperOp( final UnaryOperation< IterableInterval< T >, IterableInterval< V >> op, final V outType )
		{
			m_op = op;
			m_outType = outType;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ImgPlus< V > compute( final ImgPlus< T > input, final ImgPlus< V > output )
		{
			m_op.compute( input, output );
			return output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UnaryObjectFactory< ImgPlus< T >, ImgPlus< V >> bufferFactory()
		{
			return new ImgPlusFactory< T, V >( m_outType );
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UnaryOutputOperation< ImgPlus< T >, ImgPlus< V >> copy()
		{
			return new ImgPlusToImgPlusIIWrapperOp< T, V >( m_op.copy(), m_outType );
		}

	}

}
