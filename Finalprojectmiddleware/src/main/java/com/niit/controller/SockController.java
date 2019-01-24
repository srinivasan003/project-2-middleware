package com.niit.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.niit.model.Chat;

@Controller
public class SockController {
private List<String> users=new ArrayList<String>();
private SimpMessagingTemplate simpMessagingTemplate;
@Autowired
public SockController(SimpMessagingTemplate simpMessagingTemplate){
	this.simpMessagingTemplate=simpMessagingTemplate;
}
@SubscribeMapping(value="/join/{username}")
public List<String> join(@DestinationVariable String username ){
	if(!users.contains(username))
		users.add(username);
	simpMessagingTemplate.convertAndSend("/topic/join",username);
	return users;
}
@MessageMapping(value="/chat")
public void chatReceived(Chat chat){
	System.out.println("chat received");
	System.out.println(chat.getFrom() + " " + chat.getTo() + " " + chat.getMessage());
	if(chat.getTo().equals("All"))//Group chat, if All users is selected,
		simpMessagingTemplate.convertAndSend("/queue/chats", chat);
	else{//private chat
		simpMessagingTemplate.convertAndSend("/queue/chats/"+chat.getFrom(), chat);
		simpMessagingTemplate.convertAndSend("/queue/chats/"+chat.getTo(), chat);
	}
	
}
}

