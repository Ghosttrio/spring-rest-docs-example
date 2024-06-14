package com.restdocs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class RestdocsApplicationTests {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation))
				.build();
	}

	@Test
	void restDocTest() throws Exception {
		this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("index"));
	}

	@Test
	void hypeLike() throws Exception {
		this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("index",
						// 	응답 링크를 설명하는 조각을 생성하도록 Spring REST 문서를 구성합니다. links에서 정적 메서드를 사용합니다
						relaxedLinks(
								linkWithRel("alpha").description("Link to the alpha resource"),
								linkWithRel("bravo").description("Link to the bravo resource"))));
	}

	@Test
	void testPayload() throws Exception {
		this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("index",
						responseFields(
						fieldWithPath("contact.email").description("The user's email address"),
						fieldWithPath("contact.name").description("The user's name"))));
	}

	@Test
	void reusing() throws Exception {
		FieldDescriptor[] contact = new FieldDescriptor[] {
				fieldWithPath("contact.email").description("이메일"),
				fieldWithPath("contact.name").description("이름")
		};

		this.mockMvc.perform(get("/")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("contact", responseFields(contact)));
	}

	@Test
	void beneathPathTest() throws Exception {
		this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("location",
						responseBody(beneathPath("contact"))));
	}

	@Test
	void queryParamTest() throws Exception {
		this.mockMvc.perform(get("/?req=11"))
				.andExpect(status().isOk())
				.andDo(document("queryParam",
						queryParameters(
						parameterWithName("req").description("리쿼스트 파라미터")
				)));
	}

	@Test
	void requestHeadersTest() throws Exception {
		this.mockMvc.perform(get("/head").header("test", "test"))
				.andExpect(status().isOk())
				.andDo(document("headers", requestHeaders(headerWithName("test").description("테스트 헤더"))));
	}


}
