package org.ict4htw.atomfeed.standalone;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DummyController {

    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ResponseBody
    public String all() {
        return "Hello world";
    }

}