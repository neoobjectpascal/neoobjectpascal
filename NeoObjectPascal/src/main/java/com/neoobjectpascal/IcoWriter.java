package com.neoobjectpascal;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Writes a Windows {@code .ico} from a PNG, in pure Java (no external tools).
 * The icon embeds a single 256×256 PNG-compressed image (supported by Windows Vista+),
 * which is all jpackage needs for a Windows app-image launcher.
 */
final class IcoWriter {

    private IcoWriter() {}

    private static final int SIZE = 256;
    private static final int HEADER_BYTES = 6 + 16; // ICONDIR + one ICONDIRENTRY

    static void writeIco(File pngIn, File icoOut) throws IOException {
        BufferedImage src = ImageIO.read(pngIn);
        if (src == null) throw new IOException("Invalid or unsupported PNG: " + pngIn);

        BufferedImage img = src;
        if (src.getWidth() != SIZE || src.getHeight() != SIZE) {
            img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
            var g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src.getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();
        }

        ByteArrayOutputStream pngBuf = new ByteArrayOutputStream();
        ImageIO.write(img, "png", pngBuf);
        byte[] png = pngBuf.toByteArray();

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(icoOut)))) {
            // ICONDIR
            le16(out, 0); // reserved
            le16(out, 1); // type: 1 = icon
            le16(out, 1); // image count
            // ICONDIRENTRY
            out.writeByte(0);           // width  (0 => 256)
            out.writeByte(0);           // height (0 => 256)
            out.writeByte(0);           // palette color count
            out.writeByte(0);           // reserved
            le16(out, 1);               // color planes
            le16(out, 32);              // bits per pixel
            le32(out, png.length);      // size of image data
            le32(out, HEADER_BYTES);    // offset of image data
            out.write(png);
        }
    }

    private static void le16(DataOutputStream o, int v) throws IOException {
        o.writeByte(v & 0xFF);
        o.writeByte((v >> 8) & 0xFF);
    }

    private static void le32(DataOutputStream o, int v) throws IOException {
        o.writeByte(v & 0xFF);
        o.writeByte((v >> 8) & 0xFF);
        o.writeByte((v >> 16) & 0xFF);
        o.writeByte((v >> 24) & 0xFF);
    }
}
