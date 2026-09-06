package com.neoobjectpascal.tui;

/**
 * Unicode glyphs shared by the phase-2 interactive and feedback widgets.
 *
 * <p>String forms are used when composing {@code Text} node content; the {@code char}
 * forms are used by {@link Renderer} when writing single cells (e.g. the progress bar).
 */
public final class Glyphs {

    private Glyphs() {}

    /** Selection pointer, e.g. {@code Select}'s focused-row marker. */
    public static final String POINTER = "❯";      // ❯
    /** Success / selected tick. */
    public static final String TICK = "✔";         // ✔
    /** Error cross. */
    public static final String CROSS = "✖";        // ✖
    /** Warning sign. */
    public static final String WARNING = "⚠";      // ⚠
    /** Info sign. */
    public static final String INFO = "ℹ";         // ℹ
    /** Filled square — a completed progress-bar cell. */
    public static final String SQUARE = "◼";       // ◼
    /** Light-shade square — a remaining progress-bar cell. */
    public static final String LIGHT_SQUARE = "░"; // ░

    /** Filled square as a char (for single-cell writes). */
    public static final char SQUARE_CH = '◼';
    /** Light-shade square as a char (for single-cell writes). */
    public static final char LIGHT_SQUARE_CH = '░';

    /** Braille spinner frames used by {@code Spinner} type {@code "dots"}. */
    public static final String[] SPINNER_DOTS = {
        "⠋", "⠙", "⠹", "⠸", "⠼",
        "⠴", "⠦", "⠧", "⠇", "⠏"
    };
}
