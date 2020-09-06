import java.io.Serializable;

public class MyDate implements Comparable<MyDate>, Serializable {
    private int year, month, date, weekday;
    private static int thisYear;
    private static int weekInit = 3;


    static {
        thisYear = 2020;
    }

    public MyDate(int year, int month, int date) throws DateFormatException {
        set(year, month, date);
    }

    //注意这里用的跟老师的不一样
    public MyDate(String string) throws DateFormatException {
        String[] sp = {"年", "月", "日"};
        //split函数切割字符串
        String[] data_year = string.split(sp[0]);
        String[] data_month = data_year[1].split(sp[1]);
        String[] data_day = data_month[1].split(sp[2]);
        int y = Integer.parseInt(data_year[0]);
        int m = Integer.parseInt(data_month[0]);
        int d = Integer.parseInt(data_day[0]);
        this.set(y, m, d);
    }

    //指定缺省日期
    public MyDate() {
        this(2020, 12, 1);
    }

    //拷贝构造方法
    public MyDate(MyDate date) {
        this.year = date.year;
        this.month = date.month;
        this.date = date.date;
    }

    //设置日期
    void set(int year, int month, int date) throws DateFormatException {
//        this.date = (date >= 1 && date <= 31) ? date : 1;
//        this.month = (month >= 1 && month <= 12) ? month : 1;
//        this.year = year;
        if (year <= -2000 || year >= 2500) {
            throw new DateFormatException(year + "年份不合适");
        }
        if (month < 1 || month > 12) {
            throw new DateFormatException(month + "月份不合适");
        }
        if (date < 1 || date > MyDate.dayOfMonth(year, month)) {
            throw new DateFormatException(date + "日期不合适");
        }
        this.year = year;
        this.month = month;
        this.date = date;
    }

    //重载
    void set(MyDate myDate) {
        set(myDate.year, myDate.month, date = myDate.date);
    }

    //分别获得年月日
    int getYear() {
        return year;
    }

    int getMonth() {
        return month;
    }

    int getDate() {
        return date;
    }

    int getWeek() {
        int date_total = 0;
//        计算总天数
//        int count = 0;
        for (int i = thisYear; i <= this.year; i++) {
//            count ++;
            if (i != this.year) {
                date_total += isLeapYear(i - 1) ? 365 : 366;
                continue;
            }
            //一年
            switch (this.month - 1) {
                case 11:
                    date_total = date_total + 30;
                case 10:
                    date_total = date_total + 31;
                case 9:
                    date_total = date_total + 30;
                case 8:
                    date_total = date_total + 31;
                case 7:
                    date_total = date_total + 31;
                case 6:
                    date_total = date_total + 30;
                case 5:
                    date_total = date_total + 31;
                case 4:
                    date_total = date_total + 30;
                case 3:
                    date_total = date_total + 31;
                case 2:
                    date_total += (isLeapYear(i)) ? 29 : 28;
                case 1:
                    date_total = date_total + 31;
                case 0:
                    date_total = date_total + this.date - 1;
                default:
                    break;
            }
        }

        weekday = (date_total + weekInit) % 7;
        return weekday;
    }


    @Override
    public String toString() {
        return year + "年" + String.format("%d", month) + "月" + String.format("%d", date) + "日";
    }

    public static int getThisYear() {
        return thisYear;
    }

    //获得今年年份是否为闰年
    public static boolean isLeapYear(int year) {
        return year % 400 == 0 || year % 100 != 0 && year % 4 == 0;
    }

    //当前日期的年份是否为闰年，重载
    public boolean isLeapYear() {
        return isLeapYear(this.year);
    }


    public boolean equals(MyDate myDate) {
        return myDate.year == this.year && myDate.date == this.date && myDate.month == this.month;
    }

    public static int dayOfMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return (isLeapYear(year)) ? 29 : 28;
            default:
                return 0;
        }
    }

    public int dayOfMonth() {
        return dayOfMonth(this.year, this.month);
    }

    //this引用改成一天后的日期
    public void tomorrow() {
        this.date = this.date % dayOfMonth(this.year, this.month) + 1;
        if (this.date == 1) {
            this.month = this.month % 12 + 1;
            if (this.month == 1) {
                year++;
            }
        }

    }

    //上下两种算法,这里先深拷贝
    public MyDate yesterday() {
        MyDate md = new MyDate(this);
        md.date--;
        if (md.date == 0) {
            md.month = (md.month - 2 + 12) % 12 + 1;
            if (md.month == 12) {
                md.year--;
            }
            md.date = dayOfMonth(md.year, md.month);
        }
        return md;
    }

    //返回当前日期的n天之后
    public MyDate dayAfter(int n) {
        //把一个月补齐之后多余的天数
        int extra = 0;
        MyDate myDate = new MyDate(this);
        //当是同一个月时
        myDate.date = myDate.date + n;
        //大于一个月的时候
        if (myDate.date > myDate.dayOfMonth()) {
            myDate.month = myDate.month + 1;
            //多余的天数
            extra = myDate.date - myDate.dayOfMonth();
            //加年
            while (extra / (isLeapYear(myDate.year) ? 365 : 366) != 0) {
                this.year++;
                extra = extra - (isLeapYear(myDate.year - 1) ? 365 : 366);
            }
            //加月
            while (extra / dayOfMonth(myDate.year, myDate.month) != 0) {
                extra = extra - dayOfMonth(myDate.year, myDate.month);
                myDate.month = myDate.month + 1;
                if (myDate.month == 13) {
                    myDate.year = myDate.year + 1;
                    myDate.month = 1;
                }
            }
            if (extra / dayOfMonth(myDate.year, myDate.month) == 0) {
                myDate.date = extra;
                month = month + 1;
            }
        }
        return myDate;
    }

    @Override
    public int compareTo(MyDate o) {
        if (this.year == o.year && this.month == o.month && this.date == o.date) {
            return 0;
        }

        return (this.year > o.year || (this.year == o.year && this.month > o.month)
                || (this.year == o.year && this.month == o.month && this.date > o.date)) ? 1 : -1;
    }
}

