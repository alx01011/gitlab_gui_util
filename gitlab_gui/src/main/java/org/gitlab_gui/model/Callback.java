package org.gitlab_gui.model;


// an interface that allows to refresh parent components through children
public interface Callback {
    void execute();
}
