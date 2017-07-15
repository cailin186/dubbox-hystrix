package com.alibaba.dubbo.circuitbreak.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 日期计算函数
 * 
 * @author Administrator
 *
 */
public class DateUtil {

	private static final SimpleDateFormat sdf_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

	static final SimpleDateFormat Y_M_D = new SimpleDateFormat("yyyy-MM-dd");
	static final SimpleDateFormat Y_M_D_H_M_S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final SimpleDateFormat H_M_S = new SimpleDateFormat("HH:mm:ss");

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println("==============================");
//
//		System.out.println("当前时间：" + new Date().toLocaleString());
//		System.out.println("当天0点时间：" + sdf.format(getTimesmorning()));
//		System.out.println("当天24点时间：" + sdf.format(getTimesnight()));
//		System.out.println("本周周一0点时间：" + sdf.format(getTimesWeekmorning()));
//		System.out.println("本周周日24点时间：" + sdf.format(getTimesWeeknight()));
//		System.out.println("本月初0点时间：" + sdf.format(getTimesMonthmorning()));
//		System.out.println("本月未24点时间：" + sdf.format(getTimesMonthnight()));
//
//		System.out.println(sdf.format(getTimeNDayStart(-30)));
//		/**
//		 * 当前时间：2015-12-24 19:56:51 当天0点时间：2015-12-24 0:00:00 当天24点时间：2015-12-25
//		 * 0:00:00 本周周一0点时间：2015-12-21 0:00:00 本周周日24点时间：2015-12-28 0:00:00
//		 * 本月初0点时间：2015-12-1 0:00:00 本月未24点时间：2016-1-1 0:00:00
//		 */
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, -3);
//		System.out.println(getBetweenDates(cal.getTime(), new Date()));
//
//		System.out.println(getDateFromTimestamp(1484236800000L));
		System.out.println(judgeTime(10,48));
		System.out.println("222:" + getTimeNTimeStart(-11,0));

	}

	public static Date getDateFromTimestamp(Long time){
		return new Date(time);
	}


	public static Date getDate(int hour,int minute,int second){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,hour);
		calendar.set(Calendar.MINUTE,minute);
		calendar.set(Calendar.SECOND,second);
		return calendar.getTime();
	}
	/**
	 * 判断当前时间是否等于某个时分，24小时的 “时”
	 * @param hour
	 * @return
	 */
	public static boolean judgeTime(int hour,int minute){
		Calendar calendar = Calendar.getInstance();
		int hh = calendar.get(Calendar.HOUR_OF_DAY);
		int mm = calendar.get(Calendar.MINUTE);
		int ss = calendar.get(Calendar.SECOND);
		if (hh == hour && mm == minute ){//可能有延迟，秒就不要判断了
			return true;

		}
		return false;
	}

	/**
	 * 判断当前时间是否等于某个时，24小时的 “时”
	 * @param hour
	 * @return
	 */
	public static boolean judgeTimeHour(int hour){
		Calendar calendar = Calendar.getInstance();
		int hh = calendar.get(Calendar.HOUR_OF_DAY);
		int mm = calendar.get(Calendar.MINUTE);
		int ss = calendar.get(Calendar.SECOND);
		if (hh == hour  ){//可能有延迟，秒就不要判断了
			return true;

		}
		return false;
	}




	/**
	 * 获得当天0点时间
	 * 
	 * @return
	 */
	public static Date getTimesmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获得当天24点时间
	 * 
	 * @return
	 */
	public static Date getTimesnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 得到base基准的前一天的23:59:59
	 * @param base
	 * @return
	 */
	public static Date getTimeLastDay(Date base){
		Calendar cal = Calendar.getInstance();
		cal.setTime(base);
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();

	}
	/**
	 * 获取N小时,m分钟之前的开始时间
	 *
	 * @return
	 */
	public static Date getTimeNTimeStart(int hh,int mm) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

		cal.add(Calendar.DATE, 0);
		cal.add(Calendar.HOUR_OF_DAY, hh);
		cal.add(Calendar.MINUTE, mm);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

    /**
     * 返回从base 开始的间隔时分秒的日期
     * @param base
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getTimeNHour(Date base,int hour,int minute,int second){
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, 0);
        cal.add(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

	/**
	 * 获得本周一0点时间
	 * 
	 * @return
	 */
	public static Date getTimesWeekmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	/**
	 * 获得本周日24点时间
	 * 
	 * @return
	 */
	public static Date getTimesWeeknight() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getTimesWeekmorning());
		cal.add(Calendar.DAY_OF_WEEK, 7);
		return cal.getTime();
	}

	/**
	 * 获取N天之前的开始时间
	 * 
	 * @return
	 */
	public static Date getTimeNDayStart(int n) {
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, n);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取N天之前结束时间
	 * 
	 * @return
	 */
	public static Date getTimeNDayEnd(int n) {
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, n);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取N天之前时间
	 * 
	 * @return
	 */
	public static Date getTimeNDay(int n) {
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, n);
		return cal.getTime();
	}


	public static List<Date> getBetweenDates(Date dateFrom, Date dateTo) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateFrom);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(dateTo);
		long fromMil = calendar.getTimeInMillis();
		long toMil = calendar1.getTimeInMillis();
		while (fromMil <= toMil) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			dates.add(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 1);

			fromMil = calendar.getTimeInMillis();
		}
		return dates;
	}

	/**
	 * 获取7天之前的结束时间
	 * 
	 * @return
	 */
	public static Date getTimeEndSevenDay() {
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, -7);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获得本月第一天0点时间
	 * 
	 * @return
	 */
	public static Date getTimesMonthmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * 获得本月最后一天24点时间
	 * 
	 * @return
	 */
	public static Date getTimesMonthnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 24);
		return cal.getTime();
	}

	public static int parse_int_yyyyMMdd(Date date) {
		String format = sdf_yyyyMMdd.format(date);
		return Integer.valueOf(format);
	}

	/**
	 * 获取今天的开始时间，以毫秒时间返回
	 * 
	 * @return
	 */
	public static long getCurrentDayStartTimeInMilli(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, days);
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		return cal.getTimeInMillis();
	}

	/**
	 * 获取今天的结束时间，以毫秒时间返回
	 * 
	 * @return
	 */
	public static long getCurrentDayEndTimeInMilli(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, days);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTimeInMillis();
	}

	/**
	 * 得到当前时间的时间戳，毫秒
	 * @return
	 */
	public static long getCurrentDayInMilli(){
		return System.currentTimeMillis();
	}
	public static String parseDateToString(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String result = sdf.format(date);
		return result;
	}
	public static String parseNowDateToString() {
        Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = sdf.format(date);
		return result;
	}

	private static Long checkLong(Long timestamp) {
		if (timestamp == null) {
			return 0L;
		}
		if (timestamp < Integer.MAX_VALUE) {
			return timestamp * 1000;
		}
		return timestamp;
	}

	public static String toDateString(Long timestamp) {
		return Y_M_D.format(new Date(checkLong(timestamp)));
	}

	public static String toTimeString(Long timestamp) {
		return H_M_S.format(new Date(checkLong(timestamp)));
	}

	public static String toDateTimeString(Long timestamp) {
		return Y_M_D_H_M_S.format(new Date(checkLong(timestamp)));
	}

	static final Pattern REG_LONG = Pattern.compile("^\\d{1,19}$");

	public static Long toTime(String dateTime) {
		if (dateTime == null) {
			return null;
		}
		if (REG_LONG.matcher(dateTime).matches()) {
			return Long.parseLong(dateTime);
		}
		if (dateTime.length() != 19) {
			return null;
		}
		try {
			return Y_M_D_H_M_S.parse(dateTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
