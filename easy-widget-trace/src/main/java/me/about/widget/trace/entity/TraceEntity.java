package me.about.widget.trace.entity;

import me.about.widget.trace.util.ThreadUtils;
import me.about.widget.trace.view.TreeView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TraceEntity
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:39
 * @description:
 */
public class TraceEntity {

    protected TreeView view;

    protected int deep;

    public TraceEntity() {
        this.view = createTreeView();
        this.deep = 0;
    }

    private TreeView createTreeView() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        String threadTitle = "time=" + dtf.format(LocalDateTime.now()) + ";" + ThreadUtils.getThreadTitle(Thread.currentThread());
        return new TreeView(true, threadTitle);
    }

}
