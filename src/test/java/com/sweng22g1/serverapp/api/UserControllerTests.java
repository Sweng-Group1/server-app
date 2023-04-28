package com.sweng22g1.serverapp.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.service.UserServiceImpl;
/* Test Strategy:
Controllers need:
their status codes validated, 
verify they call the appropriate service method
(e.g. delete endpoint calls delete), 
security verification (largely users can't do admin restricted tasks), 
and checking returned data is accurate / formatted correctly.  
*/


@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public class UserControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean(name = "inMemoryUserDetailsManager")
	private UserServiceImpl userService;

	// TODO: Add test strategy. 

@Test
@WithMockUser(username = "user", password = "test", authorities = { "User", "Admin" })
public void GetUsersRequestAsAdminReturns200CodeAndGetsUsersList() throws Exception {
	

	String username1 = "getUserRequest1";
	String username2 = "getUserRequest2";
	
	Role admin = Role.builder().name("Admin").build();
	Set<Role> requestingUserRoles = Set.of(admin);
	
	User userRequester = User.builder().id(1L).username("user").roles(requestingUserRoles).build();
	User user1 = User.builder().id(2L).username(username1).build();
	User user2 = User.builder().id(3L).username(username2).build();
	List<User> foundUsersList = Arrays.asList(user1, user2);
	
	String url = "/api/v1/user";

	// Builds the HTTP request. 
	RequestBuilder getRequest = MockMvcRequestBuilders.get(url);
	when(userService.getUser("user")).thenReturn(userRequester);
	when(userService.getUsers()).thenReturn(foundUsersList);

	mockMvc.perform(getRequest).andDo(print()).andExpect(status().isOk());

	verify(userService).getUser("user");
	verify(userService).getUsers();
	
	}


//TODO: Add test for logging?
@Test
@WithMockUser(username = "user", password = "test", authorities = { "User"})
public void GetUsersRequestAsUserReturns403ForbiddenCode() throws Exception {
	

	String username1 = "getUserRequest1";
	String username2 = "getUserRequest2";
	
	Role admin = Role.builder().name("User").build();
	Set<Role> requestingUserRoles = Set.of(admin);
	
	User userRequester = User.builder().id(1L).username("user").roles(requestingUserRoles).build();
	User user1 = User.builder().id(2L).username(username1).build();
	User user2 = User.builder().id(3L).username(username2).build();
	List<User> foundUsersList = Arrays.asList(user1, user2);
	
	String url = "/api/v1/user";
	
	// Builds the HTTP request. 
	RequestBuilder getRequest = MockMvcRequestBuilders.get(url);
	when(userService.getUser("user")).thenReturn(userRequester);
	when(userService.getUsers()).thenReturn(foundUsersList);

	mockMvc.perform(getRequest).andDo(print()).andExpect(status().isForbidden());

	verify(userService).getUser("user");
	}

@Test
@WithMockUser(username = "user", password = "test", authorities = { "User"})
public void GetUserRequestForValidUserReturnsOkStatusCode() throws Exception {
	

	String username = "getUserRequest1";

	User user = User.builder().id(2L).username(username).build();
	
	String url = "/api/v1/user/" + username;

	// Builds the HTTP request. 
	RequestBuilder getRequest = MockMvcRequestBuilders.get(url)
			.param("username", username);
	when(userService.getUser(username)).thenReturn(user);

	mockMvc.perform(getRequest).andDo(print()).andExpect(status().isOk());
	verify(userService).getUser(username);
}

@Test
public void GetUserRequestAsLoggedOutUserReturnsForbiddenStatusCode() throws Exception {
	

	String username = "getUserRequest1";
	
	String url = "/api/v1/user/" + username;

	// Builds the HTTP request. 
	RequestBuilder getRequest = MockMvcRequestBuilders.get(url)
			.param("username", username);

	mockMvc.perform(getRequest).andDo(print()).andExpect(status().isForbidden());
}

@Test
@WithMockUser(username = "user", password = "test", authorities = { "User"})
public void GetUserRequestForInvalidUserReturns404StatusCode() throws Exception {
	

	String username = "getUserRequest";
	
	String url = "/api/v1/user/" + username;

	// Builds the HTTP request. 
	RequestBuilder getRequest = MockMvcRequestBuilders.get(url)
			.param("username", username);
	when(userService.getUser(username)).thenReturn(null);

	mockMvc.perform(getRequest).andDo(print()).andExpect(status().isNotFound());
	verify(userService).getUser(username);
	}


