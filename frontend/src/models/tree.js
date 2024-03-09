export class TreeNode {
    constructor(id, name, parent_id) {
        this.id = id
        this.name = name
        this.parent_id = parent_id
        this.children = []
    }

    addChild(node) {
        this.children.push(node)
    }
}