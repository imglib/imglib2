# ReshapeTransform

**Status:** implemented, not committed — 2026-09-11

This is `numpy.reshape` for imglib2 (`Views.reshape`), built as a member of the
integer-transform family that `TransformBuilder`, the view accessors, iteration,
`LoopBuilder` and `net.imglib2.blocks` all understand natively. It is an
alternative to the `ReshapeView` in `../imglib2-reshape-view`, which is an
`IntervalView` over a generic `TransformView`; see its
`dev_notes/reshape-view.md`. That design hid the reshape from `TransformBuilder`
(no bounding box, recompute on every `get()`) and from blocks.

## Why not inside `MixedTransform`

`MixedTransform` computes each image axis as `translation ± one view axis`, or
as a constant (`zero`). That covers inserting and removing size-1 axes, but not
a genuine reshape:

- A **split** makes one image axis depend on several view axes, with non-unit
  strides (`s = i + a·j`).
- A **merge** makes one view axis feed several image axes, through div/mod.

So reshape is a separate class, `transform.integer.ReshapeTransform`.
`MixedTransform` and `Mixed` are unchanged. A singleton-only reshape is still
emitted as a `MixedTransform` (via `ReshapeTransform.toMixed()`). The user chose
this over generalizing `MixedTransform`, which would have touched every consumer
that assumes ±1 and one-to-one mapping.

## Decisions

- **Groups.** Size-1 axes are dropped. The rest are cut into independent groups
  wherever the running products of the two shapes meet; the algorithm is ported
  from `BlockTransform` in the other worktree's `BlockReshapeBenchmark`. A group
  with one image axis (a split, or a copy) is linear and needs no division.
- **Floor semantics.** Division is `floorDiv`/`floorMod`, and the outermost
  image axis of each group is unbounded. Truncating division, as in
  `BlockTransform`, makes the accessor's carry/borrow disagree with absolute
  positioning at negative positions, and cursors park at min−1.
- **Not `Concatenable`.** Two reshapes can be adjacent in `TransformBuilder`'s
  list across an `IntervalView` crop, e.g.
  `reshape(interval(reshape(img,24), [0..11]), 3,4)`, and that pair does not
  compose. `ConcatenateUtils.join` concatenates on class match alone. Instead,
  `concatenate()` checks the dimensions, and `TransformBuilder.simplifyTransforms`
  merges matching neighbours itself.
  - That method now loops until nothing changes: join, merge reshapes, drop
    identity reshapes, turn singleton-only reshapes into Mixed, drop identity
    Mixed.
  - This is needed because `Views.zeroMin` always wraps in a
    `MixedTransformView`, even on a zero-min source. That leaves identity Mixed
    transforms between reshapes.
  - For the same reason, `Views.reshape` only calls `zeroMin` when the source is
    not already zero-min.
- **Dedicated view class.** `ReshapeTransformView` rather than `TransformView`,
  so that `ViewAnalyzer` can recognize it. Like `MixedTransformView`, it collapses
  a directly nested reshape, but only when the dimensions match.
- **Bounding box.** Per group: the view box maps to a flat range, both ends are
  unflattened, the digits below the highest differing one get their full extent.
  This is exact for a single flat run and conservative otherwise. It keeps
  `TransformBuilder`'s bounding box alive, so extensions can be dropped.
- **Iteration fast path** (`IterableTransformBuilder.buildFlatRunIterableInterval`).
  It applies when the view interval's elements form a box of source elements in
  the same flat order. The cursor then forwards to a flat cursor on that box,
  and reports view positions (`FlatIndexCursor`/`FlatIndexLocalizingCursor`,
  originally named `ReshapeCursor`/`ReshapeLocalizingCursor`). The
  transform chain must be one of:
  - `[M]`: a single order-preserving `Mixed` (`isOrderPreserving`): no
    inversion, no permutation, and every unused view axis has extent 1 in the
    interval. This covers singleton-only reshapes, `Views.translate`, `zeroMin`,
    `addDimension` with extent 1, and translated slices.
  - `[M?, R, M?]`: a reshape between order-preserving `Mixed` transforms. The
    interval must be inside R's domain and have as many elements as its image
    bounding box; then the image is exactly that box, in the same order.

  Details:
  - For a reshape, a single flat run in the view is *not* enough. Rows 3–9 of an
    8×15 view of 5×4×6 straddle source rows. The first draft of the tests
    assumed otherwise.
  - Non-flat sources (CellImg) are iterated in flat order with a
    `RandomAccessibleIntervalCursor` over the source box.
  - This changes behaviour beyond reshape. `cursor()` on a translated view is
    now a `FlatIndexCursor`, and `LoopBuilder` takes its cursor path for it.
    That is why the cursors were renamed away from `Reshape…`.
