/* 
 * Copyright (C) 2020 Alexander Stojanovich <coas91@rocketmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rs.alexanderstojanovich.fo2ie.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 * @param <T> data type for tree node
 */
public class Tree<T> {

    private final Node<T> root;

    public Tree(Node<T> root) {
        this.root = root;
    }

    public Node<T> getRoot() {
        return root;
    }

    /**
     * Add node as a child of this tree (also sets it's parent to the current
     * root)
     *
     * @param node node to add
     */
    public void addChild(Node<T> node) {
        List<T> preorder = preorder(null, node.parent);
        if (!preorder.contains(node.data)) {
            node.parent = root;
            root.children.add(node);
        }
    }

    /**
     * Generalized preorder traversal (children nodes first then parent node)
     *
     * @param nodelist parsed nodelist (null allowed)
     * @param node root node
     * @return preorder list of nodes
     */
    public List<T> preorder(List<T> nodelist, Node<T> node) {
        if (nodelist == null) {
            nodelist = new ArrayList<>();
        }

        if (node != null) {
            nodelist.add(node.data);
            for (Node<T> child : node.children) {
                nodelist = preorder(nodelist, child);
            }
        }
        return nodelist;
    }

    /**
     * Generalized postorder traversal (children nodes first then parent node)
     *
     * @param nodelist parsed nodelist (null allowed)
     * @param node root node
     * @return postorder list of nodes
     */
    public List<T> postrder(List<T> nodelist, Node<T> node) {
        if (nodelist == null) {
            nodelist = new ArrayList<>();
        }

        if (node != null) {
            for (Node<T> child : node.children) {
                nodelist = preorder(nodelist, child);
            }
            nodelist.add(node.data);
        }
        return nodelist;
    }

}
