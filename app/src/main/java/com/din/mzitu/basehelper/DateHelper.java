package com.din.mzitu.basehelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    private static final String[] WEEKS = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME = "HH:mm";
    private static final String DATE_TIME_SECOND = "yyyy-MM-dd_HH:mm:ss_SSS";

    /**
     * 用于格式化日期,作为日志文件名的一部分
     *
     * @return
     */
    public static String getTimeSecond() {
        return dateFormat(DATE_TIME_SECOND).format(new Date());                  // 打印的时间
    }

    /**
     * 当前时间戳转日期加时间
     *
     * @return
     */
    public static String getCurrentDateTime() {
        return dateFormat(DATE + " " + TIME).format(new Date());        // 将时间戳转化为SimpleDateFormat格式
    }

    /**
     * 当前时间戳转日期
     *
     * @return
     */
    public static String getCurrentDate() {
        return dateFormat(DATE).format(new Date());                     // 将时间戳转化为SimpleDateFormat格式;
    }

    /**
     * 当前时间戳转时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return dateFormat(TIME).format(new Date());                    // 将时间戳转化为SimpleDateFormat格式;
    }

    /**
     * 任意时间戳转日期
     *
     * @param longDate
     * @return
     */
    public static String formatLongDate(String longDate) {
        return dateFormat(DATE).format(Long.parseLong(longDate));            // 格式化时间;
    }

    /**
     * 格式化日期和时间
     *
     * @return
     */
    public static String formatDateTime(String date) {
        return dateFormat(DATE + TIME).format(date);                        // 将时间戳转化为SimpleDateFormat格式
    }

    /**
     * 任意时间戳转时间
     *
     * @param longDate
     * @return
     */
    public static String formatLongDateTime(String longDate) {
        return dateFormat(DATE + TIME).format(Long.parseLong(longDate));   // 格式化时间
    }

    /**
     * 任意时间戳转星期
     *
     * @param date
     * @return
     */
    public static String date2Week(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat(DATE).parse(date));             // 设置格式化后的时间
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return WEEKS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 日期转时间戳
     *
     * @param date
     * @return
     */
    public static long date2Long(String date) {
        try {
            Date mDate = dateFormat(DATE + TIME).parse(date);
            return mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 日期View获取PickerDialog的日期
     *
     * @param context
     */
    public static void dateTimeDialog(final Context context, final TextView date, final TextView time) {
        final Calendar calendarDate = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //--------monthOfYear 得到的月份会减1所以我们要加1
                String mMonth = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                String mDay = dayOfMonth < 10 ? "0" + (dayOfMonth) : String.valueOf(dayOfMonth);

                date.setText(String.valueOf(year) + "-" + mMonth + "/" + mDay);
                Calendar calendarTime = Calendar.getInstance();
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //------对一位数的时间在其前面加一个0
                        String mHour = hourOfDay < 10 ? ("0" + hourOfDay) : String.valueOf(hourOfDay);
                        String mMinute = minute < 10 ? ("0" + minute) : String.valueOf(minute);

                        time.setText(mHour + ":" + mMinute);
                    }
                }, calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE), true).show();

            }

        }, calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 日期View获取PickerDialog的日期
     *
     * @param context
     */
    public static void dateDialog(Context context, final TextView date) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //------monthOfYear 得到的月份会减1所以我们要加1
                String mMonth = monthOfYear < 9 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                String mDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

                date.setText(String.valueOf(year) + "-" + mMonth + "-" + mDay);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 日期View获取PickerDialog的日期
     *
     * @param context
     */

    public static void dateDialog(Context context, final TextView month, final TextView day) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //--------monthOfYear 得到的月份会减1所以我们要加1
                String mMonth = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                String mDay = dayOfMonth < 10 ? "0" + (dayOfMonth) : String.valueOf(dayOfMonth);

                month.setText(mDay);
                day.setText("-" + mMonth + "月");
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 时间View获取PickerDialog的时间
     *
     * @param context
     * @param hourText
     * @param minuteText
     */
    public static void timeDialog(Context context, final TextView hourText, final TextView minuteText) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourText.setText(Integer.toString(hourOfDay));
                minuteText.setText(Integer.toString(minute));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }


    /**
     * 获取该时间在该星期的第一天和最后一天日期
     *
     * @param date
     * @return
     */
    public static String getFirstAndLastOfWeek(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat(DATE).parse(date));                 // 设置格式化的日期
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int day = (calendar.get(Calendar.DAY_OF_WEEK) == 1) ? -6 : 2 - calendar.get(Calendar.DAY_OF_WEEK);

        calendar.add(Calendar.DAY_OF_WEEK, day);
        String firstDate = dateFormat(DATE).format(calendar.getTime());     // 所在星期开始日期
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        String lastDate = dateFormat(DATE).format(calendar.getTime());      // 所在星期结束日期
        return firstDate + "-" + lastDate;
    }

    /**
     * 现在的日期
     *
     * @return
     */
    public static String nowDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 现在的时间
     *
     * @return
     */
    public static String nowTime() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 日期格式化
     *
     * @param format
     * @return
     */
    private static SimpleDateFormat dateFormat(String format) {
        return new SimpleDateFormat(format);
    }
}