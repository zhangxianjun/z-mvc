package demo;

import annotation.ZAutowired;
import annotation.ZController;
import annotation.ZRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: zxj
 * @date: 2020/9/29 10:50
 * Description: .
 */

@ZController()
@ZRequestMapping("/hello")
public class HelloController {

    public String name = "zxj";

    @ZAutowired()
    private HelloServiceImpl helloServiceImpl;

    @ZRequestMapping("/say")
    public void sayHello(HttpServletRequest request, HttpServletResponse response, String greeting) {

        String content = helloServiceImpl.getName(greeting);


        try {
            PrintWriter writer = response.getWriter();
            writer.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
