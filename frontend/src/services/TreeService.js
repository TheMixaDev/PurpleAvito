import { TreeNode } from '@/models/tree';

export const TreeService = {
    formTree: (treeData) => {
        let tree = new TreeNode(0, "ROOT", null);
        for(let i of treeData) {
            if(i.parent_id == null)
                tree.addChild(new TreeNode(i.id, i.name, 0));
            else {
                let depth = TreeService.depthSearch(treeData, i.parent_id, []);
                if(depth.length == 0)
                    tree.children[0].addChild(new TreeNode(i.id, i.name, i.parent_id));
                let subtree = tree.children[0];
                let found = false;
                for(let j of depth.reverse()) {
                    for(let k in subtree.children) {
                        if(subtree.children[k].id == j) {
                            subtree = subtree.children[k];
                            found = true;
                            break;
                        }
                    }
                    if(found) break;
                }
                for(let j in subtree.children) {
                    if(subtree.children[j].id == i.parent_id) {
                        subtree.children[j].addChild(new TreeNode(i.id, i.name, i.parent_id));
                        break;
                    }
                }
            }
        }
        return tree;
    },
    depthSearch: (arr, val, result) => {
        let index = TreeService.binarySearch(arr, val, 0, arr.length - 1).parent_id;
        if(index == null) return result;
        result.push(index);
        return TreeService.depthSearch(arr, index, result);
    },
    binarySearch: (arr, val, start, end) => {
        if (start > end) return false;
        let mid = Math.floor((start + end) / 2);
        if (arr[mid].id === val) return arr[mid];
        if (arr[mid].id > val)
            return TreeService.binarySearch(arr, val, start, mid - 1);
        else
            return TreeService.binarySearch(arr, val, mid + 1, end);
    }
}