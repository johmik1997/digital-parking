package Utils.Pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {

    public static Pageable paginateResource(int page, int limit, String sortBy, String sortDirection){
        if (page > 0)
            page = page - 1;

        Sort sort = null;
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            sort = Sort.by(direction, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "id");
        }

        return PageRequest.of(page, limit, sort);
    }

//    public static <T> PaginatedResponse<T> createPaginatedResponse(Page<T> page) {
//        return new PaginatedResponse<>(page);
//    }
}

