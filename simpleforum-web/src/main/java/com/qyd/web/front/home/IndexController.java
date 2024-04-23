package com.qyd.web.front.home;

import com.qyd.web.front.home.helper.IndexRecommendHelper;
import com.qyd.web.front.home.vo.IndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        IndexVo vo = indexRecommendHelper.buildIndexVo(activeTab);
        model.addAttribute("vo", vo);
        return "views/home/index";
    }
}
