package com.backend.global.util.pageutil;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Page<T> {
	private List<T> data;
	private int page;
	private int size;
	private int total;
	private int totalPages;
}
