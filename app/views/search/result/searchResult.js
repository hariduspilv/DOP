define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', 'searchService',
       function($scope, serverCallService, translationService, $location, searchService) {

        // Pagination variables
        $scope.paging = {};
        $scope.paging.thisPage = 0;

        var RESULTS_PER_PAGE = 24;
        var start = 0;

        init();
        search();

        function init() {
            // Redirect to landing page if neither query or filters are present
            if (!searchService.queryExists()) {
                $location.url('/');
            }

            // Get search query and current page
            $scope.searchQuery = searchService.getQuery();
        }

        function search() {
            var isTerminal = $scope.paging.thisPage >= $scope.paging.totalPages;

            if (isTerminal) return;

            $scope.searching = true;

            start = RESULTS_PER_PAGE * $scope.paging.thisPage;

            var params = {
                'q': $scope.searchQuery,
                'start': start
            };

            if (searchService.getTaxon()) {
                params.taxon = searchService.getTaxon();
            }

            if (searchService.isPaid() === false) {
                params.paid = searchService.isPaid();
            }

            if (searchService.getType() && searchService.isValidType(searchService.getType())) {
                params.type = searchService.getType();
            }

            if (searchService.getLanguage()) {
                params.language = searchService.getLanguage();
            }

            if (searchService.getTargetGroups()) {
                params.targetGroup = searchService.getTargetGroups();
            }

            if (searchService.getResourceType()) {
                params.resourceType = searchService.getResourceType();
            }

            serverCallService.makeGet("rest/search", params, searchSuccess, searchFail);
        }

        function searchSuccess(data) {
            if (isEmpty(data)) {
                searchFail();
            } else {
                $scope.items = $scope.items || [];
                $scope.items.push.apply($scope.items, data.items);

                $scope.paging.thisPage++;
                $scope.totalResults = data.totalResults;
                $scope.paging.totalPages = Math.ceil($scope.totalResults / RESULTS_PER_PAGE);
            }

            $scope.searching = false;
        }

        function searchFail() {
            console.log('Search failed.');
            $scope.searching = false;
        }

        $scope.getNumberOfResults = function() {
            return $scope.totalResults || 0;
        };

        $scope.nextPage = function() {
            search();
        }
    }]);
});
