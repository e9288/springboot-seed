/*
 * @(#)DateUtil.java	1.0	2009. 08. 30.
 * 
 * Copyright (c) 2009 TA Networks
 * All rights reserved.
 */
package com.iandna.gateway.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 공통 Utility
 * Class 설명 : 배치업무에서 사용되는 공통 유틸리티 메소드 정의
 * @version 1.0
 * @since   2009. 03. 13
 * @author  DH.KANG
 */
public class DateUtil {
	private static final Log logger = LogFactory.getLog (DateUtil.class);
	
	public static String formatDateTime(String format) {
		return DateFormatUtils.format(System.currentTimeMillis(), format);
	}
	
	public static String formatDateTime(String date, String format) {
		GregorianCalendar calandar = new GregorianCalendar();
		calandar.set(Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.toString().substring(4,6))-1, Integer.parseInt(date.toString().substring(6,8)));
		
		return DateFormatUtils.format(calandar.getTime(), format);
	}
	
	/**
	 * 현재 일자와 시간을 반환한다. (17자리 : yyyyMMddHHmmssSSS)
	 * @return String
	 */
	public static String getCurrentDateTime17() {
		return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
	}

	/**
	 * 현재 일자와 시간을 반환한다. (16자리 : yyyyMMddHHmmssSS)
	 * @return String
	 */
	public static String getCurrentDateTime16() {
		String dateTime = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
		return dateTime.substring(0, 16);
	}

	/**
	 * 현재 일자와 시간을 반환한다. (14자리 : yyyyMMddHHmmss)
	 * @return String
	 */
	public static String getCurrentDateTime() {
		return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss");
	}

	/**
	 * 현재 일자와 시간을 반환한다. (12자리 : yyMMddHHmmss)
	 * @return String
	 */
	public static String getCurrentDateTime12() {
		return DateFormatUtils.format(System.currentTimeMillis(), "yyMMddHHmmss");
	}

	/**
	 * 현재일자를 반환한다. (8자리 : yyyyMMdd)
	 * @return String
	 */
	public static String getCurrentDate() {
		return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd"); 
	}

	/**
	 * 현재일자를 반환한다. (6자리 : yyMMdd)
	 * @return String
	 */
	public static String getCurrentDate6() {
		return DateFormatUtils.format(System.currentTimeMillis(), "yyMMdd"); 
	}

	/**
	 * 현재시간을 반환한다. (6자리 : HHmmss)
	 * @return String
	 */
	public static String getCurrentTime() {
		return DateFormatUtils.format(System.currentTimeMillis(), "HHmmss");
	}

    /**
     * 월,일에 현제 년도를 추가 반환한다. (8자리 : yyyyMMdd)
     * @param mmdd 월일
     * @return String
     */
    public static String getCalYear( String mmdd ) {
        
    	String systemDate = getCurrentDate();
    	return systemDate.substring(0, 4) + mmdd;
    }
    
	/**
	 * 오늘을 기준으로 파라메터로 입력한 값 이후의 일자를 반환한다. (8자리 : yyyyMMdd)
	 * @param day 합산할 일
	 * @return String
	 */
	public static String getDate(int day) {
		GregorianCalendar calandar = new GregorianCalendar();
		calandar.add(Calendar.DAY_OF_YEAR, day);
		return DateFormatUtils.format(calandar.getTime(), "yyyyMMdd");
	}
	
	/**
	 * 입력기준일로 부터 월을 계산하여 계산된 일자를 반환한다. (8자리 : yyyyMMdd)
	 * @param day 합산할 일
	 * @return String
	 */
	public static String getCalDateMonth(String date, int month) {
		GregorianCalendar calandar = new GregorianCalendar();

		calandar.set(Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.toString().substring(4,6))-1, Integer.parseInt(date.toString().substring(6,8)));
		calandar.add(Calendar.MONTH, month);
		return DateFormatUtils.format(calandar.getTime(), "yyyyMMdd");
	}
	
	
	/**
	 * 190920 dhkim : 입력된 날짜의 요일을 반환한다.
	 * @param date : 'yyyyMMdd'
	 * @return : 1:일 ~ 7:토
	 */
	public static int getDay(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date nDate;
		try {
			nDate = dateFormat.parse(date);
			Calendar cal = Calendar.getInstance() ;
		    cal.setTime(nDate);
		    int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;
		    return dayNum;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -9999;
		}
	}
	
	/**
	 * 190920 dhkim : 입력된 날짜간의 차이를 반환한다.
	 * @param date : 'yyyyMMdd' date1 > date2
	 * @return
	 */
	public static long getGapOfDays(String date1, String date2) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			Date startDate = dateFormat.parse(date1);
            Date endDate = dateFormat.parse(date2);
 
            //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
            long dayGap = (startDate.getTime() - endDate.getTime()) / (24*60*60*1000);
            return dayGap;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -9999;
		}
	}
	
	/**
	 * 190920 dhkim : (입력일+n)일 구하기
	 * @param date : 'yyyyMMdd'
	 * @return
	 */
	public static String getNextDate(String date, int n) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			  Calendar c = Calendar.getInstance();
			  Date d = dateFormat.parse(date);
			  
			  c.setTime(d);
			  c.add(Calendar.DATE,1);
			  date = dateFormat.format(c.getTime());
			  return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-9999";
		}
	}
	

	/**
	 * 190920 dhkim : (입력월+n)월 구하기
	 * @param date : yyyyMM
	 * @return 다음달
	 */
	public static String getNextMonth(String date, int n) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		try {
			  Calendar c = Calendar.getInstance();
			  Date d = dateFormat.parse(date);
			  
			  c.setTime(d);
			  c.add(Calendar.MONTH, n);
			  date = dateFormat.format(c.getTime());
			  return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-9999";
		}
	}
	
	/**
	 * 190920 dhkim : 개월 차 구하기
	 * @param date1,2 : yyyyMM , date1 > date2
	 * @return 다음달
	 */
	public static int getGapOfMonths(String date1, String date2) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		try {
			int strtYear = Integer.parseInt(date1.substring(0,4));
			int strtMonth = Integer.parseInt(date1.substring(4,6));
			int endYear = Integer.parseInt(date2.substring(0,4));
			int endMonth = Integer.parseInt(date2.substring(4,6));
			int month = (strtYear - endYear)* 12 + (strtMonth - endMonth);
			return month;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -9999;
		}
	}
	
	/**
	 * 입력한 두 날짜의 차이를 반환한다. (8자리 : yyyyMMdd)
	 * @param day 날짜 차이일
	 * @return String
	 */
	public static int getDayDiff(String stDate, String enDate) {
		GregorianCalendar stCalandar = new GregorianCalendar();
		GregorianCalendar enCalandar = new GregorianCalendar();
		stCalandar.set(Integer.parseInt(stDate.substring(0,4)), Integer.parseInt(stDate.toString().substring(4,6))-1, Integer.parseInt(stDate.toString().substring(6,8)));
		enCalandar.set(Integer.parseInt(enDate.substring(0,4)), Integer.parseInt(enDate.toString().substring(4,6))-1, Integer.parseInt(enDate.toString().substring(6,8)));
		
		long diffSec = (enCalandar.getTimeInMillis() - stCalandar.getTimeInMillis())/1000;
		int difDay = (int)diffSec/(60*60*24);
		
		return difDay;
	}
	
	/**
	 * 20191106 dhkim
	 * 10분전 시간 반환 (12자리 : yyMMddHHmmss)
	 * @return String
	 */
	public static String getBefTenMinDateTime12() {
		long currentTime = Long.parseLong(DateFormatUtils.format(System.currentTimeMillis(), "yyMMddHHmmss").toString());
		long befTenTime = currentTime - 1000; 
		return Long.toString(befTenTime);
	}
	
	public static String addHyphen(String date) {
		date = date.substring(0, 4) + "-" + date.substring(4,6) + "-" + date.substring(6,8);
		return date;
	}
	
	/**
	 * 201201007 dhkim
	 * 10분후 시간 반환 (14자리 : yyyyMMddHHmmss)
	 * @return String
	 */
	public static String getAftTenMinDateTime14() {
		long currentTime = Long.parseLong(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss").toString());
		long befTenTime = currentTime + 1000; 
		return Long.toString(befTenTime);
	}
	
	/**
	 * 오늘을 기준으로 파라메터로 입력한 값 이후의 일자를 반환한다. (8자리 : yyyyMMdd)
	 * @param day 합산할 일
	 * @return String
	 */
	public static String addDateByDay(int day) {
		GregorianCalendar calandar = new GregorianCalendar();
		calandar.add(Calendar.DAY_OF_YEAR, day);
		return DateFormatUtils.format(calandar.getTime(), "yyyyMMdd");
	}
	
	/**
	 * 현재 일자 + 입력 시간(초) 날짜를 반환한다. (18자리 : yyyyMMdd)
	 * @return String
	 */
	public static String getDateByPlusMilliSeconds(long milliSeconds) {
		return DateFormatUtils.format(System.currentTimeMillis() + milliSeconds, "yyyyMMdd");
	}
	
	/**
	 * 20190820 dhkim
	 * 입력된 날짜(출산예정일) 기준으로 임신 날짜를 반환한다. (6자리 : yyyyMMdd)
	 * @return String
	 */
	public static String getFregDate(String date) {
		GregorianCalendar calandar = new GregorianCalendar();
		
		calandar.set(Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.toString().substring(4,6))-1, Integer.parseInt(date.toString().substring(6,8)));
		calandar.add(calandar.DAY_OF_MONTH, -280);
		
		return DateFormatUtils.format(calandar.getTime(), "yyyyMMdd");
	}
	
	/**
	 * 20201209 dhkim
	 * 날짜포멧 -, . 제거
	 * @return String
	 */
	public static String setDateFormat(String date) {
		StringUtils.replace(date, "-", "");
		StringUtils.replace(date, ".", "");
		return date;
	}
	
	/**
	 * 20201209 dhkim
	 * 시간포멧 : 제거
	 * @return String
	 */
	public static String setTimeFormat(String time) {
		StringUtils.replace(time, ":", "");
		return time;
	}
	
	public static void main(String[] args) {
		System.out.println(getCurrentDateTime12().substring(0,6));
		System.out.println(getCurrentDateTime12().substring(6,10));
		System.out.println(getDate(-1).substring(2));
	}
}
