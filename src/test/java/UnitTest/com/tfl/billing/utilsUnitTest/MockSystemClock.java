package UnitTest.com.tfl.billing.utilsUnitTest;

import com.tfl.billing.Clock;

import java.time.LocalDateTime;
import java.time.ZoneId;


public class MockSystemClock implements Clock {
    private LocalDateTime time;
    private LocalDateTime now;
    private long epoch = 0;
    // need to set just the hour, min and seconds, we get the current day and month
    private int day;
    private int month;
    private int year;

    public MockSystemClock(){
        now = LocalDateTime.now();
        month = now.getMonthValue();
        day = now.getDayOfMonth();
        year = now.getYear();
    }

    @Override
    public long now() {
        return epoch;
    }

    public void setCurrentTime(int hour, int min, int sec) {
        // need to transform in in Epoch milliseconds so we use LocalDateTime, but we need to set just hour as system resets every night
        time = LocalDateTime.of(year,month,day,hour, min, sec);
        ZoneId zoneId = ZoneId.systemDefault();
        epoch = time.atZone(zoneId).toInstant().toEpochMilli();
    }
    public String getyearMonthDay(){
        StringBuilder str = new StringBuilder();
        str.append(day);
        str.append('.');
        str.append(month);
        str.append('.');
        str.append(year);
        return str.toString();
    }
}