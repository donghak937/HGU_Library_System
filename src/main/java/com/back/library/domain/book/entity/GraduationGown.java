package com.back.library.domain.book.entity;

/**
 * 학위복(학사복) 데이터 모델 클래스
 * 설계 상 Lendable 인터페이스를 구현하여 도서(Book) 외 물품도 대출 가능함을 증명합니다.
 * (실제 구동 DB 테이블은 간소화를 위해 Book 테이블의 "학위복" 카테고리를 활용합니다.)
 */
public class GraduationGown implements Lendable {

    private String itemId;
    private String title;
    private String category;
    private String size;
    private String gender;

    public GraduationGown(String itemId, String title, String category, String size, String gender) {
        this.itemId = itemId;
        this.title = title;
        this.category = category;
        this.size = size;
        this.gender = gender;
    }

    @Override
    public String getItemId() {
        return this.itemId;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    public String getSize() {
        return this.size;
    }

    public String getGender() {
        return this.gender;
    }
}
