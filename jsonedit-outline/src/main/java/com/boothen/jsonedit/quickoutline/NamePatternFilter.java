package com.boothen.jsonedit.quickoutline;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * The NamePatternFilter selects the elements which
 * match the given regular expression.
 */
public class NamePatternFilter extends ViewerFilter {

    private Pattern pattern = Pattern.compile(".*");

    @Override
    public Object[] filter(Viewer viewer, TreePath parentPath, Object[] elements) {
        int size = elements.length;
        ArrayList<Object> out = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            Object element = elements[i];
            if (selectTreePath(viewer, parentPath, element)) {
                out.add(element);
            }
        }
        return out.toArray();
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return selectTreePath(viewer, new TreePath(new Object[] { parentElement }), element);
    }

    private boolean selectTreePath(Viewer viewer, TreePath parentPath, Object element) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        ILabelProvider labelProvider = (ILabelProvider) treeViewer.getLabelProvider();

        String matchName = labelProvider.getText(element);
        Matcher matcher = pattern.matcher(matchName);
        if (matchName != null && matcher.find())
            return true;

        return hasUnfilteredChild(treeViewer, parentPath, element);
    }

    private boolean hasUnfilteredChild(TreeViewer viewer, TreePath parentPath, Object element) {
        TreePath elementPath = parentPath.createChildPath(element);
        IContentProvider contentProvider = viewer.getContentProvider();

        Object[] children = contentProvider instanceof ITreePathContentProvider
                ? ((ITreePathContentProvider) contentProvider).getChildren(elementPath)
                : ((ITreeContentProvider) contentProvider).getChildren(element);

        for (int i = 0; i < children.length; i++) {
            if (selectTreePath(viewer, elementPath, children[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets a new regular expression that is used for filtering. Only tree elements with matching
     * label (as defined by the viewer's {@link ILabelProvider}) pass.
     * @param regex a regular expression
     */
    public void setPattern(String regex) {
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
}
