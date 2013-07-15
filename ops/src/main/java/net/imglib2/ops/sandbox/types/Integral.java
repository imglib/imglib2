package net.imglib2.ops.sandbox.types;


public interface Integral<T> extends Real<T>, Enumerable<T>
{

//Later: 	void quotient(T b, T result);

//Later: 	void remainder(T b, T result);

//Later: 	void quotRem(T b, T quotResult, T remResult);

	void div(T b, T result);

	void mod(T b, T result);

	void divMod(T b, T divResult, T modResult);

	void toInteger(Integer result);

	/**
	 * From Functions
	 * 
	 * @return
	 */
	boolean isEven();

	/**
	 * From Functions
	 * 
	 * @return
	 */
	boolean isOdd();

	/**
	 * From Functions
	 * 
	 * @return
	 */
	// later
	// void gcd(T b, T result);

	/**
	 * From Functions
	 * 
	 * @return
	 */
	// later
	// void lcm(T b, T result);

	/**
	 * From Functions
	 * 
	 * @param result
	 */
	<Z> void fromIntegral(Number<Z> result);
}
