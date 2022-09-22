package com.nltecklib.utils;

import java.util.List;

public class QuickSort {
	/**
	 * 快速排序
	 * 
	 * @param list
	 */
	public static void quickSort(List<Double> list) {
		quickSort(list, 0, list.size() - 1);
	}

	private static void quickSort(List<Double> list, int first, int last) {
		if (first < last) { // 至少两个位置
			int pivotIndex = partition(list, first, last); // 定义pivotIndex中间位置。partition是检索这个方法
			quickSort(list, first, pivotIndex - 1); // 排序左半边
			quickSort(list, pivotIndex + 1, last); // 排序右半边
		}
	}

	private static int partition(List<Double> list, int left, int right) {// 对数组A下标从first到last中选一个主元，确定其位置，左边小于，右边大于。

		double pivot = list.get(left);// 先定义区间数组第一个元素为主元
		int i = left; // 定义最低的索引low是first+1。比主元大一位
		int j = right; // 定义最高的索引high是last
		while (i != j) { // 当low小于high的位置时，执行以下循环
			while (list.get(j) > pivot && i < j) {// 当high的索引上的值比主元大时，且索引大于low时
				j--; // 寻找比主元小的值的位置索引
			}
			while (list.get(i) <= pivot && i < j) {// 当low的索引上的值比主元小时，索引小于high时
				i++; // 寻找比主元大的值的位置索引。
			}

			if (i < j) { // 交换low和high的值
				double t = list.get(i);
				list.set(i, list.get(j));
				list.set(j, t);
			}
		}
		list.set(left, list.get(j));
		list.set(j, pivot);
		return j;
	}

}
