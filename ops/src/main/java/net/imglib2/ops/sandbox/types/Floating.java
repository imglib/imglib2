package net.imglib2.ops.sandbox.types;

public interface Floating<T> extends Fractional<T> {

	void PI(T result);

	void E(T result);

	void exp(T result);

	void sqrt(T result);

	void log(T result);

	void pow(T b, T result);

	void logBase(T b, T result);

	void sin(T result);

	void cos(T result);

	void tan(T result);

	void asin(T result);

	void acos(T result);

	void atan(T result);

	void sinh(T result);

	void cosh(T result);

	void tanh(T result);

	void asinh(T result);

	void acosh(T result);

	void atanh(T result);

}
