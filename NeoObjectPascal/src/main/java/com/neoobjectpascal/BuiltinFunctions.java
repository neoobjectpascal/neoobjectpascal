package com.neoobjectpascal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Always-available built-in functions for the Date / Time / DateTime and Currency types.
 * Registered in the Interpreter constructor (no {@code uses} required, since the types are built-in).
 * Date/Time/DateTime are backed by java.time (LocalDate/LocalTime/LocalDateTime); Currency by BigDecimal.
 */
final class BuiltinFunctions {

    private BuiltinFunctions() {}

    static void register(Interpreter interp) {
        // ---- Creation ----
        interp.registerNative("now",         (a, i) -> LocalDateTime.now());
        interp.registerNative("today",       (a, i) -> LocalDate.now());
        interp.registerNative("currentTime", (a, i) -> LocalTime.now());
        interp.registerNative("date",        (a, i) -> LocalDate.of(iarg(a, 0), iarg(a, 1), iarg(a, 2)));
        interp.registerNative("time",        (a, i) -> LocalTime.of(iarg(a, 0), iarg(a, 1), a.size() > 2 ? iarg(a, 2) : 0));
        interp.registerNative("dateTime",    (a, i) -> LocalDateTime.of(iarg(a, 0), iarg(a, 1), iarg(a, 2),
                                                        iarg(a, 3), iarg(a, 4), a.size() > 5 ? iarg(a, 5) : 0));
        interp.registerNative("parseDate",     (a, i) -> LocalDate.parse(sarg(a, 0).trim()));
        interp.registerNative("parseTime",     (a, i) -> LocalTime.parse(sarg(a, 0).trim()));
        interp.registerNative("parseDateTime", (a, i) -> LocalDateTime.parse(sarg(a, 0).trim().replace(' ', 'T')));

        // ---- Components ----
        interp.registerNative("year",   (a, i) -> yearOf(a.get(0)));
        interp.registerNative("month",  (a, i) -> monthOf(a.get(0)));
        interp.registerNative("day",    (a, i) -> dayOf(a.get(0)));
        interp.registerNative("hour",   (a, i) -> hourOf(a.get(0)));
        interp.registerNative("minute", (a, i) -> minuteOf(a.get(0)));
        interp.registerNative("second", (a, i) -> secondOf(a.get(0)));
        interp.registerNative("dayOfWeek", (a, i) -> asDate(a.get(0)).getDayOfWeek().getValue()); // 1=Mon..7=Sun

        // ---- Arithmetic ----
        interp.registerNative("addDays",   (a, i) -> plusDays(a.get(0), larg(a, 1)));
        interp.registerNative("addMonths", (a, i) -> plusMonths(a.get(0), larg(a, 1)));
        interp.registerNative("addYears",  (a, i) -> plusYears(a.get(0), larg(a, 1)));
        interp.registerNative("addHours",  (a, i) -> plusHours(a.get(0), larg(a, 1)));
        interp.registerNative("addMinutes",(a, i) -> plusMinutes(a.get(0), larg(a, 1)));
        interp.registerNative("daysBetween",  (a, i) -> (int) ChronoUnit.DAYS.between(asDate(a.get(0)), asDate(a.get(1))));
        interp.registerNative("hoursBetween", (a, i) -> (int) ChronoUnit.HOURS.between(asDateTime(a.get(0)), asDateTime(a.get(1))));

        // ---- Formatting ----
        interp.registerNative("format", (a, i) -> {
            java.time.temporal.TemporalAccessor t = (java.time.temporal.TemporalAccessor) a.get(0);
            return DateTimeFormatter.ofPattern(sarg(a, 1)).format(t);
        });

        // ---- Currency ----
        interp.registerNative("currency", (a, i) -> toBig(a.get(0)));
        interp.registerNative("roundCurrency", (a, i) -> toBig(a.get(0)).setScale(a.size() > 1 ? iarg(a, 1) : 2, RoundingMode.HALF_UP));
        interp.registerNative("formatCurrency", (a, i) -> {
            BigDecimal v = toBig(a.get(0)).setScale(2, RoundingMode.HALF_UP);
            String symbol = a.size() > 1 ? sarg(a, 1) : "";
            DecimalFormatSymbols sym = new DecimalFormatSymbols();
            sym.setDecimalSeparator(',');
            sym.setGroupingSeparator('.');
            String formatted = new DecimalFormat("#,##0.00", sym).format(v);
            return symbol.isEmpty() ? formatted : symbol + " " + formatted;
        });
    }

