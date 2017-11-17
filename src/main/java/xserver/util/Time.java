package xserver.util;

import java.text.DateFormat;

// @javadoc

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Time {

    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_HOUR_TIME_FORMAT = "yyyy-MM-dd HH";

    /**
     * 格式化日期
     *
     * @param date
     *            日期
     * @return
     */
    public static String getDateStr(Date date) {
        return new SimpleDateFormat(Time.DEFAULT_DATE_FORMAT).format(date);
    }

    /**
     * 格式化日期时间
     *
     * @param date
     *            日期时间
     * @return
     */
    public static String getDateTimeStr(Date date) {
        return new SimpleDateFormat(Time.DEFAULT_DATE_TIME_FORMAT).format(date);
    }

    /**
     * 格式化日期时间
     *
     * @param date
     *            日期时间
     * @param format
     *            指定格式
     * @return
     */
    public static String getDateTimeStr(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 返回 yyyy-MM-dd 格式的当前日期字符串
     *
     * @return
     */
    public static String getDateStr() {
        return new SimpleDateFormat(Time.DEFAULT_DATE_FORMAT).format(new Date(Time.currentTimeMillis()));
    }

    /**
     * 返回当前时间点时间戳
     *
     * @throws ParseException
     */
    public static Long getHourTime(Date date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(Time.DEFAULT_DATE_HOUR_TIME_FORMAT);
        String temp = sf.format(date);
        return sf.parse(temp).getTime();
    }

    /**
     * 格式化当前日期时间
     *
     * @return
     */
    public static String getDateTimeStr() {
        return new SimpleDateFormat(Time.DEFAULT_DATE_TIME_FORMAT).format(new Date(Time.currentTimeMillis()));
    }

    public static long parseTimeStr(String str, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(str).getTime();
    }

    /**
     * 获取每周第day天早晨的时间戳，从周一开始作为第一天
     *
     * @param day
     *            1、周一 2、周二···7、周日
     * @return
     */
    public static Long getDateBeginOfWeek(int day) {
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        dow = (dow - 1) == 0 ? 7 : dow;
        Long date = Time.currentTimeMillis() - (dow - day) * 24 * 3600 * 1000;
        return date;
    }

    public static long getDayBefore(int dayBefore) throws ParseException {
        return Time.parseTimeStr(Time.getDateStr(), Time.DEFAULT_DATE_FORMAT) - dayBefore * 24 * 60 * 60;
    }

    public static boolean examBetweenTime(long time, int start, int end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (start <= hour && hour <= end) {
            return true;
        }
        return false;
    }

    /**
     * 系统当前时间
     *
     * @return
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static Date getDateEnd(Date date) {
        SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            try {
                date = Time.getDateBeforeOrAfterDays(date, 1);
                date = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.CHINA).parse(ymdFormat.format(date));
                Date endDate = new Date();
                endDate.setTime(date.getTime() - 1000);

                return endDate;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Date getDateBeforeOrAfterDays(Date date, int days) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + days);
        return now.getTime();
    }

    public static String eliminateDateZero(String date) {
        String eliminateAfterDate = "";
        String[] years = date.split("年");
        String month = "";
        String day = "";
        String hour = "";
        String minu = "";
        String sec = "";
        // 处理年
        if (date.endsWith("年")) {
            return eliminateAfterDate + years[0] + "年";
        } else {
            if (years.length > 1) {
                eliminateAfterDate += years[0] + "年";
                month = years[1];
            } else {
                month = years[0];
            }
        }
        // 处理月
        String[] months = month.split("月");
        if (date.endsWith("月")) {
            if (months[0].startsWith("0")) {
                eliminateAfterDate += months[0].substring(1) + "月";
            } else {
                eliminateAfterDate += months[0] + "月";
            }
            return eliminateAfterDate;
        } else {
            if (months.length > 1) {
                if (months[0].startsWith("0")) {
                    eliminateAfterDate += months[0].substring(1) + "月";
                } else {
                    eliminateAfterDate += months[0] + "月";
                }
                day = months[1];
            } else {
                day = months[0];
            }
        }
        // 处理日
        String[] days = day.split("日");
        if (date.endsWith("日")) {
            if (days[0].startsWith("0")) {
                eliminateAfterDate += days[0].substring(1) + "日";
            } else {
                eliminateAfterDate += days[0] + "日";
            }
            return eliminateAfterDate;
        } else {
            if (days.length > 1) {
                if (days[0].startsWith("0")) {
                    eliminateAfterDate += days[0].substring(1) + "日";
                } else {
                    eliminateAfterDate += days[0] + "日";
                }
                hour = days[1];
            } else {
                hour = days[0];
            }
        }
        // 处理小时
        String[] hours = hour.split("点");
        if (date.endsWith("点")) {
            if (hours[0].substring(1).startsWith("0")) {
                eliminateAfterDate += hours[0].substring(2) + "点";
            } else {
                eliminateAfterDate += hours[0] + "点";
            }
            return eliminateAfterDate;
        } else {
            if (hours.length > 1) {
                if (hours[0].substring(1).startsWith("0")) {
                    eliminateAfterDate += hours[0].substring(2) + "点";
                } else {
                    eliminateAfterDate += hours[0] + "点";
                }
                minu = hours[1];
            } else {
                minu = hours[0];
            }
        }
        // 处理分钟
        String[] minutes = minu.split("分");
        if (date.endsWith("分")) {
            if (minutes[0].startsWith("0")) {
                eliminateAfterDate += minutes[0].substring(1) + "分";
            } else {
                eliminateAfterDate += minutes[0] + "分";
            }
            return eliminateAfterDate;
        } else {
            if (minutes.length > 1) {
                if (minutes[0].startsWith("0")) {
                    eliminateAfterDate += minutes[0].substring(1) + "分";
                } else {
                    eliminateAfterDate += minutes[0] + "分";
                }
                sec = minutes[1];
            } else {
                sec = minutes[0];
            }
        }
        if (sec.startsWith("0")) {
            eliminateAfterDate += sec.substring(1);
        } else {
            eliminateAfterDate += sec;
        }
        return eliminateAfterDate;
    }

    public static String eliminateDateZero(String date, String monthSpilt, String daySplit, String hourSplit,
            String minuSplit) {
        String eliminateAfterDate = "";
        String[] years = date.split("年");
        String month = "";
        String day = "";
        String hour = "";
        String minu = "";
        String sec = "";
        // 处理年
        if (date.endsWith("年")) {
            return eliminateAfterDate + years[0] + "年";
        } else {
            if (years.length > 1) {
                eliminateAfterDate += years[0] + "年";
                month = years[1];
            } else {
                month = years[0];
            }
        }
        // 处理月
        String[] months = month.split(monthSpilt);
        if (date.endsWith(monthSpilt)) {
            if (months[0].startsWith("0")) {
                eliminateAfterDate += months[0].substring(1) + monthSpilt;
            } else {
                eliminateAfterDate += months[0] + monthSpilt;
            }
            return eliminateAfterDate;
        } else {
            if (months.length > 1) {
                if (months[0].startsWith("0")) {
                    eliminateAfterDate += months[0].substring(1) + monthSpilt;
                } else {
                    eliminateAfterDate += months[0] + monthSpilt;
                }
                day = months[1];
            } else {
                day = months[0];
            }
        }
        // 处理日
        String[] days = day.split(daySplit);
        if (date.endsWith(daySplit)) {
            if (days[0].startsWith("0")) {
                eliminateAfterDate += days[0].substring(1) + daySplit;
            } else {
                eliminateAfterDate += days[0] + daySplit;
            }
            return eliminateAfterDate;
        } else {
            if (days.length > 1) {
                if (days[0].startsWith("0")) {
                    eliminateAfterDate += days[0].substring(1) + daySplit;
                } else {
                    eliminateAfterDate += days[0] + daySplit;
                }
                hour = days[1];
            } else {
                hour = days[0];
            }
        }
        // 处理小时
        String[] hours = hour.split(hourSplit);
        if (date.endsWith(hourSplit)) {
            if (hours[0].startsWith("0")) {
                eliminateAfterDate += hours[0].substring(2) + hourSplit;
            } else {
                eliminateAfterDate += hours[0] + hourSplit;
            }
            return eliminateAfterDate;
        } else {
            if (hours.length > 1) {
                if (hours[0].startsWith("0")) {
                    eliminateAfterDate += hours[0].substring(2) + hourSplit;
                } else {
                    eliminateAfterDate += hours[0] + hourSplit;
                }
                minu = hours[1];
            } else {
                minu = hours[0];
            }
        }
        // 处理分钟
        String[] minutes = minu.split(minuSplit);
        if (date.endsWith(minuSplit)) {
            if (minutes[0].startsWith("0")) {
                eliminateAfterDate += minutes[0].substring(1) + minuSplit;
            } else {
                eliminateAfterDate += minutes[0] + minuSplit;
            }
            return eliminateAfterDate;
        } else {
            if (minutes.length > 1) {
                if (minutes[0].startsWith("0")) {
                    eliminateAfterDate += minutes[0].substring(1) + minuSplit;
                } else {
                    eliminateAfterDate += minutes[0] + minuSplit;
                }
                sec = minutes[1];
            } else {
                sec = minutes[0];
            }
        }
        if (sec.startsWith("0")) {
            eliminateAfterDate += sec.substring(1);
        } else {
            eliminateAfterDate += sec;
        }
        return eliminateAfterDate;
    }

}
