package com.sweng22g1.serverapp.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.sweng22g1.serverapp.model.Media;
import com.sweng22g1.serverapp.service.MediaServiceImpl;

@ActiveProfiles("test")
@WebMvcTest(MediaController.class)
public class MediaControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MediaServiceImpl mediaService;
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void getMediaEndpointValidRequestReturns200Code(@TempDir Path tempDir) throws Exception {
	
		// Create temporary file directory for when controller attempts to find media file. 
		Path mediaFile = tempDir.resolve("filepath1");
		List<String> lines = Arrays.asList("I am the first line", "I am the second line", "I am the third line");
		Files.write(mediaFile, lines);
		
		Long id = 1L;
		String fileType = "jpg";
		Media media = Media.builder()
				.filepath(mediaFile.toAbsolutePath().toString())
				.id(id)
				.mimetype(fileType).build();
		
		String url = "/api/v1/media/" + id;
		
		RequestBuilder postRequest = MockMvcRequestBuilders
				.get(url)
				.param("id", id.toString());
		
		when(mediaService.getMedia(id)).thenReturn(media);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void getMediaEndpointValidRequestReturnsCorrectFileContents(@TempDir Path tempDir) throws Exception {
	
		// Create temporary file directory for when controller attempts to find media file. 
		Path mediaFile = tempDir.resolve("filepath1");
		List<String> lines = Arrays.asList("I am the first line", "I am the second line", "I am the third line");
		Files.write(mediaFile, lines);
		
		Long id = 1L;
		String fileType = "jpg";
		Media media = Media.builder()
				.filepath(mediaFile.toAbsolutePath().toString())
				.id(id)
				.mimetype(fileType).build();
		
		String url = "/api/v1/media/" + id;
		
		RequestBuilder postRequest = MockMvcRequestBuilders
				.get(url)
				.param("id", id.toString());
		
		when(mediaService.getMedia(id)).thenReturn(media);
		
		String response = mockMvc.perform(postRequest).andReturn().getResponse().getContentAsString();
		
		System.out.println(response);
		
		assertThat(response.contains("I am the first line")).isTrue();
		assertThat(response.contains("I am the second line")).isTrue();
		assertThat(response.contains("I am the third line")).isTrue();
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void getMediaEndpointInvalidRequestReturns404Code(@TempDir Path tempDir) throws Exception {
		
		Long id = 2L;
		String url = "/api/v1/media/" + id;
		
		RequestBuilder postRequest = MockMvcRequestBuilders
				.get(url)
				.param("id", id.toString());
		
		when(mediaService.getMedia(id)).thenReturn(null);
		
		mockMvc.perform(postRequest).andDo(print()).andExpect(status().isNotFound());
	}
	

	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void postMediaEndpointvalidRequestReturns200Code(@TempDir Path tempDir) throws Exception {
		
	String url = "/api/v1/media";
	String filepath = "filepath";
	Long id = 1L;
	String fileType = "jpg";
	
	Media media = Media.builder()
			.filepath(filepath)
			.id(id)
			.mimetype(fileType).build();
	
	MockMultipartFile mockFile = new MockMultipartFile("file", "hello.jpg", MediaType.IMAGE_JPEG_VALUE,
			"Hello, World!".getBytes());
	
	RequestBuilder postRequest = MockMvcRequestBuilders.multipart(url)
			.file(mockFile).param("mime", fileType);
	
	when(mediaService.createMedia(mockFile.getBytes(), fileType)).thenReturn(media);
	
	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void postMediaEndpointValidRequestSavesPost(@TempDir Path tempDir) throws Exception {
		
	String url = "/api/v1/media";
	String filepath = "filepath";
	Long id = 1L;
	String fileType = "jpg";
	
	Media media = Media.builder()
			.filepath(filepath)
			.id(id)
			.mimetype(fileType).build();
	
	MockMultipartFile mockFile = new MockMultipartFile("file", "hello.jpg", MediaType.IMAGE_JPEG_VALUE,
			"Hello, World!".getBytes());
	
	RequestBuilder postRequest = MockMvcRequestBuilders.multipart(url)
			.file(mockFile).param("mime", fileType);
	
	when(mediaService.createMedia(mockFile.getBytes(), fileType)).thenReturn(media);
	
	mockMvc.perform(postRequest).andDo(print());
	verify(mediaService).createMedia(mockFile.getBytes(), fileType);
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void deleteMediaEndpointValidRequestAsAdminReturns200Code(@TempDir Path tempDir) throws Exception {
		
	String filepath = "filepath";
	Long id = 1L;
	String fileType = "jpg";
	String url = "/api/v1/media/" + id;
	
	RequestBuilder postRequest = MockMvcRequestBuilders.delete(url);
	
	when(mediaService.deleteMedia(id)).thenReturn(null);
	
	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isOk());

	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void deleteMediaEndpointValidRequestAsAdminDeletesMedia(@TempDir Path tempDir) throws Exception {
		
	String filepath = "filepath";
	Long id = 1L;
	String fileType = "jpg";
	String url = "/api/v1/media/" + id;
	
	RequestBuilder postRequest = MockMvcRequestBuilders.delete(url);
	
	when(mediaService.deleteMedia(id)).thenReturn(null);
	
	mockMvc.perform(postRequest).andDo(print());
	verify(mediaService).deleteMedia(id);

	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "User" })
	public void deleteMediaEndpointValidRequestAsUserReturns404Code(@TempDir Path tempDir) throws Exception {
		
	String filepath = "filepath";
	Long id = 1L;
	String fileType = "jpg";
	String url = "/api/v1/media/" + id;
	
	RequestBuilder postRequest = MockMvcRequestBuilders.delete(url);
	
	when(mediaService.deleteMedia(id)).thenReturn(null);
	
	mockMvc.perform(postRequest).andDo(print()).andExpect(status().isForbidden());

	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
	public void deleteMediaEndpointInvalidRequestReturns500Code(@TempDir Path tempDir) throws Exception {
		
	Long id = 1L;
	String url = "/api/v1/media/" + id;
	
	IOException e = new IOException("can't delete media.");
	
	RequestBuilder postRequest = MockMvcRequestBuilders.delete(url);
	
	when(mediaService.deleteMedia(id)).thenThrow(e);
	
	mockMvc.perform(postRequest).andDo(print()).andExpect(status().is5xxServerError());

	}
}
