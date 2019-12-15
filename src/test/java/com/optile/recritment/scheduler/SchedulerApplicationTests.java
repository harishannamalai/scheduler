package com.optile.recritment.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optile.recritment.scheduler.model.JobDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Timestamp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SchedulerApplicationTests {

	@Autowired
	MockMvc mvc;


	@Test
	void contextLoads() {

	}

	@Test
	void CreateSampleJob_ScheduledNow() throws Exception {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setSchedule(new Timestamp(System.currentTimeMillis()));
		jobDefinition.setJobName("Mock Job");
		jobDefinition.setCmd("cmd.exe /C echo Mock Test!");
		jobDefinition.setPriority((short) 0);
		jobDefinition.setProcessor("winShell");
		RequestBuilder builder = MockMvcRequestBuilders
				.post("/api/jobs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(jobDefinition));

		MockHttpServletResponse response = mvc.perform(builder)
				.andReturn()
				.getResponse();

		assert (response.getStatus() == 200);
	}

	@Test
	void CreateSampleJob_ScheduledInFuture() throws Exception {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setSchedule(new Timestamp(System.currentTimeMillis() + (5 * 60 * 60 * 1000)));
		jobDefinition.setJobName("Mock Job Future");
		jobDefinition.setCmd("cmd.exe /C echo Mock Test!");
		jobDefinition.setPriority((short) 0);
		jobDefinition.setProcessor("winShell");
		RequestBuilder builder = MockMvcRequestBuilders
				.post("/api/jobs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(jobDefinition));

		MockHttpServletResponse response = mvc.perform(builder)
				.andReturn()
				.getResponse();

		assert (response.getStatus() == 200);
	}

}
