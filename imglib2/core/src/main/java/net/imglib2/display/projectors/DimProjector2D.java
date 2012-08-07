package net.imglib2.display.projectors;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;


public class DimProjector2D<A, B> extends Abstract2DProjector<A, B> {

        protected final Converter<ProjectedDimSampler<A>, B> converter;
        protected final IterableInterval<B> target;
        protected final RandomAccessible<A> source;
        
        protected final int dimX;
        protected final int dimY;

        private final int X = 0;
        private final int Y = 1;
        private final int projectedDimension;

        private final ProjectedDimSamplerImpl<A> projectionSampler;

        // min and max USED position
        private long projectedDimMinPos;
        private long projectedDimMaxPos;

        public DimProjector2D(final int dimX,
                        final int dimY, final RandomAccessible<A> source,
                        final IterableInterval<B> target,
                        final Converter<ProjectedDimSampler<A>, B> converter,
                        final int projectedDimension,
                        final long[] projectedPositions) {

                super(source.numDimensions());

                
                this.dimX = dimX;
                this.dimY = dimY;
                this.target = target;
                this.source = source;
                this.converter = converter;
                this.projectedDimension = projectedDimension;

                // get min and max of the USED part of the projection dim
                projectedDimMinPos = Long.MAX_VALUE;
                projectedDimMaxPos = Long.MIN_VALUE;
                for (long pos : projectedPositions) {
                        if (pos < projectedDimMinPos) {
                                projectedDimMinPos = pos;
                        }
                        if (pos > projectedDimMaxPos) {
                                projectedDimMaxPos = pos;
                        }
                }

                projectionSampler = new SelectiveProjectedDimSampler<A>(
                                projectedDimension, projectedPositions);
        }

        public DimProjector2D(final int dimX,
                        final int dimY,
                        final RandomAccessibleInterval<A> source,
                        final IterableInterval<B> target,
                        final Converter<ProjectedDimSampler<A>, B> converter,
                        final int projectedDimension) {
        	
        		super(source.numDimensions());

                this.dimX = dimX;
                this.dimY = dimY;
                this.target = target;
                this.source = source;
                this.converter = converter;
                this.projectedDimension = projectedDimension;

                // set min and max of the projection dim
                projectedDimMinPos = source.min(projectedDimension);
                projectedDimMaxPos = source.max(projectedDimension);

                projectionSampler = new IntervalProjectedDimSampler<A>(
                                projectedDimension, projectedDimMinPos,
                                projectedDimMaxPos);
        }

        @Override
        public void map() {
                // fix interval for all dimensions
                for (int d = 0; d < position.length; ++d)
                        min[d] = max[d] = position[d];

                min[dimX] = target.min(X);
                min[dimY] = target.min(Y);
                max[dimX] = target.max(X);
                max[dimY] = target.max(Y);
                min[projectedDimension] = projectedDimMinPos;
                max[projectedDimension] = projectedDimMaxPos;

                // get tailored random access
                final FinalInterval sourceInterval = new FinalInterval(min, max);
                final Cursor<B> targetCursor = target.localizingCursor();
                final RandomAccess<A> sourceRandomAccess = source
                                .randomAccess(sourceInterval);
                sourceRandomAccess.setPosition(position);


                projectionSampler.setRandomAccess(sourceRandomAccess);

                if (numDimensions > 1)
                        while (targetCursor.hasNext()) {
                                projectionSampler.reset();

                                final B b = targetCursor.next();
                                sourceRandomAccess
                                                .setPosition(targetCursor
                                                                .getLongPosition(X),
                                                                dimX);
                                sourceRandomAccess
                                                .setPosition(targetCursor
                                                                .getLongPosition(Y),
                                                                dimY);

                                converter.convert(projectionSampler, b);
                        }
                else
                        while (targetCursor.hasNext()) {
                                projectionSampler.reset();

                                final B b = targetCursor.next();
                                sourceRandomAccess
                                                .setPosition(targetCursor
                                                                .getLongPosition(X),
                                                                dimX);

                                converter.convert(projectionSampler, b);
                        }
        }

}
