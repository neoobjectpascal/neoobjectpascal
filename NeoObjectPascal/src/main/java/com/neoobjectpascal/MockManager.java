package com.neoobjectpascal;

import java.util.*;

/**
 * Manages mocking of functions and methods for testing
 */
public class MockManager {
    private Map<String, MockedFunction> mockedFunctions;
    private Map<String, Map<String, MockedMethod>> mockedMethods; // className -> methodName -> mock
    private Map<String, List<MethodCall>> methodCalls; // Track method calls for verification
    
    public MockManager() {
        this.mockedFunctions = new HashMap<>();
        this.mockedMethods = new HashMap<>();
        this.methodCalls = new HashMap<>();
    }
    
    /**
     * Mock a standalone function
     */
    public void mockFunction(String functionName, Object returnValue) {
        mockedFunctions.put(functionName, new MockedFunction(functionName, returnValue));
    }
    
    /**
     * Mock a method of a class
     */
    public void mockMethod(String className, String methodName, Object returnValue) {
        mockedMethods.putIfAbsent(className, new HashMap<>());
        mockedMethods.get(className).put(methodName, new MockedMethod(className, methodName, returnValue));
    }
    
    /**
     * Mock a method with parameter matching
     */
    public void mockMethodWithParams(String className, String methodName, List<Object> params, Object returnValue) {
        mockedMethods.putIfAbsent(className, new HashMap<>());
        MockedMethod mock = new MockedMethod(className, methodName, returnValue);
        mock.setExpectedParams(params);
        mockedMethods.get(className).put(methodName, mock);
    }
    
    /**
     * Check if a function is mocked
     */
    public boolean isFunctionMocked(String functionName) {
        return mockedFunctions.containsKey(functionName);
    }
    
    /**
     * Check if a method is mocked
     */
    public boolean isMethodMocked(String className, String methodName) {
        return mockedMethods.containsKey(className) && 
               mockedMethods.get(className).containsKey(methodName);
    }
    
    /**
     * Get mocked function return value
     */
    public Object getMockedFunctionReturn(String functionName, List<Object> args) {
        MockedFunction mock = mockedFunctions.get(functionName);
        if (mock != null) {
            recordFunctionCall(functionName, args);
            return mock.getReturnValue();
        }
        return null;
    }
    
    /**
     * Get mocked method return value
     */
    public Object getMockedMethodReturn(String className, String methodName, List<Object> args) {
        if (mockedMethods.containsKey(className)) {
            MockedMethod mock = mockedMethods.get(className).get(methodName);
            if (mock != null) {
                recordMethodCall(className, methodName, args);
                
                // Check if parameters match (if specified)
                if (mock.hasExpectedParams() && !mock.paramsMatch(args)) {
                    throw new RuntimeException("Mock parameter mismatch for " + className + "." + methodName);
                }
                
                return mock.getReturnValue();
            }
        }
        return null;
    }
    
    /**
     * Record a function call for verification
     */
    private void recordFunctionCall(String functionName, List<Object> args) {
        String key = "function:" + functionName;
        methodCalls.putIfAbsent(key, new ArrayList<>());
        methodCalls.get(key).add(new MethodCall(functionName, args));
    }
    
    /**
     * Record a method call for verification
     */
    private void recordMethodCall(String className, String methodName, List<Object> args) {
        String key = className + "." + methodName;
        methodCalls.putIfAbsent(key, new ArrayList<>());
        methodCalls.get(key).add(new MethodCall(methodName, args));
    }
    
    /**
     * Public method to record method calls for verification (used by interpreter)
     */
    public void recordMethodCallForVerify(String className, String methodName, List<Object> args) {
        recordMethodCall(className, methodName, args);
    }
    
    /**
     * Verify that a function was called
     */
    public boolean verifyFunctionCalled(String functionName) {
        String key = "function:" + functionName;
        return methodCalls.containsKey(key) && !methodCalls.get(key).isEmpty();
    }
    
