package imglib.ops.example.rev3.constraints;

import java.util.ArrayList;

import imglib.ops.example.rev3.condition.Condition;
import imglib.ops.example.rev3.function.IntegerIndexedScalarFunction;

public class Constraints
{
	private ArrayList<ConstraintEntry> constraints;

	public Constraints()
	{
		constraints = null;
	}
	
	public void addConstraint(IntegerIndexedScalarFunction function, Condition condition)
	{
		if (constraints == null)
			constraints = new ArrayList<ConstraintEntry>();

		constraints.add(new ConstraintEntry(function,condition));
	}
	
	public boolean areSatisfied(int[] position)
	{
		if (constraints == null)
			return true;
		
		for (ConstraintEntry entry : constraints)
		{
			if ( ! entry.condition.isSatisfied(entry.function, position) )
				return false;
		}
		
		return true;
	}
	
	private class ConstraintEntry
	{
		public IntegerIndexedScalarFunction function;
		public Condition condition;
		
		public ConstraintEntry(IntegerIndexedScalarFunction func, Condition cond)
		{
			this.function = func;
			this.condition = cond;
		}
	}
}
