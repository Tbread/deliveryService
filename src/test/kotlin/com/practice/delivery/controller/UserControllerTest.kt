package com.practice.delivery.controller


import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.practice.delivery.dto.request.LoginRequestDto
import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.entity.Role
import com.practice.delivery.entity.User
import com.practice.delivery.jwt.JwtTokenProvider
import com.practice.delivery.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var mockMvc: MockMvc

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    @Throws(Exception::class)
    fun setup(){
        var defaultUser = User()
        var businessUser = User()
        var adminUser = User()
        defaultUser.email = "default@email.com"
        businessUser.email = "business@email.com"
        adminUser.email = "admin@email.com"
        defaultUser.password = "123"
        businessUser.password = "123"
        adminUser.password = "123"
        defaultUser.role = Role.ROLE_DEFAULT
        businessUser.role = Role.ROLE_BUSINESS
        adminUser.role = Role.ROLE_ADMIN
        userRepository.save(defaultUser)
        userRepository.save(businessUser)
        userRepository.save(adminUser)
        val defaultToken = jwtTokenProvider.createToken(defaultUser.email,defaultUser.id)
        val businessToken = jwtTokenProvider.createToken(businessUser.email,businessUser.id)
        val adminToken = jwtTokenProvider.createToken(adminUser.email,adminUser.id)
    }


    @Test
    @DisplayName("01.회원가입-일반회원-정상")
    @Throws(Exception::class)
    fun registerDefaultSuccess(){

        //give
        var registerRequest = RegisterUserRequestDto("test@default.com","456",Role.ROLE_DEFAULT)

        //when
        var resultActions = mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(200))
            .andExpect(jsonPath("msg").value("성공적으로 가입하였습니다."))
            .andExpect(jsonPath("email").value("test@default.com"))
    }

    @Test
    @DisplayName("02.회원가입-어드민-정상")
    @Throws(Exception::class)
    fun registerAdminSuccess(){

        //give
        var registerRequest = RegisterUserRequestDto("test@default.com","456",Role.ROLE_ADMIN)

        //when
        var resultActions = mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(200))
            .andExpect(jsonPath("msg").value("성공적으로 신청하였습니다."))
            .andExpect(jsonPath("email").value("test@default.com"))
    }

    @Test
    @DisplayName("03.회원가입-실패-이메일누락")
    @Throws(Exception::class)
    fun registerFailNullEmail(){

        //give
        var registerRequest = RegisterUserRequestDto(null,"123",Role.ROLE_DEFAULT)

        //when
        var resultActions = mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("이메일은 필수 입력 값입니다."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @DisplayName("04.회원가입-실패-비밀번호 누락")
    @Throws(Exception::class)
    fun registerFailNullPassword(){

        //give
        var registerRequest = RegisterUserRequestDto("test@email.com",null,Role.ROLE_DEFAULT)

        //when
        var resultActions = mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("비밀번호는 필수 입력 값입니다."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @DisplayName("05.회원가입-실패-역할 누락")
    @Throws(Exception::class)
    fun registerFailNullRole(){

        //give
        var registerRequest = RegisterUserRequestDto("test@email.com","123",null)

        //when
        var resultActions = mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("회원종류는 필수 입력 값입니다."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @DisplayName("06.회원가입-실패-최상위관리자 요청")
    @Throws(Exception::class)
    fun registerFailWrongRole(){

        //give
        var registerRequest = RegisterUserRequestDto("test@email.com","123",Role.ROLE_SUPERIOR_ADMIN)

        //when
        var resultActions = mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("유효하지 않은 요청입니다."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @DisplayName("07.회원가입-실패-존재하는 이메일")
    @Throws(Exception::class)
    fun registerFailDubEmail(){

        //give
        var registerUserRequestDto = RegisterUserRequestDto("default@email.com","123",Role.ROLE_DEFAULT)

        //when
        var resultActions = mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerUserRequestDto))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("이미 존재하는 이메일입니다."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @DisplayName("09.로그인-실패-잘못된 이메일")
    @Throws(Exception::class)
    fun registerFailWrongEmail(){

        //give
        var loginRequestDto = LoginRequestDto("no@email.com","123")

        //when
        var resultActions = mockMvc.perform(post("/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequestDto))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("잘못된 이메일 또는 패스워드입니다."))
            .andExpect(jsonPath("token").isEmpty)
    }

    @Test
    @DisplayName("10.로그인-실패-잘못된 비밀번호")
    @Throws(Exception::class)
    fun registerFailWrongPassword(){

        //give
        var loginRequestDto = LoginRequestDto("default@email.com","456")

        //when
        var resultActions = mockMvc.perform(post("/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequestDto))
            .accept(MediaType.APPLICATION_JSON)).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("잘못된 이메일 또는 패스워드입니다."))
            .andExpect(jsonPath("token").isEmpty)
    }


}