package com.example.yone.iplay.db;

import com.example.yone.iplay.model.ThreadInfo;

import java.util.List;

/**
 * Created by Yone on 2015/7/14.
 * 数据访问接口
 */
public interface ThreadDAO {
    /**
     * 插入线程信息
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     * 删除线程
     * @param url
     * @param thread_id
     */
    public void deleteThread(String url,int thread_id);

    /**
     * 更新线程下载进度
     * @param url
     * @param thread_id
     * @param finished
     */
    public void updateThread(String url,int thread_id,int finished);

    /**
     * 查询文件的线程信息
     * @param url
     * @return
     */
    public List<ThreadInfo> getTreads(String url);

    /**
     * 线程是否存在
     * @param url
     * @param thread_id
     * @return
     */
    public boolean isExists(String url,int thread_id);

}
