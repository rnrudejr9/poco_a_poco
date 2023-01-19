package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import teamproject.pocoapoco.domain.dto.crew.CrewAddRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewAddResponse;
import teamproject.pocoapoco.service.CrewService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrewController.class)
class CrewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CrewService crewService;


    @Nested
    @DisplayName("Crew 게시글 등록")
    class AddCrew{

        CrewAddRequest request = new CrewAddRequest("strict", "title", "content", 10);

        @Test
        @WithMockUser
        @DisplayName("Crew 게시글 등록 성공")
        void addCrew() throws Exception {


            when(crewService.addCrew(any(), any()))
                    .thenReturn(new CrewAddResponse("Crew 등록 완료", 1L));

            mockMvc.perform(post("/api/v1/crews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("Crew 등록 완료"))
                    .andExpect(jsonPath("$.result.crewId").value("1"))
                    .andDo(print());
        }
    }

}