- **LoopBuilder.** `isFastCursor` accepts a `FlatIndexCursor` iff its source
  cursor is fast. `PlanarPlaneSubsetCursor` was also added to the list. That
  changes behaviour for non-reshape single-plane views of a `PlanarImg`.

## Blocks

- **Tier 1.** A reshape applied to an `ArrayImg`, with only converters and
  identity views in between, is removed in `ViewAnalyzer.checkReshape()`. The
  root is replaced by an `ArrayImg` with the reshaped dimensions on the same data.
  This also makes an extension *above* the reshape work.
- **Tier 2.** Everything else goes through `ReshapeCopier`, for the chain
  view →top→ A →R→ B →bottom→ root:
  1. Copy the bounding box in B from the root (plus the bottom permute/invert, if
     any).
  2. Gather the A-box from it. Per group, the offsets are built line by line:
     one division per line, carries within the line. The innermost group becomes
     contiguous runs.
  3. Apply the top permute/invert.
- **Splitting.** A bounding box larger than 4× the piece (and above 4096
  elements) is split. A flat run is cut at the boundaries of its highest
  differing digit; anything else is halved. A box above `maxBufferSize` is
  always split. The limits are constructor parameters on `ViewPrimitiveBlocks`,
  for tests.
- **Fallback, with a message**, for:
  - more than one reshape;
  - a reshape below an extension on a non-`ArrayImg` root (`checkExtensions3`
    used to skip unknown node types silently);
  - unmapped axes on either side of the reshape.
- **Two bugs fixed that existed before this work**, both for `BufferAccess`
  roots, each with a regression test in `ReshapePrimitiveBlocksTest.testBufferAccess`:
  - `PermuteInvert` was given the buffer→array `MemCopy`, which throws
    `ClassCastException`. I confirmed the test fails without the fix.
  - The constant OOB value was a primitive array fed to that same `MemCopy`.
    It is now wrapped with `PrimitiveBlocksUtils.wrapAsBuffer`.
- **Also fixed:** `getTransformedInterval` cast the translation to `int`. It moved
  to `PrimitiveBlocksUtils.transformInterval`, which uses `long`.

## Verification

- `mvn -o clean test` on 2026-09-11, after the flat-run generalization: 1342
  tests, 0 failures, 6 skipped. The skips were already there.
- The reshape tests are:
  - `ReshapeTransformTest` (12): exhaustive in-domain checks and off-domain
    properties on small shapes, and brute-force bounding boxes.
  - `ReshapeRandomAccessTest` (6): 20k-step random walks over every
    `Positionable` overload, checked against `apply`, plus int-overflow cases.
  - `ReshapeViewTest` (21).
  - `ReshapePrimitiveBlocksTest` (13): against `FallbackPrimitiveBlocks` on
    Array, Planar, ragged Cell and buffer roots, with forced splits and
    `threadSafe()`.
  - `view.iteration.FlatRunIterationTest` (9): which cursor each view gets, and
    its positions and values against random access. Covers translate,
    singleton reshapes, `addDimension`, translated slices, `Mixed` above and
    below a reshape, CellImg, and permute/invert (which must not take the fast
    path).
- JMH, same settings as the other worktree (5×100 ms warmup, 5×200 ms
  measurement, 1 fork), on a 300×300×40 `IntType` image. The `BenchmarkList` had
  111 entries after `mvn -o clean test-compile`.

`ReshapeViewBenchmark`, ms/op. "Old" is the other worktree's *after-fast-paths*
table:

| loop | img | flat1d | merge2d | split4d | singleton4d | old flat1d / split4d / singleton4d |
|---|---|---|---|---|---|---|
| flatIterableCursor | ArrayImg | 3.17 | 3.16 | 3.14 | 18.5 | 3.18 / 3.10 / 3.13 |
| flatIterableCursor | CellImg | 17.1 | 17.0 | 17.2 | 22.8 | 17.3 / 17.7 / 17.4 |
| loopBuilder | ArrayImg | 3.13 | 3.15 | 3.14 | 6.42 | 53.2 / 58.9 / 6.43 |
| loopBuilder | CellImg | 37.5 | 36.6 | 36.0 | 24.6 | 57.1 / 65.1 / 24.8 |
| randomAccessWalk | ArrayImg | 70.9 | 71.4 | 72.7 | 44.9 | 44.9 / 58.3 / 46.4 |
| randomAccessWalk | CellImg | 74.5 | 76.1 | 76.1 | 55.0 | 52.7 / 65.6 / 54.2 |

The `singleton4d` column above was measured before the flat-run fast path
covered single `Mixed` transforms. A rerun after that change also added a
`translate3d` row (`Views.translate(img, 1, 2, 3)`), in ms/op:

| loop | img | singleton4d before → after | translate3d |
|---|---|---|---|
| flatIterableCursor | ArrayImg | 18.5 → 3.14 | 3.11 |
| flatIterableCursor | CellImg | 22.8 → 18.0 | 18.2 |
| loopBuilder | ArrayImg | 6.42 → 3.14 | 3.10 |
| loopBuilder | CellImg | 24.6 → 25.1 | 19.4 |

