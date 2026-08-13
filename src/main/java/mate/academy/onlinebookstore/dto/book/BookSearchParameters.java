package mate.academy.onlinebookstore.dto.book;

public record BookSearchParameters(String[] titleParts, String[] authors, String[] isbns) {
}
