package com.neoobjectpascal;

import java.util.ArrayList;
import java.util.List;

class ExpressionEvaluator {

    private final InterpreterState state;

    ExpressionEvaluator(InterpreterState state) {
        this.state = state;
    }

    Object evaluate(NeoObjectPascalParser.ExpressionContext ctx, Interpreter interp) {
        // Unary operators (single sub-expression)
        if (ctx.expression().size() == 1) {
            if (ctx.NOT() != null) return !isTrue(interp.visit(ctx.expression(0)));
            if (ctx.SUB() != null) {
                Object val = interp.visit(ctx.expression(0));
                if (val instanceof Double) return -(Double) val;
                if (val instanceof Integer) return -(Integer) val;
                return val;
            }
        }

        // Atoms
        if (ctx.javaBlock() != null) return interp.visit(ctx.javaBlock());
        if (ctx.newExpression() != null) return interp.visit(ctx.newExpression());
        if (ctx.methodCall() != null) return interp.visit(ctx.methodCall());
        if (ctx.arrayLiteral() != null) return evaluateArrayLiteral(ctx.arrayLiteral(), interp);
        if (ctx.primary() != null) return interp.visit(ctx.primary());

        if (ctx.memberAccess() != null) {
            Object result = interp.visit(ctx.memberAccess());
            if (result instanceof MemberAccessResult) {
                MemberAccessResult mar = (MemberAccessResult) result;
                if (mar.object.hasField(mar.memberName)) {
                    return mar.object.getFieldValue(mar.memberName);
                }
            }
            return result;
        }

        // Array access: expression[expression]
        if (ctx.LBRACKET() != null) {
            Object arrObj = interp.visit(ctx.expression(0));
            Object idxObj = interp.visit(ctx.expression(1));
            if (arrObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) arrObj;
                int idx = toInt(idxObj);
                if (idx < 0 || idx >= list.size()) {
                    throw new NeoException("Array index out of bounds: " + idx + " (size: " + list.size() + ")");
                }
                return list.get(idx);
            }
            throw new NeoException("Cannot index a non-array value");
        }

        // Binary operators
        Object left = interp.visit(ctx.expression(0));
        Object right = interp.visit(ctx.expression(1));

        if (ctx.AND() != null) return isTrue(left) && isTrue(right);
        if (ctx.OR() != null)  return isTrue(left) || isTrue(right);

        if (ctx.PIPE() != null) {
            if (ctx.expression(1).primary() != null && ctx.expression(1).primary().identifier() != null) {
                String funcName = ctx.expression(1).primary().identifier().getText();
                Function function = state.functions.get(funcName);
                if (function != null && !function.getParameters().isEmpty()) {
                    try {
                        SymbolTable newScope = new SymbolTable(state.symbolTable);
                        newScope.put(function.getParameters().get(0),
                                     new Symbol(function.getParameters().get(0), null, left));
                        SymbolTable oldScope = state.symbolTable;
                        state.symbolTable = newScope;
                        interp.visit(function.getBody());
                        state.symbolTable = oldScope;
                    } catch (ReturnValue rv) {
                        return rv.value;
                    }
                }
            }
            return left;
        }

        if (ctx.MUL() != null) return numericOp(left, right, '*');
        if (ctx.DIV() != null) return numericOp(left, right, '/');

        if (ctx.ADD() != null) {
            if (left instanceof String || right instanceof String) {
                return String.valueOf(left) + String.valueOf(right);
            }
            return numericOp(left, right, '+');
        }

        if (ctx.SUB() != null) return numericOp(left, right, '-');
        if (ctx.GT() != null) return numericCompare(left, right) > 0;
        if (ctx.LT() != null) return numericCompare(left, right) < 0;
        if (ctx.GTE() != null) return numericCompare(left, right) >= 0;
        if (ctx.LTE() != null) return numericCompare(left, right) <= 0;
        if (ctx.EQUAL() != null) return areEqual(left, right);
        if (ctx.NOT_EQUAL() != null) return !areEqual(left, right);

