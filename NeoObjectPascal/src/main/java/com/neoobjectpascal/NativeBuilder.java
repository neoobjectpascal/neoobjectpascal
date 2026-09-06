package com.neoobjectpascal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Packages a {@code .npas} program into a self-contained native executable via {@code jpackage}
 * (app-image): {@code .app} on macOS, a folder with {@code .exe} on Windows, {@code bin/} on Linux.
 *
 * <p>The <b>entire project directory</b> (the program's folder and everything under it — {@code uses}
 * modules, subfolders, data files/assets) is bundled next to the interpreter jar, preserving
 * structure, so the packaged program behaves exactly as when run from source. jpackage cannot
 * cross-compile: each artifact is built on its own OS.
 */
final class NativeBuilder {

    /** Directories that never belong in a distributable app image. */
    private static final Set<String> SKIP_DIRS = Set.of(
            ".git", "node_modules", ".DS_Store", ".idea", ".vscode", ".gradle");

    static final class Options {
        String programPath;   // required
        String iconPng;       // optional
        String appName;       // optional (derived from program name)
        String outputDir;     // optional (default ./dist)
        String target;        // optional (default: current OS: mac|windows|linux)
    }

    private NativeBuilder() {}

    /** Run the build. Returns true on success. Prints progress/errors to stdout/stderr. */
    static boolean build(Options opt) {
        try {
            return doBuild(opt);
        } catch (BuildException e) {
            System.err.println("❌ Build falhou: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Build falhou: " + (e.getMessage() != null ? e.getMessage() : e));
            return false;
        }
    }

    private static boolean doBuild(Options opt) throws Exception {
        if (opt.programPath == null || opt.programPath.isBlank()) {
            throw new BuildException("nenhum arquivo .npas informado. "
                    + "Uso: --build <programa.npas> [--icon logo.png] [--name Nome] [--output dir] [--target mac|windows|linux]");
        }
        File program = new File(opt.programPath).getAbsoluteFile();
        if (!program.isFile()) throw new BuildException("programa não encontrado: " + program);

        String currentOs = currentOs();
        String target = (opt.target != null) ? normalizeOs(opt.target) : currentOs;
        if (!target.equals(currentOs)) {
            throw new BuildException("jpackage não faz cross-compile. Alvo '" + target
                    + "' deve ser construído em " + osLabel(target) + " (você está em "
                    + osLabel(currentOs) + "). Gere cada plataforma no seu próprio SO (ou via CI).");
        }

        requireJpackage();
        File interpreterJar = locateInterpreterJar();

        // The project root is the program's directory; everything under it is bundled.
        File projectRoot = program.getParentFile();
        String programRel = projectRoot.toPath().relativize(program.toPath()).toString();

        String appName = (opt.appName != null && !opt.appName.isBlank())
                ? opt.appName.trim() : deriveName(program);
        File outputDir = new File(opt.outputDir != null ? opt.outputDir : "dist").getAbsoluteFile();
        Files.createDirectories(outputDir.toPath());

        // Staging: interpreter jar + the whole project tree.
        Path staging = Files.createTempDirectory("neopascal-build-");
        try {
            System.out.println("📦 Empacotando '" + program.getName() + "' → " + osLabel(target)
                    + " (" + appName + ")");
            Files.copy(interpreterJar.toPath(), staging.resolve("neoobjectpascal.jar"),
                    StandardCopyOption.REPLACE_EXISTING);
            int copied = copyProjectTree(projectRoot, staging.toFile(), outputDir, staging.toFile());
            System.out.println("   • " + copied + " arquivo(s) do projeto incluídos (a partir de "
                    + projectRoot + ")");

            // Icon (per current OS); null => jpackage default.
            File icon = prepareIcon(opt.iconPng, target, staging);

            // Remove a stale artifact of the same name so jpackage doesn't refuse to overwrite.
            removeExistingArtifact(outputDir, appName, target);

            List<String> cmd = new ArrayList<>(Arrays.asList(
                    "jpackage",
                    "--type", "app-image",
                    "--name", appName,
                    "--app-version", "1.0.0",
                    "--input", staging.toString(),
                    "--main-jar", "neoobjectpascal.jar",
                    "--main-class", "com.neoobjectpascal.AppLauncher",
                    "--arguments", programRel,
                    "--dest", outputDir.toString()));
            if (icon != null) {
                cmd.add("--icon");
                cmd.add(icon.getAbsolutePath());
                System.out.println("   • ícone: " + icon.getName());
            }

            System.out.println("   • rodando jpackage...");
            runProcess(cmd);

            File artifact = artifactPath(outputDir, appName, target);
            System.out.println("✅ Build concluído: " + artifact);
            System.out.println("   Executar: " + runHint(artifact, appName, target));
            return true;
        } finally {
            deleteTree(staging.toFile());
        }
    }

