package mate.academy.springbootweb.dto;

public record BookSearchParameters(String[] titleParts, String[] authors, String[] isbns) {
}