        return null;
    }

    Object evaluatePrimary(NeoObjectPascalParser.PrimaryContext ctx, Interpreter interp) {
        if (ctx.INTEGER() != null) return Integer.parseInt(ctx.INTEGER().getText());
        if (ctx.REAL_NUM() != null) return Double.parseDouble(ctx.REAL_NUM().getText());

        if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            return text.substring(1, text.length() - 1);
        }

        if (ctx.recordLiteral() != null) return evaluateRecordLiteral(ctx.recordLiteral(), interp);

        if (ctx.SELF() != null) return state.currentInstance;

        if (ctx.identifier() != null) {
            String name = ctx.identifier().getText();
            if (name.equalsIgnoreCase("true")) return Boolean.TRUE;
            if (name.equalsIgnoreCase("false")) return Boolean.FALSE;
            if (state.functions.containsKey(name)) return state.functions.get(name);
            Symbol symbol = state.symbolTable.get(name);
            return symbol != null ? symbol.getValue() : null;
        }

        if (ctx.expression() != null) return interp.visit(ctx.expression());
        if (ctx.call() != null) return interp.visit(ctx.call());
        return null;
    }

    java.util.Map<String, Object> evaluateRecordLiteral(NeoObjectPascalParser.RecordLiteralContext ctx,
                                                        Interpreter interp) {
        // Insertion order preserved so consumers see fields as written.
        java.util.Map<String, Object> record = new java.util.LinkedHashMap<>();
        for (NeoObjectPascalParser.RecordEntryContext entry : ctx.recordEntry()) {
            // Keys may be identifiers (#{ padding: 1 }) or string literals (#{ "/": home }) —
            // the latter enables arbitrary keys like URL routes and JSON-style field names.
            String key;
            if (entry.STRING() != null) {
                String t = entry.STRING().getText();
                key = t.substring(1, t.length() - 1);
            } else {
                key = entry.identifier().getText();
            }
            record.put(key, interp.visit(entry.expression()));
        }
        return record;
    }

    List<Object> evaluateArrayLiteral(NeoObjectPascalParser.ArrayLiteralContext ctx, Interpreter interp) {
        List<Object> list = new ArrayList<>();
        if (ctx.expressionList() != null) {
            for (NeoObjectPascalParser.ExpressionContext expr : ctx.expressionList().expression()) {
                list.add(interp.visit(expr));
            }
        }
        return list;
    }

    // ---- Numeric helpers ----

    private Object numericOp(Object left, Object right, char op) {
        // Currency (BigDecimal) arithmetic — precise, no floating-point error.
        if (left instanceof java.math.BigDecimal || right instanceof java.math.BigDecimal) {
            java.math.BigDecimal l = toBigDecimal(left);
            java.math.BigDecimal r = toBigDecimal(right);
            switch (op) {
                case '+': return l.add(r);
                case '-': return l.subtract(r);
                case '*': return l.multiply(r);
                case '/':
                    if (r.signum() == 0) throw new NeoException("Division by zero");
                    return l.divide(r, 10, java.math.RoundingMode.HALF_UP);
                default: return java.math.BigDecimal.ZERO;
            }
        }
        boolean useDouble = (left instanceof Double) || (right instanceof Double);
        double l = toDouble(left);
        double r = toDouble(right);
        double result;
        switch (op) {
            case '+': result = l + r; break;
            case '-': result = l - r; break;
            case '*': result = l * r; break;
            case '/':
                if (r == 0) throw new NeoException("Division by zero");
                result = l / r;
                break;
            default: result = 0;
        }
        if (useDouble) return result;
        return (int) result;
    }

    private int numericCompare(Object left, Object right) {
        if (left instanceof String && right instanceof String) {
            return ((String) left).compareTo((String) right);
        }
        // Date / time / datetime and currency compare with their natural ordering.
        if (left instanceof java.time.LocalDate && right instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) left).compareTo((java.time.LocalDate) right);
        }
        if (left instanceof java.time.LocalTime && right instanceof java.time.LocalTime) {
            return ((java.time.LocalTime) left).compareTo((java.time.LocalTime) right);
        }
        if (left instanceof java.time.LocalDateTime && right instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) left).compareTo((java.time.LocalDateTime) right);
        }
        if (left instanceof java.math.BigDecimal || right instanceof java.math.BigDecimal) {
            return toBigDecimal(left).compareTo(toBigDecimal(right));
        }
        return Double.compare(toDouble(left), toDouble(right));
    }

    java.math.BigDecimal toBigDecimal(Object v) {
        if (v instanceof java.math.BigDecimal) return (java.math.BigDecimal) v;
        if (v instanceof Number) return new java.math.BigDecimal(v.toString());
        if (v instanceof String) {
            try { return new java.math.BigDecimal(((String) v).trim()); }
            catch (NumberFormatException e) { return java.math.BigDecimal.ZERO; }
        }
        return java.math.BigDecimal.ZERO;
    }

    boolean isTrue(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Integer) return (Integer) value != 0;
        if (value instanceof Double) return (Double) value != 0.0;
        return false;
    }

    boolean areEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        // Currency equality ignores scale (1.0 == 1.00).
        if (a instanceof java.math.BigDecimal && b instanceof java.math.BigDecimal) {
            return ((java.math.BigDecimal) a).compareTo((java.math.BigDecimal) b) == 0;
        }
        if (a instanceof Number && b instanceof Number) {
            return ((Number) a).doubleValue() == ((Number) b).doubleValue();
        }
        return a.equals(b);
    }

    double toDouble(Object val) {
        if (val instanceof Double) return (Double) val;
        if (val instanceof Integer) return ((Integer) val).doubleValue();
        if (val instanceof String) {
            try { return Double.parseDouble((String) val); } catch (NumberFormatException e) { return 0.0; }
        }
        return 0.0;
    }

    int toInt(Object val) {
        if (val instanceof Integer) return (Integer) val;
        if (val instanceof Double) return ((Double) val).intValue();
        return 0;
    }
}
