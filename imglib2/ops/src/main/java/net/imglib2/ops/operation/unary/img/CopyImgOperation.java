package net.imglib2.ops.operation.unary.img;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.Type;

public class CopyImgOperation<T extends Type<T>> implements
		UnaryOperation<Img<T>, Img<T>> {

	public CopyImgOperation() {

	}

	@Override
	public Img<T> compute(Img<T> input, Img<T> output) {
		Cursor<T> c1 = input.cursor();
		Cursor<T> c2 = output.cursor();
		while ((c1.hasNext() && c2.hasNext())) {
			c1.fwd();
			c2.fwd();
			c2.get().set(c1.get());
		}
		
		return output;
	}

	@Override
	public CopyImgOperation<T> copy() {
		return new CopyImgOperation<T>();
	}

}
