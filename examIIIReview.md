# Exam III Review

## Constraint Satisfaction Problems

Thus far, we've been approaching search problems wherein states are treated as atomic, indivisble, black-boxes that are either approved or rejected by some goalTest. It may be useful to decompose a state into some constituent parts, because we can identify the problematic part of the state and target that specifically when looking for a solution.

### Example: Map Coloring
In Map Coloring Problems, the goal is to assign one of K >= 2 colors to entities on a geographical map such that no two adjacent entities have the same color.

A data structure that might be useful for modeling the adjacencies between nation-states on a map such that we could then quickly check if its neighbors are the same color or not is an **undirected graph**.
This may seem like a trivial problem, but with a map consisting of 1000 nation-states, if you started with the wrong assignment, it may make the problem impossible to solve. The applications of CSPs are zoning/city planning, course/ meeting scheduling, electrical engineering, and games like Sudoku.

### Problem Specifications
**Constraint Satisfaction Problems** are those in which variables composing some problem state must be assigned some values that satisfy a set of constraints.

> CSP = <X, D, C>  

> X = a set of variables that stratify the state  
> D = for each variable X<sub>i</sub> &isin; X, the domain specifies the legal values that X<sub>i</sub> can be assigned  
> C = a set of boolean constraints that can be evaluated for the assigned value of each variable  

> A CSP solution is an assignment of values to variables from their domains such that all constraints are satisfied

### Classical Search vs. Constraint Satisfaction

|**Property**|Classical Search|Constraint Satisfaction|  
|:--:|:--:|:--:|  
|**States**| Atomic/ Black-box|Decomposable into variables and domains|  
|**Goal Test**|A state is or isn't a goal|We know which constraints are not satisfied|  
|**Purpose**|Care about path from initial to goal state|Only care about solution|

## Backtracking

### Brute Force
We could try every possible assignment and simply asses whether it satisfies all of the constraints or not. This is wasteful because we will end up testing states that we know will not work.
> The computational complexity of this algorithm is O(d^n) where n = |X| and d = |D|

This poor performance is largely as a consequence of treating the state as an atomic entity that cannot be decomposed further.

### Backtracking
**Search Formalization of CSPs**
* **States**: partial variable assignments to those assigned values thus far
* **Initial State**: empty assignment, no values have been assigned to any variable
* **Goal Test**: a boolean response to a complete assignment
* **Actions/Transitions**: assignments to a single variable at a time
* **Costs**: assumed to be constant in most CSPs (though some variant will use preferences)

**Backtracking** is an algorithmic paradigm in which depth-first search is performed on the recursion tree generated by the above search specifications with two additional requirements:
1. **Fixed variable ordering:** each ply of the resulting search tree assigns a value to a specific variable and this ordering of variables is fixed from the start
2. **Incremental Goal Testing:** subtrees are pruned as soon as a partial-assignment violates a constraint

### Improving Backtracking

When doing dumb backtracking, we keep values in variable's domains even when we know they will never work. We also did not think about the order in which we assigned variables.

### Filtering

**Filtering** is a technique for improving backtracking wherein the domains of unassigned variables are reduced by examining constraints and assignment to other variables. This improves backtracking by reducing the branching factor of the recursion tree.

### Constraint Graphs and Consistency
We used graphs to represent the map coloring problem. Graphs are also useful in other CSPs, because we can use them to determine what variables' domains are dependent based on which are found in the same constraints.

**Constraint Graphs** are structures of CSPs useful in filtering such that:
* **Nodes** are variables
* **Edges** connect any two variables that appear in a constraint together.

Variable domains are said to be **consistent** when they contain only values that are potential candidates for a solution

**Node consistency** is a filtering technique that ensure domain are consistency for any unary constraints pertaining to a particular node/ variable in the constraint graph.

**Arc consistency** is a filtering technique that ensures domains are consistent for any binary constraints pertaining to any pairs of adjacent nodes in the constraint graph

> A directed arc is consistent if and only if for all values in the tail domain there is at least one value in the head domain that satisfies the arc's constraints

Values in the tail with no satisfying values in the head are removed from the tail domain

Results of Filtering  
1. If filtering reduces a domain to the empty set, then there is no solution to this CSP.
2. If filtering reduces the domain of each variable to one value, then we have our solution.
3. If filtering reduces the domain of each variable to some values, then we still need backtracking to find a solution.

Filtering can be used as both a preprocessing tool to reduce work before backtracking as well as a tool for filtering as values are assigned during backtracking.

#### Forward Checking
**Forward Checking** is a filtering technique deployed during backtracking that removes values from domains via arc-consistency when values are added to an existing assignment. In other words, after we assign a value to a variable, we restrict the domains of related variables so that they we do not later assign them to a conflict value.  

The problem with forward checking is that it cannot detect an early failure that would result from its assignment even though its domain reductions are correct. One way we can fix this is through constraint propagation.

#### Constraint Propagation

**Constraint Propagation** is a filtering technique in which any changes to domains made by arc consistency are propagated to neighbors in the constraint graph

> The steps of **AC-3**  
> 1. Maintain a queue of arcs(tail, head) to check for consistency, starting with all arcs in the Queue  
> 2. While the Queue is not empty, pop an arc:
>       1. If it's consistent continue!
>       2. If its inconsistent, remove the offending values from the tail domain, the re-add all neighboring arcs that point to the tail back into the queue


#### Other Ways to Improve Backtracking
* **k-consistency:** Rather than verifying node (k==1) and arc (k==2) consistency, including through forward checking and constraint propagation, we could consider checking consistency between some k > 2 nod domains, and incrementally increase k until we had our original number of nodes
* **ordering heuristics:** the order in which we choose to assign to variables and the choices of values to assign during backtracking can dramatically influence the performance of the CSP solver
* **Exploiting CSP structure**: the format of  a given constraint graph can lead us to some shortcuts in solving it, and identifying these becomes a huge time saver

### CSP Ordering

A failure during backtracking is when the domain of a variable becomes the empty set. The intuition with carefully selecting an order of assignment is to avoid catastrophically shrinking any variable's domain as early in the process as possible.

1. **Minimal Remaining Values**: during backtracking, the variable with the smallest domain should have priority assignment over variables with larger ones
2. **Least Constraining Value**: given some variable choice, attempt to assign values in the order of least to greatest values removed from other domains. Doing this would require forward checking for each value and counting the domains restricted by that amount.