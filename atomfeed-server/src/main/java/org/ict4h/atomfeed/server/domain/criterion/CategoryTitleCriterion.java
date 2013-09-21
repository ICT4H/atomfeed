package org.ict4h.atomfeed.server.domain.criterion;

public class CategoryTitleCriterion implements Criterion {
    private final String category;
    private final String title;

    public CategoryTitleCriterion(String category, String title) {
        this.category = category;
        this.title = title;
    }
}