@Test
@WithMockUser(username = "user", password = "test", authorities = { "User"})
public void CreateUserPostRequestSavesUserAndReturnsOkCode() throws Exception {
	
	String username = "postUserRequest";
	String firstName = "Luke";
	String lastName = "Skywalker";
	String email = "lukeskywalker@jeditemple.net";
	String password = "maytheforcebewithyou777";

	User user = User.builder()
			.username(username)
			.firstname(firstName)
			.lastname(lastName)
			.email(email)
			.password(password)
			.build();
	
	User userWithGeneratedID = User.builder()
			.username(username)
			.firstname(firstName)
			.lastname(lastName)
			.email(email)
			.password(password)
			.id(1L)
			.build();
	
	String url = "/api/v1/user";

	// Builds the HTTP request. 
	RequestBuilder postRequest = MockMvcRequestBuilders.post(url)
			.param("username", username)
			.param("email", email)
			.param("firstname", firstName)
			.param("lastname", lastName)
			.param("password", password);
	when(userService.saveUser(user)).thenReturn(userWithGeneratedID);

	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
	verify(userService).saveUser(user);
	}

@Test
@WithMockUser(username = "user", password = "test", roles = {"Admin"})
public void UpdateUserPostRequestAsAdminUpdatesAllFieldsAndReturnsOkCode() throws Exception {
	
	String requesterUsername = "user";
	String requesterPassword = "password";
	
	String username = "JediKnight445";
	String firstName = "Anakin";
	String lastName = "Skywalker";
	String email = "anakinskywalker@jeditemple.net";
	String password = "i_hate_sand";
	
	Role admin = Role.builder().name("Admin").build();
	Set<Role> requestingUserRoles = Set.of(admin);

	User user = User.builder()
			.username(username)
			.firstname(firstName)
			.lastname(lastName)
			.email(email)
			.password(password)
			.roles(requestingUserRoles)
			.id(1L)
			.build();
	
	User requestingUser = User.builder()
			.username(requesterUsername)
			.password(requesterPassword)
			.id(3L)
			.roles(requestingUserRoles)
			.build();
	
	String updatedUsername = "SithLord666";
	String updatedFirstName = "Darth";
	String updatedLastName = "Vader";
	String updatedEmail = "darklord@imperialpalace.gov";
	String updatedPassword = "i_didnt_have_the_high_ground";
	

	User updatedUser = User.builder()
			.username(updatedUsername)
			.firstname(updatedFirstName)
			.lastname(updatedLastName)
			.email(updatedEmail)
			.password(updatedPassword)
			.roles(requestingUserRoles)
			.id(1L)
			.build();
	
	String url = "/api/v1/user/" + username;

	// Builds the HTTP request. 
	RequestBuilder postRequest = MockMvcRequestBuilders.post(url)		
			.param("newUsername", updatedUsername)
			.param("newEmail", updatedEmail)
			.param("newFirstname", updatedFirstName)
			.param("newLastname", updatedLastName)
			.param("newPassword", updatedPassword)
			.param("username", username);
	
	when(userService.getUser(requesterUsername)).thenReturn(requestingUser);
	when(userService.getUser(username)).thenReturn(user);

	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
	verify(userService).saveUser(updatedUser);
	}

@Test
@WithMockUser(username = "user", password = "test", roles = {"Admin"})
public void UpdateAnotherUserPostRequestAsUserReturnsForbiddenCode() throws Exception {
	
	String requesterUsername = "user";
	String requesterPassword = "password";
	
	String username = "JediKnight445";
	String firstName = "Anakin";
	String lastName = "Skywalker";
	String email = "anakinskywalker@jeditemple.net";
	String password = "i_hate_sand";
	
	Role admin = Role.builder().name("User").build();
	Set<Role> requestingUserRoles = Set.of(admin);

	User user = User.builder()
			.username(username)
			.firstname(firstName)
			.lastname(lastName)
			.email(email)
			.password(password)
			.roles(requestingUserRoles)
			.id(1L)
			.build();
	
	User requestingUser = User.builder()
			.username(requesterUsername)
			.password(requesterPassword)
			.id(3L)
			.roles(requestingUserRoles)
			.build();
	
	String updatedUsername = "SithLord666";
	String updatedFirstName = "Darth";
	String updatedLastName = "Vader";
	String updatedEmail = "darklord@imperialpalace.gov";
	String updatedPassword = "i_didnt_have_the_high_ground";
	
	String url = "/api/v1/user/" + username;

	// Builds the HTTP request. 
	RequestBuilder postRequest = MockMvcRequestBuilders.post(url)		
			.param("newUsername", updatedUsername)
			.param("newEmail", updatedEmail)
			.param("newFirstname", updatedFirstName)
			.param("newLastname", updatedLastName)
			.param("newPassword", updatedPassword)
			.param("username", username);
	
	when(userService.getUser(requesterUsername)).thenReturn(requestingUser);
	when(userService.getUser(username)).thenReturn(user);

	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isForbidden());
	}

