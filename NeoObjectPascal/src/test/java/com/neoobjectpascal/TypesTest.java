package com.neoobjectpascal;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the Date / Time / DateTime and Currency types (and the Double/Float aliases of Real). */
public class TypesTest {

    private Object valueOf(String code, String varName) throws IOException {
        CharStream input = CharStreams.fromString(code);
        NeoObjectPascalParser parser =
                new NeoObjectPascalParser(new CommonTokenStream(new NeoObjectPascalLexer(input)));
        ParseTree tree = parser.program();
        Interpreter interp = new Interpreter();
        interp.visit(tree);
        return interp.getSymbolTable().get(varName).getValue();
    }

    @Test
    @DisplayName("date() creates a LocalDate")
    void dateCreation() throws IOException {
        Object v = valueOf("var d: Date; begin d := date(2026, 3, 15); end.", "d");
        assertInstanceOf(LocalDate.class, v);
        assertEquals(LocalDate.of(2026, 3, 15), v);
    }

    @Test
    @DisplayName("time() and dateTime() create LocalTime / LocalDateTime")
    void timeAndDateTimeCreation() throws IOException {
        assertInstanceOf(LocalTime.class, valueOf("var t: Time; begin t := time(10, 30, 0); end.", "t"));
        Object dt = valueOf("var dt: DateTime; begin dt := dateTime(2026, 3, 15, 10, 30, 0); end.", "dt");
        assertInstanceOf(LocalDateTime.class, dt);
        assertEquals(LocalDateTime.of(2026, 3, 15, 10, 30, 0), dt);
    }

    @Test
    @DisplayName("component functions return integers")
    void components() throws IOException {
        String code = "var y: Integer; var m: Integer; var dw: Integer;"
                + " begin y := year(date(2026,3,15)); m := month(date(2026,3,15)); dw := dayOfWeek(date(2026,3,15)); end.";
        assertEquals(2026, valueOf(code, "y"));
        assertEquals(3, valueOf(code, "m"));
        assertEquals(7, valueOf(code, "dw")); // 2026-03-15 is a Sunday
    }

    @Test
    @DisplayName("date arithmetic: addDays and daysBetween")
    void dateArithmetic() throws IOException {
        assertEquals(LocalDate.of(2026, 3, 25),
                valueOf("var d: Date; begin d := addDays(date(2026,3,15), 10); end.", "d"));
        assertEquals(73,
                valueOf("var n: Integer; begin n := daysBetween(date(2026,1,1), date(2026,3,15)); end.", "n"));
    }

    @Test
    @DisplayName("dates compare with < > = operators")
    void dateComparison() throws IOException {
        assertEquals(Boolean.TRUE,
                valueOf("var r: Boolean; begin r := date(2026,3,15) > date(2026,1,1); end.", "r"));
        assertEquals(Boolean.TRUE,
                valueOf("var r: Boolean; begin r := date(2026,1,1) = date(2026,1,1); end.", "r"));
    }

    @Test
    @DisplayName("Currency arithmetic is exact (0.1 + 0.2 == 0.3)")
    void currencyPrecision() throws IOException {
        Object v = valueOf("var c: Currency; begin c := currency(0.1) + currency(0.2); end.", "c");
        assertInstanceOf(BigDecimal.class, v);
        assertEquals(0, ((BigDecimal) v).compareTo(new BigDecimal("0.3")));
    }

    @Test
    @DisplayName("Currency multiplication and comparison")
    void currencyOps() throws IOException {
        Object v = valueOf("var c: Currency; begin c := currency(199.90) * 3; end.", "c");
        assertEquals(0, ((BigDecimal) v).compareTo(new BigDecimal("599.70")));
        assertEquals(Boolean.TRUE,
                valueOf("var r: Boolean; begin r := currency(199.90) > currency(100); end.", "r"));
    }

    @Test
    @DisplayName("String coerces to Date; number coerces to Currency")
    void coercion() throws IOException {
        assertEquals(LocalDate.of(2026, 12, 25),
                valueOf("var d: Date; begin d := \"2026-12-25\"; end.", "d"));
        Object c = valueOf("var c: Currency; begin c := 19.90; end.", "c");
        assertInstanceOf(BigDecimal.class, c);
        assertEquals(0, ((BigDecimal) c).compareTo(new BigDecimal("19.90")));
    }

    @Test
    @DisplayName("format() and formatCurrency()")
    void formatting() throws IOException {
        assertEquals("15/03/2026",
                valueOf("var s: String; begin s := format(date(2026,3,15), \"dd/MM/yyyy\"); end.", "s"));
        assertEquals("R$ 599,70",
                valueOf("var s: String; begin s := formatCurrency(currency(599.7), \"R$\"); end.", "s"));
    }

    @Test
    @DisplayName("Double and Float are aliases of Real (Double runtime value)")
    void doubleFloatAliases() throws IOException {
        assertInstanceOf(Double.class, valueOf("var x: Double; begin x := 3.14; end.", "x"));
        assertInstanceOf(Double.class, valueOf("var x: Float; begin x := 2.5; end.", "x"));
    }
}
