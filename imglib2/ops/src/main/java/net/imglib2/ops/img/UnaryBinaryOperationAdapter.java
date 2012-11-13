///*
// * #%L
// * ImgLib2: a general-purpose, multidimensional image processing library.
// * %%
// * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
// * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
// * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
// * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
// * %%
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// * 
// * 1. Redistributions of source code must retain the above copyright notice,
// *    this list of conditions and the following disclaimer.
// * 2. Redistributions in binary form must reproduce the above copyright notice,
// *    this list of conditions and the following disclaimer in the documentation
// *    and/or other materials provided with the distribution.
// * 
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// * 
// * The views and conclusions contained in the software and documentation are
// * those of the authors and should not be interpreted as representing official
// * policies, either expressed or implied, of any organization.
// * #L%
// */
//package net.imglib2.ops.img;
//
//import net.imglib2.ops.buffer.BufferFactory;
//import net.imglib2.ops.operation.BinaryOperation;
//import net.imglib2.ops.operation.UnaryOperation;
//
///**
// * @author Christian Dietz (University of Konstanz)
// * 
// * @param <A>
// * @param <B>
// * @param <C>
// * @param <D>
// */
//public class UnaryBinaryOperationAdapter< A, B, C, D > implements UnaryOperation< A, D >, DoubleBufferedOperation< B, C >
//{
//
//	private final BinaryOperation< B, C, D > binaryOp;
//
//	private final UnaryOperation< A, B > unaryOp1;
//
//	private final UnaryOperation< A, C > unaryOp2;
//
//	private BufferFactory< B > buf1;
//
//	private BufferFactory< C > buf2;
//
//	public UnaryBinaryOperationAdapter( UnaryOperation< A, B > op1, UnaryOperation< A, C > op2, BinaryOperation< B, C, D > binaryOp, BufferFactory< B > buf1, BufferFactory< C > buf2 )
//	{
//		this.buf1 = buf1;
//		this.buf2 = buf2;
//		this.binaryOp = binaryOp;
//		this.unaryOp1 = op1;
//		this.unaryOp2 = op2;
//	}
//
//	public D compute( A input, D output )
//	{
//		return binaryOp.compute( unaryOp1.compute( input, buf1.instantiate() ), unaryOp2.compute( input, buf2.instantiate() ), output );
//	};
//
//	@Override
//	public UnaryOperation< A, D > copy()
//	{
//		return new UnaryBinaryOperationAdapter< A, B, C, D >( unaryOp1.copy(), unaryOp2.copy(), binaryOp.copy(), buf1, buf2 );
//	}
//
//	@Override
//	public void setBufferFactoryA( BufferFactory< B > buffer )
//	{
//		this.buf1 = buffer;
//	}
//
//	@Override
//	public void setBufferFactoryB( BufferFactory< C > buffer )
//	{
//		this.buf2 = buffer;
//	}
//
//	@Override
//	public BufferFactory< B > bufferFactoryA()
//	{
//		return this.buf1;
//	}
//
//	@Override
//	public BufferFactory< C > bufferFactoryB()
//	{
//		return this.buf2;
//	}
//}
