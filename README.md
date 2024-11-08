# Weather Tracker

| 🌦️ **Welcome to the Weather Tracker project!** Through this project, I’ve learned essential concepts such as secure session management, request filtering, and server-side rendering using Thymeleaf and Bootstrap. <br/><br>Weather Tracker lets users view, save, and organize weather information for various locations. It features session and cookie management with Redis, custom security filtering to handle authentication and authorization, and an intuitive user interface powered by Thymeleaf. <br/><br>Building this app has greatly enhanced my understanding of web security and architecture, and I look forward to leveraging this knowledge in future projects. | ![](/.github/img/weather-git-main.webp) |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|

### 🚀 Live Demo

`Visit the live version of our project here:` [Weather Tracker](https://weather-tracker.ale-os.com/api/v1/weather).

### 📚 Learn More

`If you are interested in learning more about dev stuff, you may find my article helpful, available here:` [My blog](https://ale-os.com/)

## 📖 Index

💡 [My Goals (Beyond the Technical Task)](#main-goals)

💡 [Technologies and Frameworks](#technologies-and-frameworks)

📖 [Reflection on the project](#reflection-on-the-project)

- [SecurityFilterChain Imitation](#securityfilterchain-imitation)
- [Application Context Management](#application-context-management)
- [Integration Tests](#integration-tests)
- [Session Management](#session-management)
- [Servlets Structure](#servlets-structure)

📖 [The Pattern used](#the-patterns-used)

🛠 [Deployment](#deployment)

📋 [Project Requirements](https://zhukovsd.github.io/java-backend-learning-course/projects/weather-viewer/)

💬 [Share your feedback](#share-your-feedback)

🙌 [Acknowledgments](#acknowledgments)

## Main Goals

- Gain hands-on experience with `HttpClient` for making HTTP requests.
- Explore Bootstrap and Thymeleaf to replace traditional CSS and JSP for UI design.
- Implement a custom `SecurityFilterChain` to pre-handle requests, manage authentication data during the request
  lifecycle, and control access to resources.
- Develop a deeper understanding of session and cookie management by implementing a custom `Session` and
  `SessionManager`.
- Experiment with Redis as a session store, managing serialization and deserialization of session data.
- Build foundational knowledge of integration testing.

--- 

## Technologies and Frameworks

<table align="center">
  <tr>
      <td align="middle">
          <img src="https://user-images.githubusercontent.com/25181517/117201156-9a724800-adec-11eb-9a9d-3cd0f67da4bc.png" width="50"><br>
            Java
    </td>
    <td align="middle">
        <img src="https://user-images.githubusercontent.com/25181517/117207330-263ba280-adf4-11eb-9b97-0ac5b40bc3be.png" width="50"><br>
          Docker
    </td>
    <td align="middle">
        <img src="https://avatars.githubusercontent.com/u/13393021?s=200&v=4" 
width="50"><br>
          Testcontainer
    </td>
    <td align="middle">
        <img src="https://user-images.githubusercontent.com/25181517/183868728-b2e11072-00a5-47e2-8a4e-4ebbb2b8c554.png" width="50"><br>
          CI/CD
    </td>
    <td align="middle">
          <img src="https://github.com/onemarc/tech-icons/blob/main/icons/postgressql-light.svg" width="50"><br>
            Postgresql
     </td>
    <td align="middle">
          <img src=" https://www.flaticon.com/free-icon/integration_7001490" width="50"><br>
            IT
     </td>
    <td align="middle">
        <img src="https://github.com/onemarc/tech-icons/blob/main/icons/redis-light.svg" width="50"><br>
          Redis
    </td>
    <td align="middle">
       <img src="https://github.com/onemarc/tech-icons/blob/main/icons/bootstrap-light.svg" width="50"><br>
          Bootstrap
    </td>
    <td align="middle">
          <img src="https://github.com/onemarc/tech-icons/blob/main/icons/apachemaven.svg" width="50"><br>
            Maven
    </td>
    <td align="middle">
          <img src="https://github.com/onemarc/tech-icons/blob/main/icons%232/flyway.svg" width="50"><br>
            Flyway
    </td>
    <td align="middle">
        <img src="https://github.com/onemarc/tech-icons/blob/main/icons/hibernate-dark.svg" width="50"><br>
          Hibernate
    </td>
    <td align="middle">
       <img src="https://github.com/onemarc/tech-icons/blob/main/icons/apachetomcat.svg" width="50"><br>
          Tomcat
    </td>
        <td align="middle">
       <img src="https://user-images.githubusercontent.com/25181517/117533873-484d4480-afef-11eb-9fad-67c8605e3592.png" width="50"><br>
          JUnit
    </td>
    <td align="middle">
        <img src="https://github.com/onemarc/tech-icons/blob/main/icons/html.svg" width="50"><br>
          HTML
    </td>
    <td align="middle">
        <img src="https://github.com/onemarc/tech-icons/blob/main/icons/css.svg" width="50"><br>
          CSS
    </td>
  </tr>
</table>

- ☕ **Java**: The core language used for backend application logic and business services.

- 🐳 **Docker**: Containerization platform to package and deploy application components and dependencies consistently.

- 🧪 **Testcontainers**: A Java library that provides lightweight, disposable instances of databases and services for
  integration testing.

- 🔄 **CI/CD**: Continuous Integration and Continuous Deployment pipelines to automate building, testing, and deploying
  the application.

- 🐘 **Postgresql**: A robust, open-source relational database for storing and querying application data.

- ⚡ **Redis**: An in-memory data structure store used for caching and session management, optimizing data retrieval
  speed.

- 🎨 **Bootstrap**: A front-end framework for creating responsive, mobile-first UI designs with pre-built CSS components.

- 📦 **Maven**: Dependency management and build tool that automates compilation, packaging, and deployment of Java
  applications.

- 🦋 **Flyway**: Database migration tool for version control and migrations, ensuring consistent database schema
  management.

- 🖇 **Hibernate**: An ORM framework that maps Java objects to database tables, enabling efficient database access with
  minimal SQL.

- 🐱 **Tomcat**: Java servlet container for deploying and hosting Java web applications.

- ✅ **JUnit**: Testing framework for unit testing Java code, enabling automated test execution.

- 🌐 **HTML** & 🎨 **CSS**: Core web technologies for structuring and styling the application's front-end interface.

- 🍃 **Thymeleaf**: A Java templating engine used for server-side rendering of HTML pages, allowing dynamic content
  integration into views.

---

## Reflection on the project

---

### SecurityFilterChain Imitation

The custom security filter chain is composed of several filters, each responsible for specific security-related tasks.
This chain is initialized by the `SecurityInitializer` class, which constructs and places the filter chain in the
application context.

#### Core Components

- **`DelegatingFilterProxy`**: Registers `defaultSecurityFilterChain` as a delegate object to preprocess incoming
  requests.

- **`DefaultSecurityFilterChain`**: Implements the filter chain pattern, containing a set of filters responsible for web
  security.

**Filters included in the chain:**

- `CharacterEncodingFilter`: Sets the request and response encoding.
- `SecurityContextHolderFilter`: Manages the security context, handling the current user’s authentication information.
- `AuthenticationFilter`: Handles user authentication by verifying credentials and setting the `Authentication` object
  in the `SecurityContext`.
- `AnonymousAuthenticationFilter`: Creates an anonymous user if there’s no existing `Authentication` object.
- `ExceptionTranslationFilter`: Translates authentication or authorization exceptions into HTTP responses.
- `AuthorizationFilter`: Works with `AuthorizationManager` to grant or deny access to requested resources.

---

#### Presented Filters

1. **SecurityContextHolderFilter**: Initializes a `SecurityContext` that holds information about the current user, with
   deferred loading to improve performance.

2. **AuthenticationFilter**: Manages the authentication logic, creating and setting an `Authentication` object in the
   `SecurityContext`.

3. **AnonymousAuthenticationFilter**: Sets up an anonymous user if the `SecurityContext` does not hold an
   `Authentication` object, enabling access for unauthenticated users.

4. **AuthorizationFilter**: Works with the `AuthorizationManager` to grant or deny user rights to the requested resource
   based on user roles.

---

#### Custom Security Chain Summary

This implementation mimics the Spring Security filter chain using specific custom filters and configurations. The
challenges and experience gained from this design offer valuable insights into security mechanisms and modular filter
architecture.

---

### Application Context Management

The application context management is implemented through a custom **Service Locator pattern** using the `BeanFactory`.
This approach digests configuration from the `ApplicationContextConfiguration` class, which specifies the components (
beans) used across the application, and dynamically creates and injects dependencies.

#### Key Components

- **`BeanFactory`**: Acts as the core of the Service Locator, responsible for registering and providing instances of
  beans as needed. It uses reflection to:
    - Identify methods annotated with `@Bean` in `ApplicationContextConfiguration`.
    - Instantiate beans based on their method definitions.
    - Resolve and inject dependencies by identifying parameters of each method, ensuring each bean receives its required
      dependencies.

- **`ApplicationContextInitializer`**: A `ServletContextListener` that initializes the `BeanFactory` during application
  startup and registers it in the servlet context. This setup makes the `BeanFactory` available to other components in
  the application.

    - **`contextInitialized`**: Invokes `injectFactoryBean()` to create the `BeanFactory` with
      `ApplicationContextConfiguration` and store it in the servlet context.
    - **`contextDestroyed`**: Cleans up resources, such as the `EntityManagerFactory`, and unregisters JDBC drivers upon
      application shutdown.

#### Bean Definitions and Dependency Injection

The `ApplicationContextConfiguration` class defines various beans required across the application, including:

- **Database-Related Beans**:
    - `EntityManagerFactory`: Initializes the JPA entity manager for database access.
    - `Flyway`: Configures database migration using environment variables for database credentials.
    - `JedisPool`: Configures Redis connection pooling for efficient session management.

- **Service Beans**: Services such as `UserService`, `AuthenticationService`, and `WeatherApiClient` are registered
  here, with each receiving necessary dependencies (e.g., `UserRepository`, `PasswordEncoder`, and `HttpClient`).

- **Security Beans**:
    - `PasswordEncoder` (using `BCryptPasswordEncoder`) for secure password hashing.
    - `SecurityContextRepository` to manage user security contexts in HTTP sessions.
    - `AuthorizationManager` for role-based access control.

- **Utility Beans**: Additional utility beans, such as `ObjectMapper` for JSON parsing and `ModelMapper` for
  object-to-object mapping, are also registered here.

#### `TemplateEngineInitializer`

The `TemplateEngineInitializer` is a `ServletContextListener` responsible for configuring the Thymeleaf template engine.
It:

- Sets up the `TemplateEngine` with HTML mode, UTF-8 encoding, and cache settings.
- Uses a `WebApplicationTemplateResolver` to load templates from a specified path, set through application properties.
- Stores the configured `TemplateEngine` in the servlet context for use in rendering dynamic HTML views.

#### `SecurityFilterInitializer`

The `SecurityFilterInitializer` configures the custom security filter chain by retrieving the `ServiceLocator` from the
servlet context and invoking the `SecurityInitializer.initSecurityContext()` method to set up the security context.

----

### Integration Tests

The integration test suite focuses on verifying key functionality across the application's services and servlet
components, ensuring they work as expected in a realistic environment with database and service dependencies. The
tests simulate real-world scenarios, covering database transactions and remote requests.

---

#### Key Utilities for Integration Testing

- **`ReflectionUtil`**: This utility class leverages reflection to set private fields in objects, allowing tests to
  inject dependencies and mock objects where necessary.

- **`DbUtil`**: Provides helper methods for common database-related tasks during tests.

---

#### Core Components in the Integration Tests

- **`TestContextInitializer`**: This class initializes the application context specifically for testing, using a
  Testcontainers-managed PostgreSQL instance to mimic the production database environment. It sets up the database,
  configures the `ServiceLocator` with beans from `ApplicationContextConfiguration`, and injects environment properties
  as needed.
    - **Lifecycle Management**: The `testPlanExecutionFinished` method ensures clean shutdown of database resources and
      containerized services after tests complete.

---

#### Servlet Tests

The servlet tests cover various application entry points for authentication, user management, and location services.
Each servlet test:

- Mocks request and response objects using **Mockito**.
- Spies on the target servlet to control behavior and verify interactions.

##### Examples of Servlet Tests

1. **`WelcomeServletTest`**:
    - Verifies redirection logic for authenticated and unauthenticated users.
    - Uses `ReflectionUtil` to inject the `UserService` and other dependencies.

2. **`WeatherServletTest`**:
    - Tests retrieval and rendering of weather data for user locations.
    - Uses `ArgumentCaptor` to capture and assert weather data set in the request attributes.

3. **`LocationServletTest`**:
    - Tests location addition, deletion, and update operations for users.
    - Includes parameterized tests for invalid data, ensuring robust error handling and coverage.

4. **`AuthenticationFilterTest`**:
    - Mocks the `AuthenticationService` to simulate various authentication scenarios.
    - Tests both successful authentication and error handling, verifying request forwarding for failed logins.

---

#### Test Execution and Database Management

- **Database Migrations**: Each test class runs database migrations before tests and cleans up afterward using Flyway.
  This setup ensures the database is in a known state for every test.
- **Testcontainers**: The PostgreSQL container provided by Testcontainers runs as an isolated database instance,
  allowing tests to perform real SQL operations without affecting the production database.

---

### Session Management

The session management system is designed to securely handle user sessions across requests. It combines an in-memory
session with Redis-backed storage for persistence, enabling efficient management of session data even in distributed
environments.

#### Components and Key Classes

- **`CustomHttpSessionImpl`**: This is the core session object, implementing `CustomHttpSession` to provide
  session-specific attributes, authentication, and lifecycle methods. It supports:
    - **Session Attributes**: A `ConcurrentMap` stores session attributes, allowing data to be added, retrieved, or
      removed within the session.
    - **Authentication Management**: Sessions can hold an `Authentication` object for tracking the logged-in user,
      managed through methods such as `getAuthentication`, `setAuthentication`, and `isAuthenticated`.
    - **Session Invalidation**: The `invalidate()` method clears all session attributes, effectively logging out the
      user.

- **`SessionManager`**: Manages session creation, validation, storage, and retrieval. It leverages Redis for session
  persistence and manages cookies to track sessions across requests. Key functions include:
    - **Session Creation and Persistence**: `createSession` generates a new session, assigns it a UUID, and stores it in
      Redis with a specified timeout.
    - **Session Retrieval and Validation**: `getValidSession` retrieves a session using the session ID from the cookie,
      validates its timeout, and refreshes the last accessed time if valid.
    - **Session Serialization/Deserialization**: Uses `ObjectMapper` to convert sessions to JSON for storage in Redis
      and back into session objects upon retrieval. Custom serialization allows persisting complex attributes, such as
      authentication tokens.
    - **Cookie Management**: Creates and invalidates cookies to manage session IDs between requests, ensuring sessions
      are secure with `HttpOnly` attributes and controlled expiration.

#### Key Functionalities

1. **Session Persistence in Redis**:
    - Redis is used to store session data, making it accessible across multiple server instances. `saveSessionToRedis`
      stores session data with an expiration time, while `getSession` retrieves it for session continuity.

2. **Authentication and Authorization**:
    - Sessions hold `Authentication` objects, which allow the system to verify user identity. The session’s
      `isAuthenticated` method checks if an authentication object is set and authenticated.
    - The session manager handles user login and logout by setting or removing the authentication data in sessions.

3. **Session Timeout**:
    - Session timeout is managed by checking the last accessed time of each session. Sessions older than the configured
      timeout are considered invalid and automatically removed, enhancing security by limiting session duration.

4. **Secure Cookie Handling**:
    - Cookies are created and updated to track sessions. Cookies are flagged as `HttpOnly` to protect against JavaScript
      access and set with a defined path and expiration, maintaining control over session lifecycles.

#### Benefits

- **Stateless Scalability**: The Redis-backed session storage allows sessions to persist independently of application
  server restarts, ideal for scaling across distributed systems.
- **Enhanced Security**: The session system supports secure, time-limited sessions with Redis-backed persistence and
  `HttpOnly` cookies, reducing exposure to session hijacking.

---

### Servlets Structure

#### Core Servlet Classes

- **AbstractThymeleafServlet**: The base servlet class that extends `HttpServlet` and integrates Thymeleaf for
  server-side rendering. It provides:
    - **Template Processing**: `processTemplate` method loads and processes Thymeleaf templates, sending the output to
      the response.
    - **DTO Parsing**: `parseSimpleDto` method parses request parameters into data transfer objects (DTOs) with
      validation support.
    - **Session and Authentication Management**: Handles session retrieval and redirects based on user authentication
      status.
    - **Error Handling**: Renders custom error pages for different exception scenarios.

- **WelcomeServlet**: Handles requests to the welcome page. If the user is authenticated, it redirects to the weather
  page; otherwise, it displays the welcome template.

- **WeatherServlet**: Retrieves weather data for authenticated users and renders it on a Thymeleaf template. It uses
  `UserService` to fetch user-specific weather data and sets it as a request attribute.

- **LocationServlet**: Manages user-specific location data, including adding, updating, and deleting locations. It
  supports:
    - **Method Overrides**: Uses `_method` request parameter to determine if the request should be handled as `PATCH` or
      `DELETE`.
    - **Location Management**: Communicates with `WeatherApiClient` to search and update location data, with user
      locations managed through `UserService`.

- **Authorization Servlets**: Handle user authentication flows, including login, signup, verification, and logout. These
  servlets extend `AbstractAuthServlet` for common functionality, such as checking user authentication status and
  managing redirects.
    - **SignInServlet**: Displays the login page or redirects authenticated users.
    - **SignUpServlet**: Displays the signup page and handles user registration. It also sends a verification email upon
      successful registration.
    - **VerifyServlet**: Verifies a user’s account based on a token.
    - **SignOutServlet**: Manages logout by invalidating the user session and redirecting to the welcome page.

#### Supporting Filters and Session Management

- **SessionFilter**: Checks each incoming request for a valid session. If no session is found, it creates a new one, and
  saves the session to Redis upon completion.
- **DelegatingFilterProxy**: Acts as a proxy to delegate security filters. It checks if a request matches configured
  security criteria and, if so, forwards it to the security filter chain.
- **GlobalExceptionHandler**: Catches and handles various application-specific exceptions, mapping each to an
  appropriate HTTP response or error page.

#### Thymeleaf Integration

Servlets utilize Thymeleaf templates for dynamic content rendering, managed through `AbstractThymeleafServlet`. The
template engine is configured centrally, allowing shared settings across templates.

**Thymeleaf** is a modern server-side Java templating engine designed to blend HTML templates with dynamic data in a
readable and maintainable way. Unlike JSP, which embeds Java code directly within HTML, Thymeleaf allows you to define
views using a natural templating approach. This means that Thymeleaf templates are valid HTML files, even before
rendering, making them easier to design and visualize.

Thymeleaf enables dynamic content insertion through its powerful expression language, which avoids scriptlets entirely.
Instead, it uses simple, intuitive attributes (like `th:text`, `th:if`, and `th:each`) to control how data and logic
appear within the HTML. This attribute-based approach keeps logic within the template but outside of the Java code,
allowing for a clean separation between the view layer and the application’s business logic.

When Thymeleaf processes a template, it evaluates these attributes on the server-side, injecting or modifying HTML based
on dynamic data from the application. As a result, Thymeleaf combines server-side rendering with a rich set of features:

- **Conditionals**: Use attributes like `th:if` and `th:unless` for conditional rendering.
- **Loops**: The `th:each` attribute supports iteration over collections for displaying lists or tables.
- **Text Manipulation**: Attributes like `th:text` and `th:utext` replace specific HTML elements with dynamic text or
  raw HTML.
- **URL Management**: The `th:href` and `th:src` attributes help construct URLs dynamically based on application data.

Thymeleaf templates are processed on the server, producing fully rendered HTML that can then be sent to the client. This
architecture keeps Thymeleaf highly compatible with MVC design patterns and makes it ideal for web applications that
require a clear separation of concerns between the presentation and business logic layers.

---

### Hyper-Text-Markup-Language

HTML (HyperText Markup Language) is the standard markup language used to create and structure content on the web. It
forms the backbone of any web page, defining the skeletal layout and assembly of various page elements. HTML uses a
series of elements to encapsulate different types of content, ensuring web browsers know how to display each element
correctly.

Elements are defined by tags. These elements can be nested, allowing for complex web page structures. Attributes within
the tags provide additional settings or properties for the elements, such as setting a hyperlink’s destination with the
href attribute in an <a/> tag.

HTML documents are essentially a hierarchy of elements, forming what is known as the DOM (Document Object Model), which
scripts like JavaScript can manipulate to dynamically change the displayed content. This makes HTML not just a static
skeleton but a dynamic foundation that interacts with user actions and scripting languages to create the rich,
interactive web experiences familiar today.

By defining the content structure, HTML lays the groundwork for CSS and JavaScript, which respectively style and add
interactivity to the web content, demonstrating HTML’s pivotal role in web development.

---

### Cascading Style Sheets

While HTML forms the hierarchical structure of web elements, CSS acts as their stylist, enabling the separation of
presentation from content. This separation enhances accessibility and flexibility, allowing for detailed control over
the layout, colors, and fonts without altering the HTML.

CSS operates by applying specific rules to targeted elements, reminiscent of inheritance in object-oriented
programming (OOP), where properties are dynamically determined based on a set of cascading rules. These rules dictate
how elements are styled and are applied based on criteria such as specificity, importance, and the source order of the
CSS declarations.

Mastering CSS involves understanding its complexity and the nuances of how styles are applied, which can be a
significant investment of time. My beginner attempting to use CSS cold only grasp the basics.

--- 

## The Patterns used

### 1. **Singleton Pattern**

- **Where**: `SingletonSupplier<T>`, `ServiceLocator` (in `BeanFactory`)
- **Usage**: Ensures that certain resources (e.g., `ServiceLocator` and services) are created only once and then reused
  across the application, conserving resources and maintaining consistent behavior.

### 2. **Factory Pattern**

- **Where**: `BeanFactory` in `ServiceLocator`
- **Usage**: Manages the lifecycle of beans (service instances) through reflection in `ApplicationContextConfiguration`.
  This supports dependency injection and centralizes service creation.

### 3. **Service Locator Pattern**

- **Where**: `ServiceLocator` interface and `BeanFactory` implementation
- **Usage**: Acts as a centralized registry for services (`UserService`, `WeatherApiClient`, etc.), allowing classes to
  retrieve services without needing direct knowledge of their creation, reducing coupling.

### 4. **Dependency Injection (DI) Pattern**

- **Where**: Bean initialization in `ApplicationContextConfiguration` and injection via `ServiceLocator`
- **Usage**: Dependencies (e.g., `UserService`, `EmailService`, `WeatherApiClient`) are injected rather than being
  instantiated directly, improving modularity and testability.

### 5. **Chain of Responsibility Pattern**

- **Where**: `SecurityFilterChain` and other filters (`DelegatingFilterProxy`, `AuthorizationFilter`, etc.)
- **Usage**: Processes requests through a chain of filters, each responsible for a specific security aspect. Requests
  can pass through or stop at each filter based on conditions, providing modular and flexible security handling.

### 6. **Template Method Pattern**

- **Where**: `AbstractThymeleafServlet`, `AbstractAuthServlet`
- **Usage**: Abstract classes define templates for servlet request handling and DTO parsing. Subclasses such as
  `WelcomeServlet` and `WeatherServlet` implement specific actions but follow the defined template structure, ensuring
  consistency across servlets.

### 7. **Decorator Pattern (Proxy Variation)**

- **Where**: `DelegatingFilterProxy`
- **Usage**: Acts as a wrapper to enhance security handling, conditionally applying security filters to requests
  matching specific criteria without altering the original behavior.

### 8. **Data Access Object (DAO) Pattern**

- **Where**: Classes like `UserRepository`, `VerificationTokenDao`
- **Usage**: Encapsulates data access operations, isolating persistence logic from business logic, which simplifies
  database access and improves testability.

### 9. **Adapter Pattern**

- **Where**: `Redis` used in `SessionManager`
- **Usage**: Adapts Redis as a session store by creating a layer that bridges the differences between Redis’s API and
  the application’s session management requirements.

### 10. **Observer Pattern**

- **Where**: `ServletContextListener` classes such as `TemplateEngineInitializer`, `ApplicationContextInitializer`
- **Usage**: `ServletContextListener` classes observe application context lifecycle events (initialization and
  destruction) to trigger setup tasks like initializing the template engine or database connections when the application
  starts.

### 11. **Proxy Pattern**

- **Where**: `SecurityFilterChain` delegation via `DelegatingFilterProxy`
- **Usage**: `DelegatingFilterProxy` acts as a stand-in for the security filter chain, controlling access and adding
  logging and validation, effectively securing and managing requests in a proxy structure.

---

## Deployment

### Overview

The deployment process for the Weather Tracker application is automated using GitHub Actions and follows a CI/CD
pipeline. The process ensures that the application is built, tested, and deployed.

The pipeline consists of two main stages:

1. Release Build Process
2. Deployment to Remote Server

Each of these stages is executed through GitHub Actions workflows configured in release.yml and deploy.yml.

#### Release Build Process

The release process is triggered when a tag is pushed to the repository.

- Trigger: The process starts when a Git tag is pushed (on: push: tags: '*').
- Build and Packaging:
    - The source code is checked out from the repository.
    - The JDK 21 (Eclipse Temurin) environment is set up.
    - The Maven build tool is used to compile and package the application into a .war file.
    - After the build, the .war file is copied to a designated directory.
    - This .war file is uploaded as a build artifact to GitHub for further processing.

- Release:
    - Once the artifact is uploaded, the second stage of the job downloads the .war file.
    - The ncipollo/release-action GitHub action is used to publish a release in the GitHub repository. The artifact is
      attached to the release, and this release is accessible for deployment purposes.

#### Deployment to Remote Server

The deployment is handled through the deploy.yml file, which is triggered when the release process is completed (on:
workflow_run).

- SSH into Remote Server: The workflow uses appleboy/ssh-action to remotely connect to the server where the application
  is hosted.
- Tomcat Restart: The running Tomcat server is stopped using the shutdown.sh script.
- Backup the Current WAR File: If an existing WAR file is found, it is backed up by renaming it (e.g.,
  *.war.bak).
- Download Latest Release: The workflow dynamically retrieves the latest release from the GitHub repository using the
  GitHub API. The downloaded WAR file is placed in the Tomcat webapps directory.
- Set File Permissions: The file permissions are adjusted so that Tomcat can read and execute the new WAR file.
- Start Tomcat: The Tomcat server is restarted using the startup.sh script to deploy the new version of the application.

#### Environment Configuration

1. Front-End Server (Apache2):
   The application is hosted on a remote server where Apache2 acts as the front-end web server. Apache handles all
   incoming HTTP (port 80) and HTTPS (port 443) traffic and serves as a proxy to the Tomcat server. Apache is
   responsible for managing SSL certificates and encrypting traffic.

2. Reverse Proxy to Tomcat:
   Apache2 is configured to proxy all incoming requests for the subdomain (weather-tracker.ale-os.com) to a Tomcat
   server running in the background on the same machine. Apache forwards the requests to Tomcat, which is bound to
   localhost. Tomcat handles the application logic and serves the content back to Apache, which then returns the
   response to the client.

3. Tomcat Configuration:
   Tomcat runs as a background service on the remote server and is configured to serve the application via localhost.
   The WAR file is deployed in Tomcat's webapps directory, and Apache2 forwards requests to Tomcat using proxy
   directives. Tomcat does not handle SSL directly; instead, Apache takes care of encrypting the communication and
   proxies the traffic securely to Tomcat.

4. SSL Configuration:
   SSL certificates are managed by Apache2 using Let's Encrypt. All HTTPS traffic is terminated at Apache, and the
   requests are then proxied to Tomcat over unencrypted HTTP.

#### Secrets Management

Several secrets are used within the CI/CD pipeline to ensure security:

- *SSH Keys*: The deployment process uses SSH keys (secrets.SSH_AWS) to connect securely to the remote server.
- *GitHub Token*: The release action uses a GitHub token (secrets.FOR_RELEASE_TOKEN) to create and upload releases.
- *Username and Host*: The username and host details for the remote server are stored securely in GitHub secrets (
  secrets.USERNAME, secrets.REMOTE_HOST).
- *WEATHER_TRACKER_REMOTE_API_KEY*: API key for accessing the Weather API.

#### Conclusion

This CI/CD pipeline automates the build, release, and deployment process for the Tennis-Scoreboard application. Using
GitHub Actions, Maven, and Tomcat, the system ensures that each new release is built and deployed to production with
minimal manual intervention.
---

## Share your feedback

I am continuously looking to refine my understanding and implementation of programming. If you have insights,
critiques, or advice—or if you wish to discuss any aspect of this project further—I warmly welcome your
contributions. Please feel free to [open an issue](https://github.com/aleos-dev/weather-scoreboard/issues) to share
your
thoughts.

## Acknowledgments

`I want to express my gratitude to the author of the `[technical requirements](https://zhukovsd.github.
io/java-backend-learning-course/projects/weather-viewer/)
` for this project, `[S. Zhukov](https://t.me/zhukovsd_it_mentor)`.

**Made with ☀️ and 🌧 by Aleos.**