package edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad;

/**
 * Represents viewers which might be initialized before the UI has calculated its size
 * thus requiring notification of the view's actual dimensions.
 *
 * Created by Ben on 6/29/2016.
 */
public interface NeedDimensions {
    void reportDimensions(int width, int height);
}
