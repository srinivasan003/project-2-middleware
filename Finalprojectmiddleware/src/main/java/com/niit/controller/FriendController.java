package com.niit.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.FriendDao;
import com.niit.dao.UserDao;
import com.niit.model.ErrorClazz;
import com.niit.model.Friends;
import com.niit.model.User;

@RestController
public class FriendController {
	@Autowired
	private FriendDao friendDao;
		@Autowired
	private UserDao userDao;
		@RequestMapping(value="/suggestedusers",method=RequestMethod.GET)
		public ResponseEntity<?> getAllSuggestedUsers(HttpSession session){
			String email=(String)session.getAttribute("email");
			//NOT LOGGED IN
			if(email==null){
				ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
			}
			List<User> suggestedUsers=friendDao.getAllSuggestedUsers(email);
			
			return new ResponseEntity<List<User>>(suggestedUsers,HttpStatus.OK);
		}
		@RequestMapping(value="/friendrequest",method=RequestMethod.POST)
		public ResponseEntity<?> friendRequest(@RequestBody User toId,HttpSession session){
			String email=(String)session.getAttribute("email");
			//NOT LOGGED IN
			if(email==null){
				ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
			}
			Friends friend=new Friends();
			friend.setToId(toId);
			friend.setStatus('P');
			//logged in user email id is 'email'
			friend.setFromId(userDao.getUser(email));
			
			friendDao.friendRequest(friend);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		@RequestMapping(value="/pendingrequests",method=RequestMethod.GET)
		public ResponseEntity<?> pendingRequests(HttpSession session){
			String email=(String)session.getAttribute("email");
			//NOT LOGGED IN
			if(email==null){
				ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
			}
			List<Friends> pendingRequests=friendDao.pendingRequests(email);
			return new ResponseEntity<List<Friends>>(pendingRequests,HttpStatus.OK);
		}
		@RequestMapping(value="/acceptfriendrequest",method=RequestMethod.PUT)
		public ResponseEntity<?> acceptFriendRequest(@RequestBody Friends friend,HttpSession session){
			String email=(String)session.getAttribute("email");
			//NOT LOGGED IN
			if(email==null){
				ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
			}
			friendDao.acceptFriendRequest(friend);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		@RequestMapping(value="/deletefriendrequest",method=RequestMethod.PUT)
		public ResponseEntity<?> deleteFriendRequest(@RequestBody Friends   friend,HttpSession session){
			String email=(String)session.getAttribute("email");
			//NOT LOGGED IN
			if(email==null){
				ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
			}
			friendDao.deleteFriendRequest(friend);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		@RequestMapping(value="/listoffriends",method=RequestMethod.GET)
		public ResponseEntity<?> listOfFriends(HttpSession session){
			String email=(String)session.getAttribute("email");
			//NOT LOGGED IN
			if(email==null){
				ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
			}
			List<User> friends=friendDao.listOfFriends(email);
			return new ResponseEntity<List<User>>(friends,HttpStatus.OK);
		}

}
