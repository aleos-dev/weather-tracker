### Project Overview

One of the primary goals of this project is to get familiar with the **`HttpSession`** workflow and **`Cookie`** management. To extend this learning, I decided to implement a simplified version of a **Security Filter Chain**, inspired by Spring Security's approach.

### Key Components and Workflow

1. **DelegatingFilterProxy**:
   - The **`DelegatingFilterProxy`** is a filter registered in the Tomcat context, which delegates the request to the **`SecurityFilterChain`** bean managed by the **`BeanFactory`**. This allows modular handling of security filters in the filter chain, similar to Spring Security’s **`FilterChainProxy`**.

2. **DefaultSecurityFilterChain**:
    - This class is responsible for holding and executing a series of filters related to security.
    - It is created by **`SecurityInitializer`** and customized using **`SecurityFilterChainConfigurer`**. Once configured, it is added to the **`ApplicationContext`**, represented by **`BeanFactory`**.
    - **`BeanFactory`** uses a Java configuration class, **`ApplicationContextConfiguration`**, to create and manage "beans." These beans are stored in the **`ServletContext`** with the help of **`ApplicationContextInitializer`**, which implements **`ServletContextListener`**.

### Implemented Filters

At this point, I have implemented several key filters:

1. **SecurityContextHolderFilter**:
    - This filter works with the **`SecurityContext`**. It either creates a new **`SecurityContext`** or retrieves an existing one from the **`HttpSession`** object, which is managed previously by the **`SessionFilter`**.
    - The **`SecurityContext`** stores information about the currently authenticated user. To mimic deferred loading (similar to Spring), this filter uses the **`SingletonSupplier`** class to delay context loading until it's actually needed.

2. **AuthenticationFilter**:
    - Responsible for managing the authentication logic by checking the credentials and setting the authenticated user in the security context.

3. **AnonymousAuthenticationFilter**:
    - The **`AnonymousAuthenticationFilter`** checks if the current request is associated with an authenticated user. If no authentication is found in the **`SecurityContext`**, it assigns an **anonymous** user to ensure the system can handle unauthenticated access in a controlled way

4. **AuthorizationFilter**:
    - The **`AuthorizationFilter`** uses **`AuthorizationManager`** to check whether the current authenticated user has the necessary roles to access a given URL. Authorization rules are stored in a map within **`AuthorizationManager`**, which maps URL patterns to the roles required for access.

### Supporting Classes

1. **SessionManager**:
    - This class manages all the interactions with the **`HttpSession`** object, handling session creation, expiration, and user data. It also manages the **`Cookie`** where the session ID is stored. At this stage, **cookie management** is still handled directly within this class, as there hasn't been a need to extract it into a standalone class.

2. **SingletonSupplier**:
    -The **`SingletonSupplier`** ensures that the **`SecurityContext`** is loaded lazily and only once, even if multiple requests try to access it simultaneously. This mimics the lazy-loading behavior seen in Spring Security’s context handling.

3. **ApplicationContext**:
    - Managed via **`BeanFactory`**, it serves as the core container where various beans (e.g., `SessionManager`, `SecurityContextRepository`, `AuthorizationManager`) are created and injected into the filter chain.

---