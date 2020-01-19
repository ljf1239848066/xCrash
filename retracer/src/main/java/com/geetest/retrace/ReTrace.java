package com.geetest.retrace;


import com.geetest.retrace.log.ILogger;
import com.geetest.retrace.log.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import proguard.classfile.util.ClassUtil;
import proguard.obfuscate.MappingProcessor;
import proguard.obfuscate.MappingReader;

@SuppressWarnings("unchecked")
public class ReTrace implements MappingProcessor {
    private static final String REGEX_OPTION = "-regex";

    private static final String VERBOSE_OPTION = "-verbose";

    public static final String STACK_TRACE_EXPRESSION = "(?:\\s*%c:.*)|(?:\\s*at\\s+%c.%m\\s*\\(.*?(?::%l)?\\)\\s*)";

    private static final String REGEX_CLASS = "\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b";

    private static final String REGEX_CLASS_SLASH = "\\b(?:[A-Za-z0-9_$]+/)*[A-Za-z0-9_$]+\\b";

    private static final String REGEX_LINE_NUMBER = "\\b[0-9]+\\b";

    private static final String REGEX_TYPE = "\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b(?:\\[\\])*";

    private static final String REGEX_MEMBER = "<?\\b[A-Za-z0-9_$]+\\b>?";

    private static final String REGEX_ARGUMENTS = "(?:\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b(?:\\[\\])*(?:\\s*,\\s*\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b(?:\\[\\])*)*)?";

    private final String regularExpression;

    private final boolean verbose;

    private final File mappingFile;

    private final File stackTraceFile;

    private Map classMap;

    private Map classFieldMap;

    private Map classMethodMap;

    public ReTrace(String paramString, boolean paramBoolean, File paramFile) { this(paramString, paramBoolean, paramFile, null); }

    public ReTrace(String paramString, boolean paramBoolean, File paramFile1, File paramFile2) {
        this.classMap = new HashMap();
        this.classFieldMap = new HashMap();
        this.classMethodMap = new HashMap();
        this.regularExpression = paramString;
        this.verbose = paramBoolean;
        this.mappingFile = paramFile1;
        this.stackTraceFile = paramFile2;
    }

    public void execute() throws IOException {
        MappingReader mappingReader = new MappingReader(this.mappingFile);
        mappingReader.pump(this);
        StringBuffer stringBuffer = new StringBuffer(this.regularExpression.length() + 32);
        char[] arrayOfChar = new char[32];
        byte b = 0;
        int i = 0;
        while (true) {
            int j = this.regularExpression.indexOf('%', i);
            if (j < 0 || j == this.regularExpression.length() - 1 || b == arrayOfChar.length)
                break;
            stringBuffer.append(this.regularExpression.substring(i, j));
            stringBuffer.append('(');
            char c = this.regularExpression.charAt(j + 1);
            switch (c) {
                case 'c':
                    stringBuffer.append("\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b");
                    break;
                case 'C':
                    stringBuffer.append("\\b(?:[A-Za-z0-9_$]+/)*[A-Za-z0-9_$]+\\b");
                    break;
                case 'l':
                    stringBuffer.append("\\b[0-9]+\\b");
                    break;
                case 't':
                    stringBuffer.append("\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b(?:\\[\\])*");
                    break;
                case 'f':
                    stringBuffer.append("<?\\b[A-Za-z0-9_$]+\\b>?");
                    break;
                case 'm':
                    stringBuffer.append("<?\\b[A-Za-z0-9_$]+\\b>?");
                    break;
                case 'a':
                    stringBuffer.append("(?:\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b(?:\\[\\])*(?:\\s*,\\s*\\b(?:[A-Za-z0-9_$]+\\.)*[A-Za-z0-9_$]+\\b(?:\\[\\])*)*)?");
                    break;
            }
            stringBuffer.append(')');
            arrayOfChar[b++] = c;
            i = j + 2;
        }
        stringBuffer.append(this.regularExpression.substring(i));
        Pattern pattern = Pattern.compile(stringBuffer.toString());
        LineNumberReader lineNumberReader = new LineNumberReader((this.stackTraceFile == null) ? new InputStreamReader(System.in) : new BufferedReader(new FileReader(this.stackTraceFile)));
        try {
            StringBuffer stringBuffer1 = new StringBuffer(256);
            ArrayList arrayList = new ArrayList();
            String str = null;
            while (true) {
                String str1 = lineNumberReader.readLine();
                if (str1 == null)
                    break;
                Matcher matcher = pattern.matcher(str1);
                if (matcher.matches()) {
                    int j = 0;
                    String str2 = null;
                    String str3 = null;
                    int k;
                    for (k = 0; k < b; k++) {
                        int m = matcher.start(k + 1);
                        if (m >= 0) {
                            String str4 = matcher.group(k + 1);
                            char c = arrayOfChar[k];
                            switch (c) {
                                case 'c':
                                    str = originalClassName(str4);
                                    break;
                                case 'C':
                                    str = originalClassName(ClassUtil.externalClassName(str4));
                                    break;
                                case 'l':
                                    j = Integer.parseInt(str4);
                                    break;
                                case 't':
                                    str2 = originalType(str4);
                                    break;
                                case 'a':
                                    str3 = originalArguments(str4);
                                    break;
                            }
                        }
                    }
                    k = 0;
                    stringBuffer1.setLength(0);
                    arrayList.clear();
                    byte b1;
                    for (b1 = 0; b1 < b; b1++) {
                        int m = matcher.start(b1 + 1);
                        if (m >= 0) {
                            int n = matcher.end(b1 + 1);
                            String str4 = matcher.group(b1 + 1);
                            stringBuffer1.append(str1.substring(k, m));
                            char c = arrayOfChar[b1];
                            switch (c) {
                                case 'c':
                                    str = originalClassName(str4);
                                    stringBuffer1.append(str);
                                    break;
                                case 'C':
                                    str = originalClassName(ClassUtil.externalClassName(str4));
                                    stringBuffer1.append(ClassUtil.internalClassName(str));
                                    break;
                                case 'l':
                                    j = Integer.parseInt(str4);
                                    stringBuffer1.append(str4);
                                    break;
                                case 't':
                                    str2 = originalType(str4);
                                    stringBuffer1.append(str2);
                                    break;
                                case 'f':
                                    originalFieldName(str, str4, str2, stringBuffer1, arrayList);
                                    break;
                                case 'm':
                                    originalMethodName(str, str4, j, str2, str3, stringBuffer1, arrayList);
                                    break;
                                case 'a':
                                    str3 = originalArguments(str4);
                                    stringBuffer1.append(str3);
                                    break;
                            }
                            k = n;
                        }
                    }
                    stringBuffer1.append(str1.substring(k));
                    System.out.println(stringBuffer1);
                    for (b1 = 0; b1 < arrayList.size(); b1++)
                        System.out.println(arrayList.get(b1));
                    continue;
                }
                System.out.println(str1);
            }
        } catch (IOException iOException) {
            throw new IOException("Can't read stack trace (" + iOException.getMessage() + ")");
        } finally {
            if (this.stackTraceFile != null)
                try {
                    lineNumberReader.close();
                } catch (IOException iOException) {}
        }
    }

