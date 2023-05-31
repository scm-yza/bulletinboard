package com.bulletinBoard.system.api.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bulletinBoard.system.api.common.ControllerUtil;
import com.bulletinBoard.system.api.common.response.ErrorResponse;
import com.bulletinBoard.system.api.common.response.MainResponse;
import com.bulletinBoard.system.api.response.PostResponse;
import com.bulletinBoard.system.bl.dto.PostDTO;
import com.bulletinBoard.system.bl.service.asset.AssetService;
import com.bulletinBoard.system.bl.service.post.PostService;
import com.bulletinBoard.system.web.form.PostForm;

/**
 * <h2>PostController Class</h2>
 * <p>
 * Process for PostController
 * </p>
 * 
 * @author YeZawAung
 *
 */
@RestController("postApiController")
@RequestMapping("/api/post")
public class PostController {

    /**
     * <h2>RESOURCE_URL</h2>
     * <p>
     * RESOURCE_URL
     * </p>
     */
    private static final String RESOURCE_URL = "/api/resources";

    /**
     * <h2>postService</h2>
     * <p>
     * Post Service
     * </p>
     */
    @Autowired
    private PostService postService;

    /**
     * <h2>assetService</h2>
     * <p>
     * assetService
     * </p>
     */
    @Autowired
    private AssetService assetService;

    /**
     * <h2>addPost</h2>
     * <p>
     * Add Post
     * </p>
     *
     * @param post          PostForm
     * @param bindingResult BindingResult
     *
     * @return MessageResponse
     */
    @PostMapping({ "", "/" })
    public ResponseEntity<MainResponse> addPost(@Valid @RequestBody PostForm post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorList = ControllerUtil.getErrors(bindingResult);
            return new ResponseEntity<>(new ErrorResponse("Failed to Add Post", errorList), HttpStatus.BAD_REQUEST);
        }
        post.setUserEmail("admin@gmail.com");
        this.postService.doAddPost(post);
        return new ResponseEntity<>(new MainResponse("Saving Successfully"), HttpStatus.CREATED);
    }

    /**
     * <h2>getPublicPosts</h2>
     * <p>
     * Get Public Posts
     * </p>
     *
     * @param page int
     *
     * @return List<PostDTO>
     */
    @GetMapping({ "", "/" })
    public ResponseEntity<MainResponse> getPublicPosts(@RequestParam(defaultValue = "0") int page) {
        String email = "admin@gmail.com";
        int count = this.postService.doGetPublicPostCount(email);
        int offset = ControllerUtil.getOffset(page, count);
        List<PostDTO> list = this.postService.doGetPublicPosts(offset, ControllerUtil.PAGE_SIZE, email);
        return new ResponseEntity<>(new MainResponse(this.getPostResponses(list)), HttpStatus.OK);
    }

    /**
     * <h2>getPostById</h2>
     * <p>
     * Get Post by ID
     * </p>
     *
     * @param id int
     *
     * @return PostDTO post
     */
    @GetMapping("/{id}")
    public ResponseEntity<MainResponse> getPostById(@PathVariable int id) {
        if (id == 0) {
            return new ResponseEntity<>(new ErrorResponse("Invalid ID"), HttpStatus.BAD_REQUEST);
        }
        PostDTO post = this.postService.doGetPostById(id);
        if (post == null) {
            return new ResponseEntity<>(new ErrorResponse("Post ID Not Found"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MainResponse(new PostResponse(post)), HttpStatus.OK);
    }

    /**
     * <h2>updatePost</h2>
     * <p>
     * Update Post
     * </p>
     *
     * @param post          PostForm
     * @param bindingResult BindingResult
     *
     * @return ResponseEntity<MainResponse>
     */
    @PutMapping("/{id}")
    public ResponseEntity<MainResponse> updatePost(@RequestBody PostForm post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorList = ControllerUtil.getErrors(bindingResult);
            return new ResponseEntity<>(new ErrorResponse("Failed To Update Post", errorList), HttpStatus.BAD_REQUEST);
        }
        this.postService.doUpdatePost(post);
        PostResponse postResponse = new PostResponse(this.postService.doGetPostById(post.getId()));
        return new ResponseEntity<>(new MainResponse("Update Successful", postResponse), HttpStatus.OK);
    }

    /**
     * <h2>deletePost</h2>
     * <p>
     * Delete Post
     * </p>
     *
     * @param id int
     *
     * @return MessageResponse
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MainResponse> deletePost(@PathVariable int id) {
        if (id == 0) {
            return new ResponseEntity<>(new ErrorResponse("Invalid ID"), HttpStatus.BAD_REQUEST);
        }
        if (this.postService.doGetPostById(id) == null) {
            return new ResponseEntity<>(new ErrorResponse("Post ID Not Found"), HttpStatus.OK);
        }
        this.postService.doDeletePostById(id);
        return new ResponseEntity<>(new MainResponse("Deleted Successfully"), HttpStatus.OK);
    }

    /**
     * <h2>getPostImages</h2>
     * <p>
     * Get Post Images
     * </p>
     *
     * @param id int
     *
     * @return ResponseEntity<MainResponse>
     */
    @GetMapping("/image")
    public ResponseEntity<MainResponse> getPostImages(@RequestParam int id) {
        PostDTO post = postService.doGetPostById(id);
        if (post.getImageNames() != null) {
            List<String> imgUrls = this.assetService.doGetImageUrls(id).stream().map(item -> RESOURCE_URL + item)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(new MainResponse(imgUrls), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MainResponse(post), HttpStatus.OK);
    }

    /**
     * <h2>uploadImage</h2>
     * <p>
     * Update Images
     * </p>
     *
     * @param id      int
     * @param images  MultipartFile[]
     * @param request HttpServletRequest
     *
     * @return ResponseEntity<MainResponse>
     */
    @PutMapping("/image")
    public ResponseEntity<MainResponse> updatePostImages(@RequestParam int id, @RequestPart MultipartFile[] images,
            HttpServletRequest request) {
        List<String> fileNames = new ArrayList<>();
        List<byte[]> fileBytes = new ArrayList<>();
        for (MultipartFile image : images) {
            fileNames.add(image.getOriginalFilename());
            try {
                fileBytes.add(image.getBytes());
            } catch (IOException e) {
                new ResponseEntity<>(new ErrorResponse("Unable to Fetch Data from Image"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        this.assetService.doUploadPostImages(ControllerUtil.getRootDir(request), id, fileNames, fileBytes);
        return new ResponseEntity<>(new MainResponse("Saving Successfully"), HttpStatus.OK);
    }

    /**
     * <h2>deletePostImage</h2>
     * <p>
     * Delete Post Image
     * </p>
     *
     * @param id       int
     * @param fileName String
     * @param request  HttpServletRequest
     *
     * @return ResponseEntity<MainResponse>
     */
    @DeleteMapping("/image")
    public ResponseEntity<MainResponse> deletePostImage(@RequestParam int id, @RequestParam String fileName,
            HttpServletRequest request) {
        this.assetService.doDeletePostImages(ControllerUtil.getRootDir(request), id, fileName);
        return new ResponseEntity<>(new MainResponse("Delete Successfully"), HttpStatus.OK);
    }

    /**
     * <h2>getPostResponses</h2>
     * <p>
     * Convert PostDTOs to PostResponses
     * </p>
     *
     * @param posts List<PostDTO>
     *
     * @return List<PostResponse>
     */
    private List<PostResponse> getPostResponses(List<PostDTO> posts) {
        if (posts.isEmpty())
            return null;
        return posts.stream().map(it -> new PostResponse(it)).collect(Collectors.toList());
    }
}