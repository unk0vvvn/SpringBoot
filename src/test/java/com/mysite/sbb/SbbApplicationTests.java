package com.mysite.sbb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.question.dto.QuestionForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
import com.mysite.sbb.user.dto.UserResponse;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

@SpringBootTest
class SbbApplicationTests {
	@Autowired
	private QuestionService qService;
	
	@Autowired
	private UserRepository uRepo;
	
	@Test
	void testJpa() {
		
		for(int i=0; i<100; ++i) {
			QuestionForm qForm = QuestionForm.builder()
					.subject("테스트 데이터 ["+i+"]")
					.content("test " + i)
					.build();
			qService.create(qForm, "admin");
		}
	}

}


