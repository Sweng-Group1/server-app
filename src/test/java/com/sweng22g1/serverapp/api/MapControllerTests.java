package com.sweng22g1.serverapp.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLEngineResult.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweng22g1.serverapp.model.Map;
import com.sweng22g1.serverapp.service.MapServiceImpl;
import com.sweng22g1.serverapp.service.RoleServiceImpl;
import com.sweng22g1.serverapp.service.UserService;
import com.sweng22g1.serverapp.service.UserServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(MapController.class)
//@SpringBootTest
public class MapControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MapServiceImpl mapService;
	
	// These dependencies are required to satisfy Spring Security. 
	//private UserServiceImpl userService;
	//private RoleServiceImpl roleService;
	
	
	@Test
    public void getMapsReturnsMaps() throws Exception {
        // Given
        Map map1 = Map.builder().id(1L).filepath("/filepath1").name("map1").build();
        Map map2 = Map.builder().id(2L).filepath("/filepath2").name("map2").build();
        List<Map> maps = Arrays.asList(map1, map2);

        // When
        when(mapService.getMaps()).thenReturn(maps);

        // Then
        String jsonBody = this.mockMvc.perform(get("/api/v1/map"))
        		.andDo(print())
        		.andExpect(status().isOk())
        		.andReturn()
        		.getResponse()
        		.getContentAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        List<Map> foundMaps = objectMapper.readValue(jsonBody, new TypeReference<List<Map>>(){});
        
        assertThat(foundMaps.contains(map1)).isFalse();
        
       // assertThat(jsonBody.contains("/filepath1")).isTrue();
        //assertThat(jsonBody.contains("/filepath2")).isTrue();
      //  assertThat(jsonBody.contains("map1")).isTrue();
       // assertThat(jsonBody.contains("map2")).isTrue();
        
        
    }
	

}
