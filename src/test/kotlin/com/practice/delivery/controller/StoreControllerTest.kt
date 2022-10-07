package com.practice.delivery.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.entity.Role
import com.practice.delivery.entity.Store
import com.practice.delivery.entity.StoreRegisterRequest
import com.practice.delivery.entity.User
import com.practice.delivery.jwt.JwtTokenProvider
import com.practice.delivery.repository.StoreRegisterRequestRepository
import com.practice.delivery.repository.StoreRepository
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

    @Autowired
    lateinit var storeRegisterRequestRepository: StoreRegisterRequestRepository

    @Autowired
    lateinit var storeRepository: StoreRepository

    var defaultToken = ""
    var businessToken = ""
    var adminToken = ""
    var superiorAdminToken = ""

    var defaultUser = User()
    var businessUser = User()
    var adminUser = User()
    var superiorAdminUser = User()

    @BeforeEach
    fun setup() {
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
    여기부터 가게 등록 로직 관련
    */

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
        var registerRequest = RegisterStoreRequestDto(null, "", "", 0)

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
        var registerRequest = RegisterStoreRequestDto("testName", "", "", 0)
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

    /*
    여기까지 가게 등록 로직 관련
    */

    /*
    여기부터 가게 등록 신청 조회 로직 관련
    */


    @Test
    @Transactional
    @DisplayName("가게 등록 신청 조회-성공-최상위관리자")
    @Throws(Exception::class)
    fun viewRegisterStoreRequestSuccessSuperiorAdmin() {

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/register-store-request-list")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 불러왔습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRequestList").isArray)
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 조회-성공-일반관리자")
    @Throws(Exception::class)
    fun viewRegisterStoreRequestSuccessAdmin() {

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/register-store-request-list")
                .header("Authorization", adminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 불러왔습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRequestList").isArray)
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 조회-실패-권한부족")
    @Throws(Exception::class)
    fun viewRegisterStoreRequestFailLackAuthority() {

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/register-store-request-list")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("권한이 부족합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRequestList").isEmpty)
    }

    /*
    여기까지 가게 등록 신청 조회 로직 관련
    */

    /*
    여기부터 가게 등록 신청 수락 로직 관련
    */

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 수락-성공")
    @Throws(Exception::class)
    fun acceptRegisterStoreSuccess() {
        var storeRegisterRequest = StoreRegisterRequest()
        storeRegisterRequest.storeName = "testName"
        storeRegisterRequest.storeDesc = "testDesc"
        storeRegisterRequest.storeImgSrc = "testSrc"
        storeRegisterRequest.status = StoreRegisterRequest.Status.AWAIT
        storeRegisterRequest.owner = userRepository.findByEmail("business@email.com")
        storeRegisterRequestRepository.save(storeRegisterRequest)
        var id = storeRegisterRequest.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/accept-register-request/$id")
                .header("Authorization", adminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 수락하였습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRegisterStoreRequest").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 수락-실패-권한 부족")
    @Throws(Exception::class)
    fun acceptRegisterStoreFailLackAuthority() {
        var storeRegisterRequest = StoreRegisterRequest()
        storeRegisterRequest.storeName = "testName"
        storeRegisterRequest.storeDesc = "testDesc"
        storeRegisterRequest.storeImgSrc = "testSrc"
        storeRegisterRequest.status = StoreRegisterRequest.Status.AWAIT
        storeRegisterRequest.owner = userRepository.findByEmail("business@email.com")
        storeRegisterRequestRepository.save(storeRegisterRequest)
        var id = storeRegisterRequest.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/accept-register-request/$id")
                .header("Authorization", defaultToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("권한이 부족합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRegisterStoreRequest").isEmpty)
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 수락-실패-잘못된ID")
    @Throws(Exception::class)
    fun acceptRegisterStoreFailWrongId() {

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/accept-register-request/93542")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("존재하지 않는 요청 ID입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRegisterStoreRequest").isEmpty)
    }

    /*
    여기까지 가게 등록 신청 수락 로직 관련
    */

    /*
    여기부터 가게 등록 신청 거절 로직 관련
    */

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 거절-성공")
    @Throws(Exception::class)
    fun denyRegisterStoreSuccess() {
        var storeRegisterRequest = StoreRegisterRequest()
        storeRegisterRequest.storeName = "testName"
        storeRegisterRequest.storeDesc = "testDesc"
        storeRegisterRequest.storeImgSrc = "testSrc"
        storeRegisterRequest.status = StoreRegisterRequest.Status.AWAIT
        storeRegisterRequest.owner = userRepository.findByEmail("business@email.com")
        storeRegisterRequestRepository.save(storeRegisterRequest)
        var id = storeRegisterRequest.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/deny-register-request/$id")
                .header("Authorization", adminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 거절하였습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRegisterStoreRequest").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 거절-실패-권한 부족")
    @Throws(Exception::class)
    fun denyRegisterStoreFailLackAuthority() {
        var storeRegisterRequest = StoreRegisterRequest()
        storeRegisterRequest.storeName = "testName"
        storeRegisterRequest.storeDesc = "testDesc"
        storeRegisterRequest.storeImgSrc = "testSrc"
        storeRegisterRequest.status = StoreRegisterRequest.Status.AWAIT
        storeRegisterRequest.owner = userRepository.findByEmail("business@email.com")
        storeRegisterRequestRepository.save(storeRegisterRequest)
        var id = storeRegisterRequest.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/deny-register-request/$id")
                .header("Authorization", defaultToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("권한이 부족합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRegisterStoreRequest").isEmpty)
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 신청 거절-실패-잘못된ID")
    @Throws(Exception::class)
    fun denyRegisterStoreFailWrongId() {

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/accept-register-request/93542")
                .header("Authorization", superiorAdminToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("존재하지 않는 요청 ID입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleRegisterStoreRequest").isEmpty)
    }

    /*
    여기까지 가게 등록 신청 거절 로직 관련
    */

    /*
    여기부터 메뉴 추가 로직 관련
    */

    @Test
    @Transactional
    @DisplayName("메뉴 추가-성공-추가메뉴없음")
    @Throws(Exception::class)
    fun addMenuSuccess() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = businessUser
        storeRepository.save(store)
        var addMenuRequestDto =
            AddMenuRequestDto("testMenuName", "testMenuDesc", 3000, "http://testimg.com/img.png", false, null)

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/store/add-menu")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addMenuRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 메뉴를 추가하였습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenu").hasJsonPath())
    }


}