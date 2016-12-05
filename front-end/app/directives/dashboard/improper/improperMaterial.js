define(['app',], function (app) {
    app.directive('dopImproperMaterial', function () {
        return {
            templateUrl: 'directives/dashboard/improper/improper.html',
            controller: function ($scope, $filter, serverCallService) {
                $scope = $scope.$parent;

                function init() {
                    $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_MATERIALS');
                    serverCallService.makeGet("rest/impropers/materials", {}, success, fail);
                }

                function success(data) {
                    if (data) $scope.getItemsSuccess(removeDeletedFromImpropers(data), 'byReportCount', true);
                    else fail();
                }

                function fail() {
                    console.log("Failed to get improper materials")
                }

                //TODO: This should be done in the back-end
                function removeDeletedFromImpropers(impropers) {
                    var notDeletedImpropers = [];
                    impropers.forEach(function (item) {
                        if (!item.learningObject.deleted) {
                            notDeletedImpropers.push(item);
                        }
                    });

                    return notDeletedImpropers;
                }

                init();
            }
        }
    })
});

