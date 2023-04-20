package com.sweng22g1.serverapp.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;

import com.sweng22g1.serverapp.model.Post;
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
	public void createPostsTest() throws Exception {
		
		String url = "/api/v1/post";
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
	
	

}
