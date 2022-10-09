package com.practice.delivery.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.request.UpdateMenuRequestDto
import com.practice.delivery.entity.*
import com.practice.delivery.jwt.JwtTokenProvider
import com.practice.delivery.model.OptionMenu
import com.practice.delivery.repository.*
import org.assertj.core.api.Assertions
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

    @Autowired
    lateinit var menuRepository: MenuRepository

    @Autowired
    lateinit var menuOptionRepository: MenuOptionRepository

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
        store.owner = userRepository.findByEmail("business@email.com")
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

    @Test
    @Transactional
    @DisplayName("메뉴 추가-성공-추가메뉴있음")
    @Throws(Exception::class)
    fun addMenuSuccessExistOptionalMenu() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var optionMenuList = arrayListOf<OptionMenu>()
        optionMenuList.add(OptionMenu("optionMenu1",100))
        optionMenuList.add(OptionMenu("optionMenu2",500))
        optionMenuList.add(OptionMenu("optionMenu3",1300))
        var addMenuRequestDto =
            AddMenuRequestDto("testMenuName", "testMenuDesc", 3000, "http://testimg.com/img.png", false, optionMenuList)

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

    @Test
    @Transactional
    @DisplayName("메뉴 추가-실패-권한부족")
    @Throws(Exception::class)
    fun addMenuFailLackAuthority() {
        var optionMenuList = arrayListOf<OptionMenu>()
        optionMenuList.add(OptionMenu("optionMenu1",100))
        optionMenuList.add(OptionMenu("optionMenu2",500))
        optionMenuList.add(OptionMenu("optionMenu3",1300))
        var addMenuRequestDto =
            AddMenuRequestDto("testMenuName", "testMenuDesc", 3000, "http://testimg.com/img.png", false, optionMenuList)

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/store/add-menu")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addMenuRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("권한이 부족합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenu").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("메뉴 추가-실패-소유중인 가게가 존재하지 않음")
    @Throws(Exception::class)
    fun addMenuFailNullStore() {
        var optionMenuList = arrayListOf<OptionMenu>()
        optionMenuList.add(OptionMenu("optionMenu1",100))
        optionMenuList.add(OptionMenu("optionMenu2",500))
        optionMenuList.add(OptionMenu("optionMenu3",1300))
        var addMenuRequestDto =
            AddMenuRequestDto("testMenuName", "testMenuDesc", 3000, "http://testimg.com/img.png", false, optionMenuList)

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
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("소유중인 가게가 존재하지 않습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenu").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("메뉴 추가-실패-메뉴이름 누락")
    @Throws(Exception::class)
    fun addMenuFailNullMenuName() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var optionMenuList = arrayListOf<OptionMenu>()
        optionMenuList.add(OptionMenu("optionMenu1",100))
        optionMenuList.add(OptionMenu("optionMenu2",500))
        optionMenuList.add(OptionMenu("optionMenu3",1300))
        var addMenuRequestDto =
            AddMenuRequestDto(null, "testMenuDesc", 3000, "http://testimg.com/img.png", false, optionMenuList)

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
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("메뉴 이름은 필수 값입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenu").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("메뉴 추가-실패-메뉴가격 누락")
    @Throws(Exception::class)
    fun addMenuFailNullMenuPrice() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var optionMenuList = arrayListOf<OptionMenu>()
        optionMenuList.add(OptionMenu("optionMenu1",100))
        optionMenuList.add(OptionMenu("optionMenu2",500))
        optionMenuList.add(OptionMenu("optionMenu3",1300))
        var addMenuRequestDto =
            AddMenuRequestDto("testMenuName", "testMenuDesc", null, "http://testimg.com/img.png", false, optionMenuList)

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
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("메뉴 가격은 필수 값입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenu").hasJsonPath())
    }

    @Test
    @Transactional
    @DisplayName("메뉴 추가-실패-옵션 여부 누락")
    @Throws(Exception::class)
    fun addMenuFailNullHasOption() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var optionMenuList = arrayListOf<OptionMenu>()
        optionMenuList.add(OptionMenu("optionMenu1",100))
        optionMenuList.add(OptionMenu("optionMenu2",500))
        optionMenuList.add(OptionMenu("optionMenu3",1300))
        var addMenuRequestDto =
            AddMenuRequestDto("testMenuName", "testMenuDesc", 15000, "http://testimg.com/img.png", null, optionMenuList)

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
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("옵션여부는 필수 값입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenu").hasJsonPath())
    }

    /*
    여기까지 메뉴 추가 로직 관련
    */

    /*
    여기부터 메뉴 조회 로직 관련
    */

    @Test
    @Transactional
    @DisplayName("메뉴 조회-성공")
    @Throws(Exception::class)
    fun showMenuListSuccess() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var optionMenuList = arrayListOf<OptionMenu>()
        optionMenuList.add(OptionMenu("optionMenu1",100))
        optionMenuList.add(OptionMenu("optionMenu2",500))
        optionMenuList.add(OptionMenu("optionMenu3",1300))
        var addMenuRequestDto =
            AddMenuRequestDto("testMenuName", "testMenuDesc", 15000, "http://testimg.com/img.png", true, optionMenuList)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/store/add-menu")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addMenuRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        )
        var id = store.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/show-menu-list/$id")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 불러왔습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenuList").isArray)
    }

    @Test
    @Transactional
    @DisplayName("메뉴 조회-실패-잘못된 가게 ID")
    @Throws(Exception::class)
    fun showMenuListFailWrongStoreId() {

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/store/show-menu-list/894328568")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("존재하지 않는 가게 ID입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("simpleMenuList").isEmpty)
    }

    /*
    여기까지 메뉴 조회 로직 관련
    */

    /*
    여기부터 메뉴 삭제 로직 관련
     */

    @Test
    @Transactional
    @DisplayName("메뉴 제거-성공-옵션메뉴없음")
    @Throws(Exception::class)
    fun removeMenuSuccessNullOptionalMenu() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var menu = Menu()
        menu.store = store
        menu.menuName = "testMenuName"
        menu.price = 100
        menuRepository.save(menu)
        var id = menu.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/store/remove-menu/$id")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 삭제하였습니다."))
    }

    @Test
    @Transactional
    @DisplayName("메뉴 제거-성공-옵션메뉴있음")
    @Throws(Exception::class)
    fun removeMenuSuccessExistOptionalMenu() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var menu = Menu()
        menu.store = store
        menu.menuName = "testMenuName"
        menu.price = 100
        menu.thisHasOption = true
        menuRepository.save(menu)
        var subMenu = Menu()
        subMenu.store = store
        subMenu.menuName = "optionMenu"
        subMenu.price = 100
        subMenu.thisIsOption = true
        menuRepository.save(subMenu)
        var menuOption = MenuOption()
        menuOption.topMenu = menu
        menuOption.subMenu = subMenu
        menuOptionRepository.save(menuOption)
        var id = menu.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/store/remove-menu/$id")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 삭제하였습니다."))
    }

    @Test
    @Transactional
    @DisplayName("메뉴 제거-성공-옵션메뉴")
    @Throws(Exception::class)
    fun removeMenuSuccessIsOptionalMenu() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var menu = Menu()
        menu.store = store
        menu.menuName = "testMenuName"
        menu.price = 100
        menu.thisHasOption = true
        menuRepository.save(menu)
        var subMenu = Menu()
        subMenu.store = store
        subMenu.menuName = "optionMenu"
        subMenu.price = 100
        subMenu.thisIsOption = true
        menuRepository.save(subMenu)
        var menuOption = MenuOption()
        menuOption.topMenu = menu
        menuOption.subMenu = subMenu
        menuOptionRepository.save(menuOption)
        var id = subMenu.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/store/remove-menu/$id")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 삭제하였습니다."))
    }

    @Test
    @Transactional
    @DisplayName("메뉴 제거-실패-권한부족")
    @Throws(Exception::class)
    fun removeMenuFailLackAuthority() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var menu = Menu()
        menu.store = store
        menu.menuName = "testMenuName"
        menu.price = 100
        menu.thisHasOption = true
        menuRepository.save(menu)
        var subMenu = Menu()
        subMenu.store = store
        subMenu.menuName = "optionMenu"
        subMenu.price = 100
        subMenu.thisIsOption = true
        menuRepository.save(subMenu)
        var menuOption = MenuOption()
        menuOption.topMenu = menu
        menuOption.subMenu = subMenu
        menuOptionRepository.save(menuOption)
        var id = subMenu.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/store/remove-menu/$id")
                .header("Authorization", defaultToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("권한이 부족합니다."))
    }

    @Test
    @Transactional
    @DisplayName("메뉴 제거-실패-잘못된 ID")
    @Throws(Exception::class)
    fun removeMenuFailWrongId() {
        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/store/remove-menu/999999999999")
                .header("Authorization", businessToken)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("존재하지 않는 메뉴 ID입니다."))
    }

    /*
    여기까지 메뉴 삭제 로직 관련
    */

    /*
    여기부터 메뉴 업데이트 로직 관련
    */

    @Test
    @Transactional
    @DisplayName("메뉴 업데이트-성공")
    @Throws(Exception::class)
    fun updateMenuSuccess() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var menu = Menu()
        menu.store = store
        menu.menuName = "testMenuName"
        menu.price = 100
        menu.thisHasOption = true
        menuRepository.save(menu)
        var subMenu = Menu()
        subMenu.store = store
        subMenu.menuName = "optionMenu"
        subMenu.price = 100
        subMenu.thisIsOption = true
        menuRepository.save(subMenu)
        var menuOption = MenuOption()
        menuOption.topMenu = menu
        menuOption.subMenu = subMenu
        menuOptionRepository.save(menuOption)
        var updateMenuRequestDto = UpdateMenuRequestDto("modifiedUpdateMenuName",null,null,null,null)
        var id = menu.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/store/update-menu/$id")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMenuRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("성공적으로 변경하였습니다."))
    }

    @Test
    @Transactional
    @DisplayName("메뉴 업데이트-실패-권한부족")
    @Throws(Exception::class)
    fun updateMenuFailLackAuthority() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var menu = Menu()
        menu.store = store
        menu.menuName = "testMenuName"
        menu.price = 100
        menu.thisHasOption = true
        menuRepository.save(menu)
        var subMenu = Menu()
        subMenu.store = store
        subMenu.menuName = "optionMenu"
        subMenu.price = 100
        subMenu.thisIsOption = true
        menuRepository.save(subMenu)
        var menuOption = MenuOption()
        menuOption.topMenu = menu
        menuOption.subMenu = subMenu
        menuOptionRepository.save(menuOption)
        var updateMenuRequestDto = UpdateMenuRequestDto("modifiedUpdateMenuName",null,null,null,null)
        var id = menu.id

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/store/update-menu/$id")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMenuRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("권한이 부족합니다."))
    }

    @Test
    @Transactional
    @DisplayName("메뉴 업데이트-실패-잘못된ID")
    @Throws(Exception::class)
    fun updateMenuFailWrondId() {
        var store = Store()
        store.storeName = "testStoreName"
        store.owner = userRepository.findByEmail("business@email.com")
        storeRepository.save(store)
        var menu = Menu()
        menu.store = store
        menu.menuName = "testMenuName"
        menu.price = 100
        menu.thisHasOption = true
        menuRepository.save(menu)
        var subMenu = Menu()
        subMenu.store = store
        subMenu.menuName = "optionMenu"
        subMenu.price = 100
        subMenu.thisIsOption = true
        menuRepository.save(subMenu)
        var menuOption = MenuOption()
        menuOption.topMenu = menu
        menuOption.subMenu = subMenu
        menuOptionRepository.save(menuOption)
        var updateMenuRequestDto = UpdateMenuRequestDto("modifiedUpdateMenuName",null,null,null,null)

        //when
        var resultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/store/update-menu/987978979797999")
                .header("Authorization", businessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMenuRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        //then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("msg").value("존재하지 않는 메뉴 ID입니다."))
    }

    /*
    여기까지 메뉴 업데이트 로직 관련
    */

    /*
    여기부터  로직 관련
    */

}