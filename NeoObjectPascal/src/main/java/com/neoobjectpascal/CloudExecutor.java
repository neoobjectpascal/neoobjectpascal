package com.neoobjectpascal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Handles execution of NeoObjectPascal projects on the cloud
 */
public class CloudExecutor {
    private final String baseUrl;
    private final String projectName;
    private final String username;
    private final String password;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String token;
    
    public CloudExecutor(String baseUrl, String projectName, String username, String password) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.projectName = projectName;
        this.username = username;
        this.password = password;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Execute a project on the cloud
     */
    public String execute(String mainFilePath) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     NeoObjectPascal Cloud - Execução Remota             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Step 1: Authenticate
        System.out.println("🔐 Autenticando no cloud...");
        authenticate();
        System.out.println("✓ Autenticado com sucesso!");
        System.out.println();
        
        // Step 2: Collect all .npas files
        System.out.println("📁 Coletando arquivos do projeto...");
        File mainFile = new File(mainFilePath);
        File projectDir = mainFile.getParentFile();
        if (projectDir == null) {
            projectDir = new File(".");
        }
        
        Map<String, String> files = collectNpasFiles(projectDir, mainFile);
        System.out.println("✓ Encontrados " + files.size() + " arquivo(s)");
        for (String path : files.keySet()) {
            System.out.println("  - " + path);
        }
        System.out.println();
        
        // Step 3: Sync project structure
        System.out.println("🔄 Sincronizando estrutura do projeto...");
        syncProjectStructure(files, mainFile.getName());
        System.out.println("✓ Estrutura sincronizada!");
        System.out.println();
        
        // Step 4: Upload files
        System.out.println("⬆️  Fazendo upload dos arquivos...");
        uploadFiles(files);
        System.out.println("✓ Upload concluído!");
        System.out.println();
        
        // Step 5: Execute project
        System.out.println("🚀 Executando projeto no cloud...");
        String executionId = executeProject();
        System.out.println("✓ Execução iniciada!");
        System.out.println();
        
        // Step 6: Return execution URL
        String executionUrl = baseUrl + "/executions/" + executionId;
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("✅ Projeto executado com sucesso!");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("🔗 Link da execução:");
        System.out.println("   " + executionUrl);
        System.out.println();
        
        return executionUrl;
    }
    
    /**
     * Authenticate with the cloud API
     */
    private void authenticate() throws Exception {
        ObjectNode loginData = objectMapper.createObjectNode();
        loginData.put("username", username);
        loginData.put("password", password);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginData.toString()))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha na autenticação: " + response.body());
        }
        
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        this.token = jsonResponse.get("access_token").asText();
    }
    
    /**
     * Collect all .npas files in the project directory
     */
    private Map<String, String> collectNpasFiles(File projectDir, File mainFile) throws IOException {
        Map<String, String> files = new HashMap<>();
        Path projectPath = projectDir.toPath().toAbsolutePath();
        
        try (Stream<Path> paths = Files.walk(projectPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".npas"))
                 .forEach(p -> {
                     try {
                         String relativePath = projectPath.relativize(p).toString();
                         // Normalize path separators to forward slash
                         relativePath = relativePath.replace("\\", "/");
                         String content = Files.readString(p);
                         files.put(relativePath, content);
                     } catch (IOException e) {
                         System.err.println("Erro ao ler arquivo: " + p);
                     }
                 });
        }
        
        return files;
    }
    
    /**
     * Sync project structure with the cloud
     */
    private void syncProjectStructure(Map<String, String> files, String mainFileName) throws Exception {
        ObjectNode syncData = objectMapper.createObjectNode();
        syncData.put("project", projectName);
        syncData.put("main", mainFileName);
        
        // Create structure object
        ObjectNode structure = objectMapper.createObjectNode();
        for (String filePath : files.keySet()) {
            structure.putNull(filePath);
        }
        syncData.set("structure", structure);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/project/sync"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(syncData.toString()))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao sincronizar estrutura: " + response.body());
        }
    }
    
    /**
     * Upload all files to the cloud
     */
    private void uploadFiles(Map<String, String> files) throws Exception {
        int count = 0;
        for (Map.Entry<String, String> entry : files.entrySet()) {
            String filePath = entry.getKey();
            String content = entry.getValue();
            
            count++;
            System.out.println("  [" + count + "/" + files.size() + "] " + filePath);
            
            ObjectNode fileData = objectMapper.createObjectNode();
            fileData.put("path", projectName + "/" + filePath);
            fileData.put("content", content);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/project/file"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.ofString(fileData.toString()))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Falha ao fazer upload do arquivo " + filePath + ": " + response.body());
            }
        }
    }
    
    /**
     * Execute the project on the cloud
     */
    private String executeProject() throws Exception {
        ObjectNode executeData = objectMapper.createObjectNode();
        executeData.put("project", projectName);
        executeData.set("arguments", objectMapper.createObjectNode());
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/project/execute"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(executeData.toString()))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao executar projeto: " + response.body());
        }
        
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        return jsonResponse.get("execution_id").asText();
    }
}
