package com.vitekkor.frogapi.controller

import com.vitekkor.frogapi.db.entity.User
import com.vitekkor.frogapi.db.repository.UserRepository
import com.vitekkor.frogapi.model.SingUpModel
import com.vitekkor.frogapi.util.encodeBase64
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/auth/singup")
class AuthController(private val userRepository: UserRepository) {
    @GetMapping
    fun singUpPage(model: Model): ModelAndView {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "singUp.html"
        model.addAttribute("singUpModel", SingUpModel())
        return modelAndView
    }

    @PostMapping
    fun singUp(@ModelAttribute singUpModel: SingUpModel, model: Model): String {
        if (singUpModel.isValid()) {
            if (!singUpModel.checkPassword()) {
                model.addAttribute("singUpModel", singUpModel.copy(badPassword = "bad"))
                return "singUp"
            }
            val alreadyExistsUser = userRepository.findOneByEmail(checkNotNull(singUpModel.email))
            return if (alreadyExistsUser == null) {
                val user = User(
                    name = singUpModel.name,
                    email = singUpModel.email,
                    password = singUpModel.password?.encodeBase64()
                )
                userRepository.saveAndFlush(user)
                "singUpSuccessful"
            } else {
                model.addAttribute("singUpModel", singUpModel.copy(badEmail = "bad"))
                "singUp"
            }
        } else {
            model.addAttribute("singUpModel", singUpModel.copy(badEmail = "bad", badPassword = "bad"))
            return "singUp"
        }
    }
}
