package com.sweng22g1.serverapp.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLEngineResult.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweng22g1.serverapp.model.Map;
import com.sweng22g1.serverapp.service.MapServiceImpl;
import com.sweng22g1.serverapp.service.RoleServiceImpl;
import com.sweng22g1.serverapp.service.UserService;
import com.sweng22g1.serverapp.service.UserServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(MapController.class)
public class MapControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MapServiceImpl mapService;
	
	@Test
    public void ReturnedMapJSONsHaveCorrectFormatting() throws Exception {
   
        Map map1 = Map.builder().id(1L).filepath("/filepath1").name("map1").build();
        Map map2 = Map.builder().id(2L).filepath("/filepath2").name("map2").build();
        List<Map> maps = Arrays.asList(map1, map2);

        when(mapService.getMaps()).thenReturn(maps);
        
        String jsonBody = this.mockMvc.perform(get("/api/v1/map"))
        		.andDo(print())
        		.andExpect(status().isOk())
        		.andReturn()
        		.getResponse()
        		.getContentAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map> foundMaps = objectMapper.readValue(jsonBody, new TypeReference<List<Map>>(){});
        
        assertThat(foundMaps.contains(map1)).isTrue();
        assertThat(foundMaps.contains(map2)).isTrue();   
    }
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
    public void postMapEndpointResponds200ForValidRequestAndCreatesMap() throws Exception {
		
		String mapName = "map1";
        Map map1 = Map.builder().id(1L).filepath("/filepath1").name(mapName).build();
        String url = "/api/v1/map";
        
        MockMultipartFile mockFile  = new MockMultipartFile(
          "file", 
          "hello.txt", 
          MediaType.TEXT_PLAIN_VALUE, 
          "Hello, World!".getBytes()
        );
        
        RequestBuilder postRequest = MockMvcRequestBuilders.multipart(url)
        		.file(mockFile)
        		.param("name", mapName)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        
        when(mapService.createMap(mapName, mockFile.getBytes() )).thenReturn(map1);
        
        mockMvc.perform(postRequest)
		       .andDo(print())
		       .andExpect(status().isOk());
        
        verify(mapService).createMap(mapName, mockFile.getBytes());
    }
	
	
	@Test
	@WithMockUser(username = "admin", authorities = {"User"})
    public void postMapEndpointResponds403ForbiddenWhenDoNotHaveAdminOrVerifiedAuthority() throws Exception {
		
        Map map1 = Map.builder().id(1L).filepath("/filepath1").name("map1").build();
        String url = "/api/v1/map";
        
        MockMultipartFile mockFile  = new MockMultipartFile(
          "file", 
          "hello.txt", 
          MediaType.TEXT_PLAIN_VALUE, 
          "Hello, World!".getBytes()
        );  
    
        RequestBuilder postRequest = MockMvcRequestBuilders.multipart(url)
        		.file(mockFile)
        		.param("name", "map1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        
        when(mapService.createMap("map1", mockFile.getBytes() )).thenReturn(map1);

        mockMvc.perform(postRequest)
        .andDo(print())
        .andExpect(status().isForbidden());
    }
	
	@Test
	@WithMockUser(username = "admin", authorities = { "Admin", "User" })
    public void postMapEndpointResponds417ExpectationFailedWhenPostUploadFails() throws Exception {
		
		IOException e = new IOException("Upload failed.");
		
        String url = "/api/v1/map";
        
        MockMultipartFile mockFile  = new MockMultipartFile(
          "file", 
          "hello.txt", 
          MediaType.TEXT_PLAIN_VALUE, 
          "Hello, World!".getBytes()
        );  
    
        RequestBuilder postRequest = MockMvcRequestBuilders.multipart(url)
        		.file(mockFile)
        		.param("name", "map1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        
        when(mapService.createMap("map1", mockFile.getBytes() )).thenThrow(e);

        mockMvc.perform(postRequest)
        .andDo(print())
        .andExpect(status().isExpectationFailed());
    }
	
	@Test
	@WithMockUser(username = "user", authorities = {"User", "Admin"})
    public void GetMapRequestWithValidNameReturns200CodeAndGetsMap(@TempDir Path tempDir) throws Exception {
		Path mapFile = tempDir.resolve("filepath1");
	    List<String> lines = Arrays.asList("1", "2", "3");
	    Files.write(mapFile, lines);
	    
		String mapName = "getMapRequestMap";
        Map map1 = Map.builder().id(1L).filepath(mapFile.toAbsolutePath().toString()).name(mapName).build();
        String url = "/api/v1/map/" + mapName;
    
        RequestBuilder getRequest = MockMvcRequestBuilders.get(url)
        		.param("name", mapName);
        
        when(mapService.getMap(mapName)).thenReturn(map1);
    
        mockMvc.perform(getRequest)
        			.andDo(print())
        	      .andExpect(status().isOk());
        
        verify(mapService).getMap(mapName);
    }
	
	@Test
	@WithMockUser(username = "user", authorities = {"User", "Admin"})
    public void GetMapRequestWithInvalidNameReturns404Code() throws Exception {
		String mapName = "nonexistGetMapRequestMap";
        String url = "/api/v1/map/" + mapName;
    
        RequestBuilder getRequest = MockMvcRequestBuilders.get(url)
        		.param("name", mapName);
        
        when(mapService.getMap(mapName)).thenReturn(null);
    
        mockMvc.perform(getRequest)
        			.andDo(print())
        	      .andExpect(status().isNotFound()); // Change the 'is forbidden" if the code is not 404. 
        
        verify(mapService).getMap(mapName);
    }
	
	@Test
	@WithMockUser(username = "user", authorities = {"User", "Admin"})
    public void DeleteMapRequestWithValidNameReturns200CodeAndDeletesMap() throws Exception {
		String mapName = "valdidDeleteMapRequest";
        Map map1 = Map.builder().id(1L).filepath("/filepath1").name(mapName).build();
        String url = "/api/v1/map/" + mapName;
        
        RequestBuilder deleteRequest = MockMvcRequestBuilders.delete(url)
        		.param("name", mapName);
        
        when(mapService.deleteMap(mapName)).thenReturn(null);
    
        mockMvc.perform(deleteRequest)
        			.andDo(print())
        	      .andExpect(status().isOk());
        
        verify(mapService).deleteMap(mapName);
    }
	
	@Test
	@WithMockUser(username = "user", authorities = {"User", "Admin"})
    public void DeleteMapRequestWithInvalidNameReturns500CodeServerError() throws Exception {
		String mapName = "badDeleteMapRequest";
        IOException e = new IOException("can't delete map. ");
        String url = "/api/v1/map/" + mapName;
        
        RequestBuilder deleteRequest = MockMvcRequestBuilders.delete(url)
        		.param("name", mapName);
        
        when(mapService.deleteMap(mapName)).thenThrow(e);
    
        mockMvc.perform(deleteRequest)
        			.andDo(print())
        	      .andExpect(status().is5xxServerError());
        
        verify(mapService).deleteMap(mapName);
    }
	
	@Test
	@WithMockUser(username = "admin", authorities = {"User"})
    public void deleteMapEndpointResponds403ForbiddenWhenDoNotHaveAdminOrVerifiedAuthority() throws Exception {
		
        String mapName = "deleteMapSecurityTest";
        String url = "/api/v1/map";
        
    
        RequestBuilder deleteRequest = MockMvcRequestBuilders.delete(url)
        		.param("name", mapName);
    
        mockMvc.perform(deleteRequest)
        			.andDo(print())
        	      .andExpect(status().isForbidden());  
    }
}