The other worktree measured `Views.translate` flat iteration of the same image
at 12.8–13.9 ms on `ArrayImg`. That worktree doesn't have this change, so the
comparison is across branches, but on the same machine with the same settings.
The other rows of the rerun matched the table above within noise.

`ReshapeBlocksBenchmark`, µs per 32768-element block. `view` against
`fallback`:

| img | flat1d | merge2d | split4d | fallback |
|---|---|---|---|---|
| ArrayImg (tier 1) | 2.7 | 2.9 | 3.9–4.9 | 390–450 |
| PlanarImg | 6.9–7.8 | 7.0–7.5 | 5.4 aligned / 14.0 straddling | 370–415 |
| CellImg | 8.9–9.4 | 8.4–8.9 | 2.9 aligned / 9.8 straddling | 420–450 |

Before the line-by-line gather, Planar/Cell `flat1d` took about 360 µs, no
better than the fallback. Building the offset table cost one `floorDiv` per
element.

### Where the flat cursor's overhead comes from

Every view that takes the flat-run path iterates an `ArrayImg` in about 3.1–3.2
ms, against 2.2 ms for the raw image. That includes `translate3d`, which has no
reshape. `FlatIndexCursorBenchmark` (in `view.iteration`) isolates the cause.

- Run on 2026-09-11 with the same image and loop as `ReshapeViewBenchmark`,
  5 × 200 ms warmup and 10 × 200 ms measurement. The self-check passed.
- `ForwardingCursor` is a test-only cursor that delegates `fwd`, `get` and
  `hasNext` and does nothing else.

| cursor | ms/op |
|---|---|
| `img.cursor()` (`ArrayCursor`) | 2.14 ± 0.02 |
| `img.cursor( img )` (`ArraySubIntervalCursor`) | 2.15 ± 0.06 |
| `ForwardingCursor` around `ArrayCursor` | 2.14 ± 0.04 |
| `ForwardingCursor` around `ArraySubIntervalCursor` | 2.25 ± 0.09 |
| `FlatIndexCursor` around `ArrayCursor` | 3.19 ± 0.13 |
| `FlatIndexCursor` around `ArraySubIntervalCursor` | 3.19 ± 0.08 |
| `Views.reshape( img, 3600000 ).cursor()` | 3.21 ± 0.11 |

- **The extra layer and the source cursor type cost nothing.** Delegation is
  inlined completely, and `ArraySubIntervalCursor` over the whole image matches
  `ArrayCursor`.
- **All of the overhead is the flat index counter.** The only difference
  between `ForwardingCursor` and `FlatIndexCursor` in the loop is
  `++index` in `FlatIndexCursor.fwd()`. It accounts for about 1.05 ms, or 0.29 ns
  per element.
- **Why one counter costs that much is a guess, not verified.** The baseline
  moves 14 MB in and out in 2.1 ms, close to memory bandwidth, so it is probably
  a tight, likely vectorized, loop. A second induction variable in a heap field,
  next to the array cursor's own, may stop the JIT from compiling it that way.
  Confirming this needs the JIT's assembly output (`-XX:+PrintAssembly`).
- **Candidate fix (not implemented).** Stop counting in the non-localizing
  `FlatIndexCursor`. Give it the source box instead, and compute view positions
  only in `localize`/`getLongPosition`: read the source cursor's position,
  flatten it within the source box, unflatten into the view interval.
  - A get-only loop would then run at raw-image speed.
  - `localize` becomes more expensive, which is the usual contract for
    `cursor()` as opposed to `localizingCursor()`.
  - `FlatIndexLocalizingCursor` keeps its odometer, since tracking position is
    its purpose.

## Open issues

- **`FlatIndexCursor` costs about 50% over the raw cursor** on `ArrayImg`,
  entirely from its flat index counter; see above. The counter-free variant has
  not been tried.

- **`randomAccessWalk` is 1.2–1.6× slower than the old `TransformRandomAccess`.**
  That loop sets the full position on every pixel, which goes through
  `updateAll()`. Flattening the accessor's jagged arrays and using `/` for
  non-negative values changed nothing measurable. The cause is not identified.
- **`ReshapeCopier` allocates** its tables and scratch arrays on every `copy`.
- **Blocks with a reshape below an extension** on non-`ArrayImg` roots, and
  **multiple reshapes**, still use the fallback.
- **Off-domain behaviour differs.** Blocks without an extension use border
  values (as they already did for other views), while the view's RandomAccess
  maps off-domain positions by floor arithmetic. Only in-domain blocks are
  tested.
- **`MixedTransformViewBenchmark` was not ported.** Its key row, the identity
  reshape, is now removed by `TransformBuilder`, so it would only measure
  `zeroMin`.
