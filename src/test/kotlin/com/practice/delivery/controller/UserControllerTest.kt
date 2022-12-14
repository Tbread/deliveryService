package com.practice.delivery.controller


import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.delivery.dto.request.LoginRequestDto
import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.entity.Role
import com.practice.delivery.entity.User
import com.practice.delivery.jwt.JwtTokenProvider
import com.practice.delivery.repository.AdminUserRequestRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Autowired
    lateinit var adminUserRequestRepository: AdminUserRequestRepository

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

    /*
    ???????????? ???????????? ?????? ??????
    */

    @Test
    @Transactional
    @DisplayName("????????????-????????????-??????")
    @Throws(Exception::class)
    fun registerDefaultSuccess() {

        //give
        var registerRequest = RegisterUserRequestDto("test@default.com", "456", Role.DEFAULT)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(200))
            .andExpect(jsonPath("msg").value("??????????????? ?????????????????????."))
            .andExpect(jsonPath("email").value("test@default.com"))
    }

    @Test
    @Transactional
    @DisplayName("????????????-?????????-??????")
    @Throws(Exception::class)
    fun registerAdminSuccess() {

        //give
        var registerRequest = RegisterUserRequestDto("test@default.com", "456", Role.ADMIN)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(200))
            .andExpect(jsonPath("msg").value("??????????????? ?????????????????????."))
            .andExpect(jsonPath("email").value("test@default.com"))
    }

    @Test
    @Transactional
    @DisplayName("????????????-??????-???????????????")
    @Throws(Exception::class)
    fun registerFailNullEmail() {

        //give
        var registerRequest = RegisterUserRequestDto(null, "123", Role.DEFAULT)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("???????????? ?????? ?????? ????????????."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @Transactional
    @DisplayName("????????????-??????-????????????????????????")
    @Throws(Exception::class)
    fun registerFailWrongEmailForm() {

        //give
        var registerRequest = RegisterUserRequestDto("asd", "123", Role.DEFAULT)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("???????????? ?????? ????????????????????????."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @Transactional
    @DisplayName("????????????-??????-???????????? ??????")
    @Throws(Exception::class)
    fun registerFailNullPassword() {

        //give
        var registerRequest = RegisterUserRequestDto("test@email.com", null, Role.DEFAULT)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("??????????????? ?????? ?????? ????????????."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @Transactional
    @DisplayName("????????????-??????-?????? ??????")
    @Throws(Exception::class)
    fun registerFailNullRole() {

        //give
        var registerRequest = RegisterUserRequestDto("test@email.com", "123", null)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("??????????????? ?????? ?????? ????????????."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @Transactional
    @DisplayName("????????????-??????-?????????????????? ??????")
    @Throws(Exception::class)
    fun registerFailWrongRole() {

        //give
        var registerRequest = RegisterUserRequestDto("test@email.com", "123", Role.SUPERIOR_ADMIN)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("???????????? ?????? ???????????????."))
            .andExpect(jsonPath("email").value(null))
    }

    @Test
    @Transactional
    @DisplayName("????????????-??????-???????????? ?????????")
    @Throws(Exception::class)
    fun registerFailDubEmail() {

        //give
        var registerUserRequestDto = RegisterUserRequestDto("default@email.com", "123", Role.DEFAULT)

        //when
        var resultActions = mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("?????? ???????????? ??????????????????."))
            .andExpect(jsonPath("email").value(null))
    }

    /*
    ???????????? ???????????? ?????? ??????
    */

    /*
    ???????????? ????????? ?????? ??????
    */

    @Test
    @Transactional
    @DisplayName("?????????-??????-????????? ?????????")
    @Throws(Exception::class)
    fun registerFailWrongEmail() {

        //give
        var loginRequestDto = LoginRequestDto("no@email.com", "123")

        //when
        var resultActions = mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("????????? ????????? ?????? ?????????????????????."))
            .andExpect(jsonPath("token").isEmpty)
    }

    @Test
    @Transactional
    @DisplayName("?????????-??????-????????? ????????????")
    @Throws(Exception::class)
    fun registerFailWrongPassword() {

        //give
        var loginRequestDto = LoginRequestDto("default@email.com", "456")

        //when
        var resultActions = mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("????????? ????????? ?????? ?????????????????????."))
            .andExpect(jsonPath("token").isEmpty)
    }

    /*
    ???????????? ????????? ?????? ??????
    */

    /*
    ???????????? ????????? ?????? ?????? ?????? ?????? ??????
    */



    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ??????-??????-??????????????????")
    @Throws(Exception::class)
    fun viewRegisterSuperiorAdminRequestSuccess() {

        //when
        var resultActions = mockMvc.perform(
            get("/user/register-admin-request-list")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(200))
            .andExpect(jsonPath("msg").value("??????????????? ??????????????????."))
            .andExpect(jsonPath("simpleRequestList").isArray)
    }

    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ??????-??????-????????????")
    @Throws(Exception::class)
    fun viewRegisterAdminRequestFailLackAuthority() {

        //when
        var resultActions = mockMvc.perform(
            get("/user/register-admin-request-list")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(403))
            .andExpect(jsonPath("msg").value("????????? ???????????????."))
            .andExpect(jsonPath("simpleRequestList").isEmpty)
    }

    /*
    ???????????? ????????? ?????? ?????? ?????? ?????? ??????
    */

    /*
    ???????????? ????????? ?????? ?????? ?????? ?????? ??????
    */

    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ?????? - ??????")
    @Throws(Exception::class)
    fun registerAdminAcceptSuccess() {
        //given
        var registerRequest = RegisterUserRequestDto("test@default.com", "456", Role.ADMIN)
        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        )
        var id = adminUserRequestRepository.findByEmail("test@default.com")!!.id

        //when
        var resultActions = mockMvc.perform(
            get("/user/accept-admin-request/$id")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(200))
            .andExpect(jsonPath("msg").value("??????????????? ?????????????????????."))
            .andExpect(jsonPath("simpleRegisterAdminRequest").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ?????? - ?????? - ????????? ID")
    @Throws(Exception::class)
    fun registerAdminAcceptFailWrongId() {
        var id: Long = 99999999L
        //when
        var resultActions = mockMvc.perform(
            get("/user/accept-admin-request/$id")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("???????????? ?????? ?????? ID?????????."))
            .andExpect(jsonPath("simpleRegisterAdminRequest").isEmpty)
    }

    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ?????? - ?????? - ?????? ??????")
    @Throws(Exception::class)
    fun registerAdminAcceptFailLackAuthority() {
        //given
        var registerRequest = RegisterUserRequestDto("test@default.com", "456", Role.ADMIN)
        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        )
        var id = adminUserRequestRepository.findByEmail("test@default.com")!!.id

        //when
        var resultActions = mockMvc.perform(
            get("/user/accept-admin-request/$id")
                .header("Authorization", adminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(403))
            .andExpect(jsonPath("msg").value("????????? ???????????????."))
            .andExpect(jsonPath("simpleRegisterAdminRequest").isEmpty)
    }

    /*
    ???????????? ????????? ?????? ?????? ?????? ?????? ??????
    */

    /*
    ???????????? ????????? ?????? ?????? ?????? ?????? ??????
    */


    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ?????? - ??????")
    @Throws(Exception::class)
    fun registerAdminDenySuccess() {
        //given
        var registerRequest = RegisterUserRequestDto("test@default.com", "456", Role.ADMIN)
        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        )
        var id = adminUserRequestRepository.findByEmail("test@default.com")!!.id

        //when
        var resultActions = mockMvc.perform(
            get("/user/deny-admin-request/$id")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(200))
            .andExpect(jsonPath("msg").value("??????????????? ?????????????????????."))
            .andExpect(jsonPath("simpleRegisterAdminRequest").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ??????- ?????? - ????????? ID")
    @Throws(Exception::class)
    fun registerAdminDenyFailWrongId() {
        var id: Long = 999999999L
        //when
        var resultActions = mockMvc.perform(
            get("/user/deny-admin-request/$id")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(400))
            .andExpect(jsonPath("msg").value("???????????? ?????? ?????? ID?????????."))
            .andExpect(jsonPath("simpleRegisterAdminRequest").isEmpty)
    }

    @Test
    @Transactional
    @DisplayName("????????? ?????? ?????? ?????? - ?????? - ?????? ??????")
    @Throws(Exception::class)
    fun registerAdminDenyFailLackAuthority() {
        //given
        var registerRequest = RegisterUserRequestDto("test@default.com", "456", Role.ADMIN)
        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .accept(MediaType.APPLICATION_JSON)
        )
        var id = adminUserRequestRepository.findByEmail("test@default.com")!!.id

        //when
        var resultActions = mockMvc.perform(
            get("/user/deny-admin-request/$id")
                .header("Authorization", adminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        //then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(403))
            .andExpect(jsonPath("msg").value("????????? ???????????????."))
            .andExpect(jsonPath("simpleRegisterAdminRequest").isEmpty)
    }

    /*
    ???????????? ????????? ?????? ?????? ?????? ?????? ??????
    */

}