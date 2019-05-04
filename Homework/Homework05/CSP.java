package csp;

import java.time.LocalDate;
import java.util.Set;

import huffman.Huffman.HuffNode;

import java.util.ArrayList;
import java.util.List;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSP {

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
    	DateVar date = new DateVar(0, rangeStart, rangeEnd);
       
        return date.dates;
    }
    
 // -----------------------------------------------
    // DateVar Class
    // -----------------------------------------------
    
    /**
     * DateVar class used to hold meetings and their valid domains.
     */
    private static class DateVar {
        
        int index;
        ArrayList<LocalDate> dates;
        
        DateVar (int index, LocalDate start, LocalDate end) {
            this.index = index;
            this.dates = new ArrayList<>();
            
            while (start.until(end).getDays() >= 0) {
            	dates.add(start);
            	System.out.print(start + " ") ;
            	start = start.plusDays(1);
            }
            System.out.println();
        }
        
        public boolean remove(LocalDate date) {
        	return dates.remove(date);
        }
        
        public boolean add(LocalDate date) {
        	return dates.add(date);
        }
        
    }
}
