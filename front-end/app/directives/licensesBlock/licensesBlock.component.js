'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.licenceLanguages = ['ET', 'EN', 'RU']
            this.$scope.activeLicenceLanguages = this.licenceLanguages[0]
            this.getCurrentLanguage()
            this.$scope.notifyOfGDPRUpdate = false
        }

        notifyOfGDPRUpdate() {
            this.$scope.notifyOfGDPRUpdate = !this.$scope.notifyOfGDPRUpdate
        }

        toggleLicensesLanguageInputs(licence, lang) {
            licence.activeLicenceLanguage = lang
        }

        isLangFilled(lang, licence) {
            let isFilled = false;

            if ((lang === 'ET') && !!(licence.titleEst && licence.contentEst))
                isFilled = true

            if ((lang === 'EN') && !!(licence.titleEng && licence.contentEng))
                isFilled = true

            if ((lang === 'RU') && !!(licence.titleRus && licence.contentRus))
                isFilled = true

            return isFilled;
        }

        save(licence) {
            this.$scope.isSaving = true

            if (this.$scope.notifyOfGDPRUpdate) {
                this.createAgreement()
                this.$scope.notifyOfGDPRUpdate = false
            }

            this.licensesService.saveLicence(licence)
                .then(response => {
                    if (response.status === 200) {
                        this.createDialogOpen = false
                        this.$scope.isSaving = false
                        licence.edit = !licence.edit
                        this.getLicenses()
                        this.toastService.show('TERMS_SAVED')
                    } else {
                        this.$scope.isSaving = false
                        this.toastService.show('TERMS_SAVE_FAILED')
                    }
                })
            this.$scope.isSaving = false
        }

        getCurrentLanguage() {
            return this.translationService.getLanguage()
        }

        isLicensesEditMode() {
            return this.editMode
        }

        isCreateDialogOpen() {
            return this.createDialogOpen
        }

        editLicence(licence) {
            this.createDialogOpen = !this.createDialogOpen
            licence.edit = !licence.edit;
        }

        cancelEdit(licence) {
            this.dialogService.showCancelConfirmationDialog(
                'ARE_YOU_SURE_CANCEL',
                '',
                () => this.cancelConfirmed(licence))
        }

        cancelConfirmed(licence) {
            if (licence.new) {
                this.removeLicence()
            } else {
                licence.edit = !licence.edit;
            }
            this.createDialogOpen = false
            this.$scope.notifyOfGDPRUpdate = false
            this.getLicenses()
        }

        delete(licence) {
            this.dialogService.showDeleteConfirmationDialog(
                'ARE_YOU_SURE_DELETE',
                '',
                () => this.licensesService.deleteLicence(licence)
                    .then(() => {
                        this.getLicenses()
                        this.toastService.show('TERM_DELETED')
                        this.createDialogOpen = false
                    })
            )
        }

        isSubmitDisabled(licence) {
            return !(licence.titleEst && licence.titleEng &&
                licence.titleRus && (licence.contentEst && licence.contentEst !== '<br>') &&
                (licence.contentEng && licence.contentEng !== '<br>') && (licence.contentRus && licence.contentRus !== '<br>'))

        }

        createAgreement() {
            let version = 1;
            this.serverCallService.makeGet('rest/licenceAgreement/latest')
                .then((res) => {
                    version = Number(res.data.version) + 1;
                    this.serverCallService
                        .makePost('rest/licenceAgreement', {url: '/litsentsitingimused', version: version})
                        .then((response) => {
                            if (response.status === 200) {
                                console.log('agreement added')
                            }
                            else {
                                this.toastService.show('GDPR_NOTIFY_FAILED')
                            }
                        }), () => {
                        this.toastService.show('GDPR_NOTIFY_FAILED')
                    }
                })
        }
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        'serverCallService',
        'translationService',
        'searchService',
        'licensesService',
        'toastService',
    ]
    component('dopLicensesBlock', {
        bindings: {
            licenses: '<',
            editMode: '<',
            removeLicence: '&',
            getLicenses: '&',
            createDialogOpen: '='

        },
        templateUrl: 'directives/licensesBlock/licensesBlock.html',
        controller
    })
}
