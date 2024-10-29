package com.gxy.utils;

import java.io.Serializable;

/**
 * 分页工具
 *
 */
public class Pagination implements Serializable{

	private static final long serialVersionUID = -3797791064278676394L;
	/**
	 * 页数
	 */
	private int pageNum;

	/**
	 * 每页大小
	 */
	private int pageSize;

	/**
	 * 总数
	 */
	private long totalCount;

	public Pagination(int pageNum, int pageSize) {
		this.pageNum = pageNum <= 0 ? 1 : pageNum;
		this.pageSize = pageSize <= 0 ? 10 : pageSize;
	}

	public Pagination() {
		this.pageNum = 1;
		this.pageSize = 10;
	}

	public Pagination(int pageNum, int pageSize, int totalCount) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
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

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public String toString() {
		return "Pagination: {" +
				"pageNum=" + pageNum +
				", pageSize=" + pageSize +
				", totalCount=" + totalCount +
				'}';
	}

}
