package net.imglib2.ops;


public class UnaryCondition<N extends Neighborhood<?>, T> implements Condition<N> {

	private Function<N,T> f1;
	private T f1Val;
	private UnaryRelation<T> relation;

	public UnaryCondition(Function<N,T> f1, UnaryRelation<T> relation) {
		this.f1 = f1;
		this.f1Val = f1.createVariable();
		this.relation = relation;
	}
	
	@Override
	public boolean isTrue(N neigh) {
		f1.evaluate(neigh, f1Val);
		return relation.holds(f1Val);
	}
	
}