    // ---- Argument helpers ----

    private static int iarg(List<Object> a, int i) { return (int) larg(a, i); }

    private static long larg(List<Object> a, int i) {
        Object v = a.get(i);
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) return Long.parseLong(((String) v).trim());
        throw new NeoException("Expected a numeric argument at position " + i);
    }

    private static String sarg(List<Object> a, int i) {
        Object v = a.get(i);
        return v == null ? "" : String.valueOf(v);
    }

    private static BigDecimal toBig(Object v) {
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return new BigDecimal(v.toString());
        if (v instanceof String) return new BigDecimal(((String) v).trim());
        throw new NeoException("Cannot convert value to Currency: " + v);
    }

    // ---- Temporal accessors (accept Date or DateTime / Time or DateTime) ----

    private static LocalDate asDate(Object x) {
        if (x instanceof LocalDate) return (LocalDate) x;
        if (x instanceof LocalDateTime) return ((LocalDateTime) x).toLocalDate();
        throw new NeoException("Expected a Date or DateTime value");
    }

    private static LocalDateTime asDateTime(Object x) {
        if (x instanceof LocalDateTime) return (LocalDateTime) x;
        if (x instanceof LocalDate) return ((LocalDate) x).atStartOfDay();
        throw new NeoException("Expected a DateTime value");
    }

    private static LocalTime asTime(Object x) {
        if (x instanceof LocalTime) return (LocalTime) x;
        if (x instanceof LocalDateTime) return ((LocalDateTime) x).toLocalTime();
        throw new NeoException("Expected a Time or DateTime value");
    }

    private static int yearOf(Object x)  { return asDate(x).getYear(); }
    private static int monthOf(Object x) { return asDate(x).getMonthValue(); }
    private static int dayOf(Object x)   { return asDate(x).getDayOfMonth(); }
    private static int hourOf(Object x)  { return asTime(x).getHour(); }
    private static int minuteOf(Object x){ return asTime(x).getMinute(); }
    private static int secondOf(Object x){ return asTime(x).getSecond(); }

    private static Object plusDays(Object x, long n) {
        if (x instanceof LocalDate) return ((LocalDate) x).plusDays(n);
        if (x instanceof LocalDateTime) return ((LocalDateTime) x).plusDays(n);
        throw new NeoException("addDays() expects a Date or DateTime");
    }

    private static Object plusMonths(Object x, long n) {
        if (x instanceof LocalDate) return ((LocalDate) x).plusMonths(n);
        if (x instanceof LocalDateTime) return ((LocalDateTime) x).plusMonths(n);
        throw new NeoException("addMonths() expects a Date or DateTime");
    }

    private static Object plusYears(Object x, long n) {
        if (x instanceof LocalDate) return ((LocalDate) x).plusYears(n);
        if (x instanceof LocalDateTime) return ((LocalDateTime) x).plusYears(n);
        throw new NeoException("addYears() expects a Date or DateTime");
    }

    private static Object plusHours(Object x, long n) {
        if (x instanceof LocalTime) return ((LocalTime) x).plusHours(n);
        if (x instanceof LocalDateTime) return ((LocalDateTime) x).plusHours(n);
        throw new NeoException("addHours() expects a Time or DateTime");
    }

    private static Object plusMinutes(Object x, long n) {
        if (x instanceof LocalTime) return ((LocalTime) x).plusMinutes(n);
        if (x instanceof LocalDateTime) return ((LocalDateTime) x).plusMinutes(n);
        throw new NeoException("addMinutes() expects a Time or DateTime");
    }
}
