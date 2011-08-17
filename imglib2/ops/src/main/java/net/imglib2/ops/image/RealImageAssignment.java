package net.imglib2.ops.image;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.Condition;
import net.imglib2.ops.DiscreteIterator;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.type.numeric.RealType;

// In old AssignOperation could do many things
// - set conditions on each input and output image
//     Now this can be done by creating a complex Condition
// - set regions of input and output
//     Now this can be done by creating a complex Condition
// - interrupt from another thread
//     still to do
// - observe the iteration
//     still to do
// regions in same image could be handled by a translation function that
//   transforms from one space to another
// regions in different images can also be handled this way
//   a translation function takes a function and a coord transform
// now also these regions, if shape compatible, can be composed into a N+1
//   dimensional space and handled as one dataset

public class RealImageAssignment {

	private RandomAccess<? extends RealType<?>> accessor;
	private DiscreteNeigh neigh;
	private Function<DiscreteNeigh,Real> function;
	private Condition<DiscreteNeigh> condition;
	
	public RealImageAssignment(Img<? extends RealType<?>> image, DiscreteNeigh neigh,
			Function<DiscreteNeigh,Real> function)
	{
		this.accessor = image.randomAccess();
		this.neigh = neigh;
		this.function = function;
		this.condition = null;
	}
	
	public void setCondition(Condition<DiscreteNeigh> condition) {
		this.condition = condition;
	}
	
	// TODO
	// - add listeners (like progress indicators, stat collectors, etc.)
	// - make interruptible
	
	public void assign() {
		DiscreteNeigh region = neigh.duplicate();
		Real output = function.createVariable();
		DiscreteIterator iter = neigh.getIterator();
		while (iter.hasNext()) {
			iter.fwd();
			region.moveTo(iter.getPosition());
			boolean proceed = (condition == null) || (condition.isTrue(region));
			if (proceed) {
				function.evaluate(region, output);
				accessor.setPosition(iter.getPosition());
				accessor.get().setReal(output.getReal());
			}
		}
		
	}

}
