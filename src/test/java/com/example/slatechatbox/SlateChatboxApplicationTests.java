package com.example.slatechatbox;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.slatechatbox.account.AccountController;
import com.example.slatechatbox.message.MessageController;

@SpringBootTest
class SlateChatboxApplicationTests {

	@Autowired
	private AccountController accountController;

	@Autowired
	private MessageController messageController;

	@Test
	void contextLoads() {
		assertThat(accountController).isNotNull();
		assertThat(messageController).isNotNull();
	}

}