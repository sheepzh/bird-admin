package com.bird.common;

import com.github.pagehelper.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhyyy
 */
public class PageInfo<T> {
    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 页大小
     */
    private int pageSize;

    /**
     * 总数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 当页结果集
     */
    private List<? extends T> list;

    /**
     * 是否为第一页
     */
    private boolean isFirstPage = false;


    /**
     * 是否是最后一页
     */
    private boolean isLastPage = false;

    /**
     * 包装Page对象
     *
     * @param list list
     */
    public PageInfo(List<T> list) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.pages = page.getPages();
            this.list = page;
            this.total = page.getTotal();
        } else if (list != null) {
            this.pageNum = 1;
            this.pageSize = list.size();
            this.pages = 1;
            this.list = list;
            this.total = list.size();
        }
        if (list != null) {
            //判断页面边界
            judgePageBoundary();
        }
    }


    /**
     * 判定页面边界
     */
    private void judgePageBoundary() {
        isFirstPage = pageNum == 1;
        isLastPage = pageNum == pages;
    }


    public int getPageNum() {
        return pageNum;
    }


    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }


    public int getPageSize() {
        return pageSize;
    }


    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public long getTotal() {
        return total;
    }


    public void setTotal(long total) {
        this.total = total;
    }


    public int getPages() {
        return pages;
    }


    public void setPages(int pages) {
        this.pages = pages;
    }


    public List<T> getList() {
        return new ArrayList<>(list);
    }


    public void setList(List<? extends T> list) {
        this.list = list;
    }


    public boolean isIsFirstPage() {
        return isFirstPage;
    }


    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }


    public boolean isIsLastPage() {
        return isLastPage;
    }


    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }
}