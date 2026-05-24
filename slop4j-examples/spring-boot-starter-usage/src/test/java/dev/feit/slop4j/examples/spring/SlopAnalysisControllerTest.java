package dev.feit.slop4j.examples.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SlopAnalysisControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void analyzesPostedText() throws Exception {
		String body = mockMvc
				.perform(post("/slop/analyze").contentType("text/plain")
						.content("We leverage agentic AI to unlock seamless enterprise-grade transformation."))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		assertThat(body).contains("slopScore");
		assertThat(body).contains("verdict");
		assertThat(body).contains("findings");
	}
}
