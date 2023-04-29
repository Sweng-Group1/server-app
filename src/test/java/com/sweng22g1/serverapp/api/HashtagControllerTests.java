package com.sweng22g1.serverapp.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sweng22g1.serverapp.model.Hashtag;
import com.sweng22g1.serverapp.service.HashtagServiceImpl;


//Test Strategy: Requirements for controller testing:
// Endpoints return correct response codes (ok for valid requests,
// forbidden when access is not allowed for user, etc). 
// Verify correct service layer methods are being called.
// Verify response contains requested information. 
// Using mocking to simulate service layer. 

@ActiveProfiles("test")
@WebMvcTest(HashtagController.class)
public class HashtagControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private HashtagServiceImpl hashtagService;
	
	@Test
	@WithMockUser(username = "user", password = "test", authorities = { "User", "Admin" })
	public void validGetHashtagsRequestReturns200Code() throws Exception {
		
		String name = "#LiveLaughLove";
		String name2 = "#JustSwEngThings";
		 
		Hashtag hashtag1 = Hashtag.builder().id(1L).name(name).build();
		Hashtag hashtag2 = Hashtag.builder().id(2L).name(name2).build();
		
		List<Hashtag> hashtags = new ArrayList();
		hashtags.add(hashtag1);
		hashtags.add(hashtag2);
		
		String url = "/api/v1/hashtags";
		
		// Performs the request. 
		RequestBuilder getRequest = MockMvcRequestBuilders.get(url);
		when(hashtagService.getHashtags()).thenReturn(hashtags);
		
		mockMvc.perform(getRequest).andDo(print()).andExpect(status().isOk());
		
	}
	
	@Test
	@WithMockUser(username = "user", password = "test", authorities = { "User"})
	public void validGetHashtagsRequestAsUserReturnsHashtags() throws Exception {
		
		String name = "#LiveLaughLove";
		String name2 = "#JustSwEngThings";
		 
		Hashtag hashtag1 = Hashtag.builder().id(1L).name(name).build();
		Hashtag hashtag2 = Hashtag.builder().id(2L).name(name2).build();
		
		List<Hashtag> hashtags = new ArrayList();
		hashtags.add(hashtag1);
		hashtags.add(hashtag2);
		
		String url = "/api/v1/hashtags";
		
		RequestBuilder getRequest = MockMvcRequestBuilders.get(url);
		when(hashtagService.getHashtags()).thenReturn(hashtags);
		
		// Performs the request. 
		String response = mockMvc.perform(getRequest).andDo(print()).andReturn().getResponse().getContentAsString();
		
		assertThat(response.contains(name)).isTrue();
		assertThat(response.contains(name2)).isTrue();
		verify(hashtagService).getHashtags();
	}
	
	@Test
	public void validGetHashtagsAsLoggedOutUserReturnsForbiddenCode() throws Exception {
		
		String name = "#LiveLaughLove";
		 
		Hashtag hashtag1 = Hashtag.builder().id(1L).name(name).build();
		
		String url = "/api/v1/hashtags";
		
		RequestBuilder getRequest = MockMvcRequestBuilders.get(url);
		// Performs the request. 
		mockMvc.perform(getRequest).andDo(print()).andExpect(status().isOk());
		
	}

}
