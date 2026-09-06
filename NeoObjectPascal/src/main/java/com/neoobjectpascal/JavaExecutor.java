package com.neoobjectpascal;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JavaExecutor {
    private static int classCounter = 0;

    // Java reserved words / literals that must never be used as a generated alias name.
    private static final Set<String> JAVA_RESERVED = new HashSet<>(Arrays.asList(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
        "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
        "throw", "throws", "transient", "try", "void", "volatile", "while",
        "true", "false", "null", "var", "params"
    ));

    public static Object executeJavaCode(String javaCode, List<Object> parameters) {
        return executeJavaCode(javaCode, parameters, null);
    }

    /**
     * Compiles and runs an inline Java block. Arguments are always available positionally as
     * {@code param0, param1, ...}. When {@code paramNames} supplies a valid identifier for an
     * argument (i.e. the argument was a bare NeoObjectPascal identifier), that name is ALSO
     * declared as a typed alias, so the block can reference the argument by name.
     */
    public static Object executeJavaCode(String javaCode, List<Object> parameters, List<String> paramNames) {
        try {
            // Gera nome único para a classe
            String className = "DynamicJavaClass" + (++classCounter);

            // Cria o código Java completo
            String fullJavaCode = generateFullJavaCode(className, javaCode, parameters, paramNames);
            
            // Compila o código
            Class<?> compiledClass = compileJavaCode(className, fullJavaCode);
            
            // Executa o método
            Method executeMethod = compiledClass.getMethod("execute", Object[].class);
            Object[] paramArray = parameters.toArray();
            
            return executeMethod.invoke(null, (Object) paramArray);
            
        } catch (Exception e) {
            System.err.println("Erro ao executar código Java: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static String generateFullJavaCode(String className, String javaCode,
                                               List<Object> parameters, List<String> paramNames) {
        StringBuilder sb = new StringBuilder();

        sb.append("import java.time.*;\n");
        sb.append("import java.util.*;\n");
        sb.append("import java.text.*;\n");
        sb.append("import java.math.*;\n");
        sb.append("\n");
        sb.append("public class ").append(className).append(" {\n");
        sb.append("    public static Object execute(Object[] params) {\n");

        // Adiciona declarações de parâmetros posicionais (param0, param1, ...) — sempre disponíveis.
        // Nomes já declarados, para evitar redeclaração ao gerar os aliases.
        Set<String> declared = new HashSet<>();
        for (int i = 0; i < parameters.size(); i++) {
            Object param = parameters.get(i);
            String javaType = getJavaType(param);
            sb.append("        ").append(javaType).append(" param").append(i)
              .append(" = (").append(javaType).append(") params[").append(i).append("];\n");
            declared.add("param" + i);
        }

        // Aliases nomeados: quando o argumento é um identificador simples, declara também uma
        // variável com esse nome (tipada), permitindo referenciar o argumento pelo nome.
        if (paramNames != null) {
            for (int i = 0; i < parameters.size() && i < paramNames.size(); i++) {
                String name = paramNames.get(i);
                if (isValidAlias(name, declared)) {
                    String javaType = getJavaType(parameters.get(i));
                    sb.append("        ").append(javaType).append(" ").append(name)
                      .append(" = (").append(javaType).append(") params[").append(i).append("];\n");
                    declared.add(name);
                }
            }
        }

        sb.append("\n");
        
        // Remove as chaves do código Java e adiciona o conteúdo
        String cleanCode = javaCode.trim();
        if (cleanCode.startsWith("{") && cleanCode.endsWith("}")) {
            cleanCode = cleanCode.substring(1, cleanCode.length() - 1);
        }
        
        sb.append("        ").append(cleanCode.replace("\n", "\n        ")).append("\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * True when {@code name} can be safely emitted as a Java local alias: a valid Java identifier,
     * not a reserved word/literal, and not already declared (e.g. collides with paramN or a
     * duplicate argument name). Otherwise the caller falls back to the positional paramN only.
     */
    private static boolean isValidAlias(String name, Set<String> declared) {
        if (name == null || name.isEmpty()) return false;
        if (JAVA_RESERVED.contains(name)) return false;
        if (declared.contains(name)) return false;
        if (!Character.isJavaIdentifierStart(name.charAt(0))) return false;
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) return false;
        }
        return true;
    }

    private static String getJavaType(Object obj) {
        if (obj == null) return "Object";
        if (obj instanceof Integer) return "Integer";
        if (obj instanceof String) return "String";
        if (obj instanceof Boolean) return "Boolean";
        if (obj instanceof Double) return "Double";
        return "Object";
    }
    
    private static Class<?> compileJavaCode(String className, String javaCode) throws Exception {
        // Obtém o compilador Java
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("Java Compiler não disponível. Certifique-se de estar usando JDK, não JRE.");
        }
        
        // Cria um sistema de arquivos em memória
        InMemoryFileManager fileManager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));
        
        // Cria o objeto de código fonte
        JavaSourceFromString sourceFile = new JavaSourceFromString(className, javaCode);
        
        // Cria um DiagnosticCollector para capturar erros
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        
        // Compila o código
        List<JavaSourceFromString> sourceFiles = Arrays.asList(sourceFile);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, sourceFiles);
        
        boolean success = task.call();
        if (!success) {
            // Mostra os erros de compilação
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.err.println(diagnostic.toString());
            }
            System.err.println("Código Java gerado:");
            System.err.println(javaCode);
            throw new RuntimeException("Falha na compilação do código Java");
        }
        
        // Carrega a classe compilada
        InMemoryClassLoader classLoader = new InMemoryClassLoader(fileManager.getCompiledClasses());
        return classLoader.loadClass(className);
    }
    
    // Classe para representar código fonte Java em memória
    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;
        
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }
        
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
    
    // Gerenciador de arquivos em memória
    static class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, ByteArrayOutputStream> compiledClasses = new HashMap<>();
        
        protected InMemoryFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }
        
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            return new InMemoryJavaFileObject(className, kind);
        }
        
        Map<String, byte[]> getCompiledClasses() {
            Map<String, byte[]> result = new HashMap<>();
            for (Map.Entry<String, ByteArrayOutputStream> entry : compiledClasses.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toByteArray());
            }
            return result;
        }
        
        class InMemoryJavaFileObject extends SimpleJavaFileObject {
            private final String className;
            
            InMemoryJavaFileObject(String className, Kind kind) {
                super(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind);
                this.className = className;
            }
            
            @Override
            public OutputStream openOutputStream() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compiledClasses.put(className, baos);
                return baos;
            }
        }
    }
    
    // ClassLoader para carregar classes compiladas em memória
    static class InMemoryClassLoader extends ClassLoader {
        private final Map<String, byte[]> compiledClasses;
        
        InMemoryClassLoader(Map<String, byte[]> compiledClasses) {
            this.compiledClasses = compiledClasses;
        }
        
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] classBytes = compiledClasses.get(name);
            if (classBytes == null) {
                throw new ClassNotFoundException(name);
            }
            return defineClass(name, classBytes, 0, classBytes.length);
        }
    }
}