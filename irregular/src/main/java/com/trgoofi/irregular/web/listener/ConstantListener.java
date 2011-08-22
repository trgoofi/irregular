package com.trgoofi.irregular.web.listener;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此类为在web的jsp中模拟常量的使用作准备工作，使其像普通java类里使用常量一样。
 * <p>主要用于在session等的对象索引，保证一致的key索引。</p>
 * 使用：在web.xml加上 e.g.
 * <pre>
 * &ltcontext-param>
 *   &ltparam-name>constantClassName&lt/param-name>
 *   &ltparam-value>
 *       com.blah.blah.Constant1,
 *       com.blah.Constant2
 *   &lt/param-value>
 * &lt/context-param>
 * &ltlistener>
 *   &ltlistener-class>
 *           com.blah.blah.ConstantListener
 *   &lt/listener-class>
 * &lt/listener>
 * </pre>
 * jsp中的使用：e.g. ${sessionSope[Constant1.FOO].username}
 * @author hui
 *
 */
public class ConstantListener implements ServletContextListener{
  private final static Logger log = LoggerFactory.getLogger(ConstantListener.class);

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    // TODO Auto-generated method stub

  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    this.registerConstants(context);
  }

  /**
   * 以类或接口的simpleName作为key。将常量(字段)注册到<code>ServletContext</code>中。
   * <p><b>注意：</b>如果类或接口的simpleName相同的话，前者将会被覆盖</p>
   * @param context servlet context
   */
  public void registerConstants(ServletContext context) {
    String constantClassName = context.getInitParameter("constantClassName");

    if (constantClassName == null || constantClassName == "") return;

    String[] classNames = extractClassName(constantClassName);
    for (String className : classNames) {
      try {
        Class<?> clazz = Class.forName(className);
        context.setAttribute(clazz.getSimpleName(), field2Map(clazz));
      } catch (ClassNotFoundException e) {
        String msg = "ClassNotFound:[{}]. And this class will be ignore";
        log.error(msg, className);
      }
    }
  }

  /**
   * class names must separated by commas
   * @param classNames
   * @return
   */
  public static String[] extractClassName(String classNames) {
    // delete whitespace character
    classNames = classNames.replaceAll("\\s", "");
    return classNames.split(",");
  }

  /**
   * 将一个类的所有字段(包括私有)提取到一个Map集合中。
   * 字段名作为Map的key，字段值作为Map的value。
   * <p><b>注意：</b>如果有存在非法访问的字段，其将会被忽略。</p>
   *
   * @param clazz 要提取其字段的类对象
   * @return 包括所有字段的<code>Map</code>集合
   */
  public static Map<String, Object> field2Map(Class<?> clazz) {
    Map<String, Object> fieldMap = new HashMap<String, Object>();
    Field[] fields = clazz.getDeclaredFields();
    AccessibleObject.setAccessible(fields, true);
    for (Field field : fields) {
      try {
        fieldMap.put(field.getName(), field.get(null));
      } catch (IllegalAccessException e) {
        String msg = "Illegal access of field:[{}->{}]."
            + " And this field will be ignore";
        log.error(msg, clazz.getName(), field.getName());
      }
    }
    return fieldMap;
  }
}
