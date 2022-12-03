package com.vitekkor.frogapi.controller

import com.vitekkor.frogapi.model.SingUpModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/auth/singup")
class AuthController {
    @GetMapping
    fun singUpPage(model: Model): ModelAndView {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "singUp.html"
        model.addAttribute("singUpModel", SingUpModel())
        return modelAndView
    }

    @PostMapping
    fun singUp(@ModelAttribute singUpModel: SingUpModel): String {
        return "singUpSuccessful"
    }
}