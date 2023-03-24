package com.bulletinBoard.system.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bulletinBoard.system.bl.dto.UserDTO;
import com.bulletinBoard.system.bl.service.UserService;
import com.bulletinBoard.system.web.form.UserForm;
import com.google.gson.Gson;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final String HOME_VIEW = "userListView";
    private static final String HOME_REDIRECT = "redirect:/user/list";
    private static final String ADD_VIEW = "addUserView";
    
    @Autowired
    UserService userService;
    
    @GetMapping({ "/", "" })
    protected ModelAndView getHomeView(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size) {
        return new ModelAndView(HOME_REDIRECT);
    }
    
    @GetMapping("list")
    protected ModelAndView getPostListView(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        ModelAndView mv = new ModelAndView(HOME_VIEW);        
        // Get List of Post by offset
        int offset = (page - 1) * size;
        List<UserDTO> userDtos = userService.getAll(offset, size);
        // Calculate Total Page for Pagination
        int count = userService.getCount();
        int pageCount = count / size;
        int remainder = count % size;
        if (remainder > 0) {
            pageCount += 1;
        }
        mv.addObject("users", (new Gson()).toJson(userDtos));
        mv.addObject("pageIndex", page);
        mv.addObject("pageCount", pageCount);
        mv.addObject("pageSize", size);
        return mv;
    }

    @GetMapping("add")
    protected ModelAndView getAddPostForm() {
        return new ModelAndView(ADD_VIEW);
    }
    
    @PostMapping("add")
    protected ModelAndView addUser(@RequestParam("name") String name, @RequestParam("email") String email,
            @RequestParam("address") String address, @RequestParam String password) {
        UserForm user = new UserForm(name, email, address, password);
        ModelAndView mv = new ModelAndView();
        if (!validate(user, mv, ADD_VIEW)) {
            return mv;
        }
        boolean isSuccess = userService.add(user);
        if (!isSuccess) {
            mv.addObject("msg", "Unable to Save the User");
            mv.addObject("errors", Arrays.asList("Title Must Be Unique."));
            mv.setViewName(ADD_VIEW);
            return mv;
        }
        mv.setViewName(HOME_REDIRECT);
        return mv;
    }

    @PostMapping("update")
    protected ModelAndView updatePost(@RequestParam("id") int id, @RequestParam("name") String name,
            @RequestParam("email") String email, @RequestParam("address") String address,
            HttpServletResponse resp) {        
        ModelAndView mv = new ModelAndView(HOME_REDIRECT);
        UserForm user = new UserForm(id, name, email, address);
        
          if (!validate(user, mv, HOME_REDIRECT)) { return mv; }
          userService.update(user, 1);
         
        return mv;
    }

    @PostMapping("delete")
    protected ModelAndView deletePost(@RequestParam("id") int id) {
        ModelAndView mv = new ModelAndView(HOME_REDIRECT);
        if (id <= 0) {
            mv.addObject("msg", "Invalid User ID");
            return mv;
        }
        userService.delete(id);
        return mv;
    }

    private boolean validate(UserForm form, ModelAndView mv, String viewName) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(form);
        List<String> errors = violations.stream().map(item -> item.getMessage()).collect(Collectors.toList());
        if (!errors.isEmpty()) {
            mv.setViewName(viewName);
            mv.addObject("msg", "Validation Error");
            mv.addObject("errors", errors);
            return false;
        }
        return true;
    }
}
