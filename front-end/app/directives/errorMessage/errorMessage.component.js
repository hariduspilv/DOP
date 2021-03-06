'use strict'

{
const VIEW_STATE_MAP = {
    showDeleted: [
        'delete_forever', // icon
        'ERROR_MSG_DELETED' // message translation key
    ],
    showChanged: [
        'autorenew',
        'MESSAGE_CHANGED_LEARNING_OBJECT',
        [{
            icon: () => 'undo',
            label: 'UNDO_CHANGES',
            onClick: ($ctrl) => $ctrl.setAllChanges('revert'),
            show: ($ctrl) => $ctrl.$scope.isAdmin || $ctrl.$scope.isModerator
        }, {
            icon: ($ctrl) => {
                const numNewTaxons = $ctrl.newTaxons ? $ctrl.newTaxons.length : 0
                return numNewTaxons > 1 || numNewTaxons && $ctrl.oldLink
                    ? 'done_all'
                    : 'done'
            },
            label: 'ACCEPT_CHANGES',
            onClick: ($ctrl) => $ctrl.setAllChanges('accept'),
            show: ($ctrl) => $ctrl.$scope.isAdmin || $ctrl.$scope.isModerator
        }],
        ($ctrl) => $ctrl.getChanges()
    ],
    showImproper: [
        'warning',
        'ERROR_MSG_IMPROPER',
        [{
            icon: () => 'delete',
            label: 'BUTTON_REMOVE',
            onClick: ($ctrl) => $ctrl.setDeleted(),
            show: ($ctrl) => $ctrl.$scope.isAdmin || $ctrl.$scope.isModerator,
        }, {
            icon: () => 'done',
            label: 'REPORT_NOT_IMPROPER',
            onClick: ($ctrl) => $ctrl.setNotImproper(),
            show: ($ctrl) => $ctrl.$scope.isAdmin || $ctrl.$scope.isModerator
        }],
        ($ctrl) => $ctrl.getReasons()
    ],
    showUnreviewed: [
        'lightbulb_outline',
        ($ctrl) =>
            $ctrl.data && $ctrl.data.type == '.Portfolio'
                ? 'ERROR_MSG_UNREVIEWED_PORTFOLIO'
                : 'ERROR_MSG_UNREVIEWED',
        [{
            icon: () => 'done',
            label: 'BUTTON_REVIEW',
            onClick: ($ctrl) => $ctrl.setReviewed(),
            show: ($ctrl) => $ctrl.$scope.isAdmin || $ctrl.$scope.isModerator
        }]
    ]
}
class controller extends Controller {
    $onChanges({ data }) {
        if (data && data.currentValue && (!data.previousValue || data.currentValue.id != data.previousValue.id))
            this.init()
    }
    $onInit() {
        this.oldLinkColor = this.$mdColors.getThemeColor('grey-500')

        this.$scope.getChangeType = (item) =>
            item.taxon
                ? 'DETAIL_VIEW_DOMAIN'
                : item.resourceType
                    ? 'MATERIAL_VIEW_RESOURCE_TYPE'
                    : item.targetGroup
                        ? 'DETAIL_VIEW_TARGET_GROUP'
                        : ''

        this.$scope.getChangeName = (item) =>
            item.taxon
                ? this.taxonService.getTaxonTranslationKey(item.taxon)
                : item.resourceType
                    ? item.resourceType.name
                    : item.targetGroup
                        ? 'TARGET_GROUP_' + item.targetGroup
                        : ''

        this.$scope.toggleExpandable = this.toggleExpandable.bind(this)

        this.onWindowResizeReports = () => requestAnimationFrame(
            this.toggleExpandableReports.bind(this)
        )
        this.onWindowResizeChanges = () => requestAnimationFrame(
            this.toggleExpandableChanges.bind(this)
        )

        this.$rootScope.$on('dashboard:adminCountsUpdated', () => this.init())
        this.$rootScope.$on('errorMessage:reported', () => this.init())
        this.$rootScope.$on('portfolioChanged', () => this.init());
        this.$rootScope.$watch('learningObjectChanged', (newValue, oldValue) => {
            if (newValue != oldValue)
                this.init()
        })
        this.onLearningObjectChange = this.onLearningObjectChange.bind(this)
        this.$scope.$watch(() => this.storageService.getMaterial(), this.onLearningObjectChange, true)
        this.$scope.$watch(() => this.storageService.getPortfolio(), this.onLearningObjectChange, true)
    }
    $onDestroy() {
        if (this.listeningResize) {
            window.removeEventListener('resize', this.onWindowResizeReports)
            window.removeEventListener('resize', this.onWindowResizeChanges)
        }
        this.$rootScope.learningObjectDeleted = undefined
        this.$rootScope.learningObjectImproper = undefined
        this.$rootScope.learningObjectUnreviewed = undefined
        this.$rootScope.learningObjectChanged = undefined
        this.$rootScope.learningObjectChanges = undefined
    }
    onLearningObjectChange(newLearningObject, oldLearningObject) {
        if (newLearningObject && (!oldLearningObject || newLearningObject.changed != oldLearningObject.changed))
            this.init()
    }
    init() {
        this.$scope.isAdmin = this.authenticatedUserService.isAdmin()
        this.$scope.isModerator = this.authenticatedUserService.isModerator()
        this.setState('', '', [], false); // reset

        if (!this.$rootScope.learningObjectPrivate) {
            this.bannerType =
                this.$rootScope.learningObjectDeleted ? 'showDeleted' :
                    this.$rootScope.learningObjectImproper ? 'showImproper' :
                        this.$rootScope.learningObjectUnreviewed ? 'showUnreviewed' :
                            this.$rootScope.learningObjectChanged && 'showChanged'

            // make sure if deleted then show deleted stuff
            if (this.$rootScope.learningObjectDeleted)
                this.bannerType = 'showDeleted';

            if (this.bannerType)
                this.setState(...VIEW_STATE_MAP[this.bannerType])
        }
        if (this.$rootScope.learningObjectPrivate && this.$rootScope.learningObjectDeleted) {
                this.setState(...VIEW_STATE_MAP['showDeleted'])
        }
    }

    show(cb) {
        if (!(this.$rootScope.learningObjectImproper || this.$rootScope.learningObjectUnreviewed) && !(this.$scope.isAdmin || this.$scope.isModerator)) {
            return false;
        }
        if (typeof cb === 'boolean') {
            return cb;
        } else {
            return true;
        }
    }

    setState(icon, messageKey, buttons, cb) {
        this.$scope.show = this.show(cb);
        this.$scope.icon = icon
        if (this.bannerType != 'showChanged')
            this.$scope.messageKey = typeof messageKey === 'function' ? messageKey(this) : messageKey
            this.$scope.htmlMessage = false
        this.$scope.iconTooltipKey = this.$scope.messageKey
        this.$scope.message = ''
        this.$scope.buttons = buttons
        this.$scope.reports = null
        this.$scope.showExpandableReports = false
        this.$scope.showExpandableChanges = false

        this.setMarginForIE();

        if (typeof cb === 'function')
            cb(this)
    }
    showButton(conditions) {
        return conditions.reduce(
            (show, condition) =>
                show || typeof this[condition] === 'undefined'
                    ? show
                    : this[condition],
            false
        )
    }

    setMarginForIE() {
        let messageKey = this.$scope.messageKey
        this.$scope.marginIE = !!(super.isIE() && (messageKey === 'ERROR_MSG_DELETED' || messageKey === 'ERROR_MSG_UNREVIEWED'));
    }

    setOwner(data) {
        this.$scope.isOwner = !this.authenticatedUserService.isAuthenticated()
            ? false : data && data.creator
                ? data.creator.id === this.authenticatedUserService.getUser().id : false
    }

    getReasons() {
        const { id } = this.data || {}
        this.setOwner(this.data)
        if (id) {
            if (this.$scope.isAdmin || this.$scope.isModerator) {
                this.serverCallService
                    .makeGet('rest/admin/improper/' + id)
                    .then(({data: reports}) => {
                        this.resizeWindow(reports)
                        this.setFullReason(reports)
                        }
                    )
            } else if (this.$scope.isOwner) {
                this.serverCallService
                    .makeGet('rest/impropers/owner/' + id)
                    .then(({data: reports}) => {
                            this.resizeWindow(reports)
                            this.setFullReason(reports)
                        }
                    )
            } else {
                this.serverCallService
                    .makeGet('rest/impropers/user/' + id)
                    .then(({data: reports}) => {
                        this.resizeWindow(reports)
                        this.setFullReason(reports)
                        }
                    )
            }
        }
    }

    resizeWindow(reports) {
        if (Array.isArray(reports) && reports.length) {
                if (!this.listeningResize) {
                    this.listeningResize = true
                    window.addEventListener('resize', this.onWindowResizeReports)
                }
                this.onWindowResizeReports()
        }
    }

    getTranslation(key) {
        return this.$translate.instant(key)
    }

    setFullReason(reports) {
        for (const report of reports) {
            let fullReason = this.getTranslation(report.reportingReasons[0].reason)
            for (let singleReason of report.reportingReasons.slice(1)) {
                fullReason += ', ' + this.getTranslation(singleReason.reason).toLowerCase()
            }
            report.fullReason = fullReason;
            this.$scope.reports = reports;
        }
    }

    getChanges() {
        const { id } = this.data || {}

        if (id && (this.$scope.isAdmin || this.$scope.isModerator))
            this.serverCallService
                .makeGet('rest/admin/changed/'+id)
                .then(({ data: changes }) => {
                    this.newTaxons = []
                    this.oldLink = ''
                    if (Array.isArray(changes) && changes.length) {
                        changes.forEach(change =>
                            change.taxon
                                ? this.newTaxons.push(change.taxon) // taxon was added
                                : this.oldLink = change.materialSource // link was changed
                        )
                        this.changes = changes
                        this.$rootScope.learningObjectChanges = this.changes

                        if (this.$scope.expanded)
                            this.setExpandableHeight()

                        this.metadataService.loadEducationalContexts(() => {
                            this.$scope.messageKey = ''
                            this.$scope.htmlMessage = this.getChangedMessage()

                            if (!this.listeningResize) {
                                this.listeningResize = true
                                window.addEventListener('resize', this.onWindowResizeChanges)
                            }
                            this.onWindowResizeChanges()
                        })
                    }
                })
    }
    getChangedMessage() {
        const translate = (key) => this.$translate.instant(key)
        const taxons = () =>
            this.newTaxons
                .map(t => `<span class="changed-new-taxon">${this.getTaxonLabel(t)}</span>`)
                .join(', ')

        const message = this.oldLink && !this.newTaxons.length
            ? `${translate('CHANGED_LINK')} <a href="${this.oldLink}" target="_blank" style="color: ${this.oldLinkColor}">${this.oldLink}</a>`
            : this.newTaxons.length === 1
                ? `${translate(this.oldLink ? 'CHANGED_LINK_AND_ADDED_ONE_TAXON' : 'ADDED_ONE_TAXON')} ${taxons()}`
                : `${this.sprintf(
                        translate(this.oldLink ? 'CHANGED_LINK_AND_ADDED_MULTIPLE_TAXONS' : 'ADDED_MULTIPLE_TAXONS'),
                        this.newTaxons.length
                    )} ${taxons()}`

        return message
    }
    getTaxonLabel(taxon) {
        return this.$translate.instant(
            this.taxonService.getTaxonTranslationKey(taxon)
        )
    }
    toggleExpandableReports() {
        if (this.$scope.reports.length > 0 || this.forceCollapsible)
            return this.$scope.showExpandableReports = true

        const { offsetWidth, scrollWidth } = document.getElementById('error-message-heading')
        this.$scope.showExpandableReports = offsetWidth && scrollWidth && scrollWidth > offsetWidth
    }
    toggleExpandableChanges() {
        if (this.newTaxons.length > 1 || this.oldLink && this.newTaxons.length === 1)
            return this.$scope.showExpandableChanges = true

        const { offsetWidth, scrollWidth } = document.getElementById('error-message-heading')
        this.$scope.showExpandableChanges = offsetWidth && scrollWidth && scrollWidth > offsetWidth
    }
    toggleExpandable() {
        if (!this.$scope.expanded) {
            this.setExpandableHeight()
            this.$scope.expanded = true
        } else
            this.$scope.expanded = false
    }
    setExpandableHeight() {
        const expandable = document.querySelector('.error-message-expandable')
        if (expandable) {
            expandable.style.height = ''
            expandable.style.height = Math.min(400, expandable.scrollHeight)+'px'
        }
    }
    setNotImproper() {
        const { id, type } = this.data

        if (id && (this.$scope.isAdmin || this.$scope.isModerator))
            this.serverCallService
                .makePost('rest/admin/improper/setProper', {id, type})
                .then(({ status, data }) => {
                    this.$rootScope.learningObjectImproper = false
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.learningObjectChanged = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
    }
    setDeleted() {
        const { id, type } = this.data
        // TODO ips creator can delete their portfolio
        if (id && (this.$scope.isAdmin || this.$scope.isModerator)) {
            const isPortfolio = this.isPortfolio(this.data);

            (isPortfolio
                ? this.serverCallService.makePost('rest/portfolio/delete', { id, type })
                : this.serverCallService.makePost('rest/material/delete', { id, type })
            ).then(({ status, data }) => {
                this.data.deleted = true
                this.toastService.showOnRouteChange(isPortfolio ? 'PORTFOLIO_DELETED' : 'MATERIAL_DELETED')
                this.$rootScope.learningObjectDeleted = true
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
            })
        }
    }
    setReviewed() {
        const { id, type } = this.data

        if (id && (this.$scope.isAdmin || this.$scope.isModerator))
            this.serverCallService
                .makePost('rest/admin/firstReview/setReviewed', { id, type })
                .then(({ status, data }) => {
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
    }
    setAllChanges(action, cb) {
        const { id } = this.data || {}

        if (id) {
            // making optimistic changes
            const revertedOrAccepted = this.changes.splice(0, this.changes.length)
            this.$rootScope.learningObjectChanged = false

            const undo = () => {
                // the changes were too optimistic
                ;[].splice.apply(this.changes, [0, 0].concat(revertedOrAccepted))
                this.$rootScope.learningObjectChanged = true
            }

            this.serverCallService
                .makePost(`rest/admin/changed/${id}/${action}All`)
                .then(({ status, data }) => {
                    200 <= status && status < 300
                        ? this.setData(data)
                        : undo()
                }, undo)
        }
    }
    setOneChange(action, change) {
        const { id } = this.data || {}

        if (id && change.id) {
            // making optimistic changes
            const revertedOrAcceptedIdx = this.changes.findIndex(c => c.id === change.id)
            const [revertedOrAcceptedChange] = this.changes.splice(revertedOrAcceptedIdx, 1)
            this.$rootScope.learningObjectChanged = !!this.changes.length

            const undo = () => {
                // the changes were too optimistic
                this.changes.splice(revertedOrAcceptedIdx, 0, revertedOrAcceptedChange)
                this.$rootScope.learningObjectChanged = true
            }

            this.serverCallService
                .makePost(`rest/admin/changed/${id}/${action}One/${change.id}`)
                .then(({ status, data }) => {
                    200 <= status && status < 300
                        ? this.setExpandableHeight() || this.setData(data)
                        : undo()
                }, undo)
        }
    }
    setData(data) {
        this.data = data || this.data
        this.isMaterial(data) ? this.storageService.setMaterial(data) :
        this.isPortfolio(data) && this.storageService.setPortfolio(data)
        this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
        this.$rootScope.$broadcast('tags:resetTags');
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$timeout',
    '$routeParams',
    '$translate',
    '$mdColors',
    'serverCallService',
    'storageService',
    'taxonService',
    'authenticatedUserService',
    'metadataService',
    'toastService'
]
component('dopErrorMessage', {
    bindings: {
        data: '<'
    },
    templateUrl: '/directives/errorMessage/errorMessage.html',
    controller
})
}
