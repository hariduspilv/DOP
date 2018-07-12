package ee.hm.dop.service.solr;

import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SortDirection;
import ee.hm.dop.model.SortType;
import ee.hm.dop.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

class SortBuilder {


    private static final String FAVORITE_FIRST = "gt(termfreq(favored_by_user,'%s'), 0) desc";
    private static final String PORTFOLIO_FIRST = "type desc";
    private static final String POPULAR_FIRST = "views desc";
    private static final String MATERIAL_FIRST = "type asc";
    private static final String RECENT_FIRST = "added desc";
    private static final String EARLIEST_FIRST = "added asc";
    private static final String VISIBILITY = "visibility asc";
    private static final String ICON = "icon asc";
    private static final String ID_DESC = "id desc";
    private static final String ID_ASC = "id asc";

    static String getSort(SearchFilter searchFilter) {
        SortType sort = searchFilter.getSort();
        SortDirection sortDirection = searchFilter.getSortDirection();
        if (sort == null || sortDirection == null) {
            sort = SortType.DEFAULT;
            sortDirection = SortDirection.DESCENDING;
        }

        if (sort == SortType.DEFAULT) {
            return join(defaultSort(sortDirection));
        } else if (sort == SortType.TYPE) {
            return join(typeSort(sortDirection));
        } else if (sort == SortType.ADDED) {
            return join(addedSort(sortDirection));
        } else {
            return String.join(" ", sort.getValue(), sortDirection.getValue());
        }
    }


    private static List<String> defaultSort(SortDirection dir) {
        if (dir.isDesc()) {
            return newArrayList(POPULAR_FIRST, RECENT_FIRST, ID_DESC);
        }
        return newArrayList(POPULAR_FIRST, EARLIEST_FIRST, ID_ASC);
    }

    private static ArrayList<String> addedSort(SortDirection dir) {
        if (dir.recentFirst()) {
            return newArrayList(RECENT_FIRST, POPULAR_FIRST, ID_DESC);
        }
        return newArrayList(EARLIEST_FIRST, POPULAR_FIRST, ID_ASC);
    }

    private static ArrayList<String> typeSort(SortDirection dir) {
        if (dir.portfoliosFirst()) {
            return newArrayList(PORTFOLIO_FIRST, POPULAR_FIRST, RECENT_FIRST, ID_DESC);
        }
        return newArrayList(MATERIAL_FIRST, POPULAR_FIRST, RECENT_FIRST, ID_ASC);
    }

    private static String join(List<String> sortConditions) {
        return sortConditions.stream().collect(Collectors.joining(", "));
    }

    private static List<String> favoriteFirstSort(User user, SortDirection dir) {
        List<String> sorting = defaultSort(dir);
        sorting.add(0, String.format(FAVORITE_FIRST, user.getUsername()));
        return sorting;
    }
}
