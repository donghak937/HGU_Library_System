package com.back.library.domain.book.factory;

import com.back.library.domain.book.entity.BookRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LibraryItemFactoryTest {

    @Test
    void generalBookFactoryCreatesGeneralBookSet() {
        BookRequest request = request("Clean Code", "Robert C. Martin", "IT");
        LibraryItemSet itemSet = new GeneralBookItemFactory()
                .createItemSet(request, "B-001", "9780132350884", "IT", List.of("C-001", "C-002"));

        assertEquals("Clean Code", itemSet.getBook().getTitle());
        assertEquals("IT", itemSet.getBook().getCategory());
        assertEquals(2, itemSet.getCopies().size());
        assertEquals("REQ-C-001", itemSet.getCopies().get(0).getBarcode());
        assertEquals("신착도서 코너", itemSet.getCopies().get(0).getLocation());
    }

    @Test
    void graduationGownFactoryCreatesGownSet() {
        BookRequest request = request("학위복 대여", "HGU", "학위복");
        LibraryItemSet itemSet = new GraduationGownItemFactory()
                .createItemSet(request, "B-002", "", "학위복", List.of("C-003"));

        assertEquals("학위복", itemSet.getBook().getCategory());
        assertEquals(1, itemSet.getCopies().size());
        assertEquals("GOWN-C-003", itemSet.getCopies().get(0).getBarcode());
        assertEquals("학위복 보관실", itemSet.getCopies().get(0).getLocation());
    }

    private BookRequest request(String title, String author, String category) {
        BookRequest request = new BookRequest();
        request.setTitle(title);
        request.setAuthor(author);
        request.setPublisher("publisher");
        request.setCategory(category);
        return request;
    }
}
