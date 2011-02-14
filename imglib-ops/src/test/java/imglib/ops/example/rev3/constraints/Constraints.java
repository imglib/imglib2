package imglib.ops.example.rev3.constraints;

import java.util.ArrayList;

import imglib.ops.example.rev3.condition.Condition;
import imglib.ops.example.rev3.function.IntegralScalarFunction;

public class Constraints
{
	private ArrayList<IntegralScalarFunction> functions;
	private ArrayList<Condition> conditions;

	public Constraints()
	{
		functions = null;
		conditions = null;
	}
	
	public void addConstraint(IntegralScalarFunction function, Condition condition)
	{
		if (functions == null)
		{
			functions = new ArrayList<IntegralScalarFunction>();
			conditions = new ArrayList<Condition>();
		}
		functions.add(function);
		conditions.add(condition);
	}
	
	public boolean areSatisfied(int[] position)
	{
		if (functions == null)
			return true;
		
		int numConstraints = functions.size();
		
		for (int i = 0; i < numConstraints; i++)
		{
			IntegralScalarFunction function = functions.get(i);
			Condition c = conditions.get(i);
			
			if ( ! c.isSatisfied(function, position) )
				return false;
		}
		
		return true;
	}
}