    private void originalFieldName(String paramString1, String paramString2, String paramString3, StringBuffer paramStringBuffer, List paramList) {
        int i = -1;
        Map map = (Map)this.classFieldMap.get(paramString1);
        if (map != null) {
            Set set = (Set)map.get(paramString2);
            if (set != null) {
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    FieldInfo fieldInfo = (FieldInfo)iterator.next();
                    if (fieldInfo.matches(paramString3)) {
                        if (i < 0) {
                            i = paramStringBuffer.length();
                            if (this.verbose)
                                paramStringBuffer.append(fieldInfo.type).append(' ');
                            paramStringBuffer.append(fieldInfo.originalName);
                            continue;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        for (byte b = 0; b < i; b++)
                            stringBuffer.append(' ');
                        if (this.verbose)
                            stringBuffer.append(fieldInfo.type).append(' ');
                        stringBuffer.append(fieldInfo.originalName);
                        paramList.add(stringBuffer);
                    }
                }
            }
        }
        if (i < 0)
            paramStringBuffer.append(paramString2);
    }

    private void originalMethodName(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, StringBuffer paramStringBuffer, List paramList) {
        int i = -1;
        Map map = (Map)this.classMethodMap.get(paramString1);
        if (map != null) {
            Set set = (Set)map.get(paramString2);
            if (set != null) {
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    MethodInfo methodInfo = (MethodInfo)iterator.next();
                    if (methodInfo.matches(paramInt, paramString3, paramString4)) {
                        if (i < 0) {
                            i = paramStringBuffer.length();
                            if (this.verbose)
                                paramStringBuffer.append(methodInfo.type).append(' ');
                            paramStringBuffer.append(methodInfo.originalName);
                            if (this.verbose)
                                paramStringBuffer.append('(').append(methodInfo.arguments).append(')');
                            continue;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        for (byte b = 0; b < i; b++)
                            stringBuffer.append(' ');
                        if (this.verbose)
                            stringBuffer.append(methodInfo.type).append(' ');
                        stringBuffer.append(methodInfo.originalName);
                        if (this.verbose)
                            stringBuffer.append('(').append(methodInfo.arguments).append(')');
                        paramList.add(stringBuffer);
                    }
                }
            }
        }
        if (i < 0)
            paramStringBuffer.append(paramString2);
    }

    private String originalArguments(String paramString) {
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        while (true) {
            int j = paramString.indexOf(',', i);
            if (j < 0)
                break;
            stringBuffer.append(originalType(paramString.substring(i, j).trim())).append(',');
            i = j + 1;
        }
        stringBuffer.append(originalType(paramString.substring(i).trim()));
        return stringBuffer.toString();
    }

    private String originalType(String paramString) {
        int i = paramString.indexOf('[');
        return (i >= 0) ? (originalClassName(paramString.substring(0, i)) + paramString.substring(i)) : originalClassName(paramString);
    }

    private String originalClassName(String paramString) {
        String str = (String)this.classMap.get(paramString);
        return (str != null) ? str : paramString;
    }

