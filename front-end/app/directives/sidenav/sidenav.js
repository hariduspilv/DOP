define([
    'angularAMD',
    'services/serverCallService',
    'services/searchService',
    'directives/learningObjectRow/learningObjectRow',
    'directives/sidebarTaxon/sidebarTaxon'
], function (angularAMD) {
    angularAMD.directive('dopSidenav', ['serverCallService', '$location', '$sce','searchService', 'authenticatedUserService', '$mdDialog', function () {
        return {
            scope: true,
            templateUrl: 'directives/sidenav/sidenav.html',
            controller: function ($rootScope, $scope, $location,serverCallService, $location, searchService, $timeout, metadataService, authenticatedUserService, $sce, $mdDialog) {

                $scope.oneAtATime = true;

                // List of taxon icons
                $scope.taxonIcons = [
                    'extension',
                    'accessibility',
                    'school',
                    'palette'
                ]

                // Testdata for opening taxon
                $scope.taxonTree = {
                    open: [
                        {id: 1},
                        {id: 2}
                    ],
                    child: {
                        open: [
                            {id: 1},
                            {id: 2}
                        ],
                        child: {
                            open: [
                                {id: 1},
                                {id: 2}
                            ],
                            child: {
                                open: [
                                    {id: 1},
                                    {id: 2}
                                ],
                            }
                        }
                    }
                }

                // 1. subtopic -> topic -> subject -> domain -> ec
                // 2. subtopic -> topic -> domain -> ec
                // 3. subtopic -> topic -> module -> specialization -> domain -> ec

                // TAXON - ei tea mis tase
                // function nextLevel(taxon) -- if (... != null) {return ...;}


                $scope.nextLevel = function(data) {
                    // Do we need the subtopic part?
                    if(data.subtopic != null) {
                        return data.subtopic;
                    } else if (data.topic != null) {
                        return data.topic;
                    } else if (data.subject != null) {
                        return data.subject;
                    } else if (data.domain != null) {
                        return data.domain;
                    } else if (data.educationalContext != null) {
                        return data.educationalContext;
                    } else if (data.module != null) {
                        return data.module;
                    } else if (data.specialization != null) {
                        return data.specialization;
                    } else {
                        return null;
                    }
                }

                function makeTreeKey(data) {
                    // I am the data
                    if ($location.path().indexOf('/material') !== -1 && data) {

                        var list = Array();

                        for(i = 0; i < data.taxons.length; i++) {
                            var sublist = Array();
                            var level = data.taxons[i];
                            while(level != null) {
                                sublist.push(level.id);
                                var nextLevel = $scope.nextLevel(level);
                                level = nextLevel;
                            }
                            list.push(sublist.reverse());
                        }

                        if (list != null) {

                        }
                        $scope.taxonTree = list[0];

                    } else {
                        $scope.taxonTree = null;
                    }

                }

                $scope.$watch(function() {
                 return $rootScope.currentMaterial;
                 }, function () {
                 makeTreeKey($rootScope.currentMaterial);
                 }, true);

                $scope.$watch(function() {
                    return $location.url();
                }, function () {
                    if( $location.url().indexOf('/portfolio') != -1 ) {
                        $scope.isViewPortfolioAndEdit = true;
                    } else {
                        $scope.isViewPortfolioAndEdit = false;
                    }

                }, true);

                $scope.$watch(function () {
                    return authenticatedUserService.getUser();
                }, function (user) {
                    $scope.user = user;
                    getNumbersForSidenav();
                }, true);

                $scope.isAdmin = function () {
                    return authenticatedUserService.isAdmin();
                };

                $scope.isModerator = function () {
                    return authenticatedUserService.isModerator();
                };

                $scope.checkUser = function(e, redirectURL) {
                    if ($scope.user) {
                        $location.url('/' + $scope.user.username + redirectURL);
                    } else {
                        $rootScope.afterAuthRedirectURL = redirectURL;
                        $rootScope.sidenavLogin = redirectURL;
                        openLoginDialog(e);
                    }
                };

                $scope.modUser = function() {
                    if (authenticatedUserService.isModerator() || authenticatedUserService.isAdmin()) {
                        return true;
                    } else {
                        return false;
                    }
                }


                //Checks the location
                $scope.isLocation = function (location) {
                    var isLocation = location === $location.path();
                    return isLocation;
                }

                metadataService.loadReducedTaxon(function(callback) {
                    $scope.reducedTaxon = callback;
                });

                if(window.innerWidth > 1280) {$scope.sideNavOpen = true;}



                // TODO: Taxonomy logic
                // DATA: $scope.reducedTaxon

                $scope.test = function(data) {
                    if(data.children) {
                        $scope.asd = data.children;
                        var html = $sce.trustAsHtml('<li ng-repeat="item in asd">' +
                            '<div ng-bind-html="test(item)">{{asd}}</div>' +
                            '</li>')
                        return html;
                    }
                }

                // TAXONOMY LOGIC END



              $scope.status = true;

              $scope.swapState = function() {
                  $scope.status = !$scope.status;
              }

              var SIDE_ITEMS_AMOUNT = 5;

                var params = {
                    q: 'recommended:true',
                    start: 0,
                    sort: 'recommendation_timestamp',
                    sortDirection: 'desc',
                    limit: SIDE_ITEMS_AMOUNT
                };

                serverCallService.makeGet("rest/search", params, getRecommendationsSuccess, getRecommendationsFail);


                function isSearchResultPage() {
                    return $location.url().startsWith('/' + searchService.getSearchURLbase());
                }

                if (isSearchResultPage()) {
                    var params = {
                        limit: SIDE_ITEMS_AMOUNT
                    };

                    var originalSort = searchService.getSort();
                    var originalSortDirection = searchService.getSortDirection();
                    searchService.setSort('like_score');
                    searchService.setSortDirection('desc');
                    var searchUrl = searchService.getQueryURL(true);
                    searchService.setSort(originalSort);
                    searchService.setSortDirection(originalSortDirection);

                    serverCallService.makeGet("rest/search?" + searchUrl, params, searchMostLikedSuccess, getMostLikedFail);
                } else {
                    var params = {
                        maxResults: SIDE_ITEMS_AMOUNT
                    };

                    serverCallService.makeGet("rest/search/mostLiked", params, getMostLikedSuccess, getMostLikedFail);
                }

                function getRecommendationsSuccess(data) {
                    if (isEmpty(data)) {
                        log('No data returned by recommended item search.');
                    } else {
                        $scope.recommendations = data.items;
                    }
                }

                function getRecommendationsFail(data, status) {
                    console.log('Session search failed.')
                }

                function getMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                        getMostLikedFail();
                    } else {
                        $scope.mostLikedList = data;
                    }
                }

                function getMostLikedFail() {
                    console.log('Most liked search failed.')
                }

                function searchMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                        getMostLikedFail();
                    } else {
                        $scope.mostLikedList = data.items;
                    }
                }

                function openLoginDialog(e) {
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/loginDialog/loginDialog.html',
                        controllerUrl: 'views/loginDialog/loginDialog',
                        targetEvent: e
                    }));
                }

                $scope.showMoreRecommendations = function() {
                    searchService.setSearch('recommended:true');
                    searchService.clearFieldsNotInSimpleSearch();
                    searchService.setSort('recommendation_timestamp');
                    searchService.setSortDirection('desc');

                    $location.url('/' + searchService.getSearchURLbase() + searchService.getQueryURL());
                }

                // Number for sidenav
                function getUsersFavorites() {
                    serverCallService.makeGet("rest/learningObject/usersFavorite", {}, getFavoritesSuccess, getFavoritesFail)
                }

                function getFavoritesSuccess(data) {
                    if(data) {
                        $scope.favorites = data
                    }
                }

                function getFavoritesFail() {
                    console.log("failed to retrieve learning objects favorited by the user")
                }

                function getUsersMaterials() {
                    var params = {
                        'username': $scope.user.username
                    };
                    var url = "rest/material/getByCreator";
                    serverCallService.makeGet(url, params, getUsersMaterialsSuccess, getUsersMaterialsFail);
                }

                function getUsersMaterialsSuccess(data) {
                    if (isEmpty(data)) {
                        getUsersMaterialsFail();
                    } else {
                        $scope.materials = data;
                    }
                }

                function getUsersMaterialsFail() {
                    console.log('Failed to get materials.');
                }

                function getUsersPortfolios() {
                    var params = {
                        'username': $scope.user.username
                    };
                    var url = "rest/portfolio/getByCreator";
                    serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);
                }

                function getUsersPortfoliosSuccess(data) {
                    if (isEmpty(data)) {
                        getUsersPortfoliosFail();
                    } else {
                        $scope.portfolios = data;
                    }
                }

                function getUsersPortfoliosFail() {
                    console.log('Failed to get portfolios.');
                }


                function getNumbersForSidenav() {
                    if($scope.user) {
                        if($scope.user.role === "ADMIN" || $scope.user.role === "MODERATOR") {
                            serverCallService.makeGet("rest/material/getBroken", {}, getMaterialsBrokenItemsSuccess, getItemsFail);
                            serverCallService.makeGet("rest/material/getDeleted", {}, getMaterialsDeletedItemsSuccess, getItemsFail);
                            serverCallService.makeGet("rest/portfolio/getDeleted", {}, getPortfoliosDeletedItemsSuccess, getItemsFail);
                            serverCallService.makeGet("rest/impropers", {}, getImproperItemsSuccess, getItemsFail);

                        }
                        getUsersFavorites();
                        getUsersMaterials();
                        getUsersPortfolios();
                    }
                }

                function getItemsFail() {
                    console.log("Failed to get data");
                }

                // Admin info

                function getMaterialsBrokenItemsSuccess(data) {
                    var items = data;
                    var list = [];
                    for(var i = 0; i < items.length; i++) {
                        if(items[i].material.deleted == false) {
                            list.push(items[i]);
                        }
                    }

                    $scope.brokenMaterialsCount = list.length;
                }
                function getMaterialsDeletedItemsSuccess(data) {
                    if (isEmpty(data)) {
                        log('Getting data failed.');
                    } else {
                        $scope.deletedMaterialsCount = data.length;
                    }
                }
                function getPortfoliosDeletedItemsSuccess(data) {
                    if (isEmpty(data)) {
                        log('Getting data failed.');
                    } else {
                        $scope.deletedPortfoliosCount = data.length;
                    }
                }
                function getImproperItemsSuccess(data) {
                    var impropers = data;
                    var improperMaterials = [];
                    var improperPortfolios = [];
                    for (var i = 0; i < impropers.length; i++) {
                        if (impropers[i].learningObject.type === '.Material' && !impropers[i].learningObject.deleted) {
                            improperMaterials.push(impropers[i]);
                        }
                        if (impropers[i].learningObject.type === '.Portfolio' && !impropers[i].learningObject.deleted) {
                            improperPortfolios.push(impropers[i]);
                        }
                    }
                    $scope.improperMaterialsCount = improperMaterials.length;
                    $scope.improperPortfoliosCount = improperPortfolios.length;
                }


                // Starts header color updated process
                $scope.$watch(function() {
                    return $rootScope.forceUpdate;
                }, function() {
                    getNumbersForSidenav();
                }, true);


            }
        }
    }]);
});
