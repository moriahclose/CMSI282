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
	 
	static String[] operators = {"==", "!=", "<", "<=", ">", ">="};
	static ArrayList<String> OPERATORS = new ArrayList<>(Arrays.asList(operators));
	
	static String[] opposite_operators = {"==", "!=", ">", ">=", "<", "<="};
	static ArrayList<String> OPPOSITE_OPERATORS = new ArrayList<>(Arrays.asList(operators));

	
	/**
     * Tests whether a given solution to a CSP satisfies all constraints or not
     * Original Author: Andrew Forney
     * Edited By: Moriah Tolliver
     * @param soln Full instantiation of variables to assigned values, indexed by variable
     * @param constraints The set of constraints the solution must satisfy
     * @return true if solution is valid, false otherwise
     */
    public static boolean constraintsSatisfied (List<LocalDate> soln, Set<DateConstraint> constraints) {
    	boolean satisfied = true;
        for (DateConstraint d : constraints) {
            LocalDate leftDate = soln.get(d.L_VAL),
                      rightDate = (d.arity() == 1) 
                          ? ((UnaryDateConstraint) d).R_VAL 
                          : soln.get(((BinaryDateConstraint) d).R_VAL);
            
            switch (d.OP) {
            case "==": if (!leftDate.isEqual(rightDate))  return false; break;
            case "!=": if (leftDate.isEqual(rightDate)) return false; break;
            case ">":  if (leftDate.isBefore(rightDate) || leftDate.equals(rightDate))  return false; break;
            case "<":  if (leftDate.isAfter(rightDate) || leftDate.equals(rightDate)) return false; break;
            case ">=": if (leftDate.isBefore(rightDate))  return false; break; 
            case "<=": if (leftDate.isAfter(rightDate)) return false; break;
            }
        }
        return satisfied;
        
    }

	/**
	 * Method that prunes invalid values from a variables domain
	 * @param meeting Meeting being constrained
	 * @param constraint UnaryConstraint to check consistency of
	 */
	public static ArrayList<LocalDate> nodeConsistency(Meeting meeting, UnaryDateConstraint constraint) {	
		
		switch (constraint.OP) {
        case "==": 
        	if (meeting.domain.contains(constraint.R_VAL)) { // if this is a valid date for the meeting
        		meeting.setDate(constraint.R_VAL);           // assign it to this date
        	} else {										 // otherwise empty this Meeting's domain
        		meeting.domain = new ArrayList<LocalDate>();
        	}
        	break;
        case "!=": 
        	meeting.domain.remove(constraint.R_VAL);
        	break;
        case ">":  
        	meeting.removeBefore(constraint.R_VAL);
        	meeting.domain.remove(constraint.R_VAL);
        	break;
        case "<":  
        	meeting.removeAfter(constraint.R_VAL);
        	meeting.domain.remove(constraint.R_VAL);
        	break;
        case ">=": 
        	meeting.removeBefore(constraint.R_VAL);
        	break;
        case "<=": 
        	meeting.removeAfter(constraint.R_VAL);
        	break;
        }
		
		return meeting.domain;
	}
	
	/**
	 * Method that ensures arc consistency for a BinaryDateConstraint. Returns true if arc is consistent, returns false if tail domain has been reduced to 0
	 * @param tail Meeting on left side of constraint
	 * @param head Meeting on right side of constraint
	 * @param constraint BinaryDateConstraint to ensure consistency with
	 * @return true if consistency has been ensured, false if tail domain has been reduced to 0
	 */
	public static ArrayList<LocalDate> arcConsistency(Meeting tail, Meeting head, BinaryDateConstraint constraint) {
		ArrayList<LocalDate> newTailDomain = new ArrayList<>();
					
		for (LocalDate d : tail.domain) {
			UnaryDateConstraint c = new UnaryDateConstraint(0, constraint.OP, d); // doesn't matter what left value is since nodeConsistency assumes the correct meeting was put in
			Meeting compareHead = new Meeting(head.domain);                       // preserves domain of head during nodeConsistency check

			if ( nodeConsistency(compareHead, c).size() > 0 ) {
				newTailDomain.add(d);
			} 
		}
		
		return newTailDomain;
	}
	
	/**
	 * Returns index of meeting with smallest domain size that has not been assigned from an ArrayList<Meeting>
	 * @param meetings to compare domain sizes of
	 * @return index of meeting with smallest domain size
	 */
	public static int getNextVar(ArrayList<Meeting> meetings) {
		Meeting smallestDomain = meetings.get(0);
		
		for (Meeting m : meetings) {
			smallestDomain = (m.domain.size() < smallestDomain.domain.size() && m.domain.size() > 1) ? m : smallestDomain;
		}
		
		return meetings.indexOf(smallestDomain);
	}
	
	/**
	 * Returns the constraints relevant to the variables that have already been assigned
	 * @param assignment of variables
	 * @param constraints to check if they are relevant
	 * @return set of date constraints that are relevant to the assigned variables
	 */
	public static Set<DateConstraint> getRelevantConstraints(ArrayList<LocalDate> assignment, Set<DateConstraint> constraints) {
		Set<DateConstraint> relevantConstraints = new HashSet<DateConstraint>();
		
		for ( DateConstraint c : constraints) {
			if (c.arity() == 1 && c.L_VAL < assignment.size() ) {
				relevantConstraints.add(c);
			} else if ( c.L_VAL < assignment.size() && ((BinaryDateConstraint)c).R_VAL < assignment.size() ){
				relevantConstraints.add(c);
			}
		}
		
		return relevantConstraints;
	}
	
	/**
	 * Flips a binary constraint into a unary constraint
	 * @param int index of meeting that has been assigned
	 * @param LocalDate that meeting has been assigned to
	 * @param Set<DateConstraints> to adjust
	 * @return Set<DateConstraints> where binary constraints containing the assigned meeting have been changed to unary constraints 
	 */
	public static BinaryDateConstraint flip(BinaryDateConstraint c) {
		BinaryDateConstraint newConstraint = new BinaryDateConstraint(c.L_VAL, OPPOSITE_OPERATORS.get(OPERATORS.indexOf(c.OP)), c.R_VAL);
		
		return newConstraint;
	}

	/**
	 * Performs backtracking to arrive at assignment for all variables that satisfies all constraints, returns null if no solution possible
	 * @param meetings that need assignments
	 * @param constraints that need to be satisfied
	 * @param assignment partial assignment to test for accuracy then assign the next var 
	 * @return assignment that satisfies all constraints
	 */
	public static ArrayList<LocalDate> backtrack(ArrayList<Meeting> meetings, Set<DateConstraint> constraints, ArrayList<LocalDate> assignment) {
		ArrayList<LocalDate> result = new ArrayList<>();
		
		if (assignment.size() == meetings.size() && constraintsSatisfied(assignment, getRelevantConstraints(assignment, constraints)) ) {
			return assignment;
		}
		
		Meeting meetingToAdd = meetings.get(getNextVar(meetings)); // get the next variable that has not been assigned

		for (LocalDate d : meetingToAdd.domain) {
			assignment.add(d);
			
			if ( constraintsSatisfied(assignment, getRelevantConstraints(assignment, constraints)) ) {			
		        for (Meeting m : meetings ) {
		        }
				result = backtrack(meetings, constraints, assignment);
				if ( result != null) {
					return result;
				}
			}
			assignment.remove(assignment.size()-1);
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
        	meetings.get(c.L_VAL).setDomain(nodeConsistency(meetings.get(c.L_VAL), c));
        	if (meetings.get(c.L_VAL).domainEmpty()) {
        		return null;
        	}
        }

        for (BinaryDateConstraint c : binaryConstraints) {
        	meetings.get(c.L_VAL).setDomain(arcConsistency(meetings.get(((BinaryDateConstraint)c).R_VAL), meetings.get(c.L_VAL), c));
        	if (meetings.get(c.L_VAL).domainEmpty()) {
        		return null;
        	}
        }

//        return meetings.get(0).domain;
        
        return backtrack(meetings, constraints, new ArrayList<LocalDate>());
    }
    
    // -----------------------------------------------
    // Meeting Variable
    // -----------------------------------------------
    
    /**
     * Meeting class that holds the domain of the meeting as an ArrayList<LocalDate>.
     * It also holds the date the meeting has been assigned to if it has been assigned.
     */
    private static class Meeting {
        
        ArrayList<LocalDate> domain;
        LocalDate date = null;
        
        Meeting (LocalDate start, LocalDate end ) {
            domain = new ArrayList<LocalDate>();
            while (start.isBefore(end)) {
            	domain.add(start);
            	start = start.plusDays(1);
            }
            domain.add(start); // add end date to domain
        }
        
        Meeting(LocalDate date) {
            domain = new ArrayList<LocalDate>();
            domain.add(date);
        	this.date = date;
        }
        
        Meeting(ArrayList<LocalDate> domain) {
        	setDomain(domain);
        }
        
        public boolean isAssigned() {
        	if (domain.size() == 1) {
        		date = domain.get(0);
        	}
        	
            return date != null;
        }
        
        public boolean domainEmpty() {
        	return domain.size() == 0;
        }
        
        public void setDate(LocalDate date) {
        	domain = new ArrayList<LocalDate>();
        	domain.add(date);
        	this.date = date;
        }
        
        public void setDomain(ArrayList<LocalDate> inputDomain) {
        	this.domain = new ArrayList<>();
        	for (LocalDate d : inputDomain) {
        		domain.add(d);
        	}
        }
        
        public boolean removeBefore(LocalDate date) {
        	boolean removed = false;
        	ArrayList<LocalDate> toRemove = new ArrayList<>();
        	
        	for (LocalDate d : domain) {
        		if (d.isBefore(date)) {
        			toRemove.add(d);
        		}
        	}
        	
        	for (LocalDate d : toRemove) {
        		domain.remove(d);
        	}
        	
        	return removed;
        }
        
        public boolean removeAfter(LocalDate date) {
        	boolean removed = false;
        	ArrayList<LocalDate> toRemove = new ArrayList<>();
        	
        	for (LocalDate d : domain) {
        		if (d.isAfter(date)) {
        			toRemove.add(d);
        		}
        	}
        	
        	for (LocalDate d : toRemove) {
        		domain.remove(d);
        	}
        	
        	return removed;
        }
        
        public boolean remove(LocalDate date) {
        	return domain.remove(date);
        }
        
        @Override 
        public String toString() {
        	String datesString = "";
        	for (LocalDate d : domain) {
        		datesString += d + " ";
        	}
        	return datesString;
        }
        
    }

}
