# NeoObjectPascal VS Code Extension - Installation Guide

## 📦 Installation Methods

### Method 1: Install from VSIX File (Recommended)

1. **Download** the `neoobjectpascal-2.1.0.vsix` file

2. **Open Visual Studio Code**

3. **Install via Command Palette**:
   - Press `Ctrl+Shift+P` (Windows/Linux) or `Cmd+Shift+P` (Mac)
   - Type: `Extensions: Install from VSIX...`
   - Select the downloaded `.vsix` file
   - Click "Install"
   - Restart VS Code when prompted

4. **Or install via Command Line**:
   ```bash
   code --install-extension neoobjectpascal-2.1.0.vsix
   ```

### Method 2: Install from VS Code Marketplace

*(Coming soon - after publishing)*

1. Open VS Code
2. Go to Extensions (`Ctrl+Shift+X`)
3. Search for "NeoObjectPascal"
4. Click "Install"

## ⚙️ Initial Configuration

### Option 1: Use Bundled JAR (No Configuration Needed)

The extension includes a bundled `neoobjectpascal-v2.1-cloud.jar` file. **No configuration is required!**

### Option 2: Use Custom JAR

If you want to use a custom JAR:

1. **Open Settings**:
   - `File` → `Preferences` → `Settings`
   - Or press `Ctrl+,` (Windows/Linux) or `Cmd+,` (Mac)

2. **Search for**: `NeoObjectPascal: Jar Path`

3. **Enter the directory path** (not the full file path):
   - Example (Windows): `C:\NeoObjectPascal\bin`
   - Example (Linux/Mac): `/home/user/NeoObjectPascal/bin`

4. **Or edit `settings.json` directly**:
   ```json
   {
     "neoobjectpascal.jarPath": "/path/to/jar/directory"
   }
   ```

### Cloud Configuration (Optional)

If you want to use cloud execution:

1. **Open Settings**

2. **Configure**:
   - `NeoObjectPascal: Cloud Url`: `http://localhost:8000` (or your cloud URL)
   - `NeoObjectPascal: Cloud Username`: `user@example.com` (optional)
   - `NeoObjectPascal: Cloud Password`: Leave empty (will prompt when needed)

3. **Or edit `settings.json`**:
   ```json
   {
     "neoobjectpascal.cloudUrl": "http://localhost:8000",
     "neoobjectpascal.cloudUsername": "user@example.com"
   }
   ```

**⚠️ Security Note**: Do NOT store your password in settings. The extension will prompt for it when needed.

## ✅ Verify Installation

### 1. Check Extension is Active

1. Open VS Code
2. Go to Extensions (`Ctrl+Shift+X`)
3. Search for "NeoObjectPascal"
4. Should show "Installed" status

### 2. Test Syntax Highlighting

1. Create a new file: `test.npas`
2. Add some code:
   ```pascal
   var message: String;
   
   begin
       message := "Hello!";
       WriteLn(message);
   end.
   ```
3. Verify syntax highlighting is working

### 3. Test Run Command

1. Right-click on the `.npas` file
2. Should see menu options:
   - Run NeoObjectPascal File
   - Debug NeoObjectPascal File
   - Execute Project On Cloud

### 4. Test Debugging

1. Open a `.npas` file
2. Click on a line number to set a breakpoint (red dot should appear)
3. Right-click → "Debug NeoObjectPascal File"
4. Debug session should start

## 🔧 Troubleshooting

### Extension Not Appearing

**Problem**: Extension doesn't show up after installation

**Solution**:
1. Restart VS Code completely
2. Check if `.vsix` file was corrupted during download
3. Try reinstalling: `code --uninstall-extension alvarobrito.neoobjectpascal` then reinstall

### "JAR file not found" Error

**Problem**: Error when trying to run files

**Solution**:
1. Check if Java is installed: `java -version`
2. If using custom JAR path, verify the path is correct
3. Ensure the JAR file exists in the specified directory
4. Try using the bundled JAR (remove custom path from settings)

### Syntax Highlighting Not Working

**Problem**: Code appears without colors

**Solution**:
1. Ensure file has `.npas` extension
2. Check if language is set to "NeoObjectPascal" (bottom right corner)
3. Reload window: `Ctrl+Shift+P` → "Developer: Reload Window"

### Debugger Not Starting

**Problem**: Debug session fails to start

**Solution**:
1. Ensure Java is installed and in PATH
2. Save the file before debugging
3. Check for syntax errors in the code
4. Verify JAR file exists
5. Check VS Code output panel for errors

### Context Menu Not Showing

**Problem**: Right-click menu doesn't show NeoObjectPascal options

**Solution**:
1. Ensure file has `.npas` extension
2. Reload window: `Ctrl+Shift+P` → "Developer: Reload Window"
3. Check if extension is enabled in Extensions view

### Cloud Execution Fails

**Problem**: "Error executing on cloud" message

**Solution**:
1. Verify NeoObjectPascal Cloud is running
2. Check cloud URL in settings
3. Verify network connection
4. Check credentials (username/password)

## 📋 Requirements

### Minimum Requirements

- **Visual Studio Code**: 1.60.0 or higher
- **Java**: 11 or higher
- **Operating System**: Windows, Linux, or macOS

### Verify Java Installation

```bash
java -version
```

Should output something like:
```
openjdk version "11.0.x" or higher
```

If Java is not installed:
- **Windows**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
- **Linux**: `sudo apt install openjdk-11-jdk` (Ubuntu/Debian) or `sudo yum install java-11-openjdk` (RHEL/CentOS)
- **macOS**: `brew install openjdk@11`

## 🚀 Quick Start After Installation

### 1. Create Your First Program

```pascal
// hello.npas
var name: String;

begin
    name := "World";
    WriteLn("Hello, " + name + "!");
end.
```

### 2. Run It

- Right-click → **Run NeoObjectPascal File**
- Or press `F5` to debug

### 3. Try Debugging

- Click on line 6 to set a breakpoint
- Right-click → **Debug NeoObjectPascal File**
- Use Variables view to inspect `name`
- Use Debug Console to type `p name`

### 4. Create a Test

```pascal
// test.test.npas
function add(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

test "Should add numbers"
begin
    var result: Integer;
    result := add(2, 3);
    expect(result).toBe(5);
end;
```

- Right-click → **Execute Unit Test**

## 📚 Next Steps

1. Read the [README.md](README.md) for full feature documentation
2. Check [CHANGELOG.md](CHANGELOG.md) for version history
3. Explore example files in the `examples/` directory
4. Join the community and report issues on GitHub

## 🆘 Getting Help

If you encounter issues:

1. **Check this guide** for common solutions
2. **Check VS Code Output Panel**: `View` → `Output` → Select "NeoObjectPascal"
3. **Check VS Code Developer Tools**: `Help` → `Toggle Developer Tools`
4. **Report issues** on GitHub: [github.com/alvarobrito/neoobjectpascal](https://github.com/alvarobrito/neoobjectpascal)

## 🎉 Success!

If you can:
- ✅ See syntax highlighting
- ✅ Run `.npas` files
- ✅ Set breakpoints and debug
- ✅ Execute tests

**Congratulations! You're ready to code in NeoObjectPascal!** 🚀

---

**Version**: 2.1.0  
**Author**: Álvaro Brito  
**License**: MIT
