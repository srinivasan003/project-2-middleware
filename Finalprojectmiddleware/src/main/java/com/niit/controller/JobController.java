package com.niit.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.JobDao;
import com.niit.dao.UserDao;
import com.niit.model.ErrorClazz;
import com.niit.model.Job;
import com.niit.model.User;

@RestController
public class JobController {
	@Autowired
private JobDao jobDao;
@Autowired
private UserDao userDao;
    @RequestMapping(value="/addjob",method=RequestMethod.POST)
	public ResponseEntity<?> addJob(@RequestBody Job job,HttpSession session){
		//USER IS LOGGED IN OR NOT
    	//CHECK IF THE HTTP REQUEST IS FROM AN AUTHENTICATED USER OR NOT
    	String email=(String)session.getAttribute("email");
    	if(email==null){
    		ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
    	}
    	//CHECK FOR AUTHORIZATION
    	//CHECK IF THE REQUEST FROM AN ADMIN
    	//CHECK THE ROLE OF THE USER
    	//GET THE USER OBJECT
    	//SELECT * FROM USER WHERE EMAIL=? in DAO layer, return user object
    	User user=userDao.getUser(email);
    	if(!user.getRole().equals("ADMIN"))//USER IS NOT AN ADMIN
    	{
    		ErrorClazz errorClazz=new ErrorClazz(7,"Not Authorized to post any job details..");
    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//jobform.html, Access Denied
    	}
    	job.setPostedOn(new Date());
    	try{
    	jobDao.addJob(job);
    	}catch(Exception e){
    		ErrorClazz errorClazz=new ErrorClazz(8,"Unable to insert job details.."+e.getMessage());
    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);//jobform.html, Unable to post job detail
    	}
    	return new ResponseEntity<Job>(job,HttpStatus.OK);//change the path to /getalljobs
    	
    	
	}
	@RequestMapping(value="/alljobs",method=RequestMethod.GET)
    public ResponseEntity<?> getAllJobs(HttpSession session){
    	String email=(String)session.getAttribute("email");
    	if(email==null){
    		ErrorClazz errorClazz=new ErrorClazz(6,"Please login...");
    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//login.html
    	}
    	List<Job> jobs=jobDao.getAllJobs();
    	return new ResponseEntity<List<Job>>(jobs,HttpStatus.OK);
    }
}



