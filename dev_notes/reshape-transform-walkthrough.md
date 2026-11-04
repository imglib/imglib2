# ReshapeTransform walkthrough

`net.imglib2.transform.integer.ReshapeTransform` maps a position in a
**source** (the reshaped view) to a position in a **target** (the underlying
image). The two have the same number of elements and are traversed in the same
flat order, with dimension 0 fastest. It is the transform behind
`Views.reshape`. For the design and its rationale, see `reshape-transform.md`.

All values below are real. They were printed by a small program that
constructs the example transform and calls its getters, `apply` and
`boundingBox`.

## The example

```java
ReshapeTransform t = new ReshapeTransform(
        new long[] { 4, 3, 1, 10 },     // source (view) dimensions
        new long[] { 12, 1, 2, 5 } );   // target (image) dimensions
```

Both shapes have 120 elements. This small example covers every case the
transform handles:

- **Source axes 0 and 1 (4 × 3)** become **target axis 0 (12)**. This is a
  *split* of the target axis into two view axes.
- **Source axis 3 (10)** becomes **target axes 2 and 3 (2 × 5)**. This is a
  *merge* of two target axes into one view axis.
- **Source axis 2** has size 1. It carries no information.
- **Target axis 1** has size 1. It is always 0.

```
t.toString():
ReshapeTransform{[4, 3, 1, 10]->[12, 1, 2, 5]: [4, 3]<->[12] | [10]<->[2, 5]}
```

## Groups

The constructor first drops size-1 axes:

- non-singleton source axes: `[0, 1, 3]`, with sizes `4, 3, 10`
- non-singleton target axes: `[0, 2, 3]`, with sizes `12, 2, 5`

It then builds **groups**. It takes one axis from each side, and keeps adding
an axis to whichever side has the smaller running product, until the two
products are equal:

| step | source product | target product | action |
|---|---|---|---|
| start group 0 | 4 (axis 0) | 12 (axis 0) | 4 < 12: add source axis 1 |
| | 4·3 = 12 | 12 | equal: close group 0 |
| start group 1 | 10 (axis 3) | 2 (axis 2) | 10 > 2: add target axis 3 |
| | 10 | 2·5 = 10 | equal: close group 1 |

Groups are independent. The target position in a group depends only on the
source position in the same group. A reshape is therefore a product of small
reshapes, one per group, and so is its cost.

## Internal variables

| field | value | meaning |
|---|---|---|
| `sourceDimensions` | `[4, 3, 1, 10]` | |
| `targetDimensions` | `[12, 1, 2, 5]` | |
| `sourceGroup` | `[0, 0, -1, 1]` | group of each source axis; −1 means size 1 |
| `sourceStride` | `[1, 4, 0, 1]` | stride of each source axis in its group's flat index |
| `targetGroup` | `[0, -1, 1, 1]` | group of each target axis; −1 means size 1 |
| `targetStride` | `[1, 0, 1, 2]` | stride of each target axis in its group's flat index |
| `groupSourceAxes` | `[[0, 1], [3]]` | non-singleton source axes of each group |
| `groupTargetAxes` | `[[0], [2, 3]]` | non-singleton target axes of each group |
| `zeroAxes` | `[1]` | target axes of size 1, always set to 0 |
| `inverse` | `null` | created on the first call to `inverse()`; it is the reshape `[12, 1, 2, 5] -> [4, 3, 1, 10]` |

Strides restart at 1 in every group. They are not global strides. For
example, source axis 3 has stride 1 because it is the first axis of group 1.
Its stride over the whole 4 × 3 × 1 × 10 shape would be 12.

## `apply`, step by step

```java
long[] target = new long[ 4 ];
t.apply( new long[] { 2, 1, 0, 7 }, target );   // target == [6, 0, 1, 3]
```

`apply` handles the target axes of size 1 first, then each group in turn. The
source axis of size 1 is never read.

**1. Target axes of size 1** (`zeroAxes = [1]`): `target[1] = 0`.

