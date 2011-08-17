package net.imglib2.ops;


public interface Condition<N extends Neighborhood<?>> {
	boolean isTrue(N neigh);
}

