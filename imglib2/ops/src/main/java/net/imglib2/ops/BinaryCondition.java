package net.imglib2.ops;


public class BinaryCondition<N extends Neighborhood<?>, T> implements Condition<N> {

	private Function<N,T> f1;
	private Function<N,T> f2;
	private T f1Val;
	private T f2Val;
	private BinaryRelation<T> relation;

	public BinaryCondition(Function<N,T> f1, Function<N,T> f2, BinaryRelation<T> relation) {
		this.f1 = f1;
		this.f2 = f2;
		this.f1Val = f1.createVariable();
		this.f2Val = f2.createVariable();
		this.relation = relation;
	}
	
	@Override
	public boolean isTrue(N neigh) {
		f1.evaluate(neigh, f1Val);
		f2.evaluate(neigh, f2Val);
		return relation.holds(f1Val,f2Val);
	}
	
}
