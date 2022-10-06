package com.practice.delivery.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.entity.Role
import com.practice.delivery.entity.User
import com.practice.delivery.jwt.JwtTokenProvider
import com.practice.delivery.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import javax.transaction.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreControllerTest {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var mockMvc: MockMvc

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var userRepository: UserRepository

    var defaultToken = ""
    var businessToken = ""
    var adminToken = ""
    var superiorAdminToken = ""

    @BeforeEach
    fun setup() {
        var defaultUser = User()
        var businessUser = User()
        var adminUser = User()
        var superiorAdminUser = User()
        defaultUser.email = "default@email.com"
        businessUser.email = "business@email.com"
        adminUser.email = "admin@email.com"
        superiorAdminUser.email = "superiorAdmin@email.com"
        defaultUser.password = "123"
        businessUser.password = "123"
        adminUser.password = "123"
        superiorAdminUser.password = "123"
        defaultUser.role = Role.DEFAULT
        businessUser.role = Role.BUSINESS
        adminUser.role = Role.ADMIN
        superiorAdminUser.role = Role.SUPERIOR_ADMIN
        userRepository.save(defaultUser)
        userRepository.save(businessUser)
        userRepository.save(adminUser)
        userRepository.save(superiorAdminUser)
        defaultToken = jwtTokenProvider.createToken(defaultUser.email, defaultUser.id)
        businessToken = jwtTokenProvider.createToken(businessUser.email, businessUser.id)
        adminToken = jwtTokenProvider.createToken(adminUser.email, adminUser.id)
        superiorAdminToken = jwtTokenProvider.createToken(superiorAdminUser.email, superiorAdminUser.id)
    }


    @Test
    @Transactional
    @DisplayName("가게 등록 신청 성공")
    @Throws(Exception::class)
    fun registerStoreSuccess() {

        //give
        var registerRequest = RegisterStoreRequestDto("testName", "testDesc", "http://test.com/test.png", 5000)

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/store/register")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 신청하였습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("storeName").value("testName"))
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 실패 - 권한부족")
    @Throws(Exception::class)
    fun registerStoreFailNoLogin() {

        //give
        var registerRequest = RegisterStoreRequestDto("testName", "testDesc", "http://test.com/test.png", 5000)

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/store/register")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("권한이 부족합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("storeName").isEmpty)
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 실패 - 가게명 누락")
    @Throws(Exception::class)
    fun registerStoreFailNullStoreName() {

        //give
        var registerRequest = RegisterStoreRequestDto(null,"","",0)

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/store/register")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("가게 이름은 필수 값입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("storeName").isEmpty)
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 실패 - 중복 신청")
    @Throws(Exception::class)
    fun registerStoreFailDubApply() {

        //give
        var registerRequest = RegisterStoreRequestDto("testName","","",0)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/store/register")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        )

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/store/register")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("이미 등록되었거나 신청 대기 상태입니다"))
            .andExpect(MockMvcResultMatchers.jsonPath("storeName").isEmpty)
    }



}