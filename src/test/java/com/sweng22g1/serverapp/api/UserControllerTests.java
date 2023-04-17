package com.sweng22g1.serverapp.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

	RequestBuilder getRequest = MockMvcRequestBuilders.get(url);
	when(userService.getUser("user")).thenReturn(userRequester);
	when(userService.getUsers()).thenReturn(foundUsersList);

	mockMvc.perform(getRequest).andDo(print()).andExpect(status().isForbidden());

	verify(userService).getUser("user");
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