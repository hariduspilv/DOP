package ee.hm.dop.rest.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/DevelopmentLoginResourceTest.java
=======
import ee.hm.dop.common.test.TestLayer;
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/login/DevelopmentLoginResourceTest.java
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import org.junit.Test;

public class DevelopmentLoginResourceTest extends ResourceIntegrationTestBase {

    public static final String NOT_EXISTING_USER = "123";

    @Test
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/DevelopmentLoginResourceTest.java
    public void login() {
        AuthenticatedUser authenticatedUser  = doGet(DEV_LOGIN + TestConstants.USER_MATI.idCode, AuthenticatedUser.class);
        assertNotNull(authenticatedUser.getToken());
        validateUser(authenticatedUser.getUser(), TestConstants.USER_MATI);
    }

    @Test
    public void loginWrongId() {
=======
    public void existing_user_can_use_dev_login() {
        AuthenticatedUser authenticatedUser  = doGet(DEV_LOGIN + USER_MATI.idCode, AuthenticatedUser.class);
        assertNotNull(authenticatedUser.getToken());
        validateUser(authenticatedUser.getUser(), USER_MATI, TestLayer.REST);
    }

    @Test
    public void nonexisting_user_can_not_login() {
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/login/DevelopmentLoginResourceTest.java
        Response response = doGet(DEV_LOGIN + NOT_EXISTING_USER);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void cant_log_in_without_idCode() {
        Response response = doGet(null);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

    }
}
