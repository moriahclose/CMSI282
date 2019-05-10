package csp;

import java.time.LocalDate;
import java.util.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSP {
	
	/**
	 * Flips a binary constraint into a unary constraint
	 * @param int index of meeting that has been assigned
	 * @param LocalDate that meeting has been assigned to
	 * @param Set<DateConstraints> to adjust
	 * @return Set<DateConstraints> where binary constraints containing the assigned meeting have been changed to unary constraints 
	 */
	public static BinaryDateConstraint flip(BinaryDateConstraint c) {
		String[] operators = {"==", "!=", "<", "<=", ">", ">="};
		ArrayList<String> OPERATORS = new ArrayList<>(Arrays.asList(operators));
		
		String[] opposite_operators = {"==", "!=", ">", ">=", "<", "<="};
		ArrayList<String> OPPOSITE_OPERATORS = new ArrayList<>(Arrays.asList(opposite_operators));
		
		BinaryDateConstraint newConstraint = new BinaryDateConstraint(c.R_VAL, OPPOSITE_OPERATORS.get(OPERATORS.indexOf(c.OP)), c.L_VAL);
		
		return newConstraint;
	}
	
	/**
	 * Method that prunes invalid values from a variables domain
	 * @param meeting Meeting being constrained
	 * @param constraint UnaryConstraint to check consistency of
	 */
	public static void nodeConsistency(Meeting meeting, UnaryDateConstraint constraint) {	
		ArrayList<LocalDate> toRemove = new ArrayList<>();
		
		for (LocalDate d : meeting.domain) {
			if (!constraintSatisfied(d, constraint, constraint.R_VAL)) {
				toRemove.add(d);
			}
		}
		
		for (LocalDate d : toRemove) {
			meeting.domain.remove(d);
		}
	}
	
	/**
	 * Prunes invalid values from tail's domain (assumes tail will be on left side of constraint)
	 * @param Meeting tail
	 * @param Meeting head
	 * @param BinaryDateConstraint to satisfy
	 */
	public static void arcConsistency(Meeting tail, Meeting head, BinaryDateConstraint c) {
		ArrayList<LocalDate> toAdd = new ArrayList<>();
		
		for (LocalDate d : tail.domain) {
			for ( LocalDate hd : head.domain) {
				if (constraintSatisfied(d, c, hd)) {
					toAdd.add(d);
					break; // exit this loop once the value is found, since we only need one
				}
			} 
		}
		
		tail.domain = new ArrayList<>();
		for (LocalDate d : toAdd) {
			tail.domain.add(d);
		}
	}
	
	/**
     * Tests whether a given solution to a CSP satisfies all constraints or not
     * @param soln Full instantiation of variables to assigned values, indexed by variable
     * @param constraints The set of constraints the solution must satisfy
     * @return true if all constraints are satisfied, false otherwise
     */
    public static boolean testSolution (List<LocalDate> soln, Set<DateConstraint> constraints) {
        for (DateConstraint d : constraints) {
        	LocalDate leftDate = soln.get(d.L_VAL),
                      rightDate = (d.arity() == 1) 
                          ? ((UnaryDateConstraint) d).R_VAL 
                          : soln.get(((BinaryDateConstraint) d).R_VAL);
            
            if (leftDate == null || rightDate == null) {
            	continue;
            }
                          
            if (!constraintSatisfied(leftDate, d, rightDate)) {
            	return false;
            }
        }
        return true;
    }
    
    
    /**
     * Given two dates, returns whether the dates satisfy the given constraint
     * @param LocalDate date on left side of equality
     * @param DateConstraint holding constraint operator
     * @param LocalDate date on right side of equality
     */
    public static boolean constraintSatisfied(LocalDate leftDate, DateConstraint d, LocalDate rightDate) {
    	boolean sat = false;
    	
        switch (d.OP) {
	        case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
	        case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
	        case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
	        case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
	        case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
	        case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
        }
        if (!sat) {
            return sat;
        }
        
        return true;
    }
	
    /**
     * Returns solution to a CSP using backtracking
     * @param ArrayList<Meeting> holds variables, their domains, and their relevant constraints
     * @param Set<DateConstraint> constraints to be satisfied
     * @return List<LocalDate> assignment of dates to meetings
     */
    public static ArrayList<LocalDate> backtracking(ArrayList<Meeting> meetings, Set<DateConstraint> constraints, ArrayList<LocalDate> assignment, int index) {
    	
    	if (testSolution(assignment, constraints) && !assignment.contains(null)) {
    		return assignment;
    	}
    	
    	Meeting meetingToAdd = meetings.get(index);
    	
    	for (LocalDate d : meetingToAdd.domain) {
    		assignment.set(index, d);
    		if (testSolution(assignment, constraints)) {
    			ArrayList<LocalDate> result = backtracking(meetings, constraints, assignment, index+1);
    			if (result != null) {
    				return result;
    			}
    		}
    		assignment.set(index, null);
    	}
    	
    	return null;
    	
    }
    
	/**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {   
    	 // Construct all meetings
        ArrayList<Meeting> meetings = new ArrayList<>();
        for (int i = 0; i < nMeetings; i++) {
        	Meeting newMeeting = new Meeting(rangeStart, rangeEnd);
        	meetings.add(newMeeting);
        }
                
        // node preprocessing; returns null if any domain goes to size 0
        ArrayList<DateConstraint> toAdd = new ArrayList<>();
        
        for (DateConstraint c : constraints) {
        	if (c.arity() == 2) {
            	BinaryDateConstraint newConstraint = flip((BinaryDateConstraint)c);
            	toAdd.add(newConstraint);
        	}
        }
        
        
        for (DateConstraint c : toAdd) { // done separately to avoid concurrent modification error
        	constraints.add(c);
        }
        
        // separate unary and binary constraints
        Set<UnaryDateConstraint> unaryConstraints = new HashSet<>();
        Set<BinaryDateConstraint> binaryConstraints = new HashSet<>();

        for (DateConstraint c: constraints) { 
        	if (c.arity() == 1) {
        		unaryConstraints.add((UnaryDateConstraint)c);
        	} 
        	else {
        		binaryConstraints.add((BinaryDateConstraint)c);
        	}
        }
        
        // do node preprocessing first
        for (UnaryDateConstraint c : unaryConstraints) {
        	nodeConsistency(meetings.get(c.L_VAL), c);
        	if (meetings.get(c.L_VAL).domainEmpty()) {
        		return null;
        	}
        }

        for (BinaryDateConstraint c : binaryConstraints) {
        	arcConsistency(meetings.get(c.L_VAL), meetings.get( ((BinaryDateConstraint)c).R_VAL), c);
        	if (meetings.get(c.L_VAL).domainEmpty()) {
        		return null;
        	}
        }

        ArrayList<LocalDate> assignment = new ArrayList<LocalDate>();
        for (int i = 0; i < nMeetings; i++) {
        	assignment.add(null);
        }
        
        ArrayList<LocalDate> result = backtracking(meetings, constraints, assignment, 0);
        return result;
    }
    
    /**
     * Meeting class that holds the domain of the meeting as an ArrayList<LocalDate>.
     * It also holds the date the meeting has been assigned to if it has been assigned.
     */
    private static class Meeting {
        
        ArrayList<LocalDate> domain;
        
        Meeting (LocalDate start, LocalDate end ) {
            domain = new ArrayList<LocalDate>();
            while (start.isBefore(end)) {
            	domain.add(start);
            	start = start.plusDays(1);
            }
            domain.add(start); // add end date to domain
        }
        
        public boolean domainEmpty() {
        	return domain.size() == 0;
        }
        
        @Override 
        public String toString() {
        	String datesString = "[";
        	for (LocalDate d : domain) {
        		datesString += d + " ";
        	}
        	datesString += "]";
        	return datesString;
        }
        
    }
}
