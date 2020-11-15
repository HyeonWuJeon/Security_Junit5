package com.foodPro.demo.ApiTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodPro.demo.member.domain.Member;
import com.foodPro.demo.member.dto.MemberDto;
import com.foodPro.demo.member.repository.MemberRepository;
import com.foodPro.demo.member.service.MemberService;
import com.foodPro.demo.member.service.MemberServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest
@Transactional
public class MemberApiTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
//    @Qualifier("memberService")
    MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
//                .apply(springSecurity())
                .build();
    }

    /**
     * Exception UNIT TEST
     * 1. get error
     * @throws Exception
     */
    @Test
    public void GlobalException() throws Exception {
        //given
        Long id = 1L;
        given(memberRepository.findById(id))
                .willReturn(null);

        //when
        webTestClient.get().uri("/member/settings/1").exchange()
                .expectStatus().is4xxClientError();
    }

    /**
     * 2. 이메일 중복
     * @throws Exception
     */
    @Test
    public void 중복() throws Exception{
        //given
        String email = "admin5@gmail.com";
        Member member = new Member();
        member.setEmail(email);
        em.persist(member);
        em.flush();
        String url = "http://localhost:" + port + "/api/member/checkEmail";
        //when
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(MemberDto.Request.builder().email(email).build())))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
