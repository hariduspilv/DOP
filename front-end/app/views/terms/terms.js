'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getTerms()
            this.initNewTerm()
            this.$scope.editMode = false
            this.$translate('TERMS_HEADER').then((translation) => this.$rootScope.tabTitle = translation);
        }

        initNewTerm() {
            this.$scope.newTerm = {
                titleEst: '',
                titleEng: '',
                titleRus: '',
                contentEst: '',
                contentEng: '',
                contentRus: '',
                termLanguages: ['ET', 'EN', 'RU'],
                edit: true,
                new: true
            }
            this.$scope.newTerm.activeTermLanguage = this.$scope.newTerm.termLanguages[0]
        }

        isAdmin() {
            if (this.authenticatedUserService.isAdmin()) {
                this.$scope.editMode = true
                return true
            }
            return false
        }

        getTerms() {
            this.termsService.getTerms()
                .then(({data}) => {
                    this.termsBlockExists = data.length;
                    for (const term of data) {
                        term.termLanguages = ['ET', 'EN', 'RU'];
                        term.activeTermLanguage = term.termLanguages[0];
                    }
                    this.$scope.terms = data
                })
        }

        editTermsPage() {
            this.$scope.editMode = true
        }

        createTerm() {
            this.$scope.editMode = true
            this.$scope.terms.push(this.$scope.newTerm)
            this.initNewTerm()
            this.createDialogOpen = true
            this.$timeout(() => {
                window.scrollTo(0, document.body.scrollHeight);
            }, 50)
        }

        removeTerm() {
            this.$scope.terms.pop()
            this.initNewTerm()
        }

    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$translate',
        'authenticatedUserService',
        'termsService',
        '$timeout',
    ]
    angular.module('koolikottApp').controller('termsController', controller)
}
