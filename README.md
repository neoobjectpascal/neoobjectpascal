# NeoObjectPascal

NeoObjectPascal is a modern Pascal language implemented in Java. Its interpreter runs `.npas` programs and provides object-oriented programming, native testing, error handling, modules, data parsing, HTTP requests, and direct Java integration.

The project repository is [github.com/neoobjectpascal/neoobjectpascal](https://github.com/neoobjectpascal/neoobjectpascal).

## The language

NeoObjectPascal keeps the familiar Pascal syntax while adding classes, interfaces, inheritance, polymorphism, arrays, functional pipelines, exceptions, and built-in date, time, and currency types. Programs can also use TerminalInk for terminal interfaces and WebInk for locally served web interfaces.

```npas
var message: String;

begin
    message := "Hello from NeoObjectPascal!";
    WriteLn(message);
end.
```

## Visual Studio Code extension

The [NeoObjectPascal extension for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=alvarobrito.neoobjectpascal) includes syntax highlighting, snippets, run and test commands, debugging through the Debug Adapter Protocol (DAP), native executable builds, and the visual UI Builder for `.xnpas` files.

The extension bundles the NeoObjectPascal runtime. Install the extension, make sure Java 11 or later is available on your system, and start writing `.npas` programs.

## Documentation

Read the language guide, feature reference, examples, installation instructions, and Visual Studio Code workflow at [neoobjectpascal.com](https://neoobjectpascal.com).

## Author

**Álvaro Brito**

Backend & AI Engineer

[View LinkedIn profile](https://www.linkedin.com/in/alvarogomes/)

Álvaro Brito is a Backend & AI Engineer with more than 10 years of Java experience. He designs secure, scalable microservices and delivers modern LLM (Large Language Model) and RAG (Retrieval-Augmented Generation) solutions. His work also includes Node.js, TypeScript, Python, Express, PostgreSQL, AWS, Docker, Terraform, and event-driven architectures.

He builds observable AI workflows with LangChain and Langfuse, using LLM evaluations and operational metrics from Prometheus and Grafana to improve quality and reliability. He focuses on production-ready platforms, CI/CD (Continuous Integration and Continuous Delivery) automation with Jenkins and other pipelines, real-time systems, and high-throughput data flows with FastAPI and Kafka.
