package com.whxm.harbor.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeepAliveDetect {
    Interval value() default Interval.HALF_HOUR;//默认30分钟检测一次

    enum Interval {

        HALF_HOUR(30, TimeUnit.MINUTES),
        ONE_HOUR(60, TimeUnit.HOURS),
        TWO_HOUR(120, TimeUnit.HOURS);
        private long time;
        private TimeUnit unit;

        Interval(long time, TimeUnit unit) {
            this.time = time;
            this.unit = unit;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }
    }
}
