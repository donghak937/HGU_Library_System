package com.back.library.domain.book.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 대출 가능 물품(Lendable)들을 하나로 묶어 대출할 수 있는 복합체(Composite) 클래스
 */
public class BundleLendable implements Lendable {

    private final String itemId;
    private final String title;
    private final List<Lendable> childItems = new ArrayList<>();

    public BundleLendable(String itemId, String title) {
        this.itemId = itemId;
        this.title = title;
    }

    public void add(Lendable item) {
        if (item != null) {
            childItems.add(item);
        }
    }

    public void remove(Lendable item) {
        childItems.remove(item);
    }

    public List<Lendable> getChildItems() {
        return childItems;
    }

    @Override
    public String getItemId() {
        return this.itemId;
    }

    @Override
    public String getTitle() {
        if (childItems.isEmpty()) {
            return this.title;
        }
        String childrenTitles = childItems.stream()
                .map(Lendable::getTitle)
                .collect(Collectors.joining(", "));
        return this.title + " [" + childrenTitles + "]";
    }

    @Override
    public String getCategory() {
        return "세트/번들";
    }
}
