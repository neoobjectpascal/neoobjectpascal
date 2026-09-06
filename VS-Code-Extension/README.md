# NeoObjectPascal Extension for Visual Studio Code

Complete IDE support for NeoObjectPascal with syntax highlighting, interactive debugging, and unit testing.

**Version:** 2.5.0  
**Author:** Álvaro Brito

## ✨ Features

### 🎨 Syntax Highlighting
- Full syntax highlighting for `.npas` files
- Support for classes, interfaces, inheritance, and polymorphism
- Highlighting for test files (`.test.npas`)

### ▶️ Run & Execute
- **Run File**: Execute NeoObjectPascal files locally
- Integrated terminal output

### 🐛 Interactive Debugging
- **Visual Breakpoints**: Click on line numbers to set/remove breakpoints
- **Step Over/Into**: Navigate through code execution
- **Variable Inspection**: Watch variables in real-time
- **Call Stack**: View function call hierarchy
- **Modify Variables**: Change values during debugging
- **Debug Console**: Interactive REPL for debug commands

### 🧪 Unit Testing
- **Run Single Test**: Execute individual `.test.npas` files
- **Run All Tests**: Execute all test files in workspace
- Integrated test output

## 📦 Installation

1. Install the extension from VS Code Marketplace (or install `.vsix` file)
2. The extension includes a bundled NeoObjectPascal JAR (v2.4)
3. Optionally configure custom JAR path in settings

## 🚀 Quick Start

### Running a File

1. Open a `.npas` file
2. Right-click in the editor
3. Select **"Run NeoObjectPascal File"**

Or click the **▶ play button** in the editor title bar.

### Debugging

1. Open a `.npas` file
2. Click on line numbers to set breakpoints (red dots appear)
3. Right-click and select **"Debug NeoObjectPascal File"**
4. Use debug controls:
   - **Continue** (F5): Resume execution
   - **Step Over** (F10): Execute next line
   - **Step Into** (F11): Enter function
   - **Stop** (Shift+F5): Stop debugging

### Debug Console Commands

While debugging, use these commands in the **Debug Console**:

| Command | Description |
|---------|-------------|
| `b <line>` | Add breakpoint at line |
| `d <line>` | Remove breakpoint from line |
| `list` | List all breakpoints |
| `c` or `continue` | Continue execution |
| `s` or `step` | Step over (next line) |
| `i` or `into` | Step into function |
| `w <var>` | Watch variable |
| `p <var>` | Print variable value |
| `set <var> <value>` | Modify variable |
| `vars` | List all variables |
| `stack` | Show call stack |
| `q` or `quit` | Quit debugger |

### Running Tests

**Single Test:**
1. Right-click on a `.test.npas` file
2. Select **"Execute Unit Test"**

**All Tests:**
1. Right-click on any `.test.npas` file
2. Select **"Execute All Unit Tests"**

## ⚙️ Configuration

Open VS Code settings (`Ctrl+,` or `Cmd+,`) and search for **"NeoObjectPascal"**:

### Settings

#### `neoobjectpascal.jarPath`
- **Type**: `string`
- **Default**: `""` (uses bundled JAR)
- **Description**: Path to directory containing `neoobjectpascal.jar`

### Example settings.json

```json
{
  "neoobjectpascal.jarPath": "/path/to/jar/directory"
}
```

## 📝 Context Menus

### For `.npas` files (non-test):
- ▶️ **Run NeoObjectPascal File**
- 🐛 **Debug NeoObjectPascal File**
- 📦 **Build Native Executable**

### For `.test.npas` files:
- 🧪 **Execute Unit Test**
- 🧪 **Execute All Unit Tests**

## 🐛 Debug Views

When debugging, the following views are available:

### Variables View
- Shows all local variables
- Displays variable types and values
- Allows modification of values (right-click → Set Value)

### Watch View
- Add expressions to watch
- Automatically updates when values change
- Use `w <var>` command to add watches

### Call Stack View
- Shows function call hierarchy
- Click frames to navigate
- See line numbers and file names

### Breakpoints View
- List all breakpoints
- Enable/disable breakpoints
- Remove breakpoints

### Debug Console
- Interactive REPL
- Execute debug commands
- View debug output

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `F5` | Start Debugging / Continue |
| `Shift+F5` | Stop Debugging |
| `Ctrl+Shift+F5` | Restart Debugging |
| `F9` | Toggle Breakpoint |
| `F10` | Step Over |
| `F11` | Step Into |
| `Shift+F11` | Step Out |

