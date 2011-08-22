package com.trgoofi.irregular.web.listener;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class TestConstantListenerc
 */
@WebServlet("/TestConstantListener")
public class TestConstantListener extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    this.doPost(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String something = "test constant in view(jsp)";
    request.getSession().setAttribute(ConstantDefinition.FOO, something);
    request.getRequestDispatcher("testConstantListener.jsp")
               .forward(request, response);
  }

}
