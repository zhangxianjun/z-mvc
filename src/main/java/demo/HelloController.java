package demo;

import annotation.ZAutowired;
import annotation.ZController;

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

@ZController("/hello")
public class HelloController {

    @ZAutowired("HelloServiceImpl")
    private HelloService helloService;

    public void sayHello(HttpServletRequest request, HttpServletResponse response) {

        String content = helloService.getName("Hello");


        try {
            PrintWriter writer = response.getWriter();
            writer.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
