package com.neoobjectpascal;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java com.neoobjectpascal.Main [options] <file.npas>");
            System.out.println("Options:");
            System.out.println("  --no-warnings, -q    Suppress parsing warnings");
            System.out.println("  --help, -h           Show this help message");
            return;
        }

        // Parse command line arguments
        List<String> argList = Arrays.asList(args);
        boolean suppressWarnings = argList.contains("--no-warnings") || argList.contains("-q");
        boolean showHelp = argList.contains("--help") || argList.contains("-h");
        boolean testMode = argList.contains("--test") || argList.contains("-t");
        boolean testAllMode = argList.contains("--test-all") || argList.contains("-ta");
        boolean debugMode = argList.contains("--debug") || argList.contains("-d");
        boolean dapMode = argList.contains("--dap");
        boolean buildMode = argList.contains("--build");
        
        // Check for cloud execution
        String cloudBaseUrl = null;
        String cloudProject = null;
        String cloudUsername = null;
        String cloudPassword = null;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--execute-on-cloud") && i + 4 < args.length) {
                cloudBaseUrl = args[i + 1];
                cloudProject = args[i + 2];
                cloudUsername = args[i + 3];
                cloudPassword = args[i + 4];
                break;
            }
        }
        
        if (showHelp) {
            System.out.println("NeoObjectPascal Interpreter");
            System.out.println("Usage: java com.neoobjectpascal.Main [options] <file.npas>");
            System.out.println();
            System.out.println("Options:");
            System.out.println("  --no-warnings, -q                        Suppress parsing warnings and errors");
            System.out.println("  --test, -t                               Run in test mode (execute .test.npas files)");
            System.out.println("  --test-all, -ta                          Run all tests recursively with coverage report");
            System.out.println("  --debug, -d                              Run in debug mode (interactive debugger)");
            System.out.println("  --dap                                    Run in DAP mode (for VS Code integration)");
            System.out.println("  --build <file.npas>                      Build a native executable (app-image) for the current OS");
            System.out.println("    --icon <png>                             App icon (PNG; converted per platform)");
            System.out.println("    --name <AppName>                         App/executable name (default: program name)");
            System.out.println("    --output <dir>                           Output directory (default: ./dist)");
            System.out.println("    --target <mac|windows|linux>             Target OS (default: current; no cross-compile)");
            System.out.println("  --execute-on-cloud <url> <proj> <user> <pass>  Execute on NeoObjectPascal Cloud");
            System.out.println("  --help, -h                               Show this help message");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("  java -jar neoobjectpascal.jar example.npas");
            System.out.println("  java -jar neoobjectpascal.jar --no-warnings example.npas");
            System.out.println("  java -jar neoobjectpascal.jar -t example.test.npas");
            System.out.println("  java -jar neoobjectpascal.jar -d example.npas");
            System.out.println("  java -jar neoobjectpascal.jar --execute-on-cloud http://api.cloud.com myproject user pass example.npas");
            return;
        }

        // Handle build mode (package a native executable via jpackage)
        if (buildMode) {
            boolean ok = NativeBuilder.build(parseBuildOptions(args));
            if (!ok) System.exit(1);
            return;
        }

        // Handle test-all mode (recursive test execution with coverage)
        if (testAllMode) {
            // Find the base directory (last non-option argument, or current directory)
            String baseDirectory = ".";
            for (String arg : args) {
                if (!arg.startsWith("-")) {
                    baseDirectory = arg;
                }
            }
            
            try {
                runAllTestsWithCoverage(baseDirectory, suppressWarnings);
            } catch (Exception e) {
                System.err.println("Error running tests: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            return;
        }
        
        // Find the file path (last non-option argument)
        String filePath = null;
        for (String arg : args) {
            if (!arg.startsWith("-")) {
                filePath = arg;
            }
        }
        
        if (filePath == null) {
            System.err.println("Error: No input file specified");
            System.err.println("Use --help for usage information");
            return;
        }
        
        // Handle cloud execution
        if (cloudBaseUrl != null) {
            try {
                CloudExecutor cloudExecutor = new CloudExecutor(cloudBaseUrl, cloudProject, cloudUsername, cloudPassword);
                String executionUrl = cloudExecutor.execute(filePath);
                System.out.println("Execução concluída!");
                System.out.println("URL: " + executionUrl);
                return;
            } catch (Exception e) {
                System.err.println("❌ Erro ao executar no cloud: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        
        // Handle DAP mode (Debug Adapter Protocol for VS Code)
        if (dapMode) {
            try {
                // Start DAP server (it will handle parsing and execution)
                DAPServer dapServer = new DAPServer(System.in, System.out, filePath);
                dapServer.start();
                
                return;
            } catch (Exception e) {
                System.err.println("DAP Error: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        CharStream input = CharStreams.fromFileName(filePath);
        NeoObjectPascalLexer lexer = new NeoObjectPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NeoObjectPascalParser parser = new NeoObjectPascalParser(tokens);
        
        // Suppress warnings if requested
        if (suppressWarnings) {
            lexer.removeErrorListeners();
            parser.removeErrorListeners();
            // Add a silent error listener that does nothing
            BaseErrorListener silentListener = new BaseErrorListener();
            lexer.addErrorListener(silentListener);
            parser.addErrorListener(silentListener);
        }
        
        ParseTree tree = parser.program();

        // Obter o diretório base do arquivo
        String baseDirectory = getBaseDirectory(filePath);
        Interpreter visitor = new Interpreter(baseDirectory, suppressWarnings);
        
        if (testMode) {
            visitor.setTestMode(true);
        }
        
        if (debugMode) {
            visitor.enableDebugger();
        }
        
        visitor.visit(tree);
        
        // If in test mode, print test results
        if (testMode) {
            printTestResults(visitor.getTestResults());
        }
    }
    
    private static void printTestResults(List<TestResult> results) {
        // ANSI color codes (work on Windows 10+, Mac, Linux)
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";
        final String CYAN = "\u001B[36m";
        final String BOLD = "\u001B[1m";
        final String DIM = "\u001B[2m";
        
        int passed = 0;
        int failed = 0;
        
        System.out.println();
        System.out.println(CYAN + BOLD + "╔══════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD + "║            NEOOBJECTPASCAL TEST RESULTS                  ║" + RESET);
        System.out.println(CYAN + BOLD + "╚══════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        
        for (TestResult result : results) {
            if (result.isPassed()) {
                passed++;
                System.out.println(GREEN + "  ✓ " + BOLD + result.getTestName() + RESET);
                if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
                    System.out.println(DIM + "    " + result.getErrorMessage() + RESET);
                }
            } else {
                failed++;
                System.out.println(RED + "  ✗ " + BOLD + result.getTestName() + RESET);
                if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
                    System.out.println(RED + "    " + result.getErrorMessage() + RESET);
                }
            }
            System.out.println();
        }
        
        System.out.println(CYAN + "─────────────────────────────────────────────────────────" + RESET);
        System.out.println();
        
        // Summary with colors
        System.out.print("  " + BOLD + "Total: " + RESET + results.size() + " tests");
        System.out.print("  │  ");
        System.out.print(GREEN + BOLD + "✓ Passed: " + passed + RESET);
        System.out.print("  │  ");
        
        if (failed > 0) {
            System.out.println(RED + BOLD + "✗ Failed: " + failed + RESET);
        } else {
            System.out.println(GREEN + BOLD + "✗ Failed: " + failed + RESET);
        }
        
        System.out.println();
        
        // Final status
        if (failed == 0) {
            System.out.println(GREEN + BOLD + "  🎉 ALL TESTS PASSED!" + RESET);
        } else {
            System.out.println(RED + BOLD + "  ❌ SOME TESTS FAILED" + RESET);
        }
        
        System.out.println();
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println();
        
        // Exit with error code if any test failed
        if (failed > 0) {
            System.exit(1);
        }
    }
    
    private static void runAllTestsWithCoverage(String baseDirectory, boolean suppressWarnings) throws IOException {
        // ANSI color codes
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";
        final String CYAN = "\u001B[36m";
        final String YELLOW = "\u001B[33m";
        final String BOLD = "\u001B[1m";
        
        // Find all test files recursively
        List<String> testFiles = findFilesRecursively(baseDirectory, ".test.npas");
        
        // Find all non-test .npas files for coverage calculation
        List<String> allNpasFiles = findFilesRecursively(baseDirectory, ".npas");
        allNpasFiles.removeAll(testFiles); // Remove test files from the list
        
        if (testFiles.isEmpty()) {
            System.out.println(YELLOW + "No test files found in " + baseDirectory + RESET);
            return;
        }
        
        System.out.println();
        System.out.println(CYAN + BOLD + "╔══════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD + "║        NEOOBJECTPASCAL RECURSIVE TEST EXECUTION          ║" + RESET);
        System.out.println(CYAN + BOLD + "╚══════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println("Base directory: " + baseDirectory);
        System.out.println("Test files found: " + testFiles.size());
        System.out.println("Source files found: " + allNpasFiles.size());
        System.out.println();
        
        // Enumerate the public class methods declared across the source files (static, no execution),
        // then track which ones the tests exercise — that is the real coverage.
        java.util.Set<String> declaredPublic = new java.util.LinkedHashSet<>();
        for (String src : allNpasFiles) {
            try { collectPublicMethods(src, declaredPublic); } catch (Exception ignored) { }
        }
        CoverageTracker.enable();

        int totalPassed = 0;
        int totalFailed = 0;
        Map<String, List<TestResult>> resultsByFile = new java.util.LinkedHashMap<>();

        // Execute each test file
        for (String testFile : testFiles) {
            System.out.println(CYAN + "─────────────────────────────────────────────────────────" + RESET);
            System.out.println(BOLD + "Testing: " + testFile + RESET);
            System.out.println(CYAN + "─────────────────────────────────────────────────────────" + RESET);
            
            try {
                CharStream input = CharStreams.fromFileName(testFile);
                NeoObjectPascalLexer lexer = new NeoObjectPascalLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                NeoObjectPascalParser parser = new NeoObjectPascalParser(tokens);
                
                if (suppressWarnings) {
                    lexer.removeErrorListeners();
                    parser.removeErrorListeners();
                }
                
                ParseTree tree = parser.program();
                String testBaseDir = getBaseDirectory(testFile);
                Interpreter visitor = new Interpreter(testBaseDir, suppressWarnings);
                visitor.setTestMode(true);
                visitor.visit(tree);
                
                List<TestResult> results = visitor.getTestResults();
                resultsByFile.put(testFile, results);
                
                // Count results
                for (TestResult result : results) {
                    if (result.isPassed()) {
                        totalPassed++;
                        System.out.println(GREEN + "  ✓ " + result.getTestName() + RESET);
                    } else {
                        totalFailed++;
                        System.out.println(RED + "  ✗ " + result.getTestName() + RESET);
                        if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
                            System.out.println(RED + "    " + result.getErrorMessage() + RESET);
                        }
                    }
                }
                
            } catch (Exception e) {
                System.out.println(RED + "  ✗ Error executing test file: " + e.getMessage() + RESET);
                totalFailed++;
            }
            
            System.out.println();
        }
        
        // Print summary
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println(CYAN + BOLD + "                    SUMMARY REPORT                         " + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println();
        
        System.out.println(BOLD + "Test Files Executed: " + RESET + testFiles.size());
        System.out.println(BOLD + "Total Tests: " + RESET + (totalPassed + totalFailed));
        System.out.println(GREEN + BOLD + "✓ Passed: " + RESET + totalPassed);
        System.out.println(RED + BOLD + "✗ Failed: " + RESET + totalFailed);
        System.out.println();
        
        // Coverage = public class methods exercised by tests / total public class methods.
        CoverageTracker.disable();
        java.util.Set<String> invoked = CoverageTracker.getInvoked();
        java.util.List<String> covered = new ArrayList<>();
        java.util.List<String> uncovered = new ArrayList<>();
        for (String m : declaredPublic) {
            if (invoked.contains(m)) covered.add(m); else uncovered.add(m);
        }
        double coveragePercentage = declaredPublic.isEmpty() ? 100.0
                : (double) covered.size() / (double) declaredPublic.size() * 100.0;

        System.out.println(CYAN + "─────────────────────────────────────────────────────────" + RESET);
        System.out.println(BOLD + "Test Coverage (public class methods):" + RESET);
        System.out.println("  Public methods: " + declaredPublic.size());
        System.out.println("  Covered: " + covered.size());
        System.out.printf("  Coverage: %.2f%%\n", coveragePercentage);
        for (String m : covered) {
            System.out.println(GREEN + "    ✓ " + m + RESET);
        }
        for (String m : uncovered) {
            System.out.println(YELLOW + "    ✗ " + m + "  (not covered)" + RESET);
        }
        if (declaredPublic.isEmpty()) {
            System.out.println(YELLOW + "  (no public class methods found)" + RESET);
        }
        System.out.println();

        if (coveragePercentage < 50) {
            System.out.println(RED + "  ⚠ Low coverage! Add tests for the public methods listed above." + RESET);
        } else if (coveragePercentage < 80) {
            System.out.println(YELLOW + "  ⚠ Moderate coverage. Aim for 80%+." + RESET);
        } else {
            System.out.println(GREEN + "  ✓ Good coverage!" + RESET);
        }
        
        System.out.println();
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        
        // Final status
        if (totalFailed == 0) {
            System.out.println(GREEN + BOLD + "  🎉 ALL TESTS PASSED!" + RESET);
        } else {
            System.out.println(RED + BOLD + "  ❌ SOME TESTS FAILED" + RESET);
        }
        
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println();
        
        // Exit with error code if any test failed
        if (totalFailed > 0) {
            System.exit(1);
        }
    }
    
    /**
     * Statically collects {@code "Class.method"} names of PUBLIC class methods/procedures declared in
     * a source file, by walking the parse tree (no execution — main blocks and side effects don't run).
     */
    private static void collectPublicMethods(String file, java.util.Set<String> out) throws IOException {
        CharStream input = CharStreams.fromFileName(file);
        NeoObjectPascalLexer lexer = new NeoObjectPascalLexer(input);
        lexer.removeErrorListeners();
        NeoObjectPascalParser parser = new NeoObjectPascalParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        NeoObjectPascalParser.ProgramContext prog = parser.program();
        for (NeoObjectPascalParser.DeclarationContext decl : prog.declaration()) {
            NeoObjectPascalParser.ClassDeclarationContext cls = decl.classDeclaration();
            if (cls == null || cls.classBody() == null) continue;
            String className = cls.identifier(0).getText();
            for (NeoObjectPascalParser.ClassMemberContext m : cls.classBody().classMember()) {
                if (m.methodDeclaration() != null && m.methodDeclaration().PUBLIC() != null) {
                    out.add(className + "." + m.methodDeclaration().identifier().getText());
                } else if (m.procedureDeclaration() != null && m.procedureDeclaration().PUBLIC() != null) {
                    out.add(className + "." + m.procedureDeclaration().identifier().getText());
                }
            }
        }
    }

    private static List<String> findFilesRecursively(String baseDirectory, String extension) {
        List<String> files = new ArrayList<>();
        java.io.File dir = new java.io.File(baseDirectory);
        
        if (!dir.exists() || !dir.isDirectory()) {
            return files;
        }
        
        findFilesRecursivelyHelper(dir, extension, files);
        return files;
    }
    
    private static void findFilesRecursivelyHelper(java.io.File directory, String extension, List<String> files) {
        java.io.File[] fileList = directory.listFiles();
        if (fileList == null) return;
        
        for (java.io.File file : fileList) {
            if (file.isDirectory()) {
                findFilesRecursivelyHelper(file, extension, files);
            } else if (file.getName().endsWith(extension)) {
                files.add(file.getAbsolutePath());
            }
        }
    }
    
    private static String getBaseDirectory(String filePath) {
        int lastSlash = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        if (lastSlash >= 0) {
            return filePath.substring(0, lastSlash + 1);
        }
        return "./";
    }

    /** Parse {@code --build} options. Value flags consume their argument; the remaining bare arg is the program. */
    private static NativeBuilder.Options parseBuildOptions(String[] args) {
        NativeBuilder.Options o = new NativeBuilder.Options();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
                case "--build":
                    break;
                case "--icon":
                    if (i + 1 < args.length) o.iconPng = args[++i];
                    break;
                case "--name":
                    if (i + 1 < args.length) o.appName = args[++i];
                    break;
                case "--output":
                case "--out":
                    if (i + 1 < args.length) o.outputDir = args[++i];
                    break;
                case "--target":
                    if (i + 1 < args.length) o.target = args[++i];
                    break;
                default:
                    if (!a.startsWith("-")) o.programPath = a; // program (last bare arg wins)
            }
        }
        return o;
    }
}

