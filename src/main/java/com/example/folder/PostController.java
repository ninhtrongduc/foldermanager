package com.example.folder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

/**
 * Controller xử lý các yêu cầu liên quan đến bài viết (News/Post)
 * Tương thích với Java 25 và Spring Boot 3.x
 */
@Controller
@RequestMapping("/posts")
public class PostController {

    /**
     * Hiển thị trang Thêm bài viết
     * Endpoint: GET /admin/posts/add
     */
    @GetMapping("/add")
    public String showAddPostForm(Model model) {
        // Bạn có thể thêm các object vào model ở đây nếu cần (ví dụ: danh sách chuyên mục)
        model.addAttribute("pageTitle", "Thêm bài viết mới");
        
        // Trả về tên file HTML (add-post.html) trong thư mục src/main/resources/templates
        return "post";
    }
}
