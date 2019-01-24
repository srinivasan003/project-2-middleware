package com.niit.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.UserDao;
import com.niit.model.ErrorClazz;
import com.niit.model.User;

@RestController  
public class UserController {
	@Autowired
private UserDao userDao;
	
	public UserController(){
		System.out.println("userController bean is created");
	}
	
	//ResponseEntity has two properties - Data and Status Code
	// ? means Any type of data 
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public   ResponseEntity<?>  registration(@RequestBody User user){
		
		//if email is duplicate (PRIMARY KEY VIOLATION)
		if(!userDao.isEmailValid(user.getEmail())){
			ErrorClazz errorClazz=new ErrorClazz(2,"Email already exists.. please choose different email id");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try{
		userDao.registerUser(user);
		}catch(Exception e){
			ErrorClazz errorClazz=new ErrorClazz(1,"Unable to register user details.."+e.getMessage());
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
	@RequestMapping(value="/login",method=RequestMethod.PUT)
	//user{email:'james.s@niit.com',password:'1234'}
	public ResponseEntity<?> login(@RequestBody User user,HttpSession session){//user will have values only for email and password
		User validUser=userDao.login(user);//valid user will have values for all properties..for valid credentials
		if(validUser==null){
		//select * from user where email='james.s@niit.com' and password='1234' -> 0 row
			ErrorClazz errorClazz=new ErrorClazz(5,"Email or password is incorrect..please enter valid credentials..");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		//set online property as true
		System.out.println(session.getId());
		session.setAttribute("email", user.getEmail());
		
		validUser.setOnline(true);
		userDao.update(validUser);
		return new ResponseEntity<User>(validUser,HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/logout",method=RequestMethod.PUT)
    public ResponseEntity<?> logout(HttpSession session){
    	//UPDATE ONLINE STATUS FROM TRUE TO FALSE
    	//WE NEED USER OBJECT FOR UPDATING ONLINE STATUS
    	String email=(String)session.getAttribute("email");
    	if(email==null){
    		ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
    	}
    	User user=userDao.getUser(email);
    	user.setOnline(false);
    	userDao.update(user);
    	session.removeAttribute("email");
    	session.invalidate();
    	return new ResponseEntity<Void>(HttpStatus.OK);
    }
	@RequestMapping(value="/getuser",method=RequestMethod.GET)
	public ResponseEntity<?> getUser(HttpSession session){
		String email=(String)session.getAttribute("email");
		if(email==null){
			ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		User user=userDao.getUser(email);
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
	
	@RequestMapping(value="/updateprofile",method=RequestMethod.PUT)
	public ResponseEntity<?> updateUserProfile(@RequestBody User user,HttpSession session){
		String email=(String)session.getAttribute("email");
		if(email==null){
			ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		try{
		userDao.update(user);
		}catch(Exception e){
			ErrorClazz errorClazz=new ErrorClazz(7,"Unable to update user details.."+e.getMessage());
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
}
