package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;
import mpicbg.imglib.type.numeric.RealType;

/** Condition - a boolean condition */
public interface Condition<T extends RealType<T>>
{
	/** the core method. a boolean value specifiying whether a function at a position satisfies a condition. note there is no limit
	 * on what kind of conditions can be satisfied. conditions can check spatially around the passed in position. conditions can be
	 * composed with ands/ors/nots into more complex conditions. */
	boolean isSatisfied(IntegralScalarFunction<T> function, int[] position);

	/** for performance optimization */
	void initEvaluationState();
	
	/** for performance optimization */
	boolean functionWasFullyEvaluated();

	/** for performance optimization */
	double getLastFunctionEvaluation();
}
