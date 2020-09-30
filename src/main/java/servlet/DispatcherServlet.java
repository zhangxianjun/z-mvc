package servlet;

import annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: zxj
 * @date: 2020/9/29 11:05
 * Description: .
 */

public class DispatcherServlet extends HttpServlet {

    List<String> classNames = new ArrayList<String>();

    // IOC容器
    Map<String, Object> beans = new HashMap<String, Object>();

    // url method
    Map<String, Object> urls = new HashMap<String, Object>();

    @Override
    public void init(ServletConfig config) {
        // 扫描
        scanBasePackage("demo");

        // 实例类
        instanceClass();

        // 注入值
        autoWired();

        // url 映射 method
        urlMapping();


    }

    private void urlMapping() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {

            Object instance = entry.getValue();

            Class cls = instance.getClass();

            if (cls.isAnnotationPresent(ZController.class)) {
                ZRequestMapping clsRM = (ZRequestMapping) cls.getAnnotation(ZRequestMapping.class);
                String clsPath = clsRM.value();

                Method[] methods = cls.getDeclaredMethods();

                for (Method method : methods) {
                    if (method.isAnnotationPresent(ZRequestMapping.class)) {
                        ZRequestMapping mRM = method.getAnnotation(ZRequestMapping.class);
                        String mPath = mRM.value();

                        // 请求路径
                        urls.put(clsPath + mPath, method);
                    }
                }

            }
        }
    }

    private void autoWired() {

        for (Map.Entry<String, Object> entry : beans.entrySet()) {

            Object instance = entry.getValue();

            Class cls = instance.getClass();


            if (cls.isAnnotationPresent(ZController.class)) {
                Field[] fields = cls.getDeclaredFields();

                for (Field f : fields) {

                    if (f.isAnnotationPresent(ZAutowired.class)) {

                        ZAutowired autowired = f.getAnnotation(ZAutowired.class);

//                        String key = autowired.value();

                        // 获取注解的对象  demo.HelloServiceImpl
                        String key = f.getType().getName();

                        System.out.println("注入成员变量" + key);

                        Object bean = beans.get(key);

                        f.setAccessible(true);
                        try {
                            f.set(instance, bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } finally {
                            f.setAccessible(false);
                        }
                    }
                }

            }

        }

    }



    private void instanceClass() {
        for (String cn : classNames) {
            cn = cn.replace(".class", "");

            try {
                Class cls = Class.forName(cn);

                if (cls.isAnnotationPresent(ZController.class)) {
                    // ZController
                    Object instance = cls.getDeclaredConstructor().newInstance();

                    // 获取注解
                    ZRequestMapping mapping = (ZRequestMapping) cls.getAnnotation(ZRequestMapping.class);

                    String key = mapping.value();

                    beans.put(key, instance);
                } else if (cls.isAnnotationPresent(ZService.class)) {
                    // ZService
                    Object instance = cls.getDeclaredConstructor().newInstance();

                    // 获取注解
                    ZService mapping = (ZService) cls.getAnnotation(ZService.class);


                    String key = mapping.value();

                    // demo.HelloServiceImpl

                    System.out.println("IOC存储的bean " + cls.getName());

                    beans.put(cls.getName(), instance);
                } else {
                    continue;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void scanBasePackage(String basePackage) {
        // 编译好的类路径
//        String path = "/" + basePackage.replaceAll("\\.", "/");
        URL url = this.getClass().getClassLoader().getResource(basePackage);

        String rootPath = url.getFile();

        // 获取文件对象
        File file = new File(rootPath);

        // 获取该文件对象中所有目录、文件的路径
        String[] subFilePaths = file.list();

        for (String sfp : subFilePaths) {
            File f = new File(rootPath + "/"+ sfp);

            if (f.isDirectory()) {
                scanBasePackage(basePackage + "/" + sfp);
            } else {
//                classNames.add(f.getName());
                classNames.add("demo." + f.getName());
            }
        }
    }




    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        super.doPost(req, resp);

        String uri = req.getRequestURI();

        System.out.println(uri);

        String cPath = req.getContextPath();

        String path = uri.replace(cPath, "");

        Method method = (Method) urls.get(path);

        Parameter[] parameters = method.getParameters();

        Object[] args = new Object[parameters.length];


        int args_i = 0;
        int index = 0;
        for (Parameter p : parameters) {
            if (ServletRequest.class.isAssignableFrom(p.getType())) {
                args[args_i++] = req;
                continue;
            }
            if (ServletResponse.class.isAssignableFrom(p.getType())) {
                args[args_i++] = resp;
                continue;
            }

            Annotation[] pas = method.getParameterAnnotations()[index];

            if (pas.length > 0) {
                for (Annotation a : pas) {
                    if (ZRequestParam.class.isAssignableFrom(a.getClass())) {
                        ZRequestParam rp = (ZRequestParam) a;
                        args[args_i++] = req.getParameter(rp.value());
                    }
                }
            } else {
                args[args_i++] = req.getParameter(p.getName());
            }

            index++;
        }


        Object clazz = beans.get("/" + path.split("/")[1]);


        try {
            method.invoke(clazz, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
