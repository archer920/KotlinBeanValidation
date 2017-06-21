package com.stonesoupprogramming

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.context.WebApplicationContext
import javax.validation.Valid
import javax.validation.constraints.Size

@SpringBootApplication
class SpringValidationApplication

fun main(args: Array<String>) {
    SpringApplication.run(SpringValidationApplication::class.java, *args)
}

data class User(@get: NotBlank(message = "{first_name.required}")
                var firstName: String = "",

                @get: NotBlank(message = "{last_name.required}")
                var lastName: String = "",

                @get: NotBlank(message = "{email.required}")
                @get: Email(message = "{email.invalid}")
                var email: String = "",

                @get: NotBlank(message = "{phone.required}")
                var phone: String = "",

                @get: NotBlank(message = "{address.required}")
                var address: String = "",

                @get: NotBlank(message = "{city.required}")
                var city: String = "",

                @get: NotBlank(message = "{state.required}")
                @get: Size(min = 2, max = 2, message = "{state.size}")
                var state: String = "",

                @get: NotBlank(message = "{zip.required}")
                var zip: String = "")

@Controller
@RequestMapping("/")
@Scope(WebApplicationContext.SCOPE_REQUEST)
class ValidatorController(@Autowired val registrationService: RegistrationService) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun doGet(model: Model): String {
        model.addAttribute("user", User())
        return "index"
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun doPost(@Valid user: User, //The @Valid annotation tells Spring to Validate this object
               errors: Errors)  //Spring injects this class into this method. It will hold any
            //errors that are found on the object
            : String {
        val result: String
        when {
        //Test for errors
            errors.hasErrors() -> result = "index"
            else -> {
                //Otherwise proceed to the next page
                registrationService.user = user
                result = "redirect:/congrats"
            }
        }
        return result
    }
}

@Controller
@RequestMapping("/congrats")
@Scope(WebApplicationContext.SCOPE_REQUEST)
class CongratsController(@Autowired val registrationService: RegistrationService) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun doGet(model: Model): String {
        print("Hi request")
        model.addAttribute("welcomeMessage", "Congratulations ${registrationService.user.firstName} ${registrationService.user.lastName}")
        model.addAttribute("user", registrationService.user)
        return "/congrats"
    }
}

@Service
@Scope(WebApplicationContext.SCOPE_SESSION)
class RegistrationService {

    var user = User()

}