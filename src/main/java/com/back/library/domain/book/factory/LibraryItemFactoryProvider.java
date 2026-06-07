package com.back.library.domain.book.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LibraryItemFactoryProvider {

    private static final String GOWN_CATEGORY = "학위복";

    private final GeneralBookItemFactory generalBookItemFactory;
    private final GraduationGownItemFactory graduationGownItemFactory;

    public LibraryItemFactory getFactory(String category) {
        if (GOWN_CATEGORY.equals(category)) {
            return graduationGownItemFactory;
        }
        return generalBookItemFactory;
    }
}
