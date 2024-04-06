package pt.up.fe.comp2024.symboltable;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp2024.ast.Kind;
import pt.up.fe.comp2024.ast.NodeUtils;
import pt.up.fe.comp2024.ast.TypeUtils;
import pt.up.fe.specs.util.SpecsCheck;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static pt.up.fe.comp2024.ast.Kind.METHOD_DECL;
import static pt.up.fe.comp2024.ast.Kind.VAR_DECL;
import static pt.up.fe.comp2024.ast.Kind.TYPE;
import static pt.up.fe.comp2024.ast.Kind.PARAM;
import static pt.up.fe.comp2024.ast.Kind.IMPORT_DECL;

public class JmmSymbolTableBuilder {


    public static JmmSymbolTable build(JmmNode root) {

        var classDecl = root.getJmmChild(root.getChildren().size() - 1);
        SpecsCheck.checkArgument(Kind.CLASS_DECL.check(classDecl), () -> "Expected a class declaration: " + classDecl);
        String className = classDecl.get("name");
        String superClass = classDecl.hasAttribute("superClass") ? classDecl.get("superClass") : null;
        var imports = buildImports(root);

        var fields = buildFields(classDecl);
        var methods = buildMethods(classDecl);
        var returnTypes = buildReturnTypes(classDecl);
        var params = buildParams(classDecl);
        var locals = buildLocals(classDecl);

        return new JmmSymbolTable(className, superClass, imports, fields, methods, returnTypes, params, locals);
    }

    private static List<String> buildImports(JmmNode root) {
        return root.getChildren(IMPORT_DECL).stream()
                .map(child -> String.join(".",  child.getObjectAsList("name", String.class)))
                .toList();
    }

    private static Map<String, Type> buildReturnTypes(JmmNode classDecl) {
        // TODO: Simple implementation that needs to be expanded

        Map<String, Type> map = new HashMap<>();

        classDecl.getChildren(METHOD_DECL).stream()
                .forEach(method -> {
                    String name = method.get("name");
                    Type returnType = new Type("void", false);
                    if (!name.equals("main")) {
                        returnType = new Type(method.getChildren(TYPE).get(0).get("name"),
                                NodeUtils.getBooleanAttribute(method.getChildren(TYPE).get(0),
                                        "isArray", "false"));
                    }
                    map.put(name, returnType);
                });

        return map;
    }

    private static Map<String, List<Symbol>> buildParams(JmmNode classDecl) {

        Map<String, List<Symbol>> paramsMap = new HashMap<>();

        //note: getChildren(METHOD_DECL) to access method nodes
        classDecl.getChildren(METHOD_DECL).forEach(method -> {
            List<Symbol> methodParams = new ArrayList<>();
            if (method.get("name").equals("main")) {
                Type type = new Type("String", true);
                methodParams.add(new Symbol(type, method.get("args")));
                paramsMap.put("main", methodParams);
            }
            else {
                method.getChildren(PARAM).forEach(param -> {
                    //note: extract type and name from the param node
                    Type type = new Type(param.getChildren(TYPE).get(0).get("name"), NodeUtils.getBooleanAttribute(param.getChildren(TYPE).get(0),
                            "isArray", "false"));
                    if(NodeUtils.getBooleanAttribute(param.getChildren(TYPE).get(0),"varArgs","false")) {
                       type.putObject("varArgs",true);
                    }
                    String name = param.get("name");
                    methodParams.add(new Symbol(type, name));
                });
                paramsMap.put(method.get("name"), methodParams);
            }
        });

        return paramsMap;
    }

    private static Map<String, List<Symbol>> buildLocals(JmmNode classDecl) {
        // TODO: Simple implementation that needs to be expanded

        Map<String, List<Symbol>> localsMap = new HashMap<>();

        classDecl.getChildren(METHOD_DECL).forEach(method -> {
            List<Symbol> methodLocals = new ArrayList<>();
            method.getChildren(VAR_DECL).forEach(varDecl -> {
                Type type = new Type(varDecl.getChildren(TYPE).get(0).get("name"), NodeUtils.getBooleanAttribute(method.getChildren(TYPE).get(0),
                        "isArray", "false"));
                String name = varDecl.get("name");
                methodLocals.add(new Symbol(type, name));
            });
            localsMap.put(method.get("name"), methodLocals);
        });

        return localsMap;

    }

    private static List<Symbol> buildFields(JmmNode classDecl) {
        return classDecl.getChildren(VAR_DECL).stream()
                .map(varDecl -> new Symbol(new Type(varDecl.getChildren(TYPE).get(0).get("name"), NodeUtils.getBooleanAttribute(varDecl.getChildren(TYPE).get(0),
                        "isArray", "false")), varDecl.get("name")))
                .toList();
    }

    private static List<String> buildMethods(JmmNode classDecl) {

        return classDecl.getChildren(METHOD_DECL).stream()
                .map(method -> method.get("name")
                )
                .toList();
    }


    private static List<Symbol> getLocalsList(JmmNode methodDecl) {
        // TODO: Simple implementation that needs to be expanded

        var intType = new Type(TypeUtils.getIntTypeName(), false);

        return methodDecl.getChildren(VAR_DECL).stream()
                .map(varDecl -> new Symbol(intType, varDecl.get("name")))
                .toList();
    }

}
