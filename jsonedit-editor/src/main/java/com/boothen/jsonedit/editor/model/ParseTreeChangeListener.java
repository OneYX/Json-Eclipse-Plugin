package com.boothen.jsonedit.editor.model;

import org.antlr.v4.runtime.tree.ParseTree;

public interface ParseTreeChangeListener {

    /**
     * @param node the node that was added
     */
    void nodeAdded(ParseTree node);

    /**
     * @param node the node that was removed
     */
    void nodeRemoved(ParseTree node);
}
