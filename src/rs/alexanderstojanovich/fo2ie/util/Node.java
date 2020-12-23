/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 * @param <T> data type for node
 */
public class Node<T> {

    protected final T data;
    protected Node<T> parent; // cannot set parent outside protection
    protected final List<Node<T>> children = new ArrayList<>();

    public Node(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public Node<T> getParent() {
        return parent;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

}
