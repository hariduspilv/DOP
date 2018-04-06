'use strict'

{
class controller {
    setAuthenticatedUser(authenticatedUser) {
        localStorage.setItem('authenticatedUser', JSON.stringify(authenticatedUser))
    }
    getAuthenticatedUser() {
        return JSON.parse(localStorage.getItem('authenticatedUser'))
    }
    removeAuthenticatedUser() {
        localStorage.removeItem('authenticatedUser')
    }
    isAuthenticated() {
        return !!this.getAuthenticatedUser()
    }
    isAdmin() {
        return this.hasRole('ADMIN');
    }
    isPublisher() {
        const user = this.getUser()
        return user && isDefined(user.publisher)
    }
    isRestricted() {
        return this.hasRole('RESTRICTED');
    }
    isModerator() {
        return this.hasRole('MODERATOR');
    }
    isUser(){
        return this.hasRole('USER');
    }
    isUserOrModerator(){
        return this.isUser() || this.isModerator()
    }
    isUserPlus(){
        return this.isUser() || this.isModerator() || this.isAdmin()
    }
    isModeratorOrAdmin() {
        return this.isModerator() || this.isAdmin()
    }
    isModeratorOrAdminOrCreator(learningObject) {
        return this.isModerator() || this.isAdmin() || learningObject && this.getUser().id === learningObject.creator.id
    }
    getUser() {
        const authenticatedUser = this.getAuthenticatedUser()
        return authenticatedUser
            ? authenticatedUser.user
            : null
    }
    getToken() {
        const authenticatedUser = this.getAuthenticatedUser()
        return authenticatedUser
            ? authenticatedUser.token
            : null
    }
    hasRole(role) {
        const user = this.getUser()
        return user && user.role === role
    }
}

factory('authenticatedUserService', controller)
}
