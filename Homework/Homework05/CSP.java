package csp;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSP {
	
	public static ArrayList<DateVar> arcPreprocess(ArrayList<DateVar> meetings, Set<DateConstraint> constraints) {
		for (int i = 0; i < meetings.size(); i++) {
			if (meetings.get(i).dates.size() > 0 ) {
				meetings.set(i, new DateVar(meetings.get(i).dates.get(0))); // set current meeting to the first date in it's available range
				
				ArrayList<DateConstraint> toRemove = new ArrayList<>();
				ArrayList<DateConstraint> toAdd = new ArrayList<>();

				for (DateConstraint constraint: constraints) {
					if (constraint.arity() == 2 && i == ((BinaryDateConstraint)constraint).R_VAL) {
						toRemove.add(constraint);
						UnaryDateConstraint newConstraint = new UnaryDateConstraint(constraint.L_VAL, constraint.OP, meetings.get(i).dates.get(0));
						toAdd.add(newConstraint);
					} else if ( constraint.arity() == 2 && i == constraint.L_VAL ) {
						toRemove.add(constraint);
						UnaryDateConstraint newConstraint = new UnaryDateConstraint(((BinaryDateConstraint)constraint).R_VAL, constraint.OP, meetings.get(i).dates.get(0));
						toAdd.add(newConstraint);
					}
				}
				
				for (DateConstraint d : toRemove) {
					constraints.remove(d);
				}
				
				for (DateConstraint d : toAdd) {
					constraints.add(d);
				}
				
				meetings = nodePreprocess(meetings, constraints);
				
			} else if (meetings.get(i).dates.size() == 0 ){
				DateVar newMeeting = meetings.get(0);
				newMeeting.dates.remove(0);
				meetings.set(0, newMeeting);
				arcPreprocess(meetings, constraints);
			} else {
				return null;
			}
		}
		return meetings;
	}
	
	public static ArrayList<DateVar> nodePreprocess(ArrayList<DateVar> meetings, Set<DateConstraint> constraints) {
		for (DateConstraint constraint : constraints) {
			if (constraint.arity() == 1) { 
	    		DateVar newDateVar;
	    		
	            switch (constraint.OP) {
	            case "==": 
	            	if (meetings.get(constraint.L_VAL).dates.contains(((UnaryDateConstraint) constraint).R_VAL)) {
		            	newDateVar = new DateVar(((UnaryDateConstraint) constraint).R_VAL);
		            	meetings.set(constraint.L_VAL, newDateVar);
	            	} else {
	            		meetings = null;
	            	}
	            	break;
	            case "!=": 
	            	newDateVar = meetings.get(constraint.L_VAL);
	            	newDateVar.remove(((UnaryDateConstraint) constraint).R_VAL);
	            	meetings.set(constraint.L_VAL, newDateVar);
	            	break;
	            case ">":  
	            	newDateVar = meetings.get(constraint.L_VAL);
	            	newDateVar.remove(((UnaryDateConstraint) constraint).R_VAL);
	            	newDateVar.removeDatesLessThan(((UnaryDateConstraint) constraint).R_VAL);
	            	meetings.set(constraint.L_VAL, newDateVar);
	            	break;
	            case "<":  
	            	newDateVar = meetings.get(constraint.L_VAL);
	            	newDateVar.remove(((UnaryDateConstraint) constraint).R_VAL);
	            	newDateVar.removeDatesLessThan(((UnaryDateConstraint) constraint).R_VAL);
	            	meetings.set(constraint.L_VAL, newDateVar);
	            	break;
	            case ">=": 
	            	newDateVar = meetings.get(constraint.L_VAL);
	            	newDateVar.removeDatesLessThan(((UnaryDateConstraint) constraint).R_VAL);
	            	meetings.set(constraint.L_VAL, newDateVar);
	            	break;
	            case "<=": 
	            	newDateVar = meetings.get(constraint.L_VAL);
	            	newDateVar.removeDatesGreaterThan(((UnaryDateConstraint) constraint).R_VAL);
	            	meetings.set(constraint.L_VAL, newDateVar);
	            	break;
	            }
			}
            
    	}
		return meetings;
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
    	// set all meeting vars with all dates in range and its corresponding index
    	ArrayList<DateVar> meetings = new ArrayList<>();
    	
    	for ( int i = 0; i < nMeetings; i++) {
    		DateVar date = new DateVar(rangeStart, rangeEnd);
    		meetings.add(date);
    	}

    	// preprocessing
    	meetings = nodePreprocess(meetings, constraints);
    	if (meetings == null) {
    		return null;
    	}
    	
    	meetings = arcPreprocess(meetings, constraints);
    	if (meetings == null) {
    		return null;
    	}
    	
    	// TO DELETE
    	if (meetings != null ) {
	        for (DateVar d : meetings) {
	        	System.out.println( "     " + d );
	        }
    	}
        return (meetings != null ) ? meetings.get(0).dates : null;
    }
    
 // -----------------------------------------------
    // DateVar Class
    // -----------------------------------------------
    
    /**
     * DateVar class used to hold meetings and their valid domains.
     */
    private static class DateVar {
        
        ArrayList<LocalDate> dates;
        
        DateVar (LocalDate start, LocalDate end) { // if given two dates will interpret as range
            this.dates = new ArrayList<>();
            
            while (start.until(end).getDays() >= 0) {
            	dates.add(start);
            	start = start.plusDays(1);
            }
        }
        
        DateVar (LocalDate date) {
        	this.dates = new ArrayList<>();
        	dates.add(date);
        }
        
        public boolean remove(LocalDate date) {
        	return dates.remove(date);
        }
        
        public boolean removeDatesGreaterThan(LocalDate date) {
        	boolean didRemove = false;
        	ArrayList<LocalDate> toRemove = new ArrayList<>(); 

        	for (LocalDate d : dates) {
        		if (d.compareTo(date) > 0) {
        			toRemove.add(d);
        			didRemove = true;
        		}
        	}
        	
        	for (LocalDate d : toRemove) {
        		dates.remove(d);
        	}
        	
        	return didRemove;
        }
        
        public boolean removeDatesLessThan(LocalDate date) {
        	boolean didRemove = false;
        	ArrayList<LocalDate> toRemove = new ArrayList<>(); 

        	for (LocalDate d : dates) {
        		if (d.compareTo(date) < 0) {
        			toRemove.add(d);
        			didRemove = true;
        		}
        	}
        	
        	for (LocalDate d : toRemove) {
        		dates.remove(d);
        	}
        	
        	return didRemove;
        }
        
        public boolean add(LocalDate date) {
        	return dates.add(date);
        }
        
        @Override
        public String toString() {
        	String dateStrings = "";
        	for (LocalDate date : dates) {
        		dateStrings += date + " ";
        	}
        	return dateStrings;
        }
        
    }
}
