package net.imglib2.ops.expression;

import net.imglib2.Sampler;

public abstract class AbstractBinaryOp< O, I1, I2 > implements BinaryOp< O, I1, I2 >
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

	protected Sampler< I2 > input2;

	protected final Port< I2 > input2Port = new Port< I2 >()
	{
		@Override
		public void set( final Sampler< I2 > sampler )
		{
			input2 = sampler;
		}

		@Override
		public void setConst( final I2 t )
		{
			input2 = new Const< I2 >( t );
		}
	};

	public AbstractBinaryOp()
	{
		this( null, null, null );
	}

	public AbstractBinaryOp( final Sampler< O > output, final Sampler< I1 > input1, final Sampler< I2 > input2 )
	{
		this.output = output;
		if ( input1 instanceof PortRef )
		{
			this.input1 = null;
			( ( PortRef< I1 > ) input1 ).assign( input1Port );
		}
		else
			this.input1 = input1;
		if ( input2 instanceof PortRef )
		{
			this.input2 = null;
			( ( PortRef< I2 > ) input2 ).assign( input2Port );
		}
		else
			this.input2 = input2;
	}

	protected AbstractBinaryOp( final AbstractBinaryOp< O, I1, I2 > expression )
	{
		this.output = expression.output == null ? null : expression.output.copy();
		this.input1 = expression.input1 == null ? null : expression.input1.copy();
		this.input2 = expression.input2 == null ? null : expression.input2.copy();
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

	@Override
	public Port< I2 > input2()
	{
		return input2Port;
	}
}