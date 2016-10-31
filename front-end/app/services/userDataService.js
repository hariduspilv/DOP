define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticatedUserService'
], function (angularAMD) {
    var instance;

    var brokenMaterialsCountCallbacks = [];
    var deletedMaterialsCountCallbacks = [];
    var deletedPortfoliosCountCallbacks = [];
    var improperMaterialsCountCallbacks = [];
    var improperPortfoliosCountCallbacks = [];
    var userFavoritesCountCallbacks = [];
    var userMaterialsCountCallbacks = [];
    var userPortfoliosCountCallbacks = [];


    angularAMD.factory('userDataService', ['serverCallService', 'authenticatedUserService',
        function (serverCallService, authenticatedUserService) {
            init();

            function init() {
                if (authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) {
                    serverCallService.makeGet("rest/material/getBroken/count", {}, getBrokenMaterialsCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getDeleted/count", {}, getDeletedMaterialsCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getDeleted/count", {}, getDeletedPortfoliosCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/impropers/materials/count", {}, getImproperMaterialsCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/impropers/portfolios/count", {}, getImproperPortfoliosCountSuccess, getItemsFail);
                }
                if (authenticatedUserService.isAuthenticated()) {
                    serverCallService.makeGet("rest/learningObject/usersFavorite/count", {}, getFavoritesCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getByCreator/count", getUsername(), getUsersMaterialsCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getByCreator/count", getUsername(), getUsersPortfoliosCountSuccess, getItemsFail);
                }
            }

            function getUsername() {
                if (authenticatedUserService.isAuthenticated()) return {'username': authenticatedUserService.getUser().username};
            }

            function getItemsFail() {
                console.log("Failed to get data");
            }


            function getBrokenMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    brokenMaterialsCountCallbacks.forEach(function (callback) {
                        callback(data);
                    });
                    brokenMaterialsCountCallbacks = [];
                    localStorage.setItem("brokenMaterialsCount", data);
                }
            }

            function getDeletedMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    deletedMaterialsCountCallbacks.forEach(function (callback) {
                        callback(data);
                    });
                    deletedMaterialsCountCallbacks = [];
                    localStorage.setItem("deletedMaterialsCount", data);
                }
            }

            function getDeletedPortfoliosCountSuccess(data) {
                if (!isEmpty(data)) {
                    deletedPortfoliosCountCallbacks.forEach(function (callback) {
                        callback(data);
                    });
                    deletedPortfoliosCountCallbacks = [];
                    localStorage.setItem("deletedPortfoliosCount", data);
                }
            }

            function getImproperMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    improperMaterialsCountCallbacks.forEach(function (callback) {
                        callback(data);
                    });
                    improperMaterialsCountCallbacks = [];
                    localStorage.setItem("improperMaterialsCount", data);
                }
            }

            function getImproperPortfoliosCountSuccess(data) {
                if (!isEmpty(data)) {
                    improperPortfoliosCountCallbacks.forEach(function (callback) {
                        callback(data);
                    });
                    improperPortfoliosCountCallbacks = [];
                    localStorage.setItem("improperPortfoliosCount", data);
                }
            }

            function getFavoritesCountSuccess(data) {
                userFavoritesCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                userFavoritesCountCallbacks = [];
                localStorage.setItem("userFavoritesCount", data);

            }

            function getUsersMaterialsCountSuccess(data) {
                userMaterialsCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                userMaterialsCountCallbacks = [];
                localStorage.setItem("userMaterialsCount", data);

            }

            function getUsersPortfoliosCountSuccess(data) {
                userPortfoliosCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                userPortfoliosCountCallbacks = [];
                localStorage.setItem("userPortfoliosCount", data);

            }


            instance = {
                loadBrokenMaterialsCount: function (callback) {
                    var data = localStorage.getItem("brokenMaterialsCount");
                    if (data) {
                        callback(data);
                    }
                    brokenMaterialsCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getBroken/count", {}, getBrokenMaterialsCountSuccess, getItemsFail);
                },
                loadDeletedMaterialsCount: function (callback) {
                    var data = localStorage.getItem("deletedMaterialsCount");
                    if (data) {
                        callback(data);
                    }
                    deletedMaterialsCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getDeleted/count", {}, getDeletedMaterialsCountSuccess, getItemsFail);

                },
                loadDeletedPortfoliosCount: function (callback) {
                    var data = localStorage.getItem("deletedPortfoliosCount");
                    if (data) {
                        callback(data);
                    }
                    deletedPortfoliosCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/portfolio/getDeleted/count", {}, getDeletedPortfoliosCountSuccess, getItemsFail);
                },

                loadImproperMaterialsCount: function (callback) {
                    var data = localStorage.getItem("improperMaterialsCount");
                    if (data) {
                        callback(data);
                    }
                    improperMaterialsCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/impropers/materials/count", {}, getImproperMaterialsCountSuccess, getItemsFail);
                },

                loadImproperPortfoliosCount: function (callback) {
                    var data = localStorage.getItem("improperPortfoliosCount");
                    if (data) {
                        callback(data);
                    }
                    improperPortfoliosCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/impropers/portfolios/count", {}, getImproperPortfoliosCountSuccess, getItemsFail);
                },

                loadUserFavoritesCount: function (callback) {
                    var data = localStorage.getItem("userFavoritesCount");
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/learningObject/usersFavorite/count", {}, getFavoritesCountSuccess, getItemsFail);
                },
                loadUserMaterialsCount: function (callback) {
                    var data = localStorage.getItem("userMaterialsCount");
                    if (data) {
                        callback(data);
                    }
                    userMaterialsCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getByCreator/count", getUsername(), getUsersMaterialsCountSuccess, getItemsFail);
                },
                loadUserPortfoliosCount: function (callback) {
                    var data = localStorage.getItem("userPortfoliosCount");
                    if (data) {
                        callback(data);
                    }
                    userPortfoliosCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/portfolio/getByCreator/count", getUsername(), getUsersPortfoliosCountSuccess, getItemsFail);
                }

            };

            return instance;
        }
    ]);
});