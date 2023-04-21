package com.sweng22g1.serverapp.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;

import com.sweng22g1.serverapp.model.Post;
import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.service.PostServiceImpl;
import com.sweng22g1.serverapp.service.UserServiceImpl;

@ActiveProfiles("test")
@WebMvcTest(PostController.class)
public class PostControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostServiceImpl postService;
	
	@MockBean(name = "inMemoryUserDetailsManager")
	private UserServiceImpl userService;
	
	@Test
	@WithMockUser(username = "user", authorities = { "User", "Admin" })
	public void createPostTestSavesPostAndReturnsOkCode() throws Exception {
		
		
		String url = "/api/v1/post/";
		String xmlContent = "this is a test. I'm off to the zoo.";
		String latitude = "42.42";
		String longitude = "42.42";
		String validityHours = "24";
		
		RequestBuilder postRequest = MockMvcRequestBuilders.post(url)
				.param("xmlContent", xmlContent)
				.param("latitude", latitude)
				.param("longitude", longitude)
				.param("validityHours", validityHours);
		
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		Post newPost = Post.builder()
				.xmlContent(xmlContent)
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.build();
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
		
		verify(postService).savePost(any(Post.class));
	}
	
	@Test
	@WithMockUser(username = "user", authorities = { "User", "Admin" })
	public void getPostTestAsLoggedInUserGetsPostsAndReturnsOkCode() throws Exception {
		
		String url = "/api/v1/post";
		String xmlContent = "this is a test. I'm off to the zoo.";
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		RequestBuilder postRequest = MockMvcRequestBuilders.get(url);
		
		Post post1 = Post.builder()
				.xmlContent(xmlContent)
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.build();
		
		Post post2 = Post.builder()
				.xmlContent(xmlContent + "2")
				.expiry(postExpiry)
				.latitude(77.77)
				.longitude(77.77)
				.build();
		
		List<Post> postsList = new ArrayList<Post>();
		postsList.add(post1);
		postsList.add(post2);
		
		when(postService.getPosts()).thenReturn(postsList);
		
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
		
		verify(postService).getPosts();
	}
	
	@Test
	public void getPostTestAsLoggedOutUserGetsAdminVerifiedPostsAndReturnsOkCode() throws Exception {
		
		
		String url = "/api/v1/post";
		String xmlContent = "this is a test. I'm off to the zoo.";
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		RequestBuilder postRequest = MockMvcRequestBuilders.get(url);
		
		// Build list/set of posts to be retrieved. 
		
		
		// post1 is added to an admit user. 
		Post post1 = Post.builder()
				.xmlContent(xmlContent + "I'm an Admin. Secret Word: Platypus")
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.build();
		
		// post2 is added to an ordinary user. 
		Post post2 = Post.builder()
				.xmlContent(xmlContent + "I'm a User. Secret Word: Beaver")
				.expiry(postExpiry)
				.latitude(77.77)
				.longitude(77.77)
				.build();
		
		Set<Post> postsSetAdmin = new HashSet<Post>();
		postsSetAdmin.add(post1);
		
		Set<Post> postsSetUser = new HashSet<Post>();
		postsSetUser.add(post2);
		
		// Required to let PostController retrieve a list of posts 
		// to use as an empty list, bug workaround. 
		// Does not need to have content as it gets cleared. 
		List<Post> postsList = new ArrayList<Post>();
		
		// Build users list for when PostController checks list of 
		// admin / verified users. 
		
		String username1 = "getUserRequest1";
		String username2 = "getUserRequest2";
		
		Role admin = Role.builder().name("Admin").build();
		Set<Role> adminRole = Set.of(admin);
		
		User adminUser = User.builder().id(2L).username(username1).roles(adminRole).posts(postsSetAdmin).build();
		User user = User.builder().id(3L).username(username2).posts(postsSetUser).build();
		List<User> usersList = Arrays.asList(adminUser, user);
		
		
		// For when PostController gets PostList to instantiate an empty list.
		when(postService.getPosts()).thenReturn(postsList);
		
		// For when PostController gets gets all users to check list of 
		// admin / verified users. 
		when(userService.getUsers()).thenReturn(usersList);
		
		MvcResult response = mockMvc.perform(postRequest).andDo(print()).andReturn();
		
		
		// If "Platypus" is present but "Beaver" is not, it's successfully returned the admin post post1
		// but not returned the ordinary user post post2. 
		assertThat(response.getResponse().getContentAsString().contains("Platypus")).isTrue();
		assertThat(response.getResponse().getContentAsString().contains("Beaver")).isFalse();
		assertThat(response.getResponse().getStatus()).isEqualTo(200);
		
	}
	
	@Test
	@WithMockUser(username = "user", authorities = { "User", "Admin" })
	public void updatePostAsAdminUpdatesAnotherUsersPostAndReturnsOkCode() throws Exception {
		
		String url = "/api/v1/post/2";
		String xmlContent = "this is a test. I'm off to the zoo.";
		
		String updatedXml = "this is an updated test. I'm off to the pool.";
		String updatedLat = "61.34";
		String updatedLon = "97.31";
		
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		Post existingPost = Post.builder()
				.xmlContent(xmlContent)
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.id(2L)
				.build();
		
		// for the step where it checks if the current user is an admin. 
		String username = "user";
		
		Role admin = Role.builder().name("Admin").build();
		Set<Role> adminRole = Set.of(admin);
		
		User adminUser = User.builder().id(2L).username(username).roles(adminRole).build();
		
		RequestBuilder postRequest = MockMvcRequestBuilders.post(url)
				.param("newXmlContent", updatedXml)
				.param("newLatitude", updatedLat)
				.param("newLongitude", updatedLon)
				.param("id", "2");
		
		
		when(userService.getUser(username)).thenReturn(adminUser);
		when(postService.getPost(2L)).thenReturn(existingPost);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
		
		verify(postService).savePost(argThat(argument -> argument.getXmlContent().equals(updatedXml)
				&& argument.getLatitude() == 61.34
				&& argument.getLongitude() == 97.31
				&& argument.getId() == 2));
		
	}
	
	@Test
	@WithMockUser(username = "user", authorities = {"User"})
	public void updatePostAsUserCannotUpdateAnotherUsersPostAndReturnsForbiddenCode() throws Exception {
		
		String url = "/api/v1/post/2";
		String xmlContent = "this is a test. I'm off to the zoo.";
		
		String updatedXml = "this is an updated test. I'm off to the pool.";
		String updatedLat = "61.34";
		String updatedLon = "97.31";
		
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		Post existingPost = Post.builder()
				.xmlContent(xmlContent)
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.id(2L)
				.build();

		
		// for the step where it checks if the current user is an Admin. 
		String username = "user";
		
		Role admin = Role.builder().name("User").build();
		Set<Role> adminRole = Set.of(admin);
		
		User adminUser = User.builder().id(2L).username(username).roles(adminRole).build();
		
		RequestBuilder postRequest = MockMvcRequestBuilders.post(url)
				.param("newXmlContent", updatedXml)
				.param("newLatitude", updatedLat)
				.param("newLongitude", updatedLon)
				.param("id", "2");
		
		when(userService.getUser(username)).thenReturn(adminUser);
		when(postService.getPost(2L)).thenReturn(existingPost);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "user", authorities = { "User" })
	public void updatePostAsUserUpdatesOwnPostAndReturnsOkCode() throws Exception {
		
		String url = "/api/v1/post/2";
		String xmlContent = "this is a test. I'm off to the zoo.";
		
		String updatedXml = "this is an updated test. I'm off to the pool.";
		String updatedLat = "61.34";
		String updatedLon = "97.31";
		
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		Post existingPost = Post.builder()
				.xmlContent(xmlContent)
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.id(2L)
				.build();
		
		Set<Post> userPosts = new HashSet<Post>();
		userPosts.add(existingPost);		
		
		// for the step where it checks if the current user is an admin. 
		String username = "user";
		
		Role userRole = Role.builder().name("User").build();
		Set<Role> userRoleSet = Set.of(userRole);
		
		User user = User.builder().id(2L).username(username).roles(userRoleSet).posts(userPosts).build();
		
		RequestBuilder postRequest = MockMvcRequestBuilders.post(url)
				.param("newXmlContent", updatedXml)
				.param("newLatitude", updatedLat)
				.param("newLongitude", updatedLon)
				.param("id", "2");
		
		when(userService.getUser(username)).thenReturn(user);
		when(postService.getPost(2L)).thenReturn(existingPost);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
		
		verify(postService).savePost(argThat(argument -> argument.getXmlContent().equals(updatedXml)
				&& argument.getLatitude() == 61.34
				&& argument.getLongitude() == 97.31
				&& argument.getId() == 2));
		
	}
	
	@Test
	@WithMockUser(username = "user", authorities = { "User" })
	public void DeletePostAsAdminDeletesAnotherUsersPostAndReturnsOkCode() throws Exception {
		
		String xmlContent = "this is a test. I'm off to the zoo.";
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		Post existingPost = Post.builder()
				.xmlContent(xmlContent)
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.id(2L)
				.build();
		
		// for the step where it checks if the current user is an admin. 
		String username = "user";
		
		Role admin = Role.builder().name("Admin").build();
		Set<Role> adminRole = Set.of(admin);
		
		User adminUser = User.builder().id(2L).username(username).roles(adminRole).build();
		
		String url = "/api/v1/post/2";
		RequestBuilder postRequest = MockMvcRequestBuilders.delete(url)
				.param("id", "2");
		
		when(userService.getUser(username)).thenReturn(adminUser);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
		
		verify(postService).deletePost(2L);
		
	}
	
	@Test
	@WithMockUser(username = "user", authorities = { "User" })
	public void DeleteOwnPostAsUserDeletesPostAndReturnsOkCode() throws Exception {
		
		String xmlContent = "this is a test. I'm off to the zoo.";
		LocalDateTime postExpiry = LocalDateTime.now().plusHours(24);
		
		Post existingPost = Post.builder()
				.xmlContent(xmlContent)
				.expiry(postExpiry)
				.latitude(42.42)
				.longitude(42.42)
				.id(2L)
				.build();
		
		Set<Post> postSet = new HashSet<Post>();
		postSet.add(existingPost);
		
		// for the step where it checks if the current user is an admin. 
		String username = "user";
		
		Role userRole = Role.builder().name("User").build();
		Set<Role> userRoleSet = Set.of(userRole);
		
		User user = User.builder().id(2L).username(username).roles(userRoleSet).posts(postSet).build();
		
		String url = "/api/v1/post/2";
		RequestBuilder postRequest = MockMvcRequestBuilders.delete(url)
				.param("id", "2");
		
		when(userService.getUser(username)).thenReturn(user);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
		
		verify(postService).deletePost(2L);
		
	}
	
	@Test
	@WithMockUser(username = "user", authorities = { "User" })
	public void DeleteAnotherUsersPostAsUserReturnsForbiddenCode() throws Exception {
		
		// for the step where it checks if the current user is an admin. 
		String username = "user";
		
		Role userRole = Role.builder().name("User").build();
		Set<Role> userRoleSet = Set.of(userRole);
		
		User user = User.builder().id(2L).username(username).roles(userRoleSet).build();
		
		String url = "/api/v1/post/2";
		RequestBuilder postRequest = MockMvcRequestBuilders.delete(url)
				.param("id", "2");
		
		when(userService.getUser(username)).thenReturn(user);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isForbidden());
		
		verify(postService, never()).deletePost(any());
		
	}
	
	
	

}
