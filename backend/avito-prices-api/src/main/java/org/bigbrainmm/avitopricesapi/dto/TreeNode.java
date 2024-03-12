package org.bigbrainmm.avitopricesapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Модель дерева для вывода категорий или локаций")
public class TreeNode {

    @Schema(description = "Идентификатор узла", example = "1")
    private Long id;
    @Schema(description = "Имя узла", example = "root")
    private String name;
    @JsonIgnore
    @Schema(description = "Родительский узел")
    private TreeNode parent;
    @Schema(description = "Список подузлов")
    private List<TreeNode> subNodes = new ArrayList<>();

    public TreeNode(Long id, String name, TreeNode parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    private void printTree(StringBuilder sb, TreeNode node, int level) {
        sb.append("\t".repeat(Math.max(0, level)));
        sb.append(node.name);
        sb.append("\n");
        for (TreeNode sn : node.subNodes) {
            printTree(sb, sn, level + 1);
        }
    }

    public void addSubNode(TreeNode sn) {
        if (sn != null) {
            subNodes.add(sn);
            sn.setParent(this);
        }
    }

    public TreeNode getById(Long id) {
        if (this.id.equals(id)) {
            return this;
        }
        for (TreeNode sn : subNodes) {
            TreeNode found = sn.getById(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printTree(sb, this, 0);
        return sb.toString();
    }

    public static TreeNode buildTreeFromFile(String filePath) {
        Long idCounter = 0L;
        TreeNode root = new TreeNode(idCounter, "root", null);
        TreeNode currentParent = root;
        int currentIndent = -1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(filePath).getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int indent = countIndent(line);
                String nodeName = line.trim();

                TreeNode node = new TreeNode();

                idCounter++;

                node.setId(idCounter);
                node.setName(nodeName);

                if (indent > currentIndent) {
                    currentParent.addSubNode(node);
                    currentParent = node;
                    currentIndent = indent;
                } else if (indent == currentIndent) {
                    currentParent.getParent().addSubNode(node);
                    currentParent = node;
                } else {
                    while (currentIndent >= indent && currentParent != null) {
                        currentParent = currentParent.getParent();
                        currentIndent--;
                    }
                    currentParent.addSubNode(node);
                    currentParent = node;
                    currentIndent = indent;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return root;
    }

    private static int countIndent(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static void printTree(TreeNode node, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append(node.getName());
        System.out.println(sb.toString());
        for (TreeNode subNode : node.getSubNodes()) {
            printTree(subNode, indent + 1);
        }
    }
}
