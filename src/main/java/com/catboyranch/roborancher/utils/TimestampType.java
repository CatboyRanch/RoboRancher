package com.catboyranch.roborancher.utils;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public enum TimestampType {
    SHORT_TIME("t"),
    LONG_TIME("T"),
    SHORT_DATE("d"),
    LONG_DATE("D"),
    LONG_DATE_SHORT_TIME("f"),
    LONG_DATE_DOW_SHORT_TIME("F"),
    RELATIVE("R");

    private final String sign;

    TimestampType(String sign) {
        this.sign = sign;
    }

    public String formatNow() {
        return format(TimeUtils.getUnixTime());
    }

    public String format(@Nullable OffsetDateTime time) {
        if(time == null)
            return format(0);
        return format(time.toEpochSecond());
    }

    public String format(LocalDateTime time) {
        long unix = time.atZone(ZoneId.systemDefault()).toEpochSecond();
        return format(unix);
    }

    public String format(long unixTime) {
        return String.format("<t:%s:%s>", unixTime, sign);
    }
}