    // ---- Project bundling ----

    /** Copy the whole project tree into {@code destRoot}, preserving structure. Returns file count. */
    private static int copyProjectTree(File projectRoot, File destRoot, File outputDir, File staging)
            throws IOException {
        Path rootPath = projectRoot.toPath();
        Path outPath = outputDir.getCanonicalFile().toPath();
        Path stagePath = staging.getCanonicalFile().toPath();
        int[] count = {0};
        try (Stream<Path> walk = Files.walk(rootPath)) {
            for (Path p : (Iterable<Path>) walk::iterator) {
                if (p.equals(rootPath)) continue;
                if (shouldSkip(rootPath, p, outPath, stagePath)) continue;
                Path rel = rootPath.relativize(p);
                Path dest = destRoot.toPath().resolve(rel);
                if (Files.isDirectory(p)) {
                    Files.createDirectories(dest);
                } else {
                    Files.createDirectories(dest.getParent());
                    Files.copy(p, dest, StandardCopyOption.REPLACE_EXISTING);
                    count[0]++;
                }
            }
        }
        return count[0];
    }

    private static boolean shouldSkip(Path root, Path p, Path outPath, Path stagePath) {
        try {
            Path canon = p.toFile().getCanonicalFile().toPath();
            // Never descend into the output dir (may be nested in the project) or the staging dir.
            if (canon.startsWith(outPath) || canon.startsWith(stagePath)) return true;
        } catch (IOException ignored) { /* fall through */ }
        for (Path seg : root.relativize(p)) {
            if (SKIP_DIRS.contains(seg.toString())) return true;
        }
        return false;
    }

    // ---- Icon ----

    private static File prepareIcon(String iconPng, String target, Path staging) throws Exception {
        if (iconPng == null || iconPng.isBlank()) return null;
        File png = new File(iconPng).getAbsoluteFile();
        if (!png.isFile()) throw new BuildException("ícone não encontrado: " + png);

        switch (target) {
            case "linux":
                return png; // jpackage accepts PNG directly on Linux
            case "windows": {
                File ico = staging.resolve("app-icon.ico").toFile();
                IcoWriter.writeIco(png, ico);
                return ico;
            }
            case "mac":
            default: {
                // Canonical, reliable macOS path: build an .iconset (sips resizes to each required
                // size) then let iconutil assemble the .icns. `sips -s format icns` directly is
                // flaky (fails on RGB/non-alpha or large PNGs); the iconset route is robust.
                File iconset = staging.resolve("app-icon.iconset").toFile();
                Files.createDirectories(iconset.toPath());
                int[] sizes = {16, 32, 128, 256, 512};
                for (int s : sizes) {
                    sipsResize(png, new File(iconset, "icon_" + s + "x" + s + ".png"), s);
                    sipsResize(png, new File(iconset, "icon_" + s + "x" + s + "@2x.png"), s * 2);
                }
                File icns = staging.resolve("app-icon.icns").toFile();
                runProcess(Arrays.asList("iconutil", "-c", "icns",
                        iconset.getAbsolutePath(), "-o", icns.getAbsolutePath()));
                if (!icns.isFile()) throw new BuildException("falha ao gerar o ícone .icns");
                return icns;
            }
        }
    }

