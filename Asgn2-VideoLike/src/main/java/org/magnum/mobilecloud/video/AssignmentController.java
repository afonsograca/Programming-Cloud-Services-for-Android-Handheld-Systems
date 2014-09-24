/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class AssignmentController {
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	
	@Autowired
	private VideoRepo videos;

	//GET /video
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList(){
		return Lists.newArrayList(videos.findAll());
	}
	
	//POST /video
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video){
		return videos.save(video);
	}
	
	//GET /video/{id}
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video findById(@PathVariable("id") long id){
		return videos.findOne(id);
	}
	
	//POST /video/{id}/like
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<Void> likeVideo(@PathVariable("id") long id, Principal p){
		if(videos.exists(id)){
			Video video = videos.findOne(id);
			if(video.userAlreadyLikes(p.getName())){
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
			else{
				video.addUserLike(p.getName());
				videos.save(video);
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
		}
		else {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	//POST /video/{id}/unlike
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<Void> unlikeVideo(@PathVariable("id") long id, Principal p){
		Video video = videos.findOne(id);
		if(videos.exists(id)){
			if(video.userAlreadyLikes(p.getName())){
				video.removeUserLike(p.getName());
				videos.save(video);
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			else{
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	//GET /video/{id}/likedby
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH+"/{id}/likedby", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<Collection<String>> getUsersWhoLikedVideo(@PathVariable("id")long id){
		if(videos.exists(id)){
			return new ResponseEntity<Collection<String>>(videos.findOne(id).getUsersLikes(),HttpStatus.OK);
		}
		else {
			return new ResponseEntity<Collection<String>>(HttpStatus.NOT_FOUND);
		}
	}
	
	//GET /video/search/findByName?title={title}
	@RequestMapping(value=VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(@RequestParam(VideoSvcApi.TITLE_PARAMETER) String title){
		return videos.findByName(title);
	}
	
	//GET /video/search/findByDurationLessThan?duration={duration}
	@RequestMapping(value=VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(@RequestParam(VideoSvcApi.DURATION_PARAMETER) long duration){
		return videos.findByDurationLessThan(duration);
	}
}
