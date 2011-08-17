package net.imglib2.ops;

public interface Function<INPUT, OUTPUT> {
	void evaluate(INPUT input, OUTPUT output);
	OUTPUT createVariable();
}

