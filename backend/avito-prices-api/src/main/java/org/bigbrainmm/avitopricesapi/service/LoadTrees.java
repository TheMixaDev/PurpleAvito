package org.bigbrainmm.avitopricesapi.service;

import jakarta.annotation.PostConstruct;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.bigbrainmm.avitopricesapi.dto.TreeNode;
import org.springframework.stereotype.Service;

@Service
public class LoadTrees {
    @PostConstruct
    public void load() {
        StaticStorage.microCategoryRoot = TreeNode.buildTreeFromFile("microcategories.txt");
        StaticStorage.locationsRoot = TreeNode.buildTreeFromFile("locations.txt");
    }
}
