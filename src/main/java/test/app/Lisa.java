package test.app;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.*;
import java.util.*;

public class Lisa {

  public static void main(String[] args) {
    File projectDir = new File("sample_code");
    explore(projectDir);
  }

  private static void explore(File file) {
    if (file.isDirectory()) {
      for (File child : file.listFiles()) {
        explore(child);
      }
    } else if (file.getName().endsWith(".java")) {
      try {
        FileInputStream fis = new FileInputStream(file);
        CompilationUnit cu = StaticJavaParser.parse(fis);
        new ListVisitor().visit(cu, null);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static class ListVisitor extends VoidVisitorAdapter<Void> {
    private final Map<String, Boolean> listChecked = new HashMap<>();

    @Override
    public void visit(MethodDeclaration n, Void arg) {
      listChecked.clear();
      super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Void arg) {
      super.visit(n, arg);
      n.getVariables().forEach(v -> {
        if (isListType(v.getType())) {
          listChecked.put(v.getNameAsString(), false);
          System.out.println("List declared: " + v + " at line " + n.getBegin().get().line);
        }
      });
    }

    @Override
    public void visit(MethodCallExpr n, Void arg) {
      super.visit(n, arg);
      n.getScope().ifPresent(scope -> {
        String variableName = scope.toString();
        // Check if the variable is a list and if the method call is not 'add' or
        // 'clear'
        if (listChecked.containsKey(variableName) && !n.getNameAsString().equals("add")
            && !n.getNameAsString().equals("clear")) {
          if (n.getNameAsString().equals("isEmpty") || n.getNameAsString().equals("size")) {
            // Mark the list as checked if there's an isEmpty or size check within an if
            // block
            boolean isInIf = isInIfBlock(n);
            listChecked.put(variableName, isInIf);
          } else {
            // Report usage based on whether there was a preceding check
            boolean wasChecked = listChecked.getOrDefault(variableName, false);
            String message = wasChecked ? "after check" : "without check";
            System.out.println("List " + variableName + " used at line " + n.getBegin().get().line +
                " within if block: " + isInIfBlock(n) + " " + message);
            // Assume list may be modified and reset its checked status after usage
            listChecked.put(variableName, false);
          }
        }
      });
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
      super.visit(n, arg);
      String variableName = n.getIterable().toString();
      if (listChecked.containsKey(variableName)) {
        boolean wasChecked = listChecked.get(variableName);
        String message = wasChecked ? "safely within if block" : "potentially unsafe without check";
        System.out.println("List " + variableName + " iterated at line " + n.getBegin().get().line + " " + message);
      }
    }

    private boolean isInIfBlock(Node node) {
      while (node != null) {
        if (node instanceof IfStmt) {
          IfStmt ifStmt = (IfStmt) node;
          boolean usedInCondition = ifStmt.getCondition().toString().contains(node.toString());
          if (!usedInCondition) {
            return true;
          }
        }
        node = node.getParentNode().orElse(null);
      }
      return false;
    }

    private boolean isListType(Type type) {
      return type.asString().matches("List<.*>");
    }
  }
}
