package net.imglib2.ops.sandbox;


import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class TMP {

	public <V extends ComplexType<V>> void complex(V arg) {
		//
	}
	
	public <T extends RealType<T>> void real(T arg) {
		complex(arg);
	}
	
	public <V extends ComplexType<V>> void complexOp(ComplexOp<V> op) {
		
	}
	
	public <T extends RealType<T>> void realOp(RealOp<T> op) {
		complexOp(op);
	}
	
	public class ComplexOp<V extends ComplexType<V>> { }
	
	public class RealOp<T extends RealType<T>> extends ComplexOp<T> { }

	private class Assignment<K extends ComplexType<K>> {
		ComplexOp<K> op;
		public Assignment(ComplexOp<K> op) {
			this.op = op;
		}
		public void run() {};
	}
	
	public static void main(String[] args) {
		TMP tmp = new TMP();
		
		ComplexOp<ComplexDoubleType> cOp = null;
		
		Assignment<ComplexDoubleType> c = tmp.new Assignment<ComplexDoubleType>(cOp);
		c.run();
		
		RealOp<FloatType> rOp = null;
		
		Assignment<FloatType> r = tmp.new Assignment<FloatType>(rOp);
		r.run();
	}
}
