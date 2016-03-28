/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility class for converting and formatting time and date values.
 * 
 */

public class DateManager {

	public static final TimeZone TAIPEI_TIMEZONE = TimeZone.getTimeZone("Asia/Taipei");
	
	/**
	 * Converts the given simple date String to a Date.
	 * From: MM.DD.YYYY (e.g. "02.29.2012")
	 * @param dateString
	 * 			: the String representation of the Date in MM.DD.YYYY format
	 * @return a Date instance
	 */
	public static Date getSimpleDateValue(String date){
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		
		try {
			// Parse date
			String[] dateTokens = date.split("[.]");
			int month = Integer.valueOf(dateTokens[0]) - 1;
			int day = Integer.valueOf(dateTokens[1]);
			int year = Integer.valueOf(dateTokens[2]);

			int hours = 0;
			int minutes = 0;
			int seconds = 1;
	
			calendar.set(year, month, day, hours, minutes, seconds);
	
			return calendar.getTime();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Converts the given simple Taiwan-style date String to a Date.
	 * From: YYYY-MM-DD (e.g. "2014-05-16")
	 * @param dateString
	 * 			: the String representation of the Date in YYYY-MM-DD format
	 * @return a Date instance
	 */
	public static Date getSimpleDateValueTaiwan(String date){
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		
		try {
			// Parse date
			String[] dateTokens = date.split("[-]");
			int year = Integer.valueOf(dateTokens[0]);
			int month = Integer.valueOf(dateTokens[1]) - 1;
			int day = Integer.valueOf(dateTokens[2]);
			

			int hours = 0;
			int minutes = 0;
			int seconds = 1;
	
			calendar.set(year, month, day, hours, minutes, seconds);
	
			return calendar.getTime();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Converts the given String to a Date.
	 * From: 2/29/2012 17:49:03
	 * @param dateString
	 * 			: the String representation of the Date
	 * @return a Date instance
	 */
	public static Date getDateValue(String date){
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		
		try {
			String[] tokens = date.split(" ");
			
			// Parse date
			String[] dateTokens = tokens[0].split("/");
			int month = Integer.valueOf(dateTokens[0]) - 1;
			int day = Integer.valueOf(dateTokens[1]);
			int year = Integer.valueOf(dateTokens[2]);
			
			// Parse time
			String[] timeTokens = tokens[1].split(":");
			int hours = Integer.valueOf(timeTokens[0]);
			int minutes = Integer.valueOf(timeTokens[1]);
			int seconds = 0;
	
			calendar.set(year, month, day, hours, minutes, seconds);
	
			return calendar.getTime();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Converts the given ISO 8601 String to a Date.
	 * From: ISO 8601 date format (http://www.iso.org/iso/iso8601) 
	 * and (http://en.wikipedia.org/wiki/ISO_8601).
	 * 
	 * ISO 8601 Format is as follows: YYYY-MM-DD[Thh:mm:ss[±hh:mm]]
	 * Where T is the separator between date and time.
	 * The time zone is defined by the number of hours and minutes after or before 
	 * UTC time, denoted by + or -.
	 * Example: 2014-03-05T23:22:19+00:00
	 * This date/time represents March 5, 2014 at 11:22:19 pm UTC time.
	 * 
	 * Taiwan local time is UTC+8, therefore an example would be:
	 * 2014-03-05T23:22:19+08:00
	 * @param dateString
	 * 			: the ISO 8601 String representation of the Date
	 * @return a Date instance
	 */
	public static Date getDateValueISO8601(String date){

		GregorianCalendar calendar = null;
		try {
			String[] tokens = date.split("T");
			
			// Parse date
			String[] dateTokens = tokens[0].split("-");
			int year = Integer.valueOf(dateTokens[0]);
			int month = Integer.valueOf(dateTokens[1]) - 1;
			int day = Integer.valueOf(dateTokens[2]);
			
			// Parse time
			if (tokens.length > 1) {
				String[] timeTokens = tokens[1].split("[\\+\\-]");
				
				// Parse local time
				String[] localTimeTokens = timeTokens[0].split(":");
				int hours = Integer.valueOf(localTimeTokens[0]);
				int minutes = Integer.valueOf(localTimeTokens[1]);
				int seconds = Integer.valueOf(localTimeTokens[2]);
				
				// Parse time zone
				if (timeTokens.length > 1) {
					String[] timeZoneTokens = timeTokens[1].split(":");
					String timeZoneHours = timeZoneTokens[0];
					String timeZoneMinutes = timeZoneTokens[1];
					
					int offsetPosition = tokens[1].indexOf('+') != -1 ? 
							tokens[1].indexOf('+') : tokens[1].indexOf('-');
					char offset = tokens[1].charAt(offsetPosition);
					
					String timeZoneString = "GMT" + offset + timeZoneHours + timeZoneMinutes;
					TimeZone timeZone = 
							TimeZone.getTimeZone(timeZoneString);
					calendar = new GregorianCalendar(timeZone);
				}
				else {
					calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
				}

				calendar.set(year, month, day, hours, minutes, seconds);
			}
			else {
				calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
				calendar.set(year, month, day);
			}

			return calendar.getTime();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets timezone from ISO 8601 date string.
	 * From: ISO 8601 date format (http://www.iso.org/iso/iso8601) 
	 * and (http://en.wikipedia.org/wiki/ISO_8601).
	 * 
	 * ISO 8601 Format is as follows: YYYY-MM-DD[Thh:mm:ss[±hh:mm]]
	 * Where T is the separator between date and time.
	 * The time zone is defined by the number of hours and minutes after or before 
	 * UTC time, denoted by + or -.
	 * Example: 2014-03-05T23:22:19+00:00
	 * This date/time represents March 5, 2014 at 11:22:19 pm UTC time.
	 * 
	 * Taiwan local time is UTC+8, therefore an example would be:
	 * 2014-03-05T23:22:19+08:00
	 * @param dateString
	 * 			: the ISO 8601 String representation of the Date
	 * @return the timezone of this date representation
	 */
	public static TimeZone getTimeZoneFromISO8601Date(String date){

		TimeZone timeZone = null;
		try {
			String[] tokens = date.split("T");
			
			// Parse time
			if (tokens.length > 1) {
				String[] timeTokens = tokens[1].split("[\\+\\-]");
				
				// Parse time zone
				if (timeTokens.length > 1) {
					String[] timeZoneTokens = timeTokens[1].split(":");
					String timeZoneHours = timeZoneTokens[0];
					String timeZoneMinutes = timeZoneTokens[1];
					
					int offsetPosition = tokens[1].indexOf('+') != -1 ? 
							tokens[1].indexOf('+') : tokens[1].indexOf('-');
					char offset = tokens[1].charAt(offsetPosition);
					
					String timeZoneString = "GMT" + offset + timeZoneHours + timeZoneMinutes;
					timeZone =
							TimeZone.getTimeZone(timeZoneString);
				}
			}

			return timeZone;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Converts the given hours and minutes to a Date
	 * 
	 * @param hours
	 *            : the hours for this time
	 * @param minutes
	 * 			  : the minutes for this time
	 * @return a Date instance
	 */
	public static Date getDateValue(int hours, int minutes) {
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, hours);
		calendar.set(GregorianCalendar.MINUTE, minutes);

		return calendar.getTime();
	}	

	/**
	 * Prints the given Date as a string (MM/dd/yyyy HH:mm)
	 * using the CST (China Standard Time) timezone
	 * 
	 * @param date
	 *            : the date to print
	 * @return a String representation of the given Date
	 */
	public static String printDateAsString(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		simpleDateFormat.setTimeZone(TAIPEI_TIMEZONE);
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Prints the given Date as a string (yyyy/mm/dd HH:mm:ss)
	 * using the CST (China Standard Time) timezone and the Dr. Storage
	 * date format for sensor data CSV files
	 * 
	 * @param date
	 *            : the date to print
	 * @return a String representation of the given Date
	 */
	public static String printDateAsStringDrStorage(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TAIPEI_TIMEZONE);
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Prints the given Date as a string (yyyy-MM-dd)
	 * using the CST (China Standard Time) timezone
	 * 
	 * @param date
	 *            : the date to print
	 * @return a String representation of the given Date
	 */
	public static String printDateAsStringWithDash(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		simpleDateFormat.setTimeZone(TAIPEI_TIMEZONE);
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Prints the given Date as a string in ISO 8601 format.
	 * (http://www.iso.org/iso/iso8601) and (http://en.wikipedia.org/wiki/ISO_8601).
	 * 
	 * ISO 8601 Format is as follows: YYYY-MM-DD[Thh:mm:ss[±hh:mm]]
	 * Where T is the separator between date and time.
	 * The time zone is defined by the number of hours and minutes after or before 
	 * UTC time, denoted by + or -.
	 * Example: 2014-03-05T23:22:19+00:00
	 * This date/time represents March 5, 2014 at 11:22:19 pm UTC time.
	 * 
	 * Taiwan local time is UTC+8, therefore an example would be:
	 * 2014-03-05T23:22:19+08:00
	 * 
	 * @param date
	 *            : the date to print
	 * @param timeZone
	 * 			  : the timezone of the date
	 * @return a String representation of the given Date and timezone
	 */
	public static String printDateAsStringISO8601(Date date, TimeZone timeZone) {
		if (date == null) {
			return "";
		}
		
		if(timeZone == null){
			timeZone = TAIPEI_TIMEZONE;
		}
		
		String dateToPrint;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		simpleDateFormat.setTimeZone(timeZone);
		
		String timeZoneString = timeZone.getDisplayName();
		String[] timeZoneTokens = timeZoneString.split("[\\+\\-]");
		if (timeZoneTokens[0].equals("GMT") && timeZoneTokens.length == 2) {
			int offsetPosition = timeZoneString.indexOf('+') != -1 ? 
					timeZoneString.indexOf('+') : timeZoneString.indexOf('-');
			char offset = timeZoneString.charAt(offsetPosition);
			timeZoneString = offset + timeZoneTokens[1];
		}
		
		dateToPrint = simpleDateFormat.format(date);
		dateToPrint += timeZoneString;
		
		return dateToPrint;
	}
	
	/**
	 * Prints the given Date as a string (MM/dd/yyyy HH:mm)
	 * 
	 * @param date
	 *            : the date to print
	 * @return a String representation of the given Date
	 */
	public static String printDateAsString(Date date, TimeZone timeZone) {
		if (date == null || timeZone == null) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		simpleDateFormat.setTimeZone(timeZone);
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Prints the given Date as time (hh:mm)
	 * 
	 * @param date
	 *            : the date to print
	 * @return a String representation of the given Date as a time format
	 */
	public static String printDateAsTime(Date date) {
		if (date == null) {
			return "";
		}
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		timeFormat.setTimeZone(TAIPEI_TIMEZONE);
		
		return timeFormat.format(date);
	}
	
	/**
	 * Prints the given Date as time in 24-hour format (HH:mm)
	 * 
	 * @param date
	 *            : the date to print
	 * @return a String representation of the given Date as a 24-hour time format
	 */
	public static String printDateAsTime24(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		simpleDateFormat.setTimeZone(TAIPEI_TIMEZONE);
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Returns the hours for this Date
	 * 
	 * @param date
	 *            : the date
	 * @return the hours corresponding to this date
	 */
	public static int getHours(Date date) {
		if (date == null) {
			return 0;
		}
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		calendar.setTime(date);
		
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * Returns the minutes for this Date
	 * 
	 * @param date
	 *            : the date
	 * @return the minutes corresponding to this date
	 */
	public static int getMinutes(Date date) {
		if (date == null) {
			return 0;
		}
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		calendar.setTime(date);
		
		return calendar.get(Calendar.MINUTE);
	}
	
	/**
	 * Get the last day of the given month and year.
	 * @param month
	 * @param year
	 * @return the maximum value for the day of the given month
	 * and year
	 */
	public static int getLastDayOfMonth(int month, int year) {
		  Calendar calendar = Calendar.getInstance();
		  int date = 1;
		  calendar.set(year, month, date);

		  return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Returns the given Date converted to Calendar
	 * 
	 * @param date
	 *            : the date
	 * @return the GregorianCalendar corresponding to this Date
	 */
	public static GregorianCalendar getCalendar(Date date) {
		if (date == null) {
			return null;
		}
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		calendar.setTime(date);
		
		return calendar;
	}
	
	/**
	 * Add minutes to a date.
	 * @param date
	 * 			: the date to modify
	 * @param minutes
	 * 			: the minutes to add
	 * @return a Date instance with the added minutes
	 */
	public static Date addMinutesToDate(Date date, int minutes) {
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);

		return calendar.getTime();
	}
	
	/**
	 * Delete days from a date.
	 * @param date
	 * 			: the date to modify
	 * @param days
	 * 			: the days to subtract
	 * @return a Date instance with the subtracted days
	 */
	public static Date subtractDaysFromDate(Date date, int days) {
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days * -1);

		return calendar.getTime();
	}
	
	/**
	 * Delete seconds from a date.
	 * @param date
	 * 			: the date to modify
	 * @param seconds
	 * 			: the seconds to subtract
	 * @return a Date instance with the subtracted seconds
	 */
	public static Date subtractSecondsFromDate(Date date, int seconds) {
		GregorianCalendar calendar = new GregorianCalendar(TAIPEI_TIMEZONE);
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, seconds * -1);

		return calendar.getTime();
	}
	
	/**
	 * Get the date closest to the date to check from a list of dates
	 * @param dates: the list of dates to check
	 * @param dateToCheck: the date to check
	 * @param maxMinuteDifference: the maximum number of minutes that can be away
	 * from the dateToCheck
	 * @return the date closest to the date to check
	 */
	public static Date getClosestDateFromList(
			Collection<Date> dates, Date dateToCheck, int maxMinuteDifference) {
		
		Date closestDate = null;
		int minMinuteDifference = Integer.MAX_VALUE;
		for (Date date : dates) {
			// The date to check must be greater than or equal to a date in the list
			if (dateToCheck.compareTo(date) >= 0) {
				// The difference in minutes between the two dates cannot exceed
				// the maxMinuteDifference
				long minutesDate = date.getTime() / 60000;
				long minutesDateToCheck = dateToCheck.getTime() / 60000;
				int minuteDifference = (int) Math.abs(minutesDate - minutesDateToCheck);
				if (minuteDifference <= maxMinuteDifference) {
					// Check current minMinuteDifference
					if (minuteDifference <= minMinuteDifference) {
						minMinuteDifference = minuteDifference;
						closestDate = date;
					}
				}
			}
		}
		
		return closestDate;
	}
}