    // ---- jpackage / process helpers ----

    private static void requireJpackage() throws BuildException {
        try {
            Process p = new ProcessBuilder("jpackage", "--version")
                    .redirectErrorStream(true).start();
            p.getInputStream().readAllBytes();
            if (p.waitFor() != 0) throw new BuildException("jpackage retornou erro");
        } catch (BuildException e) {
            throw e;
        } catch (Exception e) {
            throw new BuildException("jpackage não encontrado no PATH. Instale um JDK 14+ "
                    + "(o jpackage acompanha o JDK).");
        }
    }

    /** Resize a PNG to {@code size}×{@code size} via sips (macOS iconset generation). */
    private static void sipsResize(File src, File dest, int size) throws Exception {
        runProcess(Arrays.asList("sips", "-z", String.valueOf(size), String.valueOf(size),
                src.getAbsolutePath(), "--out", dest.getAbsolutePath()));
    }

    private static void runProcess(List<String> cmd) throws Exception {
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes());
        int code = p.waitFor();
        if (code != 0) {
            String tool = cmd.get(0);
            throw new BuildException(tool + " falhou (código " + code + "):\n"
                    + out.strip());
        }
    }

    private static File locateInterpreterJar() throws BuildException {
        try {
            File self = new File(NativeBuilder.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            if (!self.isFile() || !self.getName().endsWith(".jar")) {
                throw new BuildException("--build exige rodar a partir do jar empacotado "
                        + "(java -jar neoobjectpascal.jar --build ...).");
            }
            return self;
        } catch (URISyntaxException e) {
            throw new BuildException("não foi possível localizar o jar do interpretador");
        }
    }

    // ---- OS / naming helpers ----

    private static String currentOs() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) return "windows";
        if (os.contains("mac") || os.contains("darwin")) return "mac";
        return "linux";
    }

    private static String normalizeOs(String t) throws BuildException {
        String s = t.toLowerCase(Locale.ROOT);
        if (s.startsWith("win")) return "windows";
        if (s.startsWith("mac") || s.equals("osx") || s.equals("macos") || s.equals("darwin")) return "mac";
        if (s.startsWith("lin")) return "linux";
        throw new BuildException("alvo inválido: '" + t + "' (use mac, windows ou linux)");
    }

    private static String osLabel(String os) {
        switch (os) {
            case "windows": return "Windows (.exe)";
            case "linux":   return "Linux (bin)";
            default:        return "macOS (.app)";
        }
    }

    private static String deriveName(File program) {
        String base = program.getName();
        int dot = base.indexOf('.');
        if (dot > 0) base = base.substring(0, dot);
        base = base.replaceAll("[^A-Za-z0-9 _-]", "").trim();
        if (base.isEmpty()) base = "NeoApp";
        return Character.toUpperCase(base.charAt(0)) + base.substring(1);
    }

    private static File artifactPath(File outputDir, String name, String target) {
        switch (target) {
            case "mac":     return new File(outputDir, name + ".app");
            default:        return new File(outputDir, name); // windows/linux: app-image folder
        }
    }

    private static String runHint(File artifact, String name, String target) {
        switch (target) {
            case "windows": return "\"" + new File(artifact, name + ".exe") + "\"";
            case "linux":   return "\"" + new File(new File(artifact, "bin"), name) + "\"";
            default:        return "open \"" + artifact + "\"   (ou " + name
                    + ".app/Contents/MacOS/" + name + ")";
        }
    }

    private static void removeExistingArtifact(File outputDir, String name, String target) {
        deleteTree(artifactPath(outputDir, name, target));
    }

    private static void deleteTree(File f) {
        if (f == null || !f.exists()) return;
        File[] kids = f.listFiles();
        if (kids != null) for (File k : kids) deleteTree(k);
        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }

    private static final class BuildException extends Exception {
        BuildException(String msg) { super(msg); }
    }
}