    /**
     * Verify that a method was called
     */
    public boolean verifyMethodCalled(String className, String methodName) {
        String key = className + "." + methodName;
        return methodCalls.containsKey(key) && !methodCalls.get(key).isEmpty();
    }
    
    /**
     * Verify that a function was called with specific arguments
     */
    public boolean verifyFunctionCalledWith(String functionName, List<Object> expectedArgs) {
        String key = "function:" + functionName;
        if (!methodCalls.containsKey(key)) return false;
        
        for (MethodCall call : methodCalls.get(key)) {
            if (call.argsMatch(expectedArgs)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verify that a method was called with specific arguments
     */
    public boolean verifyMethodCalledWith(String className, String methodName, List<Object> expectedArgs) {
        String key = className + "." + methodName;
        if (!methodCalls.containsKey(key)) return false;
        
        for (MethodCall call : methodCalls.get(key)) {
            if (call.argsMatch(expectedArgs)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the number of times a function was called
     */
    public int getFunctionCallCount(String functionName) {
        String key = "function:" + functionName;
        return methodCalls.containsKey(key) ? methodCalls.get(key).size() : 0;
    }
    
    /**
     * Get the number of times a method was called
     */
    public int getMethodCallCount(String className, String methodName) {
        String key = className + "." + methodName;
        return methodCalls.containsKey(key) ? methodCalls.get(key).size() : 0;
    }
    
    /**
     * Clear all mocks
     */
    public void clearAll() {
        mockedFunctions.clear();
        mockedMethods.clear();
        methodCalls.clear();
    }
    
    /**
     * Clear mocks for a specific function
     */
    public void clearFunction(String functionName) {
        mockedFunctions.remove(functionName);
        methodCalls.remove("function:" + functionName);
    }
    
    /**
     * Clear mocks for a specific method
     */
    public void clearMethod(String className, String methodName) {
        if (mockedMethods.containsKey(className)) {
            mockedMethods.get(className).remove(methodName);
        }
        methodCalls.remove(className + "." + methodName);
    }
    
    // Inner classes
    
    private static class MockedFunction {
        private String name;
        private Object returnValue;
        
        public MockedFunction(String name, Object returnValue) {
            this.name = name;
            this.returnValue = returnValue;
        }
        
        public Object getReturnValue() {
            return returnValue;
        }
    }
    
    private static class MockedMethod {
        private String className;
        private String methodName;
        private Object returnValue;
        private List<Object> expectedParams;
        
        public MockedMethod(String className, String methodName, Object returnValue) {
            this.className = className;
            this.methodName = methodName;
            this.returnValue = returnValue;
            this.expectedParams = null;
        }
        
        public void setExpectedParams(List<Object> params) {
            this.expectedParams = params;
        }
        
        public boolean hasExpectedParams() {
            return expectedParams != null;
        }
        
        public boolean paramsMatch(List<Object> actualParams) {
            if (expectedParams == null) return true;
            if (actualParams == null) return expectedParams.isEmpty();
            if (expectedParams.size() != actualParams.size()) return false;
            
            for (int i = 0; i < expectedParams.size(); i++) {
                if (!Objects.equals(expectedParams.get(i), actualParams.get(i))) {
                    return false;
                }
            }
            return true;
        }
        
        public Object getReturnValue() {
            return returnValue;
        }
    }
    
    private static class MethodCall {
        private String name;
        private List<Object> args;
        
        public MethodCall(String name, List<Object> args) {
            this.name = name;
            this.args = args != null ? new ArrayList<>(args) : new ArrayList<>();
        }
        
        public boolean argsMatch(List<Object> expectedArgs) {
            if (expectedArgs == null) return args.isEmpty();
            if (args.size() != expectedArgs.size()) return false;
            
            for (int i = 0; i < args.size(); i++) {
                if (!Objects.equals(args.get(i), expectedArgs.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