@Test
@WithMockUser(username = "user", password = "test", roles = {"Admin"})
public void DeleteUserPostRequestAsAdminDeletesUserAndReturnsOkCode() throws Exception {
	
	String requesterUsername = "user";
	String requesterPassword = "password";
	
	String username = "JediKnight445";
	String firstName = "Anakin";
	String lastName = "Skywalker";
	String email = "anakinskywalker@jeditemple.net";
	String password = "i_hate_sand";
	
	Role admin = Role.builder().name("Admin").build();
	Set<Role> requestingUserRoles = Set.of(admin);
	
	User user = User.builder()
			.username(username)
			.firstname(firstName)
			.lastname(lastName)
			.email(email)
			.password(password)
			.id(1L)
			.build();
	
	User requestingUser = User.builder()
			.username(requesterUsername)
			.password(requesterPassword)
			.id(2L)
			.roles(requestingUserRoles)
			.build();
	
	when(userService.getUser(requesterUsername)).thenReturn(requestingUser);
	when(userService.getUser(username)).thenReturn(user);
	when(userService.deleteUser(username)).thenReturn(null);
	
	String url = "/api/v1/user/" + username;
	
	// Builds the HTTP request. 
	RequestBuilder postRequest = MockMvcRequestBuilders.delete(url)		
			.param("username", username);
	
	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
	verify(userService).deleteUser(username);
	}

@Test
@WithMockUser(username = "user", password = "test", roles = {"User"})
public void DeleteUserPostRequestAsUserForAnotherUserReturnsForbiddenCode() throws Exception {
	
	String requesterUsername = "user";
	String requesterPassword = "password";
	
	String username = "JediKnight445";
	String firstName = "Anakin";
	String lastName = "Skywalker";
	String email = "anakinskywalker@jeditemple.net";
	String password = "i_hate_sand";
	
	Role admin = Role.builder().name("User").build();
	Set<Role> requestingUserRoles = Set.of(admin);
	
	User user = User.builder()
			.username(username)
			.firstname(firstName)
			.lastname(lastName)
			.email(email)
			.password(password)
			.id(1L)
			.build();
	
	User requestingUser = User.builder()
			.username(requesterUsername)
			.password(requesterPassword)
			.id(2L)
			.roles(requestingUserRoles)
			.build();
	
	when(userService.getUser(requesterUsername)).thenReturn(requestingUser);
	when(userService.getUser(username)).thenReturn(user);
	
	String url = "/api/v1/user/" + username;
	
	// Builds the HTTP request. 
	RequestBuilder postRequest = MockMvcRequestBuilders.delete(url)		
			.param("username", username);
	
	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isForbidden());
	}

@Test
@WithMockUser(username = "user", password = "test", roles = {"Admin"})
public void DeleteUserPostRequestAsUserForOwnUserDeletesUserAndReturnsOkCode() throws Exception {
	
	String requesterUsername = "user";
	String requesterPassword = "password";
	
	Role admin = Role.builder().name("Admin").build();
	Set<Role> requestingUserRoles = Set.of(admin);
	
	User requestingUser = User.builder()
			.username(requesterUsername)
			.password(requesterPassword)
			.id(2L)
			.roles(requestingUserRoles)
			.build();
	
	when(userService.getUser(requesterUsername)).thenReturn(requestingUser);
	when(userService.deleteUser(requesterUsername)).thenReturn(null);
	
	String url = "/api/v1/user/" + requesterUsername;
	
	// Builds the HTTP request. 
	RequestBuilder postRequest = MockMvcRequestBuilders.delete(url)		
			.param("username", requesterUsername);
	
	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
	verify(userService).deleteUser(requesterUsername);
	}
}
//TODO: Determine whether we want to go to the effort of sorting out all the throws exception declarations, e.g.
//  getUsers does not have any exception passing. So cannot test for it. 

//
//@Test
//@WithMockUser(username = "user", password = "test", authorities = { "User", "Admin"})
//public void GetUsersRequestAsAdminReturns500ServerErrorCodeWhenExceptionThrown() throws Exception {
//	
//
//	String username1 = "getUserRequest1";
//	String username2 = "getUserRequest2";
//	
//	Role admin = Role.builder().name("Admin").build();
//	Set<Role> requestingUserRoles = Set.of(admin);
//	
//	User userRequester = User.builder().id(1L).username("user").roles(requestingUserRoles).build();
//	User user1 = User.builder().id(2L).username(username1).build();
//	User user2 = User.builder().id(3L).username(username2).build();
//	List<User> foundUsersList = Arrays.asList(user1, user2);
//	
//	String url = "/api/v1/user";
//
//	RequestBuilder getRequest = MockMvcRequestBuilders.get(url);
//	when(userService.getUser("user")).thenReturn(userRequester);
//	when(userService.getUsers()).thenThrow(e);
//
//	mockMvc.perform(getRequest).andDo(print()).andExpect(status().is5xxServerError());
//
//	verify(userService).getUsers();
//	
//	}
//}