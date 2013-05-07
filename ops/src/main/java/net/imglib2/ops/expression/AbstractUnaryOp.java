package net.imglib2.ops.expression;

import net.imglib2.Sampler;

public abstract class AbstractUnaryOp< O, I1 > implements UnaryOp< O, I1 >
{
	protected Sampler< O > output;

	protected final Port< O > outputPort = new Port< O >()
	{
		@Override
		public void set( final Sampler< O > sampler )
		{
			output = sampler;
		}

		@Override
		public void setConst( final O t )
		{
			output = new Const< O >( t );
		}
	};

	protected Sampler< I1 > input1;

	protected final Port< I1 > input1Port = new Port< I1 >()
	{
		@Override
		public void set( final Sampler< I1 > sampler )
		{
			input1 = sampler;
		}

		@Override
		public void setConst( final I1 t )
		{
			input1 = new Const< I1 >( t );
		}
	};

	public AbstractUnaryOp()
	{
		this( null, null );
	}

	public AbstractUnaryOp(  final Sampler< O > output, final Sampler< I1 > input1 )
	{
		this.output = output;
		if ( input1 instanceof PortRef )
		{
			this.input1 = null;
			( ( PortRef< I1 > ) input1 ).assign( input1Port );
		}
		else
			this.input1 = input1;
	}

	protected AbstractUnaryOp( final AbstractUnaryOp< O, I1 > expression )
	{
		this.output = expression.output == null ? null : expression.output.copy();
		this.input1 = expression.input1 == null ? null : expression.input1.copy();
	}


	@Override
	public Port< O > output()
	{
		return outputPort;
	}

	@Override
	public Port< I1 > input1()
	{
		return input1Port;
	}
}
