package org.bigbrainmm.avitopricesapi.staticstorage;

import org.bigbrainmm.avitopricesapi.dto.TreeNode;

public class Trees {
    public static TreeNode microCategoryRoot = TreeNode.buildTreeFromFile("microcategories.txt");
    public static TreeNode locationRoot = TreeNode.buildTreeFromFile("locations.txt");
}