    public boolean processClassMapping(String paramString1, String paramString2) {
        this.classMap.put(paramString2, paramString1);
        return true;
    }

    public void processFieldMapping(String paramString1, String paramString2, String paramString3, String paramString4) {
        Map map = (Map)this.classFieldMap.get(paramString1);
        if (map == null) {
            map = new HashMap();
            this.classFieldMap.put(paramString1, map);
        }
        Set set = (Set)map.get(paramString4);
        if (set == null) {
            set = new LinkedHashSet();
            map.put(paramString4, set);
        }
        set.add(new FieldInfo(paramString2, paramString3));
    }

    public void processMethodMapping(String paramString1, int paramInt1, int paramInt2, String paramString2, String paramString3, String paramString4, String paramString5) {
        Map map = (Map)this.classMethodMap.get(paramString1);
        if (map == null) {
            map = new HashMap();
            this.classMethodMap.put(paramString1, map);
        }
        Set set = (Set)map.get(paramString5);
        if (set == null) {
            set = new LinkedHashSet();
            map.put(paramString5, set);
        }
        set.add(new MethodInfo(paramInt1, paramInt2, paramString2, paramString4, paramString3));
    }

    @Override
    public void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, int origFirstLineNumber, int origLastLineNumber, String newMethodName) {
        // Original class name -> obfuscated method names.
        Map methodMap = (Map)classMethodMap.get(className);
        if (methodMap == null)
        {
            methodMap = new HashMap();
            classMethodMap.put(className, methodMap);
        }

        // Obfuscated method name -> methods.
        Set methodSet = (Set)methodMap.get(newMethodName);
        if (methodSet == null)
        {
            methodSet = new LinkedHashSet();
            methodMap.put(newMethodName, methodSet);
        }

        // Add the method information.
        methodSet.add(new MethodInfo(firstLineNumber,
                lastLineNumber,
                methodReturnType,
                methodArguments,
                methodName,
                origFirstLineNumber,
                origLastLineNumber));
    }

    private static class FieldInfo {
        private String type;

        private String originalName;

        private FieldInfo(String param1String1, String param1String2) {
            this.type = param1String1;
            this.originalName = param1String2;
        }

        private boolean matches(String param1String) { return (param1String == null || param1String.equals(this.type)); }
    }

    private static class MethodInfo {
        private int firstLineNumber;

        private int lastLineNumber;

        private String type;

        private String arguments;

        private String originalName;

        private int    origFirstLineNumber;

        private int    origLastLineNumber;

        private MethodInfo(int param1Int1, int param1Int2, String param1String1, String param1String2, String param1String3) {
            this.firstLineNumber = param1Int1;
            this.lastLineNumber = param1Int2;
            this.type = param1String1;
            this.arguments = param1String2;
            this.originalName = param1String3;
        }

        private MethodInfo(int firstLineNumber, int lastLineNumber, String type, String arguments, String originalName, int origFirstLineNumber, int origLastLineNumber) {
            this.firstLineNumber = firstLineNumber;
            this.lastLineNumber = lastLineNumber;
            this.type = type;
            this.arguments = arguments;
            this.originalName = originalName;
            this.origFirstLineNumber = origFirstLineNumber;
            this.origLastLineNumber = origLastLineNumber;
        }

        private boolean matches(int param1Int, String param1String1, String param1String2) { return ((param1Int == 0 || (this.firstLineNumber <= param1Int && param1Int <= this.lastLineNumber) || this.lastLineNumber == 0) && (param1String1 == null || param1String1.equals(this.type)) && (param1String2 == null || param1String2.equals(this.arguments))); }
    }

    public static void main(String[] args) {
        Logger log = Logger.get().setLevel(ILogger.VERBOSE).setTag("ReTrace");

        if (args.length < 1) {
            System.err.println("Usage: java proguard.ReTrace [-verbose] <mapping_file> [<stacktrace_file>]");
            System.exit(-1);
        }
        String str = "(?:\\s*%c:.*)|(?:\\s*at\\s+%c.%m\\s*\\(.*?(?::%l)?\\)\\s*)";
        boolean bool = false;
        byte b = 0;
        while (b < args.length) {
            String str1 = args[b];
            if (str1.equals("-regex")) {
                str = args[++b];
            } else if (str1.equals("-verbose")) {
                bool = true;
            } else {
                break;
            }
            b++;
        }
        log.d("main args=" + Arrays.toString(args) + ", b=" + b);
        if (b >= args.length) {
            System.err.println("Usage: java proguard.ReTrace [-regex <regex>] [-verbose] <mapping_file> [<stacktrace_file>]");
            System.exit(-1);
        }
        File file1 = new File(args[b++]);
        File file2 = (b < args.length) ? new File(args[b]) : null;
        ReTrace reTrace = new ReTrace(str, bool, file1, file2);
        try {
            reTrace.execute();
        } catch (IOException iOException) {
            if (bool) {
                iOException.printStackTrace();
            } else {
                System.err.println("Error: " + iOException.getMessage());
            }
            System.exit(1);
        }
        System.exit(0);
    }
}