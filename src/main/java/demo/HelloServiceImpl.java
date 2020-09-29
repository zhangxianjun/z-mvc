package demo;

import annotation.ZService;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: zxj
 * @date: 2020/9/29 10:50
 * Description: .
 */

@ZService
public class HelloServiceImpl implements HelloService {

    @Override
    public String getName(String greeting) {
        return greeting + ", " + this.getClass().getName();
    }
}
