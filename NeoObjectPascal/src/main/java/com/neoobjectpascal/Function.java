package com.neoobjectpascal;

import java.util.List;

public class Function {
    private final String name;
    private final List<String> parameters;
    private final NeoObjectPascalParser.BlockContext body;

    public Function(String name, List<String> parameters, NeoObjectPascalParser.BlockContext body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public NeoObjectPascalParser.BlockContext getBody() {
        return body;
    }
}

