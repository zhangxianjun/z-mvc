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

    public String getName(String name) {
        return "HelloServiceImpl" + name;
    }
}
