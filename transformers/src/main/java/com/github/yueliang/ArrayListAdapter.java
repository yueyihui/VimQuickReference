package com.github.yueliang;

import java.util.List;

/**
 * Created by c_yiguoc on 16-12-12.
 */

public interface ArrayListAdapter {
    public void changeData(String[] data);

    public void changeData(String[] left, String[] right);

    public void changeData(List<String> dataList);
}
