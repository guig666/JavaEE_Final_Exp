package com.konsonx.controller.manage.api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "TestServlet", urlPatterns = {"/testapi"})
public class TestServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== TEST SERVLET DO PUT ==========");
        System.out.println("Query String: " + req.getQueryString());

        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String address = req.getParameter("address");
        String alias = req.getParameter("alias");
        String amountStr = req.getParameter("amount");
        String bylocationStr = req.getParameter("bylocation");

        System.out.println("city: " + city);
        System.out.println("district: " + district);
        System.out.println("address: " + address);
        System.out.println("alias: " + alias);
        System.out.println("amount: " + amountStr);
        System.out.println("bylocation: " + bylocationStr);

        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if (city == null || "".equals(city)) {
            String errorJson = "{\"code\":-1,\"msg\":\"city参数为空\",\"data\":null}";
            System.out.println("返回: " + errorJson);
            out.write(errorJson);
        } else {
            String successJson = "{\"code\":1,\"msg\":\"测试成功\",\"data\":null}";
            System.out.println("返回: " + successJson);
            out.write(successJson);
        }

        System.out.println("========== TEST SERVLET END ==========");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== TEST SERVLET DO GET ==========");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        String testJson = "{\"code\":1,\"msg\":\"GET测试成功\",\"data\":null}";
        System.out.println("返回: " + testJson);
        out.write(testJson);
        System.out.println("========== TEST SERVLET END ==========");
    }
}