**2. Group 0.** Source axes `[0, 1]`, target axes `[0]`.

- Flatten: `f = sourceStride[0]·2 + sourceStride[1]·1 = 1·2 + 4·1 = 6`.
- Unflatten into `[0]`. The group has a single target axis, so there is
  nothing to divide: `target[0] = 6`.

  A split group never divides. This is why a split is linear.

**3. Group 1.** Source axes `[3]`, target axes `[2, 3]`.

- Flatten: `f = sourceStride[3]·7 = 7`.
- Unflatten into `[2, 3]`:
  - For target axis 2 (size 2): `q = floorDiv(7, 2) = 3`, so
    `target[2] = 7 − 3·2 = 1` and `f = 3`.
  - Target axis 3 is the group's last axis, so it takes what is left:
    `target[3] = 3`.

**Result: `[6, 0, 1, 3]`.**

Check through the global flat index:
- source `[2, 1, 0, 7]`: `2 + 4·1 + 12·0 + 12·7 = 90`
- target `[6, 0, 1, 3]`: `6 + 12·0 + 12·1 + 24·3 = 90`

The code is the same for each group:

```java
long f = 0;
for ( final int d : groupSourceAxes[ g ] )        // flatten
    f += sourceStride[ d ] * source[ d ];
final int[] tAxes = groupTargetAxes[ g ];
final int last = tAxes.length - 1;
for ( int i = 0; i < last; ++i )                  // unflatten
{
    final int d = tAxes[ i ];
    final long q = Math.floorDiv( f, targetDimensions[ d ] );
    target[ d ] = f - q * targetDimensions[ d ];
    f = q;
}
target[ tAxes[ last ] ] = f;                      // last axis: no bound
```

## More positions

| source | target | note |
|---|---|---|
| `[2, 1, 0, 7]` | `[6, 0, 1, 3]` | the walkthrough above |
| `[3, 2, 0, 9]` | `[11, 0, 1, 4]` | the last element; both flat indices are 119 |
| `[0, 0, 5, 0]` | `[0, 0, 0, 0]` | source axis 2 has size 1 and is ignored, even off-domain |
| `[-1, 0, 0, 0]` | `[-1, 0, 0, 0]` | off-domain in a split group: `f = −1` goes to the last (unbounded) target axis |
| `[0, 0, 0, -1]` | `[0, 0, 1, -1]` | off-domain in a merge group: `floorDiv(−1, 2) = −1`, remainder 1 |

The last row shows why the unflatten uses `floorDiv` rather than `/`:
- Truncating division would give `[0, 0, −1, 0]`.
- `ReshapeRandomAccess` computes the same position incrementally. Stepping back
  from `[0, 0, 0, 0]` borrows: target axis 2 wraps around to 1, and target
  axis 3 goes to −1.

Floor division is what makes the two computations agree. Cursors routinely sit
at such positions (min − 1) before their first `fwd()`.

Outside the domain, the result of each group is the unique target position
whose group flat index is `f`, where every target axis of the group except the
last lies within its dimension.

## Bonus: `boundingBox`

`boundingBox` handles each group separately, from the flat indices of the box
corners. For the source box `[1..2] × [1..2] × [0] × [3..4]`:

- **Group 0**: `f` ranges from `1 + 4·1 = 5` to `2 + 4·2 = 10`, so target
  axis 0 gets `[5, 10]`.

  This is conservative. The box only contains the flat indices 5, 6, 9 and 10,
  because it is not a single flat run.
- **Group 1**: `f` ranges from 3 to 4.
  - Unflattened, the ends are `3 → (1, 1)` and `4 → (0, 2)`.
  - The highest target axis where they differ is axis 3, which gets `[1, 2]`.
  - Every axis below it takes its full extent, so axis 2 gets `[0, 1]`.
- **Target axis 1** has size 1: `[0, 0]`.

Result: `[5, 0, 0, 1] .. [10, 0, 1, 2]`.
