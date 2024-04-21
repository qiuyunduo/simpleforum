package com.qyd.web.front.home;

import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.web.front.home.helper.IndexRecommendHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-08 17:26
 */

@Controller
public class IndexController {

    @Autowired
    IndexRecommendHelper indexRecommendHelper;

    @GetMapping(path = {"/", "/index", "/login"})
    public String index(Model model, HttpServletRequest request) {
        String activeTab = request.getParameter("category");

        return "views/home/index";
    }
}
