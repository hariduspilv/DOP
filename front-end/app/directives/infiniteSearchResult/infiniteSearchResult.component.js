'use strict'

{
class controller extends Controller {
    $onChanges({ title, subtitle, filter, params, exactTitle, similarTitle }) {
        if (title && title.currentValue !== title.previousValue) this.setTitle()
        if (exactTitle && exactTitle.currentValue !== exactTitle.previousValue) this.setPhraseTitlesExact()
        if (similarTitle && similarTitle.currentValue !== similarTitle.previousValue) this.setPhraseTitlesSimilar()
        if (subtitle && subtitle.currentValue !== subtitle.previousValue) this.$scope.subtitle = subtitle.currentValue
        if (filter && filter.currentValue !== filter.previousValue) this.$scope.filter = filter.currentValue

        if (params && !params.isFirstChange() && this.params) {
            this.initialParams = Object.assign({}, this.params)
            this.search(true)
        }
    }
    $onInit() {
        if (!this.url) this.url = 'rest/search'

        this.initialParams = Object.assign({}, this.params)

        this.$scope.items = []
        this.$scope.sortOptions = []
        this.$scope.filterGroups = {}
        this.$scope.filterGroupsExact = {}
        this.$scope.searching = false
        this.createMultipleSortOptions(
            ['ADDED_DATE_DESC', 'added', 'desc'],
            ['ADDED_DATE_ASC', 'added', 'asc'],
        )
        this.createMultipleFilterGroups(
            ['GROUPS_TITLES', 'title'],
            ['GROUPS_TAGS', 'tag'],
            ['GROUPS_AUTHORS', 'author'],
            ['GROUPS_DESCRIPTIONS', 'description'],
            ['GROUPS_PUBLISHERS', 'publisher'],
            ['GROUPS_ALL', 'all'],
        )
        this.$scope.nextPage = () => this.$timeout(this.search.bind(this))
        this.search(true)
        this.$rootScope.$on('logout:success', this.search.bind(this))
    }
    createMultipleSortOptions(...options) {
        options.forEach((option) =>
            this.$scope.sortOptions.push(controller.createSortOption(option[0], option[1], option[2]))
        )
    }
    static createSortOption(optionKey, fieldValue, sortDirection) {
        return {
            option: optionKey,
            field: fieldValue,
            direction: sortDirection,
        }
    }
    createMultipleFilterGroups(...groups) {
        groups.forEach((group) => {
            this.createSingleFilterGroup(group[0], group[1])
        })
    }
    createSingleFilterGroup(groupName, groupId) {
        this.$scope.filterGroups[groupId] = controller.createFilterGroupModel(groupName)
        this.$scope.filterGroupsExact[groupId] = controller.createFilterGroupModel(groupName)
    }
    static createFilterGroupModel(groupName) {
        return {
            name: groupName,
            countMaterial: 0,
            countPortfolio: 0,
            isMaterialActive: false,
            isPortfolioActive: false
        }
    }
    setParams() {
        if (!this.params) this.params = {}
        this.searchCount = 0
        this.maxResults = this.params.maxResults || this.params.limit || 20
        this.expectedItemCount = this.maxResults
    }
    setTitle() {
        const t = (key) => this.$translate.instant(key)
        const translationsKeys = {
            noResults: 'SEARCH_RESULT_NO_RESULT',
            singleResultPart1: 'SEARCH_RESULT_1_RESULT_PART_1',
            singleResultPart2: 'SEARCH_RESULT_1_RESULT_PART_2',
            multipleResultsPart1: 'SEARCH_RESULT_PART_1',
            multipleResultsPart2: 'SEARCH_RESULT_PART_2',
        }
        this.$translate.onReady().then(() => this.$scope.title = this.buildTitle(
            t, this.title, this.totalResults, translationsKeys
        ))
    }
    buildTitle(t, title, totalResults, translationsKeys) {
        const singleResult = `${t(translationsKeys.singleResultPart1)} <strong>${totalResults}</strong> ${t(translationsKeys.singleResultPart2)}`
        const multipleResults = `${t(translationsKeys.multipleResultsPart1)} <strong>${totalResults}</strong> ${t(translationsKeys.multipleResultsPart2)}`
        return title ? t(title)
            : this.$scope.searching ? t('SEARCH_RESULTS')
                : !totalResults ? t(translationsKeys.noResults)
                    : totalResults === 1 ? singleResult
                        : totalResults > 1 ? multipleResults
                            : ''
    }
    setPhraseTitlesExact() {
        const t = (key) => this.$translate.instant(key)
        const translationsKeys = {
            noResults: 'SEARCH_RESULT_NO_RESULT_EXACT',
            singleResultPart1: 'SEARCH_RESULT_1_RESULT_PART_1_EXACT',
            singleResultPart2: 'SEARCH_RESULT_1_RESULT_PART_2_EXACT',
            multipleResultsPart1: 'SEARCH_RESULT_PART_1_EXACT',
            multipleResultsPart2: 'SEARCH_RESULT_PART_2_EXACT',
        }
        this.$translate.onReady().then(() =>
            this.$scope.exactTitle = this.buildTitle(
                t, this.exactTitle, this.totalPhraseResults['exact'], translationsKeys
            ))
    }
    setPhraseTitlesSimilar() {
        const t = (key) => this.$translate.instant(key)
        const translationsKeys = {
            noResults: 'SEARCH_RESULT_NO_RESULT_SIMILAR',
            singleResultPart1: 'SEARCH_RESULT_1_RESULT_PART_1_SIMILAR',
            singleResultPart2: 'SEARCH_RESULT_1_RESULT_PART_2_SIMILAR',
            multipleResultsPart1: 'SEARCH_RESULT_PART_1_SIMILAR',
            multipleResultsPart2: 'SEARCH_RESULT_PART_2_SIMILAR',
        }
        this.$translate.onReady().then(() =>
            this.$scope.similarTitle = this.buildTitle(
                t, this.similarTitle, this.totalPhraseResults['similar'], translationsKeys
            ))
    }
    setPhraseTitles() {
        this.setPhraseTitlesExact()
        this.setPhraseTitlesSimilar()
    }
    resetSort() {
        this.params.sort = this.initialParams.sort
        this.params.sortDirection = this.initialParams.sortDirection
        this.search(true)
    }
    sort(field, direction) {
        this.params.sort = field
        this.params.sortDirection = direction
        this.search(true)
    }
    allResultsLoaded() {
        return (this.$scope.items || []).length >= this.totalResults || this.$scope.start >= this.totalResults
    }
    search(isNewSearch) {
        if (isNewSearch) this.setParams()
        if (this.$scope.searching || !isNewSearch && this.allResultsLoaded()) return

        this.$scope.searching = true
        this.$scope.start = this.searchCount * this.maxResults

        this.params.limit = this.maxResults
        this.params.maxResults = this.maxResults
        this.params.start = this.$scope.start

        this.serverCallService.makeGet(
            this.url,
            this.params,
            this.params.isGrouped ? this.groupedSearchSuccess.bind(this) : this.searchSuccess.bind(this),
            this.searchFail.bind(this),
            {},
            false,
            !!this.cache
        )
    }
    searchSuccess(data) {
        if (!data || !data.items) return this.searchFail()
        if (data.start === 0) this.$scope.items.splice(0, this.$scope.items.length)
        this.$scope.showFilterGroups = 'noGrouping'

        ;[].push.apply(this.$scope.items, data.items)

        this.totalResults = data.totalResults
        this.searchCount++
        this.$scope.searching = false

        this.setTitle()
        this.searchMoreIfNecessary()
    }
    groupedSearchSuccess(data) {
        if (!data || !data.groups) return this.searchFail()
        if (data.start === 0) {
            this.$scope.items.splice(0, this.$scope.items.length)
            controller.disableAllGroupsForFilter(this.$scope.filterGroups)
            controller.disableAllGroupsForFilter(this.$scope.filterGroupsExact)
        }
        this.$scope.showFilterGroups = this.params.isGrouped && data.totalResults !== 0
            ? data.groups.hasOwnProperty('exact')
                ? 'phraseGrouping' : 'grouping'
            : 'noGrouping'

        this.totalPhraseResults = {}
        let foundItems = this.extractItemsFromGroups(data.groups)
        ;[].push.apply(this.$scope.items, foundItems)

        this.totalResults = data.totalResults
        this.$scope.filterGroups['all'].countMaterial = this.totalResults

        this.searchCount++
        this.$scope.searching = false

        this.$scope.showFilterGroups === 'phraseGrouping' ? this.setPhraseTitles() : this.setTitle()
        this.searchMoreIfNecessary()
    }
    extractItemsFromGroups(groups, groupType, searchType) {
        let allItems = []
        Object.entries(groups).forEach(([name, content]) => {
            if (name === 'material' || name === 'portfolio') groupType = name
            if (name === 'exact' || name === 'similar') {
                searchType = name
                this.totalPhraseResults[name] = content.totalResults
            }
            if (content.hasOwnProperty('items')) {
                this.setGroupItemsCount(groupType, searchType, name, content)
                this.updateItemAttributes(content, name, searchType, allItems)
            }
            else allItems = allItems.concat(this.extractItemsFromGroups(content, groupType, searchType))
        })
        return allItems
    }
    updateItemAttributes(content, name, searchType, allItems) {
        content.items.forEach((item) => {
            item['foundFrom'] = name
            if (searchType) item['searchType'] = searchType
            allItems.push(item)
        })
    }
    setGroupItemsCount(groupType, searchType, name, content) {
        if (groupType === 'material') {
            if (searchType === 'exact')
                this.$scope.filterGroupsExact[name].countMaterial = content.totalResults
            else this.$scope.filterGroups[name].countMaterial = content.totalResults
        }
        if (groupType === 'portfolio') {
            if (searchType === 'exact')
                this.$scope.filterGroupsExact[name].countPortfolio = content.totalResults
            this.$scope.filterGroups[name].countPortfolio = content.totalResults
        }
    }
    searchFail() {
        this.$scope.searching = false
        this.setTitle()
    }
    searchMoreIfNecessary() {
        this.$scope.items.length < this.expectedItemCount && !this.allResultsLoaded()
            ? this.search()
            : this.expectedItemCount += this.maxResults
    }
    selectMaterialGroup(groupId, isExact) {
        let groups = isExact ? this.$scope.filterGroupsExact : this.$scope.filterGroups
        const isAllActive = groups['all'].isMaterialActive
        if (groupId === 'all' && !isAllActive) controller.disableAllGroupsForFilter(groups)
        if (groupId !== 'all' && isAllActive) controller.disableAllGroupsForFilter(groups)
        groups[groupId].isMaterialActive = !groups[groupId].isMaterialActive
    }
    selectPortfolioGroup(groupId, isExact) {
        let groups = isExact ? this.$scope.filterGroupsExact : this.$scope.filterGroups
        const isAllActive = groups['all'].isMaterialActive
        if (groupId !== 'all' && isAllActive) controller.disableAllGroupsForFilter(groups)
        groups[groupId].isPortfolioActive = !groups[groupId].isPortfolioActive
    }
    static disableAllGroupsForFilter(filter) {
        Object.entries(filter).forEach(([name, content]) => {
            content.isMaterialActive = false
            content.isPortfolioActive = false
        })
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$element',
    '$timeout',
    '$translate',
    'serverCallService'
]
component('dopInfiniteSearchResult', {
    bindings: {
        params: '<',
        url: '<?',
        title: '<',
        subtitle: '<',
        filter: '<',
        cache: '<?',
        isPreferred: '<',
        exactTitle: '<',
        similarTitle: '<',
    },
    templateUrl: 'directives/infiniteSearchResult/infiniteSearchResult.html',
    controller
})
}
