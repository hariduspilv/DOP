package ee.hm.dop.guice.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.service.SearchEngineService;

/**
 * Guice provider of Search Engine Service.
 */
@Singleton
public class SearchEngineServiceTestProvider implements Provider<SearchEngineService> {

    @Override
    public synchronized SearchEngineService get() {
        return new SearchEngineServiceMock();
    }
}

class SearchEngineServiceMock implements SearchEngineService {

    private static final Map<String, List<Document>> searchResponses;

    private static final int RESULTS_PER_PAGE = 3;

    static {
        searchResponses = new HashMap<>();

        addArabicQuery();
        addBigQuery();
        addQueryWithSubjectFilter();
        addQueryWithResourceTypeFilter();
        addQueryWithSubjectAndResourceTypeFilter();
        addQueryWithEducationalContextFilter();
        addQueryWithSubjectAndEducationalContextFilter();
        addQueryWithResourceTypeAndEducationalContextFilter();
        addQueryWithAllFilters();
    }

    private static void addArabicQuery() {
        String arabicQuery = "المدرسية*";
        ArrayList<Document> arabicSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("3");
        arabicSearchResult.add(newDocument);

        searchResponses.put(arabicQuery, arabicSearchResult);
    }

    private static void addBigQuery() {
        String bigQuery = "thishasmanyresults*";
        ArrayList<Document> bigQueryDocuments = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            Document newDocument = new Document();
            newDocument.setId(Long.toString(i));
            bigQueryDocuments.add(newDocument);
        }

        searchResponses.put(bigQuery, bigQueryDocuments);
    }

    private static void addQueryWithSubjectFilter() {
        String filteredQuery = "(filteredquery*) AND subject:\"mathematics\"";
        ArrayList<Document> filteredSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("5");
        filteredSearchResult.add(newDocument);

        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithResourceTypeFilter() {
        String filteredQuery = "(beethoven*) AND resource_type:\"audio\"";
        ArrayList<Document> filteredSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("4");
        filteredSearchResult.add(newDocument);

        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndResourceTypeFilter() {
        String filteredQuery = "(beethoven*) AND subject:\"mathematics\" AND resource_type:\"audio\"";
        ArrayList<Document> filteredSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("7");
        filteredSearchResult.add(newDocument);

        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextFilter() {
        String filteredQuery = "(beethoven*) AND educational_context:\"preschool\"";
        ArrayList<Document> filteredSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("6");
        filteredSearchResult.add(newDocument);

        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndEducationalContextFilter() {
        String filteredQuery = "(beethoven*) AND subject:\"mathematics\" AND educational_context:\"preschool\"";
        ArrayList<Document> filteredSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("8");
        filteredSearchResult.add(newDocument);

        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithResourceTypeAndEducationalContextFilter() {
        String filteredQuery = "(beethoven*) AND resource_type:\"audio\" AND educational_context:\"preschool\"";
        ArrayList<Document> filteredSearchResult = new ArrayList<>();
        Document newDocument1 = new Document();
        newDocument1.setId("7");
        Document newDocument2 = new Document();
        newDocument2.setId("8");
        filteredSearchResult.add(newDocument1);
        filteredSearchResult.add(newDocument2);

        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithAllFilters() {
        String filteredQuery = "(john*) AND subject:\"mathematics\" AND resource_type:\"audio\" AND educational_context:\"preschool\"";
        ArrayList<Document> filteredSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("2");
        filteredSearchResult.add(newDocument);

        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    @Override
    public SearchResponse search(String query, long start) {
        if (!searchResponses.containsKey(query)) {
            return new SearchResponse();
        }

        List<Document> allDocuments = searchResponses.get(query);
        List<Document> selectedDocuments = new ArrayList<>();
        for (int i = 0; i < allDocuments.size(); i++) {
            if (i >= start && i < start + RESULTS_PER_PAGE) {
                selectedDocuments.add(allDocuments.get(i));
            }
        }

        Response response = new Response();
        response.setDocuments(selectedDocuments);
        response.setStart(start);
        response.setTotalResults(selectedDocuments.size());

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);

        return searchResponse;
    }
}
