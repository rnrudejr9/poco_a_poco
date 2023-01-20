package teamproject.pocoapoco.controller;

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
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.CrewEntityFixture;
import teamproject.pocoapoco.service.CrewService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrewController.class)
class CrewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CrewService crewService;

    CrewRequest request = new CrewRequest("strict", "title", "content", 10);



    @Nested
    @DisplayName("크루 게시글 등록")
    class AddCrew {

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 등록 성공")
        void addCrew() throws Exception {

            when(crewService.addCrew(any(), any()))
                    .thenReturn(new CrewResponse("Crew 등록 완료", 1L));

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

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 등록 실패1 :  해당 아이디 없음")
        void addCrew2() throws Exception {

            when(crewService.addCrew(any(), any()))
                    .thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            mockMvc.perform(post("/api/v1/crews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " " + ErrorCode.USERID_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 등록 실패2 : 해당 게시글 없음")
        void addCrew3() throws Exception {

            when(crewService.addCrew(any(), any()))
                    .thenThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            mockMvc.perform(post("/api/v1/crews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.CREW_NOT_FOUND.name() + " " + ErrorCode.CREW_NOT_FOUND.getMessage()))
                    .andDo(print());
        }
    }


    @Nested
    @DisplayName("크루 게시글 수정")
    class UpdateCrew {

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 수정 성공")
        void update1() throws Exception {

            when(crewService.updateCrew(any(), any(), any()))
                    .thenReturn(new CrewResponse("Crew 수정 완료", 1L));

            mockMvc.perform(post("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("Crew 수정 완료"))
                    .andExpect(jsonPath("$.result.crewId").value("1"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 수정 실패1 :  해당 아이디 없음")
        void update2() throws Exception {

            when(crewService.updateCrew(any(), any(), any()))
                    .thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            mockMvc.perform(post("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " " + ErrorCode.USERID_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 수정 실패2 : 해당 게시글 없음")
        void update3() throws Exception {

            when(crewService.updateCrew(any(), any(), any()))
                    .thenThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            mockMvc.perform(post("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.CREW_NOT_FOUND.name() + " " + ErrorCode.CREW_NOT_FOUND.getMessage()))
                    .andDo(print());

        }

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 수정 실패3 : 권한 없음")
        void update4() throws Exception {

            when(crewService.updateCrew(any(), any(), any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(post("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.INVALID_PERMISSION.name() + " " + ErrorCode.INVALID_PERMISSION.getMessage()))
                    .andDo(print());
        }
    }


    @Nested
    @DisplayName("크루 게시글 삭제")
    class DeleteCrew {

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 삭제 성공")
        void deleteCrew1() throws Exception {

            when(crewService.deleteCrew(any(), any()))
                    .thenReturn(new CrewResponse("Crew 삭제 완료", 1L));

            mockMvc.perform(delete("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("Crew 삭제 완료"))
                    .andExpect(jsonPath("$.result.crewId").value("1"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 삭제 실패1 :  해당 아이디 없음")
        void deleteCrew2() throws Exception {

            when(crewService.deleteCrew(any(), any()))
                    .thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            mockMvc.perform(delete("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " " + ErrorCode.USERID_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 삭제 실패2 : 해당 게시글 없음")
        void deleteCrew3() throws Exception {

            when(crewService.deleteCrew(any(), any()))
                    .thenThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            mockMvc.perform(delete("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.CREW_NOT_FOUND.name() + " " + ErrorCode.CREW_NOT_FOUND.getMessage()))
                    .andDo(print());

        }

        @Test
        @WithMockUser
        @DisplayName("크루 게시글 삭제 실패3 : 권한 없음")
        void deleteCrew4() throws Exception {

            when(crewService.deleteCrew(any(), any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(delete("/api/v1/crews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.INVALID_PERMISSION.name() + " " + ErrorCode.INVALID_PERMISSION.getMessage()))
                    .andDo(print());
        }
    }

    // 미완성
//    @Nested
//    @DisplayName("크루 게시글 상세조회")
//    class DetailCrew {
//
//        @Test
//        @WithMockUser
//        @DisplayName("크루 게시글 상세조회 성공")
//        void detailCrew1() throws Exception {
//
//            Long crewId = 1L;
//            CrewDetailResponse crewDetailResponse = CrewDetailResponse.fromEntity(CrewEntityFixture.get(crewId));
//
//            when(crewService.detailCrew(any(), any()))
//                    .thenReturn(crewDetailResponse);
//
//
//            mockMvc.perform(get("/api/v1/crews/1")
//                            .with(csrf())
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsBytes(request)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
//                    .andDo(print());
//        }
//    }

    @Test
    void detailCrew() {
    }

    @Test
    void getAllCrews() {
    }

    @Test
    void getAllCrewWithStrict() {
    }
}