package com.bulletinBoard.system.bl.service.post.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bulletinBoard.system.bl.dto.PostDTO;
import com.bulletinBoard.system.bl.service.ServiceUtil;
import com.bulletinBoard.system.bl.service.post.PostService;
import com.bulletinBoard.system.persistance.entity.Post;
import com.bulletinBoard.system.persistance.repository.PostRepository;
import com.bulletinBoard.system.persistance.repository.UserRepository;
import com.bulletinBoard.system.web.form.PostForm;

/**
 * <h2>PostServiceImpl Class</h2>
 * <p>
 * Process for PostServiceImpl
 * </p>
 * 
 * @author YeZawAung
 *
 */
@Service
public class PostServiceImpl implements PostService {

    /**
     * <h2>postRepository</h2>
     * <p>
     * postRepository
     * </p>
     */
    @Autowired
    private PostRepository postRepository;

    /**
     * <h2>userRepository</h2>
     * <p>
     * userRepository
     * </p>
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * <h2>doAddPost</h2>
     * <p>
     * Add Post
     * </p>
     * 
     * @param post PostForm
     * @return boolean
     */
    @Override
    public boolean doAddPost(PostForm post) {
        Post postToMatch = new Post();
        postToMatch.setTitle(post.getTitle());
        if (postRepository.exists(Example.of(postToMatch))) {
            return false;
        }
        Post postEntity = new Post(post);
        postEntity.setUser(this.userRepository.dbGetUserByEmail(post.getUserEmail()).get());
        this.postRepository.save(postEntity);
        return true;
    }

    /**
     * <h2>doGetPostList</h2>
     * <p>
     * Get A List Of All Posts
     * </p>
     * 
     * @param page int
     * @param size int
     * @return Page<PostDTO>
     */
    @Override
    public List<PostDTO> doGetPostList(int page, int size) {
        Pageable paging = ServiceUtil.getPagable(page, size);
        return postRepository.findAll(paging).map(item -> new PostDTO(item)).getContent();
    }

    @Override
    public PostDTO doGetPostById(int id) {
        return new PostDTO(this.postRepository.findById(id).get());
    }

    /**
     * <h2>doGetPublicPosts</h2>
     * <p>
     * Get Public Posts
     * </p>
     * 
     * @param page  int
     * @param size  int
     * @param email String
     * @return List<PostDTO>
     */
    @Override
    public List<PostDTO> doGetPublicPosts(int page, int size, String email) {
        int userId = this.userRepository.dbGetUserByEmail(email).get().getId();
        Pageable pageable = ServiceUtil.getPagable(page, size);
        return this.getPostDto(this.postRepository.dbGetUserPosts(userId, pageable).getContent());
    }

    /**
     * <h2>doGetPublicPostsByTitleAndAuthorName</h2>
     * <p>
     * Get Public Posts by Title and Author's Name
     * </p>
     * 
     * @param page       int
     * @param size       int
     * @param postTitle  String
     * @param authorName String
     * @return List<PostDTO>
     */
    @Override
    public List<PostDTO> doGetPublicPostsByTitleAndAuthorName(int page, int size, String postTitle, String authorName) {
        Pageable pageable = ServiceUtil.getPagable(page, size);
        return this.getPostDto(
                this.postRepository.dbGetPublicPostsByTitleAndAuthorName(postTitle, authorName, pageable).getContent());
    }

    /**
     * <h2>doGetUserPosts</h2>
     * <p>
     * Get User Posts
     * </p>
     * 
     * @param page  int
     * @param size  int
     * @param email String
     * @return List<PostDTO>
     */
    @Override
    public List<PostDTO> doGetUserPosts(int page, int size, String email) {
        int userId = this.userRepository.dbGetUserByEmail(email).get().getId();
        Pageable pageable = ServiceUtil.getPagable(page, size);
        return this.getPostDto(this.postRepository.dbGetUserPosts(userId, pageable).getContent());
    }

    /**
     * <h2>doGetActivePosts</h2>
     * <p>
     * Get Active Posts
     * </p>
     * 
     * @param offset int
     * @param size   int
     * @return List<PostDTO>
     */
    @Override
    public List<PostDTO> doGetActivePosts(int page, int size) {
        Pageable pageable = ServiceUtil.getPagable(page, size);
        return this.getPostDto(this.postRepository.dbGetPostsByActiveStatus(pageable).getContent());
    }

    /**
     * <h2>doUpdatePost</h2>
     * <p>
     * Update Post
     * </p>
     * 
     * @param post PostForm
     */
    @Override
    public void doUpdatePost(PostForm postForm) {
        Post post = new Post(postForm);
        post.setUser(userRepository.dbGetUserByEmail(postForm.getUserEmail()).get());
        this.postRepository.save(post);
    }

    /**
     * <h2>doEnableDisablePost</h2>
     * <p>
     * Enable or Disable Post
     * </p>
     *
     * @param postForm PostForm
     */
    @Override
    public void doEnableDisablePost(PostForm postForm) {
        Post post = new Post(postForm);
        post.setUser(userRepository.dbGetUserByEmail(postForm.getUserEmail()).get());
        post.setIsActive(!post.getIsActive());
        this.postRepository.save(post);
    }

    /**
     * <h2>doUpdateImages</h2>
     * <p>
     * Update Images
     * </p>
     * 
     * @param id     int
     * @param images List<String>
     */
    @Override
    public void doUpdateImages(int id, List<String> images) {
        Post post = this.postRepository.findById(id).get();
        post.setUser(userRepository.findById(id).get());
        post.setImageNames(images);
        this.postRepository.save(post);
    }

    /**
     * <h2>doDeletePostById</h2>
     * <p>
     * Delete Post By ID
     * </p>
     * 
     * @param id int
     */
    @Override
    public void doDeletePostById(int id) {
        this.postRepository.deleteById(id);
    }

    /**
     * <h2>getPostDto</h2>
     * <p>
     * Convert And Get A List Of Post from Post Entity List
     * </p>
     *
     * @param postList List<Post>
     * @return List<PostDTO>
     */
    private List<PostDTO> getPostDto(List<Post> postList) {
        if (postList == null) {
            return List.of();
        }
        return postList.stream().map(item -> new PostDTO(item)).collect(Collectors.toList());
    }
}
