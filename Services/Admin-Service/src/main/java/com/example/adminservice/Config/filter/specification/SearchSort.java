package com.example.adminservice.Config.filter.specification;

import lombok.Data;

@Data
public class SearchSort {
    private String selector;
    private boolean desc;

    public SearchSort() {
    }
}
