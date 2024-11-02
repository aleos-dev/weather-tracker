package com.aleos.servlet;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.exception.repository.DaoOperationException;
import com.aleos.exception.servlet.CoordinateParsingException;
import com.aleos.model.entity.Location;
import com.aleos.repository.UserRepository;
import com.aleos.service.UserService;
import com.aleos.service.WeatherApiClient;
import com.aleos.util.ReflectionUtil;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.aleos.util.DbUtil.setLocationForUser;
import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServletTest extends AbstractTestServlet {

    private static final UserRepository USER_REPOSITORY = TestContextInitializer.getBean(UserRepository.class);

    private static LocationServlet realServlet;
    private static LocationServlet spyServlet;

    private static final String LOCATION_NAME = "Kyiv - UA";
    private static final String LONGITUDE = "50.4500336";
    private static final String LATITUDE = "30.5241361";

    @BeforeAll
    static void beforeAll() {
        initializeDependencies();
        TestContextInitializer.getBean(Flyway.class).migrate();
    }

    @AfterAll
    static void afterAll() {
        TestContextInitializer.getBean(Flyway.class).clean();
    }

    @BeforeEach
    void setUp() {
        spyServlet = Mockito.spy(realServlet);
    }

    @Test
    void doGet_ShouldSearchForLocation_WhenLocationNameAttributeIsPresent() {
        configureSpyServlet(spyServlet);
        setLocationQueryParameter("Kiev");

        spyServlet.doGet(request, response);

        verifyLocationQueryParameterRetrieved();
        List<Location> locationsFound = captureFoundLocations();
        assertNonEmptyList(locationsFound, "Expected locations to be found based on the query.");
        verifyLocationsSetInRequest(locationsFound);
        verifyTemplateRendered("weather", spyServlet);
    }

    @Test
    void doGet_ShouldDoNothing_WhenLocationNameAttributeIsNull() {
        configureSpyServlet(spyServlet);
        setLocationQueryParameter("   ");

        spyServlet.doGet(request, response);

        verifyLocationQueryParameterRetrieved();
        verify(request, never()).setAttribute(eq("locationsByQuery"), Mockito.any());
        verifyTemplateRendered("weather", spyServlet);
    }

    @Test
    void doGet_ShouldDoNothing_WhenLocationNameAttributeIsBlank() {
        configureSpyServlet(spyServlet);

        spyServlet.doGet(request, response);

        verifyLocationQueryParameterRetrieved();
        verify(request, never()).setAttribute(eq("locationsByQuery"), Mockito.any());
        verifyTemplateRendered("weather", spyServlet);
    }

    @Test
    void doPost_ShouldSaveLocationForUser_WhenDataIsCorrect() throws IOException {
        String username = "Post_1";
        setupVerifiedUser(username, "testPassword", "123@localhost.com");
        mockRequestParameters(username, LOCATION_NAME, LONGITUDE, LATITUDE);

        spyServlet.doPost(request, response);

        verify(spyServlet).retrieveAuthenticationPrincipal(request);
        assertLocationSaved(username, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        verify(response).sendRedirect(anyString());
    }

    @ParameterizedTest
    @CsvSource({
            "'abc', 50.4500336",
            "'null', 50.4500336",
            "50.4500336, 'abc'",
            "50.4500336, 'null'"
    })
    void doPost_ShouldThrowCoordinateParsingException_WhenDataIsIncorrect(String longitude, String latitude) throws IOException {
        mockRequestParameters("any", "any", longitude, latitude);

        assertThrows(CoordinateParsingException.class, () -> spyServlet.doPost(request, response));
        verify(response, never()).sendRedirect(anyString());
    }


    @Test
    void doDelete_ShouldDeleteLocationForUser_WhenLocationExists() throws IOException {
        String username = "Delete_1";
        setupVerifiedUser(username, "testPassword", "delete_1@localhost.com");
        setLocationForUser(username, LOCATION_NAME, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        assertLocationSaved(username, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        mockRetrieveAuthenticationPrincipal(username);
        mockRequestCoordinates(LONGITUDE, LATITUDE);


        spyServlet.doDelete(request, response);

        verify(spyServlet).retrieveAuthenticationPrincipal(request);
        assertLocationNotPresentForUser(username, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        verify(response).sendRedirect(anyString());
    }

    @ParameterizedTest
    @CsvSource({
            "'abc', 50.4500336",
            "'null', 50.4500336",
            "50.4500336, 'abc'",
            "50.4500336, 'null'"
    })
    void doDelete_ShouldThrowCoordinateParsingException_WhenDataIsIncorrect(String longitude, String latitude) throws IOException {
        mockRequestParameters("any", "any", longitude, latitude);

        assertThrows(CoordinateParsingException.class, () -> spyServlet.doDelete(request, response));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void doPatch_ShouldRenameLocationForUser_WhenDataIsCorrect() {
        String username = "patch_1";
        String newLocationName = "Patch_1_location";
        setupVerifiedUser(username, "testPassword", "patch_1@localhost.com");
        setLocationForUser(username, LOCATION_NAME, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        assertLocationSaved(username, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        mockRequestParameters(username, newLocationName, LONGITUDE, LATITUDE);
        mockRetrieveAuthenticationPrincipal(username);

        spyServlet.doPatch(request, response);

        verify(spyServlet).retrieveAuthenticationPrincipal(request);
        verify(request).getParameter("locationName");
        assertLocationHasNewName(username, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE), newLocationName);
        verify(response).setStatus(SC_NO_CONTENT);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLocationNames")
    void doPatch_ShouldThrowDaoOperationException_WhenNewLocationNameIsNull(String username, String newLocationName) {
        setupVerifiedUser(username, "testPassword", username + "@localhost.com");
        setLocationForUser(username, LOCATION_NAME, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        assertLocationSaved(username, Double.parseDouble(LONGITUDE), Double.parseDouble(LATITUDE));
        mockRequestParameters(username, newLocationName, LONGITUDE, LATITUDE);
        mockRetrieveAuthenticationPrincipal(username);

        assertThrows(DaoOperationException.class, () -> spyServlet.doPatch(request, response));
    }

    private static void initializeDependencies() {
        realServlet = new LocationServlet();
        ReflectionUtil.setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
        ReflectionUtil.setFieldToObject(realServlet, "weatherClient", TestContextInitializer.getBean(WeatherApiClient.class));
        ReflectionUtil.setFieldToObject(realServlet, "userService", TestContextInitializer.getBean(UserService.class));
    }


    private static Stream<Arguments> provideInvalidLocationNames() {
        return Stream.of(
                Arguments.of("patch_2", null),    // Case where location name is null
                Arguments.of("patch_3", ""),      // Case where location name is empty
                Arguments.of("patch_4", "   ")    // Case where location name is only whitespace
        );
    }

    private void setLocationQueryParameter(String query) {
        doReturn(query).when(request).getParameter("query");
    }

    private void verifyLocationQueryParameterRetrieved() {
        verify(request).getParameter("query");
    }

    private List<Location> captureFoundLocations() {
        ArgumentCaptor<List<Location>> captor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("locationsByQuery"), captor.capture());
        return captor.getValue();
    }

    private void verifyLocationsSetInRequest(List<Location> locations) {
        verify(request).setAttribute("locationsByQuery", locations);
    }

    private void mockRequestParameters(String username, String locationName, String longitude, String latitude) {
        lenient().doReturn(username).when(spyServlet).retrieveAuthenticationPrincipal(request);
        lenient().doReturn(locationName).when(request).getParameter("locationName");
        lenient().doReturn(longitude).when(request).getParameter("lon");
        lenient().doReturn(latitude).when(request).getParameter("lat");
    }

    private void mockRequestCoordinates(String longitude, String latitude) {
        doReturn(longitude).when(request).getParameter("lon");
        doReturn(latitude).when(request).getParameter("lat");
    }

    private void assertLocationSaved(String username, double longitude, double latitude) {
        boolean isLocationSaved = USER_REPOSITORY.fetchUserLocationData(username).stream()
                .map(this::mapRowToLocation)
                .anyMatch(loc -> loc.getCoordinates().equals(new Location.Coordinates(longitude, latitude)));

        assertTrue(isLocationSaved, "Expected location to be saved for the user.");
    }

    private void assertLocationNotPresentForUser(String username, double longitude, double latitude) {
        boolean noActiveLocations = USER_REPOSITORY.fetchUserLocationData(username).stream()
                .map(this::mapRowToLocation)
                .noneMatch(loc -> loc.getCoordinates().equals(new Location.Coordinates(longitude, latitude)));

        assertTrue(noActiveLocations, "Expected no location with given coordinates.");
    }

    private void assertLocationHasNewName(String username, double longitude, double latitude, String newLocationName) {
        boolean isLocationHasNewName = USER_REPOSITORY.fetchUserLocationData(username).stream()
                .map(this::mapRowToLocation)
                .filter(loc -> loc.getCoordinates().equals(new Location.Coordinates(longitude, latitude)))
                .anyMatch(loc -> loc.getName().equals(newLocationName));

        assertTrue(isLocationHasNewName, "Expected location to be saved for the user.");
    }


    private Location mapRowToLocation(Object[] row) {
        var lon = (double) row[0];
        var lat = (double) row[1];
        var preferredName = (String) row[2];
        return new Location(preferredName, lon, lat);
    }

    private void mockRetrieveAuthenticationPrincipal(String username) {
        doReturn(username).when(spyServlet).retrieveAuthenticationPrincipal(request);
    }

}