package net.imglib2.ops.measure;

/*

want to be able to build without passing func everywhere

so in general it's
  Mean mean = new Mean(new Sum(), new ElemCount());
  and then
  mean.calculate(function, region, output)

does this make construction get messy passing all these temps around?
    in general the construction is very difficult
	
	could have a MeasureSet that starts out empty
	set.add(Mean.class);
	  the set figures out what things the Mean needs (like Sum and ElemCount),
	  creates them, and passes them to Mean's constructor
	  (like curtis said we've done this kind of work for our plugins already)
  then calc all measures in the set (order does not matter)
  
  btw how would you construct a WeightedSum? How does the ctor know about
  the weights? Do you first register them with the set? What if you have
  two different set of weights in the set?
  
  could values get cached? this would solve Aivar's chained function idea.
  but the cost of generating a key and caching and retrieving might be
  too prohibitive as it is.
  
  anyhow chaining idea might be supportable by having all measures support
  the preproc(), visit(), postproc(). the dependencies of functions could
  be figured out and all of same level of dep can share the one visit. The
  MeasurementSet::measure() method would start iters for each level one at
  a time starting with the first. This is simple and would work.

  note: I can measure a weighted average by providing weights externally.
  But I can't think of any method that allows a new Measure to base itself
  on a weighted average that can somehow provide weights with no arg ctor

  what if you set up measure set empty. provide it with a Weights class that
  has to be a Measurement. Then when other Measurements are added if they
  need a Weights in their constructor they just use it. This would allow a
  single set of Weights but not more than one. We need a general solution.

  (Note: to last issue see TODOs in NewMeasurementService)
*/

public class MeasureIdeas {

}
