package mpicbg.imglib.algorithm.math.operation;

import java.util.ArrayList;
import java.util.List;

import mpicbg.imglib.type.numeric.NumericType;

public class TypeAddition <S extends NumericType<S>> implements TypeOperation<S, S>
{

	private final ArrayList<S> factors;
	private final S type;
	
	public TypeAddition(S inType)
	{
		factors = new ArrayList<S>();
		type = inType.clone();
		type.setOne();
	}
	
	public void addFactor()
	{
		addFactor(type);
	}
	
	public void addFactor(S s)
	{
		factors.add(s.clone());
	}
	
	public void setFactor(int i, S s)
	{
		factors.get(i).set(s);
	}
	
	@Override
	public int maxArgs() {
		return -1;
	}

	@Override
	public int minArgs() {
		return 1;
	}

	@Override
	public S operate(List<S> typeList) {
		S output = typeList.get(0).createVariable();
		S scale = output.clone();
		int i = 0;
		output.setZero();
		
		for (S s : typeList)
		{			
			scale.set(s);
			scale.mul(factors.get(i++));
			output.add(scale);
		}
		
		return output;
	}
	
	@Override
	public boolean ready() {
		return true;
	}

	@Override
	public void reset() {
		factors.clear();	
	}
	
	
	
}