## 💡 Examples

### Example 1: Simple Program

```pascal
// hello.npas
var message: String;

begin
    message := "Hello from NeoObjectPascal!";
    WriteLn(message);
end.
```

**Run**: Right-click → **Run NeoObjectPascal File**

### Example 2: Class with Debugging

```pascal
// person.npas
class Person
    var name: String;
    var age: Integer;
    
    constructor Create(n: String, a: Integer)
    begin
        self.name := n;
        self.age := a;
    end;
    
    public function greet(): String
    begin
        return "Hello, I'm " + self.name;
    end;
end;

var p: Person;
var greeting: String;

begin
    p := new Person("John", 30);
    greeting := p.greet();  // Set breakpoint here (click line number)
    WriteLn(greeting);
end.
```

**Debug**: 
1. Click line number to set breakpoint (red dot appears)
2. Right-click → **Debug NeoObjectPascal File**
3. Use **Variables** view to inspect `p` and `greeting`
4. Use **Debug Console** to type `p name` to see the name value

### Example 3: Unit Test

```pascal
// calculator.test.npas
function add(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

test "Should add two numbers correctly"
begin
    // GIVEN
    var a: Integer;
    var b: Integer;
    a := 10;
    b := 20;
    
    // WHEN
    var result: Integer;
    result := add(a, b);
    
    // THEN
    expect(result).toBe(30);
end;
```

**Run Test**: Right-click → **Execute Unit Test**

## 🔧 Troubleshooting

### JAR Not Found

**Problem**: "JAR file not found" error

**Solution**:
1. Check if bundled JAR exists: `<extension-path>/bin/neoobjectpascal.jar`
2. Or configure custom JAR path in settings
3. Ensure Java is installed: `java -version`

### Debugger Not Starting

**Problem**: Debug session fails to start

**Solution**:
1. Ensure file is saved
2. Check for syntax errors in your `.npas` file
3. Verify JAR path is correct
4. Check Java installation (`java -version`)
5. Try restarting VS Code

### Tests Not Running

**Problem**: Tests don't execute

**Solution**:
1. Ensure file has `.test.npas` extension
2. Check test syntax (use `test "name" begin ... end;`)
3. Verify JAR supports testing (`-t` flag)

### Breakpoints Not Working

**Problem**: Breakpoints are ignored

**Solution**:
1. Ensure you're using **Debug** mode, not **Run** mode
2. Check that breakpoints are on executable lines (not comments or blank lines)
3. Verify the file is saved before debugging

## 📋 Requirements

- **Visual Studio Code** 1.60.0 or higher
- **Java** 11 or higher

## 📜 Release Notes

### 2.4.0 (Latest)

**New Features:**
- ✅ Record literals `#{ }`, plus highlighting for boolean/error-handling/OO/testing keywords
- ✅ TerminalInk highlighting + snippets (terminal UI framework)
- ✅ HTTP/API function highlighting + snippets (`httpGet`/`httpPost`/…/`httpForm`)
- ✅ Named java-block arguments (access args by name, not only `param0`)
- ✅ New extension icon
- ✅ Bundled interpreter JAR refreshed to the current build (`neoobjectpascal.jar`)

See `CHANGELOG.md` for the full history.

### 2.1.0

**New Features:**
- ✅ Interactive debugging with breakpoints
- ✅ Variable inspection and modification
- ✅ Call stack view
- ✅ Debug console with REPL
- ✅ Unit testing support (`.test.npas` files)
- ✅ Context menus for all actions
- ✅ Bundled JAR (v2.4)

**Improvements:**
- Enhanced syntax highlighting for OO features
- Better error messages
- Improved terminal integration
- Separate menus for test files

### 1.0.0

- Initial release
- Basic syntax highlighting
- Run command

## 🤝 Contributing

Contributions are welcome! Please visit the [GitHub repository](https://github.com/alvarobrito/neoobjectpascal).

## 📄 License

MIT License - See LICENSE file for details

## 👤 Author

**Álvaro Brito**

---

**Enjoy coding in NeoObjectPascal with full IDE support!** 🚀

## 🎓 Learn More

- [NeoObjectPascal Documentation](https://neoobjectpascal.netlify.app/)
- [VS Code Extension Development](https://code.visualstudio.com/api)
