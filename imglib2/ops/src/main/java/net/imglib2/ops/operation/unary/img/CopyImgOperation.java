package net.imglib2.ops.operation.unary.img;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.Type;

public class CopyImgOperation<T extends Type<T>> implements UnaryOperation<Img<T>, Img<T>> {

	public CopyImgOperation() {
		
	}
	
	@Override
	public void compute(Img<T> input, Img<T> output) {
		Cursor<T> c1 = input.cursor();
		Cursor<T> c2 = output.cursor();
		while ((c1.hasNext() && c2.hasNext())) {
			c1.fwd();
			c2.fwd();
			c2.get().set(c1.get());
		}
	}

	@Override
	public Img<T> createOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnaryOperation<Img<T>, Img<T>> duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Img<T> createOutput(Img<T> in) {
		// TODO Auto-generated method stub
		return in.factory().create(in, in.firstElement());
	}

}
