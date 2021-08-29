package com.example.demo.pkg1;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Repository;

@Repository
public class examRepository {

	//1
	/**
	 * 
	 * @return All Meetings
	 */
	public List<Meeting> getAllMeetings(){
		return Randoms.getMeetings();
	}
	
	public int[] getAllhappendMeetingsForDate(Date startDate, Date endDate){
		int all = 1;
		int happend = 0;
		 for (Meeting m: getAllMeetings()) {
			 if (m.getMeetingDateTime().after(startDate) && m.getMeetingDateTime().before(endDate)) {
				 all++;
				 if (m.isCancelled() == false) {
					 happend++;
				 }
			 }
		 }
		 int[] toReturn = {happend,all-happend, Math.round(100*happend/all)};
		 return toReturn;
	 }
	
	//2 
	public List <Parent> getAllActiveparents (){
		return Randoms.getParents();
	}
	

	 public int[] getNumOfActiveParentsForDate(Date startDate, Date endDate){
			int all = 1;
			int active = 0;
			 for (Parent p: getAllActiveparents()) {
				 if (p.getActiveDate().after(startDate) && p.getActiveDate().before(endDate)) {
					 all++;
					 if (p.getActive() == Status.Active) {
						 active++;
					 }
				 }
			 }
			 int[] toReturn = {active,all-active, Math.round(100*active/all)};
			 return toReturn;
	}
	 
	 //3
	 public List<Kid> retrieveAllKids(){
			return Randoms.getKids();
		}
	
		 public int[] getNumOfActiveKidsForDate(Date startDate, Date endDate){
			int all = 1;
			int active = 0;
				for (Kid k: retrieveAllKids()) {
				 if (k.getActiveDate().after(startDate) && k.getActiveDate().before(endDate)) {
					 all++;
					 if (k.getStatus() == Status.Active) {
						 active++;
					 }
				 }
			 }
				 int[] toReturn = {active,all-active, Math.round(100*active/all)};
			 return toReturn;
		}
		 
		 //4
		 public List<Category> getAllCategories(){
				return Randoms.getCats();
			}

			public ArrayList<Integer> getkidsNumInCategory(Date startDate, Date endDate){
				HashMap<Category, Integer> catKidsNum = new HashMap<Category, Integer>();
				ArrayList<Integer> arr;
				for (Category cat : getAllCategories()) {
					int count = 0;
					for (Course course : getCategoryCourses(cat.getId())) {
						if ((course.getStartDateTime().after(startDate) && course.getStartDateTime().before(endDate))|| (course.getFinishDateTime().after(startDate) && course.getFinishDateTime().before(endDate)) || (course.getStartDateTime().before(startDate) && course.getFinishDateTime().after(endDate))) {
							for (String kidId : course.getKidsIDs()) {
								if (getKidById(kidId).getStatus() == Status.Active) {
									catKidsNum.put(cat, count++);
								}
							}
						}
					}	
				}
				arr = new ArrayList<Integer>(catKidsNum.values());

				return arr;
			}
				
			public ArrayList<Course> getCategoryCourses(String categoryID) {
				ArrayList<Course> categoryCourses = new ArrayList<Course>();
				for (Course c : Randoms.getCourses())
					if (c.getCategoryId().equals(categoryID)) {
						categoryCourses.add(c);
					}
				return categoryCourses;
			}
			
			public Kid getKidById(String kidId) {
				for (Kid k : Randoms.getKids()) {
					if (k.getId().equals(kidId)) {
						return k;
					}
				}
				return null;
			}
			
			//5
			public HashMap<Integer,HashMap<Integer, Integer>> getCategoryWeeklytrend(){
				DateTime dateTime = new DateTime();
				HashMap<Category, HashMap<Integer, Integer>> trend= new HashMap<Category, HashMap<Integer, Integer>>();
				HashMap<Integer,HashMap<Integer, Integer>> values = new HashMap<Integer,HashMap<Integer, Integer>>();
				int k=0;
				for (Category cat : getAllCategories()) {
					trend.put(cat, new HashMap<Integer, Integer>());
					values.put(k, new HashMap<Integer, Integer>());
					for (int i = 0; i < 7; i ++) {
						//System.out.println("--------");
						int count = 0;
						DateTime day = dateTime.minusDays(i);
						//System.out.println( "todays is: " + day);
						for (Course course : getCategoryCourses(cat.getId())) {
							if (new DateTime(course.getStartDateTime()).equals(day)|| new DateTime(course.getFinishDateTime()).equals(day) || (course.getStartDateTime().before(day.toDate()) && course.getFinishDateTime().after(day.toDate()))) {
								//System.out.println(course.getName() + ": " + course.getStartDateTime() + course.getFinishDateTime());
								for (String kidId : course.getKidsIDs()) {
									if (getKidById(kidId).getStatus() == Status.Active) {
										count++;
									}
								}
							}
						}
						//System.out.println((7-i) + "count: " + count);
						//System.out.println("--------");
						trend.get(cat).put(7 - i, count);
						values.get(k).put(7 - i, count);
						
					}
					k++;
				}

			
			return values;
			}
			
			public HashMap<Integer,HashMap<Integer, Integer>> getCategoryMonthllyYearlytrend(int periods){
				if (periods != 4 && periods != 12) {
					return null;
				}
				DateTime dateTime = new DateTime();
				DateTime lastDay = null;
				DateTime firstDay = null;
				HashMap<Category, HashMap<Integer, Integer>> trend= new HashMap<Category, HashMap<Integer, Integer>>();
				HashMap<Integer,HashMap<Integer, Integer>> values = new HashMap<Integer,HashMap<Integer, Integer>>();
				int k=0;
				for (Category cat : getAllCategories()) {
					trend.put(cat, new HashMap<Integer, Integer>());
					values.put(k, new HashMap<Integer, Integer>());
					for (int i = 0; i < periods; i ++) {
						//System.out.println("--------");
						int count = 0;
						if (periods == 4) {
							lastDay = dateTime.minusWeeks(i);
							firstDay = dateTime.minusWeeks(i + 1);
						}
						if (periods == 12) {
							lastDay = dateTime.minusMonths(i);
							firstDay = dateTime.minusMonths(i + 1);
						}
						Interval interval = new Interval(firstDay, lastDay);
						//System.out.println( "todays is: " + firstDay + " - " + lastDay);
						for (Course course : getCategoryCourses(cat.getId())) {
							if (interval.contains(new DateTime(course.getStartDateTime()))|| interval.contains(new DateTime(course.getFinishDateTime())) || new Interval(new DateTime(course.getStartDateTime()), new DateTime(course.getFinishDateTime())).contains(interval)) {
								//System.out.println(course.getName() + ": " + course.getStartDateTime() + course.getFinishDateTime());
								for (String kidId : course.getKidsIDs()) {
									if (getKidById(kidId).getStatus() == Status.Active) {
										count++;
									}
								}
							}
						}
						//System.out.println((7-i) + "count: " + count);
						//System.out.println("--------");
						trend.get(cat).put(periods - i, count);
						values.get(k).put(periods - i, count);
					}
					k++;
				}
				return values;
			}
			
			
			
}
