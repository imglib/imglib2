package net.imglib2.ops.operation.img.unary;

import net.imglib2.img.Img;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;

public class ImgUnaryOutputOpWrapper< T extends RealType< T >> implements UnaryOutputOperation< Img< T >, Img< T >>
{

	private final UnaryOperation< Img< T >, Img< T >> m_op;

	public ImgUnaryOutputOpWrapper( UnaryOperation< Img< T >, Img< T >> op )
	{
		m_op = op;
	}

	@Override
	public Img< T > compute( Img< T > input, Img< T > output )
	{
		return m_op.compute( input, output );
	}

	@Override
	public UnaryOutputOperation< Img< T >, Img< T >> copy()
	{
		return new ImgUnaryOutputOpWrapper< T >( m_op.copy() );
	}

	@Override
	public Img< T > createEmptyOutput( Img< T > in )
	{
		return in.factory().create( in, in.firstElement() );
	}

	@Override
	public Img< T > compute( Img< T > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}

	public < C > UnaryOutputOperation< C, Img< T >> concatenate( final UnaryOutputOperation< C, Img< T >> op )
	{
		return new ConcatenatedUnaryOutputOperation< C, Img< T >, Img< T >>( op, this );
	}

	public < C > UnaryOutputOperation< Img< T >, C > preConcatenate( final UnaryOutputOperation< Img< T >, C > op )
	{
		return new ConcatenatedUnaryOutputOperation< Img< T >, Img< T >, C >( this, op );
	}
}

class ConcatenatedUnaryOutputOperation< I, B, O > implements UnaryOutputOperation< I, O >
{

	private final UnaryOutputOperation< I, B > m_first;

	private final UnaryOutputOperation< B, O > m_rest;

	public ConcatenatedUnaryOutputOperation( UnaryOutputOperation< I, B > first, UnaryOutputOperation< B, O > rest )
	{
		super();
		m_first = first;
		m_rest = rest;
	}

	@Override
	public O compute( I input, O output )
	{
		return m_rest.compute( m_first.compute( input ), output );
	}

	@Override
	public UnaryOutputOperation< I, O > copy()
	{
		return new ConcatenatedUnaryOutputOperation< I, B, O >( m_first.copy(), m_rest.copy() );
	}

	@Override
	public O createEmptyOutput( I in )
	{
		return m_rest.createEmptyOutput( m_first.createEmptyOutput( in ) );
	}

	@Override
	public O compute( I in )
	{
		return m_rest.compute( m_first.compute( in ) );
	}
};
